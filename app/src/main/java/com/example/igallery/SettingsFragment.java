package com.example.igallery;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.igallery.rm.utils.RmSave;
import com.hsalf.smileyrating.SmileyRating;

public class SettingsFragment extends Fragment {
    private LinearLayout layoutFeedback, layoutPrivacyPolicy, layoutShare, layoutRate, layoutMoreApps, layoutGoPremium;
    public static final String ID_DEV = "8333071100450515738";
    public static final String EMAIL = "ToanHuu2020@gmail.com";
    public static final String TITLE_EMAIL = "IGallery";
    private TextView txtVersion;
    public static final String version = "1.0.0";
    int countingsStars;
    String rateStatus = "RateStatus";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        layoutFeedback = view.findViewById(R.id.layoutFeedback);
        layoutPrivacyPolicy = view.findViewById(R.id.layoutPrivacyPolicy);
        layoutShare = view.findViewById(R.id.layoutShare);
        layoutRate = view.findViewById(R.id.layoutRate);
        layoutMoreApps = view.findViewById(R.id.layoutMoreApps);
        txtVersion = view.findViewById(R.id.txtVersion);
        layoutGoPremium = view.findViewById(R.id.layoutGoPremium);

        if (RmSave.getPay(view.getContext()))
            layoutGoPremium.setVisibility(View.GONE);

        txtVersion.setText(getString(R.string.version) + " " + version);

        layoutGoPremium.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null)
                mainActivity.goPremium();
        });

        layoutRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp(getContext());
            }
        });

        layoutMoreApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreApps(getContext());
            }
        });

        layoutPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                policy(getContext());
            }
        });

        layoutFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback(getContext());
            }
        });

        return view;
    }

    public static void feedback(Context c) {
        String[] email = {EMAIL};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, TITLE_EMAIL);
        try {
            c.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(c, c.getString(R.string.no_email), Toast.LENGTH_SHORT).show();
        }
    }

    public void policy(Context context) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://igalleryforandroid.blogspot.com/2021/12/privacy-policy-igallery-ios-for-android.html"));
            context.startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void shareApp(Context context) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "Install now";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.igallery.iosgallery.forandroid";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }

    }

    public void moreApps(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri.Builder uriBuilder = Uri.parse("https://play.google.com/store/apps/dev")
                .buildUpon()
                .appendQueryParameter("id", ID_DEV);
        intent.setData(uriBuilder.build());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setLayout(getWindowWidth() - 50, LinearLayout.LayoutParams.WRAP_CONTENT);

        SmileyRating rating = dialog.findViewById(R.id.smileyRating);
        rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                switch (type) {
                    case TERRIBLE:
                        countingsStars = 1;
                        ratePkg(getContext(), getContext().getPackageName());
                        break;
                    case BAD:
                        countingsStars = 2;
                        ratePkg(getContext(), getContext().getPackageName());
                        break;
                    case OKAY:
                        countingsStars = 3;
                        ratePkg(getContext(), getContext().getPackageName());
                        break;
                    case GOOD:
                        countingsStars = 4;
                        ratePkg(getContext(), getContext().getPackageName());
                        break;
                    case GREAT:
                        countingsStars = 5;
                        ratePkg(getContext(), getContext().getPackageName());
                        break;
                }
                SharedPreferences preferences = getContext().getSharedPreferences(rateStatus, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("STAR", countingsStars);
                editor.commit();
            }
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finishAffinity();
            }
        });
        dialog.show();
    }

    public void ratePkg(Context context, String pkg) {
        if (pkg == null)
            return;
        Uri uri = Uri.parse("market://details?id=" + pkg);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int getWindowWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}