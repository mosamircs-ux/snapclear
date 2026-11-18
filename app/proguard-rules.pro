# Keep WebView JavascriptInterface methods
-keepclassmembers class com.luminasaver.media.NativeBridge {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
-keepattributes *Annotation*

# Keep youtubedl-android, FFmpeg, and Jackson serialization models
-keep class com.yausername.** { *; }
-dontwarn com.yausername.**
-keep class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.**
-keep class org.apache.commons.** { *; }
-dontwarn org.apache.commons.**

# Keep Google Mobile Ads & UMP
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.ump.** { *; }
