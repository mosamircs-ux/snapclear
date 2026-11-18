package com.luminasaver.media;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages Google UMP consent, Anchored Adaptive Banner ads, and frequency-capped Interstitial ads.
 */
public class AdManager {

    private static final String TAG = "LuminaAdManager";
    private static final long INTERSTITIAL_COOLDOWN_MS = 90_000L; // Max 1 interstitial per 90s

    private final Activity activity;
    private final FrameLayout bannerContainer;
    private final AtomicBoolean isMobileAdsInitialized = new AtomicBoolean(false);

    private ConsentInformation consentInformation;
    private AdView bannerAdView;
    private InterstitialAd interstitialAd;
    private boolean isLoadingInterstitial = false;
    private long lastInterstitialShownAt = 0L;
    private int completedDownloadsSinceAd = 0;

    public AdManager(Activity activity, FrameLayout bannerContainer) {
        this.activity = activity;
        this.bannerContainer = bannerContainer;
    }

    public void initialize() {
        ConsentRequestParameters params = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        activity,
                        loadAndShowError -> {
                            if (loadAndShowError != null) {
                                Log.w(TAG, "UMP consent error: " + loadAndShowError.getMessage());
                            }
                            if (consentInformation.canRequestAds()) {
                                initializeMobileAdsSdk();
                            }
                        }
                ),
                requestConsentError -> {
                    Log.w(TAG, "UMP consent info update failed: " + requestConsentError.getMessage());
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk();
                    }
                }
        );

        // If consent was already obtained in a prior session, start immediately
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitialized.getAndSet(true)) {
            return;
        }
        new Thread(() -> MobileAds.initialize(activity, initializationStatus -> {
            Log.d(TAG, "MobileAds initialized (testMode=" + BuildConfig.USING_TEST_ADS + ")");
            activity.runOnUiThread(() -> {
                loadAdaptiveBanner();
                preloadInterstitial();
            });
        })).start();
    }

    private void loadAdaptiveBanner() {
        if (activity.isFinishing() || activity.isDestroyed() || bannerContainer == null) {
            return;
        }

        bannerContainer.post(() -> {
            if (activity.isFinishing() || activity.isDestroyed()) return;

            if (bannerAdView != null) {
                bannerAdView.destroy();
            }

            AdView adView = new AdView(activity);
            adView.setAdUnitId(BuildConfig.ADMOB_BANNER_ID);
            adView.setAdSize(getAdaptiveBannerSize());
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    bannerContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError error) {
                    Log.w(TAG, "Banner failed to load: " + error.getMessage());
                    bannerContainer.setVisibility(View.GONE);
                }
            });

            bannerContainer.removeAllViews();
            bannerContainer.addView(adView);
            bannerAdView = adView;

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        });
    }

    private AdSize getAdaptiveBannerSize() {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int widthPx = bannerContainer.getWidth();
        if (widthPx <= 0) {
            widthPx = metrics.widthPixels;
        }
        int adWidthDp = Math.max(320, (int) (widthPx / metrics.density));
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidthDp);
    }

    public void preloadInterstitial() {
        if (isLoadingInterstitial || interstitialAd != null) return;
        if (consentInformation != null && !consentInformation.canRequestAds()) return;

        isLoadingInterstitial = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                activity,
                BuildConfig.ADMOB_INTERSTITIAL_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        isLoadingInterstitial = false;
                        interstitialAd = ad;
                        Log.d(TAG, "Interstitial ad preloaded.");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        isLoadingInterstitial = false;
                        interstitialAd = null;
                        Log.w(TAG, "Interstitial failed to load: " + error.getMessage());
                    }
                }
        );
    }

    /**
     * Shows a frequency-capped interstitial cleanly after a download completes.
     */
    public void onDownloadCompleted() {
        completedDownloadsSinceAd++;
        long now = System.currentTimeMillis();
        boolean cooldownPassed = (now - lastInterstitialShownAt) >= INTERSTITIAL_COOLDOWN_MS;

        if (interstitialAd != null && cooldownPassed && completedDownloadsSinceAd >= 1) {
            activity.runOnUiThread(() -> {
                if (activity.isFinishing() || activity.isDestroyed() || interstitialAd == null) {
                    return;
                }
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAd = null;
                        lastInterstitialShownAt = System.currentTimeMillis();
                        completedDownloadsSinceAd = 0;
                        preloadInterstitial();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        interstitialAd = null;
                        preloadInterstitial();
                    }
                });
                interstitialAd.show(activity);
            });
        } else if (interstitialAd == null) {
            activity.runOnUiThread(this::preloadInterstitial);
        }
    }

    public void updateBannerBackground(boolean isDark) {
        if (bannerContainer != null) {
            bannerContainer.setBackgroundColor(Color.parseColor(isDark ? "#0A1220" : "#E8EEF5"));
        }
    }

    public boolean isPrivacyOptionsRequired() {
        return consentInformation != null
                && consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    public void showPrivacyOptionsForm() {
        activity.runOnUiThread(() -> UserMessagingPlatform.showPrivacyOptionsForm(
                activity,
                formError -> {
                    if (formError != null) {
                        Log.w(TAG, "Privacy form error: " + formError.getMessage());
                    }
                }
        ));
    }

    public void onResume() {
        if (bannerAdView != null) {
            bannerAdView.resume();
        }
    }

    public void onPause() {
        if (bannerAdView != null) {
            bannerAdView.pause();
        }
    }

    public void onDestroy() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
            bannerAdView = null;
        }
        interstitialAd = null;
    }
}
