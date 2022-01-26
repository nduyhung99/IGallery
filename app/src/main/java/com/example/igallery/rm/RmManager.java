package com.example.igallery.rm;

import android.app.Activity;

import com.example.igallery.R;
import com.example.igallery.rm.utils.RmSave;
import com.example.igallery.rm.utils.RmUtils;
import com.google.android.gms.ads.MobileAds;
import com.ironsource.mediationsdk.IronSource;

public class RmManager {

    public RmManager(Activity a) {
        MobileAds.initialize(a);
        IronSource.init(a, a.getString(R.string.ads_irc));

        new Thread(() -> {
            String str = RmUtils.getTextWithUrl(RmUtils.LINK_FIST);
            RmSave.putFist(a, str);
        }).start();
    }
}
