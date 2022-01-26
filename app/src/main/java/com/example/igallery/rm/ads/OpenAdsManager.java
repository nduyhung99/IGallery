package com.example.igallery.rm.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.igallery.R;
import com.example.igallery.rm.itf.LoadAdsListen;
import com.example.igallery.rm.itf.ShowAdsListen;
import com.example.igallery.rm.utils.RmSave;
import com.example.igallery.rm.utils.RmUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import java.util.Date;

public class OpenAdsManager {

    private static OpenAdsManager openAds;

    public static OpenAdsManager getInstance() {
        if (openAds == null)
            openAds = new OpenAdsManager();
        return openAds;
    }

    private boolean isLoading = false;
    private long loadTime = 0;
    private boolean googleFist;

    private AppOpenAd appOpenAd;
    private final AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private final InterstitialListener loadIrcCallback;
    private LoadAdsListen loadAdsListen;
    private ShowAdsListen showAdsListen;

    private Context c;
    private final Handler handler;

    public OpenAdsManager() {
        handler = new Handler(msg -> {
            switch (msg.what) {
                case 1:
                    loadComplete(true);
                    break;
                case 2:
                    loadComplete(false);
                    break;
                case 3:
                    if (OpenAdsManager.this.showAdsListen != null)
                        OpenAdsManager.this.showAdsListen.onAdsIsNull();
                    break;
                case 4:
                    if (OpenAdsManager.this.showAdsListen != null)
                        OpenAdsManager.this.showAdsListen.onShowError();
                    break;
                case 5:
                    if (OpenAdsManager.this.showAdsListen != null)
                        OpenAdsManager.this.showAdsListen.onShowed();
                    break;
                case 6:
                    if (OpenAdsManager.this.showAdsListen != null)
                        OpenAdsManager.this.showAdsListen.onCloseAds();
                    break;
                case 7:
                    if (OpenAdsManager.this.showAdsListen != null)
                        OpenAdsManager.this.showAdsListen.onClickAds();
                    break;
            }
            return true;
        });
        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                OpenAdsManager.this.appOpenAd = ad;
                OpenAdsManager.this.loadTime = (new Date()).getTime();
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (googleFist) {
                    loadIrcAds();
                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        };
        loadIrcCallback = new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {
                OpenAdsManager.this.loadTime = (new Date()).getTime();
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                if (googleFist) {
                    handler.sendEmptyMessage(2);
                } else {
                    if (c != null) {
                        try {
                            loadGoogleAds(c);
                        } catch (IllegalStateException e) {
                            handler.sendEmptyMessage(2);
                        }
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                }
            }

            @Override
            public void onInterstitialAdOpened() {
                handler.sendEmptyMessage(5);
            }

            @Override
            public void onInterstitialAdClosed() {
                handler.sendEmptyMessage(6);
            }

            @Override
            public void onInterstitialAdShowSucceeded() {

            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                handler.sendEmptyMessage(4);
            }

            @Override
            public void onInterstitialAdClicked() {
                handler.sendEmptyMessage(7);
            }
        };
    }

    /**
     * Load ads
     */
    public void loadAds(Context c, LoadAdsListen loadAdsListen) {
        if (RmSave.getPay(c)) {
            loadComplete(false);
            return;
        }
        this.loadAdsListen = loadAdsListen;
        if (isAdAvailable() || isLoading)
            return;
        isLoading = true;
        googleFist = RmSave.getGoogleFist(c);
        if (googleFist) {
            loadGoogleAds(c);
        } else {
            this.c = c;
            loadIrcAds();
        }
    }

    private void loadGoogleAds(Context c) {
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(c, c.getString(R.string.ads_open), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    private void loadIrcAds() {
        if (IronSource.isInterstitialReady()) {
            handler.sendEmptyMessage(1);
            return;
        }
        IronSource.setInterstitialListener(loadIrcCallback);
        IronSource.loadInterstitial();
    }

    private void loadComplete(boolean success) {
        isLoading = false;
        if (c != null)
            c = null;
        if (loadAdsListen != null) {
            if (success)
                loadAdsListen.onLoaded();
            else
                loadAdsListen.onLoadError();
        }
    }

    /**
     * Show ads
     */
    public void showAds(Activity activity, ShowAdsListen showAdsListen) {
        if (activity.isDestroyed() || RmSave.getPay(activity)) {
            showAdsListen.onShowError();
            return;
        }
        this.showAdsListen = showAdsListen;
        if (appOpenAd != null) {
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    handler.sendEmptyMessage(4);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    handler.sendEmptyMessage(5);
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    OpenAdsManager.this.appOpenAd = null;
                    handler.sendEmptyMessage(6);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    handler.sendEmptyMessage(7);
                }
            });
            appOpenAd.show(activity);
        } else if (IronSource.isInterstitialReady()) {
            IronSource.setInterstitialListener(loadIrcCallback);
            IronSource.showInterstitial();
        } else {
            handler.sendEmptyMessage(3);
        }
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && RmUtils.wasLoadTimeLessThanNHoursAgo(loadTime, 4);
    }

    public void onResume(Activity activity) {
        IronSource.onResume(activity);
    }

    public void onPause(Activity activity) {
        IronSource.onPause(activity);
    }
}
