package com.luminasaver.media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NativeBridge.HostCallback {

    private LinearLayout rootContainer;
    private WebView webView;
    private FrameLayout adBannerContainer;
    private NativeBridge nativeBridge;
    private AdManager adManager;

    private static final String INDEX_URL = "file:///android_asset/www/index.html";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootContainer = findViewById(R.id.root_container);
        webView = findViewById(R.id.main_webview);
        adBannerContainer = findViewById(R.id.ad_banner_container);

        // Apply Edge-to-Edge system bar insets so status bar & nav bar never overlap UI or ads
        ViewCompat.setOnApplyWindowInsetsListener(rootContainer, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        applySystemBarTheme(true);

        // Configure WebView
        webView.setBackgroundColor(Color.parseColor("#060B14"));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        nativeBridge = new NativeBridge(this, webView, this);
        webView.addJavascriptInterface(nativeBridge, "LuminaNative");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri == null) return false;
                String scheme = uri.getScheme();
                if ("file".equals(scheme)) {
                    return false;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                } catch (Exception e) {
                    return true;
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Only ignore sub-resource errors (e.g., offline thumbnails); never show legacy server dialog
                super.onReceivedError(view, request, error);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        // Modern predictive-back handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Initialize Google UMP Consent + AdMob Banner & Interstitial
        adManager = new AdManager(this, adBannerContainer);
        adManager.initialize();

        handleIncomingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        String targetUrl = INDEX_URL;

        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction())) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText == null) {
                CharSequence subj = intent.getCharSequenceExtra(Intent.EXTRA_SUBJECT);
                if (subj != null) sharedText = subj.toString();
            }
            String extractedUrl = extractUrl(sharedText);
            if (extractedUrl != null) {
                if (webView.getUrl() != null && webView.getUrl().startsWith("file:///android_asset")) {
                    webView.evaluateJavascript(
                            "window.handleExternalUrl && window.handleExternalUrl(" + JSONObject.quote(extractedUrl) + ");",
                            null);
                    return;
                }
                targetUrl = INDEX_URL + "?shared_url=" + Uri.encode(extractedUrl);
            }
        }

        if (webView.getUrl() == null) {
            webView.loadUrl(targetUrl);
        } else if (!targetUrl.equals(INDEX_URL)) {
            webView.loadUrl(targetUrl);
        }
    }

    private String extractUrl(String text) {
        if (text == null) return null;
        Pattern pattern = Pattern.compile("(https?://[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void applySystemBarTheme(boolean isDark) {
        int bgColor = Color.parseColor(isDark ? "#060B14" : "#F4F7FA");
        if (rootContainer != null) rootContainer.setBackgroundColor(bgColor);
        if (webView != null) webView.setBackgroundColor(bgColor);
        if (adManager != null) adManager.updateBannerBackground(isDark);

        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(!isDark);
            controller.setAppearanceLightNavigationBars(!isDark);
        }
    }

    @Override
    public void onThemeChanged(boolean isDark) {
        applySystemBarTheme(isDark);
    }

    @Override
    public void onDownloadCompletedForAd() {
        if (adManager != null) {
            adManager.onDownloadCompleted();
        }
    }

    @Override
    public boolean isPrivacyOptionsRequired() {
        return adManager != null && adManager.isPrivacyOptionsRequired();
    }

    @Override
    public void openPrivacyOptionsForm() {
        if (adManager != null) {
            adManager.showPrivacyOptionsForm();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adManager != null) adManager.onResume();
    }

    @Override
    protected void onPause() {
        if (adManager != null) adManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adManager != null) adManager.onDestroy();
        if (nativeBridge != null) nativeBridge.shutdown();
        if (webView != null) {
            webView.removeJavascriptInterface("LuminaNative");
            webView.destroy();
        }
        super.onDestroy();
    }
}
