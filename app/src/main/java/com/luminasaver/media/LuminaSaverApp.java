package com.luminasaver.media;

import android.app.Application;
import android.util.Log;

import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LuminaSaverApp extends Application {
    private static final String TAG = "LuminaSaverApp";

    private static final CountDownLatch engineLatch = new CountDownLatch(1);
    private static volatile boolean engineReady = false;
    private static volatile String engineError = null;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            try {
                YoutubeDL.getInstance().init(this);
                FFmpeg.getInstance().init(this);
                engineReady = true;
                Log.d(TAG, "On-device media engine & FFmpeg initialized successfully.");
            } catch (Exception e) {
                engineError = e.getMessage();
                Log.e(TAG, "Failed to initialize on-device engine: " + e.getMessage(), e);
            } finally {
                engineLatch.countDown();
            }
        }, "LuminaEngineInit").start();
    }

    public static boolean isEngineReady() {
        return engineReady;
    }

    public static String getEngineError() {
        return engineError;
    }

    public static boolean awaitEngineReady(long timeoutMs) {
        if (engineReady) return true;
        try {
            engineLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return engineReady;
    }
}
