# Lumina Saver — Personal Media Saver & Audio Extractor

**Package ID:** `com.luminasaver.saver`  
**Target SDK:** Android 16 (API 36) • **Min SDK:** Android 7.0 (API 24)

Lumina Saver is a standalone on-device Android media saver and FFmpeg audio extractor from the Lumina utility suite.

## Features
- **100% On-Device Engine**: Direct media downloading + embedded FFmpeg 192kbps MP3 audio extraction with zero external server required.
- **Play-Safe Compliance**: YouTube URLs are explicitly disabled in compliance with Google Play policies.
- **Modern Bilingual UI**: Obsidian Dark & Crisp Light themes with Emerald/Cyan accents, English (LTR) & Arabic (RTL) toggle, native Android Clipboard paste, and a local Library tab with Open/Share support.
- **Google AdMob + UMP Consent**: Non-overlapping Anchored Adaptive Banner + frequency-capped post-download Interstitial Ad with GDPR/EEA privacy consent support.

## Building
1. Configure live AdMob IDs in `admob.properties` (`debug` builds always use official Google test IDs).
2. Configure release signing credentials in `keystore.properties` (excluded from git).
3. Run:
   - Debug APK: `./gradlew assembleDebug`
   - Signed Play Store App Bundle: `./gradlew bundleRelease`
   - Signed Release APK: `./gradlew assembleRelease`
