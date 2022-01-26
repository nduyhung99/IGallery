package com.example.igallery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.igallery.rm.RmManager;
import com.example.igallery.rm.ads.OpenAdsManager;
import com.example.igallery.rm.itf.LoadAdsListen;
import com.example.igallery.rm.itf.ShowAdsListen;
import com.example.igallery.rm.utils.RmSave;

public class IntroduceActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;

    private Handler handler;
    private long timeLoad;
    private boolean isPause;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, Color.WHITE, true, false, IntroduceActivity.this);
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(1792);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window2 = getWindow();
            window2.setNavigationBarColor(0);
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(0);
        }
        setContentView(R.layout.activity_introduce);

        new RmManager(this);
        handler = new Handler();

        checkPermissions();
    }

    private void startSplash() {
        if (OpenAdsManager.getInstance().isAdAvailable() || RmSave.getPay(this)) {
            handler.postDelayed(rShow, 2000);
        } else {
            timeLoad = System.currentTimeMillis();
            OpenAdsManager.getInstance().loadAds(this, loadAdsListen);
            handler.postDelayed(rTimeOut, 10000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startSplash();
        }
        if (IntroduceActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && IntroduceActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startSplash();
        } else {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(IntroduceActivity.this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                startSplash();
            } else {
                requestPermission();
                finish();
            }
        }
    }

    private void requestPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IntroduceActivity.this);
        builder.setTitle("permissionNeeded")
                .setMessage("messagePermissionNeeded")
                .setPositiveButton("ok", (dialog, which) -> {
                    ActivityCompat.requestPermissions(IntroduceActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                    if (ContextCompat.checkSelfPermission(IntroduceActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(IntroduceActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(IntroduceActivity.this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                        startSplash();
                    }
                })
                .setNegativeButton("cancel", (dialog, which) -> {
                    dialog.dismiss();
                    IntroduceActivity.this.finish();
                });
        if (ActivityCompat.shouldShowRequestPermissionRationale(IntroduceActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(IntroduceActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
        } else {
            ActivityCompat.requestPermissions(IntroduceActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenAdsManager.getInstance().onResume(this);
        isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        OpenAdsManager.getInstance().onPause(this);
        isPause = true;
    }

    private final Runnable rShow = new Runnable() {
        @Override
        public void run() {
            if (isPause) {
                finish();
                return;
            }
            showAds();
        }
    };

    private final Runnable rStart = this::startAc;

    private final Runnable rTimeOut = new Runnable() {
        @Override
        public void run() {
            if (isPause) {
                finish();
                return;
            }
            startAc();
        }
    };

    private void startAc() {
        isPause = true;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showAds() {
        OpenAdsManager.getInstance().showAds(this, showAdsListen);
    }

    private final LoadAdsListen loadAdsListen = new LoadAdsListen() {
        @Override
        public void onLoaded() {
            handler.removeCallbacks(rTimeOut);
            if (isPause) {
                finish();
                return;
            }
            long time = System.currentTimeMillis() - timeLoad;
            if (time > 1500) {
                showAds();
            } else {
                handler.postDelayed(rShow, 1000);
            }
        }

        @Override
        public void onLoadError() {
            handler.removeCallbacks(rTimeOut);
            long time = System.currentTimeMillis() - timeLoad;
            if (time > 1500) {
                startAc();
            } else {
                handler.postDelayed(rStart, 1000);
            }
        }
    };

    private final ShowAdsListen showAdsListen = new ShowAdsListen() {
        @Override
        public void onShowError() {
            startAc();
        }

        @Override
        public void onShowed() {

        }

        @Override
        public void onCloseAds() {
            startAc();
        }

        @Override
        public void onClickAds() {

        }

        @Override
        public void onAdsIsNull() {
            startAc();
        }
    };
}