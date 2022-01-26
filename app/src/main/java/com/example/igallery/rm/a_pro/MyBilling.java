package com.example.igallery.rm.a_pro;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.example.igallery.R;
import com.example.igallery.rm.utils.RmSave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBilling implements BillingClientStateListener {

    private final Activity a;
    private BillingClient billingClient;
    private boolean isConnected;

    private MyBillingResult myBillingResult;
    private final BillingConnectListen billingConnectListen;

    public MyBilling(Activity a, BillingConnectListen billingConnectListen) {
        this.a = a;
        this.billingConnectListen = billingConnectListen;
        isConnected = false;
        billingClient = BillingClient.newBuilder(a)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(this);
    }

    @Override
    public void onBillingServiceDisconnected() {
        isConnected = false;
        reConnectToGooglePlay();
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            isConnected = true;
            checkHistory();
            if (billingConnectListen != null)
                billingConnectListen.onConnected();
        } else {
            reConnectToGooglePlay();
        }
    }

    private void reConnectToGooglePlay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (billingClient != null)
                billingClient.startConnection(MyBilling.this);
        }, 1000);
    }

    private void checkHistory() {
        if (!RmSave.getPay(a))
            billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                    (billingResult, purchasesList) -> {
                        boolean flag = (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ||
                                billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK);
                        if (purchasesList != null) {
                            for (PurchaseHistoryRecord purchase : purchasesList) {
                                if (purchase != null) {
                                    for (String arrId : purchase.getSkus()) {
                                        if (arrId.equals(a.getString(R.string.id_pay))) {
                                            RmSave.putPay(a, flag);
                                        }
                                    }
                                }
                            }
                        }
                    });
    }

    public void getSkuList(PriceResult priceResult, String... list) {
        if (!isConnected) {
            priceResult.onListPrice(new ArrayList<>());
            return;
        }
        ArrayList<String> arrKey = new ArrayList<>();
        Collections.addAll(arrKey, list);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(arrKey).setType(BillingClient.SkuType.INAPP);
        try {
            billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
                int responseCode = billingResult.getResponseCode();
                if (responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null && skuDetailsList.size() > 0) {
                    priceResult.onListPrice(skuDetailsList);
                }
            });
        } catch (NullPointerException e) {
            priceResult.onListPrice(new ArrayList<>());
        }
    }

    public void makePurchase(String key, MyBillingResult myBillingResult) {
        this.myBillingResult = myBillingResult;
        if (!isConnected) {
            if (myBillingResult != null)
                myBillingResult.onNotConnect();
            return;
        }
        ArrayList<String> arrKey = new ArrayList<>();
        arrKey.add(key);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(arrKey).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null && skuDetailsList.size() > 0) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    if (skuDetails.getSku().equals(key)) {
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();
                        billingClient.launchBillingFlow(a, flowParams);
                        return;
                    }
                }
            }
        });
    }

    public final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    for (String arrId : purchase.getSkus()) {
                        if (arrId.equals(a.getString(R.string.id_pay))) {
                            RmSave.putPay(a, true);
                            handlePurchaseOneTime(purchase);
                            sendPurchasesResult();
                        } else {
                            if (myBillingResult != null)
                                myBillingResult.onPurchasesProcessing();
                            handlePurchaseMoreTime(purchase);
                        }
                    }
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR
                    || billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR
                    || billingResult.getResponseCode() < 0) {
                if (myBillingResult != null)
                    myBillingResult.onPurchasesCancel();
            }
        }
    };

    private void sendPurchasesResult() {
        if (myBillingResult != null)
            myBillingResult.onPurchasesDone();
    }

    private void handlePurchaseMoreTime(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                sendPurchasesResult();
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    private void handlePurchaseOneTime(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {

                });
            }
        }
    }

    public void onDestroy() {
        try {
            if (this.billingClient != null) {
                this.billingClient.endConnection();
                this.billingClient = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.billingClient = null;
    }

}
