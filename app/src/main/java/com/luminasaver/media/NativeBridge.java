package com.luminasaver.media;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NativeBridge {

    private static final String TAG = "LuminaNativeBridge";
    private static final String PREFS_HISTORY = "LuminaSaverHistory";
    private static final String KEY_HISTORY_JSON = "items_json";
    private static final int MAX_HISTORY_ITEMS = 50;

    public interface HostCallback {
        void onThemeChanged(boolean isDark);
        void onDownloadCompletedForAd();
        boolean isPrivacyOptionsRequired();
        void openPrivacyOptionsForm();
    }

    private final Activity activity;
    private final WebView webView;
    private final HostCallback hostCallback;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final SharedPreferences historyPrefs;

    public NativeBridge(Activity activity, WebView webView, HostCallback hostCallback) {
        this.activity = activity;
        this.webView = webView;
        this.hostCallback = hostCallback;
        this.historyPrefs = activity.getSharedPreferences(PREFS_HISTORY, Context.MODE_PRIVATE);
    }

    @JavascriptInterface
    public boolean isEngineReady() {
        return LuminaSaverApp.isEngineReady();
    }

    @JavascriptInterface
    public String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @JavascriptInterface
    public boolean isPrivacyOptionsRequired() {
        return hostCallback != null && hostCallback.isPrivacyOptionsRequired();
    }

    @JavascriptInterface
    public void openPrivacySettings() {
        if (hostCallback != null) {
            hostCallback.openPrivacyOptionsForm();
        }
    }

    @JavascriptInterface
    public void setSystemTheme(boolean isDark) {
        if (hostCallback != null) {
            activity.runOnUiThread(() -> hostCallback.onThemeChanged(isDark));
        }
    }

    @JavascriptInterface
    public String getClipboardText() {
        try {
            ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null && cm.hasPrimaryClip()) {
                ClipData clip = cm.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    CharSequence text = clip.getItemAt(0).coerceToText(activity);
                    if (text != null) {
                        return text.toString().trim();
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Clipboard read failed: " + e.getMessage());
        }
        return "";
    }

    @JavascriptInterface
    public String getDownloadHistory() {
        return historyPrefs.getString(KEY_HISTORY_JSON, "[]");
    }

    @JavascriptInterface
    public void clearDownloadHistory() {
        historyPrefs.edit().putString(KEY_HISTORY_JSON, "[]").apply();
    }

    @JavascriptInterface
    public void openSavedMedia(String uriString, String mimeType) {
        activity.runOnUiThread(() -> {
            try {
                if (uriString == null || uriString.isEmpty()) {
                    Toast.makeText(activity, "File saved in Downloads folder", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(uriString), mimeType != null && !mimeType.isEmpty() ? mimeType : "*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(Intent.createChooser(intent, "Open with"));
            } catch (Exception e) {
                Toast.makeText(activity, "Open your Downloads folder to view this file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void shareSavedMedia(String uriString, String mimeType) {
        activity.runOnUiThread(() -> {
            try {
                if (uriString == null || uriString.isEmpty()) return;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType(mimeType != null && !mimeType.isEmpty() ? mimeType : "*/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriString));
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(Intent.createChooser(share, "Share media"));
            } catch (Exception e) {
                Toast.makeText(activity, "Unable to share file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void inspectUrl(String url, String lang) {
        final boolean isAr = "ar".equalsIgnoreCase(lang);
        if (url == null || url.trim().isEmpty()) {
            sendErrorToWeb(isAr ? "يرجى إدخال رابط وسائط صالح." : "Please enter a valid media link.");
            return;
        }

        final String trimmedUrl = url.trim();
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            sendErrorToWeb(isAr ? "يجب أن يبدأ الرابط بـ https:// أو http://" : "Link must start with https:// or http://");
            return;
        }

        // Play Store policy compliance: strictly block YouTube URLs
        if (isBlockedPlatformUrl(trimmedUrl)) {
            sendErrorToWeb(isAr
                    ? "تنزيل محتوى يوتيوب غير مدعوم التزاماً بسياسات Google Play وحقوق الملكية."
                    : "YouTube links are not supported in compliance with Google Play policies.");
            return;
        }

        executor.execute(() -> {
            // Fast path for direct media file links (.mp4, .mp3, .webm, .m4a, .wav, etc.)
            if (isDirectMediaUrl(trimmedUrl)) {
                JSONObject directInfo = inspectDirectMediaUrl(trimmedUrl, isAr);
                if (directInfo != null) {
                    sendInspectResultToWeb(directInfo.toString());
                    return;
                }
            }

            // Wait for on-device engine readiness (avoids first-launch race condition)
            if (!LuminaSaverApp.awaitEngineReady(45_000L)) {
                sendErrorToWeb(isAr
                        ? "المحرك الداخلي لا يزال قيد التهيئة. حاول مرة أخرى بعد ثوانٍ."
                        : "On-device media engine is still initializing. Please try again shortly.");
                return;
            }

            try {
                Log.d(TAG, "Inspecting public media URL: " + trimmedUrl);
                YoutubeDLRequest request = new YoutubeDLRequest(trimmedUrl);
                request.addOption("--no-warnings");
                request.addOption("--no-playlist");

                VideoInfo info = YoutubeDL.getInstance().getInfo(request);
                if (info == null) {
                    sendErrorToWeb(isAr
                            ? "تعذر قراءة بيانات الرابط. تأكد أن الرابط عام ومتاح."
                            : "Could not read media details. Make sure the link is public and accessible.");
                    return;
                }

                JSONObject result = new JSONObject();
                result.put("title", info.getTitle() != null && !info.getTitle().isEmpty()
                        ? info.getTitle()
                        : (isAr ? "ملف وسائط" : "Media File"));
                result.put("thumbnail", info.getThumbnail() != null ? info.getThumbnail() : "");
                result.put("duration", formatDuration(info.getDuration()));
                result.put("uploader", info.getUploader() != null && !info.getUploader().isEmpty()
                        ? info.getUploader()
                        : extractHostName(trimmedUrl));
                result.put("platform", extractHostName(trimmedUrl));
                result.put("url", trimmedUrl);

                JSONArray options = new JSONArray();

                JSONObject bestOpt = new JSONObject();
                bestOpt.put("id", "video-best");
                bestOpt.put("type", "video");
                bestOpt.put("quality", "best");
                bestOpt.put("label", isAr ? "أعلى جودة متوافقة (HD MP4)" : "Best Compatible HD (MP4)");
                bestOpt.put("badge", isAr ? "موصى به" : "Recommended");
                bestOpt.put("ext", "mp4");
                options.put(bestOpt);

                JSONObject hd720 = new JSONObject();
                hd720.put("id", "video-720");
                hd720.put("type", "video");
                hd720.put("quality", "720");
                hd720.put("label", isAr ? "فيديو 720p HD" : "720p HD Video");
                hd720.put("badge", "720p");
                hd720.put("ext", "mp4");
                options.put(hd720);

                JSONObject sd480 = new JSONObject();
                sd480.put("id", "video-480");
                sd480.put("type", "video");
                sd480.put("quality", "480");
                sd480.put("label", isAr ? "فيديو 480p موفر للبيانات" : "480p Data Saver");
                sd480.put("badge", "480p");
                sd480.put("ext", "mp4");
                options.put(sd480);

                JSONObject mp3Opt = new JSONObject();
                mp3Opt.put("id", "mp3-best");
                mp3Opt.put("type", "audio");
                mp3Opt.put("quality", "mp3");
                mp3Opt.put("label", isAr ? "استخراج صوت MP3 نقي" : "Extract MP3 Audio (192kbps)");
                mp3Opt.put("badge", "FFmpeg MP3");
                mp3Opt.put("ext", "mp3");
                options.put(mp3Opt);

                result.put("options", options);
                sendInspectResultToWeb(result.toString());

            } catch (Exception e) {
                Log.e(TAG, "Inspect failed: " + e.getMessage(), e);
                sendErrorToWeb(isAr
                        ? "تعذر تحليل الرابط: تأكد أنه رابط وسائط عام مدعوم."
                        : "Unable to analyze this link. Please verify it is a supported public media URL.");
            }
        });
    }

    @JavascriptInterface
    public void startDownload(String url, String formatType, String quality, String titleHint, String lang) {
        final boolean isAr = "ar".equalsIgnoreCase(lang);
        if (url == null || url.trim().isEmpty()) {
            sendDownloadFailed(isAr ? "الرابط غير صالح." : "Invalid media link.");
            return;
        }
        final String trimmedUrl = url.trim();
        if (isBlockedPlatformUrl(trimmedUrl)) {
            sendDownloadFailed(isAr
                    ? "تنزيل محتوى يوتيوب غير مدعوم التزاماً بسياسات Google Play."
                    : "YouTube links are not supported in compliance with Google Play policies.");
            return;
        }

        executor.execute(() -> {
            try {
                // Fast direct HTTP download when saving original direct file (no transcode needed)
                if ("direct_file".equals(formatType)) {
                    downloadDirectFile(trimmedUrl, quality, titleHint, isAr);
                    return;
                }

                if (!LuminaSaverApp.awaitEngineReady(45_000L)) {
                    sendDownloadFailed(isAr
                            ? "المحرك الداخلي غير جاهز بعد. يرجى المحاولة مجدداً."
                            : "Media engine is still initializing. Please retry in a moment.");
                    return;
                }

                String taskId = "lumina_" + System.currentTimeMillis();
                File tempDir = new File(activity.getCacheDir(), taskId);
                tempDir.mkdirs();

                updateDownloadProgress(10, isAr ? "جاري تحضير المحرك المدمج..." : "Preparing on-device engine...");

                YoutubeDLRequest request = new YoutubeDLRequest(trimmedUrl);
                request.addOption("-o", new File(tempDir, "%(title).60s.%(ext)s").getAbsolutePath());
                request.addOption("--no-playlist");

                boolean isAudio = "audio".equals(formatType);
                if (isAudio) {
                    request.addOption("-x");
                    request.addOption("--audio-format", "mp3");
                    request.addOption("--audio-quality", "192K");
                } else {
                    if ("best".equals(quality)) {
                        request.addOption("-f", "bestvideo[vcodec^=avc1][height<=1080]+bestaudio[ext=m4a]/bestvideo[height<=1080]+bestaudio/best[height<=1080]/best");
                    } else {
                        try {
                            int h = Integer.parseInt(quality);
                            request.addOption("-f", "bestvideo[vcodec^=avc1][height<=" + h + "]+bestaudio[ext=m4a]/bestvideo[height<=" + h + "]+bestaudio/best[height<=" + h + "]/best");
                        } catch (Exception ex) {
                            request.addOption("-f", "bestvideo[vcodec^=avc1]+bestaudio/best");
                        }
                    }
                    request.addOption("--merge-output-format", "mp4");
                }

                YoutubeDL.getInstance().execute(request, taskId, (progress, etaInSeconds, line) -> {
                    if (progress != null) {
                        int p = Math.max(10, Math.min(96, progress.intValue()));
                        updateDownloadProgress(p, isAr
                                ? "جاري الحفظ المباشر (" + p + "%)"
                                : "Saving on-device (" + p + "%)");
                    }
                    return kotlin.Unit.INSTANCE;
                });

                updateDownloadProgress(98, isAr ? "جاري الحفظ في مجلد التنزيلات..." : "Saving to Downloads gallery...");

                File[] files = tempDir.listFiles();
                if (files != null && files.length > 0) {
                    File producedFile = files[0];
                    String targetName = sanitizeFileName(producedFile.getName(), isAudio ? ".mp3" : ".mp4");
                    long sizeBytes = producedFile.length();
                    String mimeType = isAudio ? "audio/mpeg" : "video/mp4";

                    Uri savedUri = saveToPublicDownloads(producedFile, targetName, mimeType);
                    cleanupDir(tempDir);

                    if (savedUri != null) {
                        recordHistoryItem(titleHint != null && !titleHint.isEmpty() ? titleHint : targetName,
                                targetName, savedUri.toString(), mimeType, isAudio ? "MP3" : "MP4", sizeBytes);
                        notifyDownloadComplete(targetName, savedUri.toString(), mimeType, isAr);
                    } else {
                        sendDownloadFailed(isAr ? "تعذر حفظ الملف في مجلد التنزيلات." : "Could not save file to Downloads.");
                    }
                } else {
                    cleanupDir(tempDir);
                    sendDownloadFailed(isAr ? "لم يتم العثور على الملف بعد المعالجة." : "Output file was not produced.");
                }

            } catch (Exception e) {
                Log.e(TAG, "Download failed: " + e.getMessage(), e);
                sendDownloadFailed(isAr ? "فشل الحفظ: " + e.getMessage() : "Save failed: " + e.getMessage());
            }
        });
    }

    private JSONObject inspectDirectMediaUrl(String url, boolean isAr) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 15) LuminaSaver/1.0");

            int code = conn.getResponseCode();
            long length = conn.getContentLengthLong();
            String contentType = conn.getContentType();
            conn.disconnect();

            if (code >= 400) return null;

            Uri uri = Uri.parse(url);
            String lastSeg = uri.getLastPathSegment();
            if (lastSeg == null || lastSeg.isEmpty()) lastSeg = "media_file.mp4";
            String lower = lastSeg.toLowerCase(Locale.US);
            boolean isAudioFile = lower.endsWith(".mp3") || lower.endsWith(".m4a") || lower.endsWith(".wav") || lower.endsWith(".ogg") || lower.endsWith(".aac");
            String ext = getExtension(lower, isAudioFile ? "mp3" : "mp4");
            String sizeLabel = length > 0 ? formatBytes(length) : (isAr ? "رابط مباشر" : "Direct Link");

            JSONObject result = new JSONObject();
            result.put("title", lastSeg);
            result.put("thumbnail", "");
            result.put("duration", sizeLabel);
            result.put("uploader", extractHostName(url));
            result.put("platform", isAr ? "ملف وسائط مباشر" : "Direct Media");
            result.put("url", url);

            JSONArray options = new JSONArray();

            JSONObject orig = new JSONObject();
            orig.put("id", "direct-original");
            orig.put("type", "direct_file");
            orig.put("quality", ext);
            orig.put("label", isAr ? "حفظ الملف الأصلي (" + ext.toUpperCase(Locale.US) + ")" : "Save Original File (" + ext.toUpperCase(Locale.US) + ")");
            orig.put("badge", sizeLabel);
            orig.put("ext", ext);
            options.put(orig);

            if (!isAudioFile) {
                JSONObject audioOpt = new JSONObject();
                audioOpt.put("id", "direct-mp3");
                audioOpt.put("type", "audio");
                audioOpt.put("quality", "mp3");
                audioOpt.put("label", isAr ? "استخراج صوت MP3 عبر FFmpeg" : "Extract MP3 Audio (FFmpeg)");
                audioOpt.put("badge", "192kbps MP3");
                audioOpt.put("ext", "mp3");
                options.put(audioOpt);
            }

            result.put("options", options);
            return result;
        } catch (Exception e) {
            Log.w(TAG, "Direct HEAD inspect failed, falling back: " + e.getMessage());
            return null;
        }
    }

    private void downloadDirectFile(String directUrl, String ext, String titleHint, boolean isAr) {
        File tempFile = null;
        try {
            updateDownloadProgress(12, isAr ? "جاري الاتصال المباشر بالملف..." : "Connecting to media file...");
            HttpURLConnection conn = (HttpURLConnection) new URL(directUrl).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 15) LuminaSaver/1.0");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(25_000);

            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                sendDownloadFailed(isAr ? "تعذر الوصول للملف (كود " + code + ")" : "Could not reach media file (HTTP " + code + ")");
                return;
            }

            long totalLength = conn.getContentLengthLong();
            String safeExt = (ext != null && !ext.isEmpty()) ? "." + ext.replace(".", "") : ".mp4";
            String baseTitle = (titleHint != null && !titleHint.isEmpty()) ? titleHint : ("Lumina_" + System.currentTimeMillis());
            String finalName = sanitizeFileName(baseTitle, safeExt);
            String mimeType = guessMimeFromExt(safeExt);

            tempFile = new File(activity.getCacheDir(), "direct_" + System.currentTimeMillis() + safeExt);
            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buf = new byte[16384];
                int read;
                long downloaded = 0;
                int lastPct = 12;
                while ((read = in.read(buf)) != -1) {
                    out.write(buf, 0, read);
                    downloaded += read;
                    if (totalLength > 0) {
                        int pct = (int) ((downloaded * 82) / totalLength) + 12;
                        if (pct > lastPct && pct <= 96) {
                            lastPct = pct;
                            updateDownloadProgress(pct, isAr ? "جاري التنزيل (" + pct + "%)" : "Downloading (" + pct + "%)");
                        }
                    }
                }
                out.flush();
            }

            updateDownloadProgress(98, isAr ? "جاري الحفظ في التنزيلات..." : "Saving to Downloads...");
            long sizeBytes = tempFile.length();
            Uri savedUri = saveToPublicDownloads(tempFile, finalName, mimeType);
            if (savedUri != null) {
                recordHistoryItem(baseTitle, finalName, savedUri.toString(), mimeType,
                        safeExt.replace(".", "").toUpperCase(Locale.US), sizeBytes);
                notifyDownloadComplete(finalName, savedUri.toString(), mimeType, isAr);
            } else {
                sendDownloadFailed(isAr ? "تعذر حفظ الملف." : "Failed to save file.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Direct download failed: " + e.getMessage(), e);
            sendDownloadFailed(isAr ? "فشل التنزيل: " + e.getMessage() : "Download failed: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private Uri saveToPublicDownloads(File sourceFile, String displayName, String mimeType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                ContentResolver resolver = activity.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/LuminaSaver");
                values.put(MediaStore.MediaColumns.IS_PENDING, 1);

                Uri itemUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (itemUri != null) {
                    try (InputStream in = new FileInputStream(sourceFile);
                         OutputStream out = resolver.openOutputStream(itemUri)) {
                        if (out == null) return null;
                        byte[] buf = new byte[16384];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.flush();
                    }
                    values.clear();
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                    resolver.update(itemUri, values, null, null);
                    return itemUri;
                }
            } catch (Exception e) {
                Log.e(TAG, "MediaStore save error: " + e.getMessage(), e);
            }
        }

        // Legacy fallback for Android 7–9 (API 24–28)
        try {
            File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "LuminaSaver");
            if (!downloadDir.exists()) downloadDir.mkdirs();
            File dest = new File(downloadDir, displayName);
            try (InputStream in = new FileInputStream(sourceFile);
                 OutputStream out = new FileOutputStream(dest)) {
                byte[] buf = new byte[16384];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            }
            MediaScannerConnection.scanFile(activity, new String[]{dest.getAbsolutePath()}, new String[]{mimeType}, null);
            return Uri.fromFile(dest);
        } catch (Exception e) {
            Log.e(TAG, "Legacy file save error: " + e.getMessage(), e);
        }
        return null;
    }

    private void recordHistoryItem(String title, String filename, String uri, String mimeType, String format, long sizeBytes) {
        try {
            JSONArray arr = new JSONArray(historyPrefs.getString(KEY_HISTORY_JSON, "[]"));
            JSONObject item = new JSONObject();
            item.put("id", String.valueOf(System.currentTimeMillis()));
            item.put("title", title);
            item.put("filename", filename);
            item.put("uri", uri);
            item.put("mimeType", mimeType);
            item.put("format", format);
            item.put("size", formatBytes(sizeBytes));
            item.put("timestamp", System.currentTimeMillis());

            JSONArray updated = new JSONArray();
            updated.put(item);
            for (int i = 0; i < Math.min(arr.length(), MAX_HISTORY_ITEMS - 1); i++) {
                updated.put(arr.get(i));
            }
            historyPrefs.edit().putString(KEY_HISTORY_JSON, updated.toString()).apply();
        } catch (Exception e) {
            Log.w(TAG, "Failed to update download history: " + e.getMessage());
        }
    }

    private boolean isBlockedPlatformUrl(String url) {
        try {
            String host = new URI(url).getHost();
            if (host == null) return false;
            host = host.toLowerCase(Locale.US);
            return host.equals("youtube.com") || host.endsWith(".youtube.com")
                    || host.equals("youtu.be") || host.endsWith(".youtu.be")
                    || host.equals("youtube-nocookie.com") || host.endsWith(".youtube-nocookie.com")
                    || host.equals("youtubekids.com") || host.endsWith(".youtubekids.com");
        } catch (Exception e) {
            String lower = url.toLowerCase(Locale.US);
            return lower.contains("youtube.com") || lower.contains("youtu.be");
        }
    }

    private boolean isDirectMediaUrl(String url) {
        try {
            String path = new URI(url).getPath();
            if (path == null) return false;
            String lower = path.toLowerCase(Locale.US);
            return lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mov")
                    || lower.endsWith(".m4v") || lower.endsWith(".mp3") || lower.endsWith(".m4a")
                    || lower.endsWith(".wav") || lower.endsWith(".ogg") || lower.endsWith(".aac");
        } catch (Exception e) {
            return false;
        }
    }

    private String extractHostName(String url) {
        try {
            String host = new URI(url).getHost();
            if (host != null) {
                return host.startsWith("www.") ? host.substring(4) : host;
            }
        } catch (Exception ignored) {}
        return "Web";
    }

    private String sanitizeFileName(String raw, String fallbackExt) {
        String cleaned = raw.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (cleaned.isEmpty()) {
            cleaned = "Lumina_" + System.currentTimeMillis() + fallbackExt;
        } else if (!cleaned.contains(".")) {
            cleaned = cleaned + fallbackExt;
        }
        return cleaned;
    }

    private String getExtension(String lowerName, String fallback) {
        int idx = lowerName.lastIndexOf('.');
        if (idx > 0 && idx < lowerName.length() - 1) {
            return lowerName.substring(idx + 1);
        }
        return fallback;
    }

    private String guessMimeFromExt(String ext) {
        String lower = ext.toLowerCase(Locale.US);
        if (lower.endsWith("mp3")) return "audio/mpeg";
        if (lower.endsWith("m4a") || lower.endsWith("aac")) return "audio/mp4";
        if (lower.endsWith("wav")) return "audio/wav";
        if (lower.endsWith("ogg")) return "audio/ogg";
        if (lower.endsWith("webm")) return "video/webm";
        if (lower.endsWith("mov")) return "video/quicktime";
        return "video/mp4";
    }

    private String formatBytes(long bytes) {
        if (bytes <= 0) return "";
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb);
        double mb = kb / 1024.0;
        return String.format(Locale.US, "%.1f MB", mb);
    }

    private String formatDuration(long seconds) {
        if (seconds <= 0) return "--:--";
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format(Locale.US, "%02d:%02d:%02d", h, m, s);
        return String.format(Locale.US, "%02d:%02d", m, s);
    }

    private void cleanupDir(File dir) {
        if (dir == null || !dir.exists()) return;
        File[] children = dir.listFiles();
        if (children != null) {
            for (File f : children) {
                f.delete();
            }
        }
        dir.delete();
    }

    private void sendInspectResultToWeb(String jsonString) {
        activity.runOnUiThread(() ->
                webView.evaluateJavascript("window.renderVideoData && window.renderVideoData(" + jsonString + ");", null));
    }

    private void sendErrorToWeb(String msg) {
        activity.runOnUiThread(() ->
                webView.evaluateJavascript("window.showAlert && window.showAlert(" + JSONObject.quote(msg) + ");", null));
    }

    private void updateDownloadProgress(int percent, String statusText) {
        activity.runOnUiThread(() ->
                webView.evaluateJavascript(
                        "window.updateNativeProgress && window.updateNativeProgress(" + percent + ", " + JSONObject.quote(statusText) + ");",
                        null));
    }

    private void notifyDownloadComplete(String filename, String uriString, String mimeType, boolean isAr) {
        activity.runOnUiThread(() -> {
            Toast.makeText(activity,
                    (isAr ? "تم الحفظ في التنزيلات: " : "Saved to Downloads: ") + filename,
                    Toast.LENGTH_SHORT).show();
            webView.evaluateJavascript(
                    "window.onNativeDownloadReady && window.onNativeDownloadReady("
                            + JSONObject.quote(filename) + ", "
                            + JSONObject.quote(uriString) + ", "
                            + JSONObject.quote(mimeType) + ");",
                    null);
            if (hostCallback != null) {
                hostCallback.onDownloadCompletedForAd();
            }
        });
    }

    private void sendDownloadFailed(String error) {
        activity.runOnUiThread(() ->
                webView.evaluateJavascript("window.onNativeDownloadFailed && window.onNativeDownloadFailed(" + JSONObject.quote(error) + ");", null));
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
