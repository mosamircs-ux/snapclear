# Lumina Saver — Google Play Store Listing & Release Kit

**App Name:** Lumina Saver  
**Application ID:** `com.luminasaver.saver`  
**Play Console App ID:** `4973668036027392673`  
**Play Console URL:** https://play.google.com/console/u/0/developers/7344771316685325507/app/4973668036027392673/app-dashboard

---

## Visual Assets Ready for Upload
- **App Icon (512 × 512 PNG):** `store_assets/lumina_saver_icon_512.png`
- **Feature Graphic (1024 × 500 PNG):** `store_assets/lumina_saver_feature_1024x500.png`

---

## English Store Listing (en-US)

### Short Description (78 chars)
Fast on-device personal media saver & FFmpeg MP3 audio extractor. Dark & Light.

### Full Description
Lumina Saver is a clean, standalone personal media saver and on-device audio extractor from the Lumina utility suite.

Paste any authorized public media link or direct video/audio URL—or share a link directly from another app via the Android Share Sheet—to save offline copies straight into your phone's Downloads/LuminaSaver folder.

**Key Features:**
• **100% On-Device Processing:** Built-in media engine and FFmpeg work locally on your device without external processing servers.
• **Crisp MP3 Audio Extraction:** Convert supported videos or direct media streams into clean 192kbps MP3 audio files.
• **Multiple Video Qualities:** Choose Best Compatible HD MP4, 720p HD, or 480p Data Saver formats.
• **Built-in Saved Library:** Browse, open, and share your recently saved videos and audio tracks in one place.
• **Modern Obsidian Dark & Crisp Light Modes:** Switch themes and languages (English & Arabic RTL) with a single tap.
• **No Account Required:** Your links and saved files stay privately on your device.

**Important Notice:**
Lumina Saver is designed strictly for personal backups, royalty-free media, and public links you have explicit permission to save. Downloading copyrighted material without authorization is prohibited. In compliance with Google Play policies, YouTube links are not supported.

---

## Arabic Store Listing (ar)

### Short Description (76 chars)
حافظ وسائط شخصي ومستخرج صوت MP3 بالكامل على الجهاز. وضع داكن وفاتح وثنائي اللغة.

### Full Description
**Lumina Saver** أداة حديثة ومستقلة لحفظ الوسائط الشخصية واستخراج الصوت مباشرة على هاتفك ضمن مجموعة تطبيقات Lumina.

الصق أي رابط وسائط عام مصرح به أو رابط فيديو/صوت مباشر—أو شارك الرابط مباشرة من التطبيقات الأخرى—لحفظه في مجلد `Downloads/LuminaSaver` على جهازك بكل سهولة.

**أبرز المميزات:**
• **معالجة كاملة على الجهاز 100%:** محرك مدمج مع FFmpeg يعمل محلياً بدون خوادم خارجية.
• **استخراج صوت MP3 نقي:** تحويل الفيديوهات المدعومة إلى ملفات صوتية MP3 بجودة 192kbps.
• **خيارات جودة متعددة:** اختر أعلى جودة HD MP4 أو 720p أو 480p الموفرة للبيانات.
• **مكتبة محلية مدمجة:** استعرض ملفاتك المحفوظة وافتحها أو شاركها مباشرة من داخل التطبيق.
• **وضع داكن وفاتح + لغتان:** واجهة عربية وإنجليزية متكاملة بتصميم عصري أنيق.
• **بدون تسجيل حساب:** ملفاتك تبقى محلياً على جهازك.

**تنويه هام:**
التطبيق مخصص للنسخ الاحتياطي الشخصي والوسائط المجانية والمحتوى المصرح لك بحفظه فقط. تنزيل محتوى يوتيوب غير مدعوم التزاماً بسياسات Google Play.

---

## AdMob Production Setup (`admob.properties`)
Once you create **Lumina Saver** in [Google AdMob](https://apps.admob.com/) with:
1. Banner Ad Unit: `Lumina Saver Banner`
2. Interstitial Ad Unit: `Lumina Saver Interstitial`

Update `admob.properties` in the project root with your real IDs:
```properties
ADMOB_APP_ID=ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY
ADMOB_BANNER_ID=ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ
ADMOB_INTERSTITIAL_ID=ca-app-pub-XXXXXXXXXXXXXXXX/WWWWWWWWWW
```
Then re-run `.\gradlew.bat bundleRelease assembleRelease` to produce the live-ads Play Store `.aab`.
