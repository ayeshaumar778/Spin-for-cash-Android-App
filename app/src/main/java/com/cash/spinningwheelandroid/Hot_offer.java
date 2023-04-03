package com.cash.spinningwheelandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cash.spinningwheelandroid.Constant.POINT_VIDEO_MAX;
import static com.cash.spinningwheelandroid.Constant.POINT_VIDEO_MIN;
import static com.cash.spinningwheelandroid.Constant.claim;
import static com.cash.spinningwheelandroid.MainActivity.desc;
import static com.cash.spinningwheelandroid.MainActivity.loadRewardedVideoAd;
import static com.cash.spinningwheelandroid.MainActivity.mDatabase;
import static com.cash.spinningwheelandroid.MainActivity.randomNumberInRange;
import static com.cash.spinningwheelandroid.MainActivity.setPoint;

/**
 * Created by Hetal on 14-Apr-18.
 */

public class Hot_offer extends AppCompatActivity {
    public static TextView textView;
    public static Button btnShow;
    Toolbar toolbar;
    public static RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_ad);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.show);
        btnShow = (Button) findViewById(R.id.btnShow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Hot Offer");
        // loadRewardedVideoAd();
        toolbar.getBackground().setAlpha(0);


        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

                //  Toast.makeText(Hot_offer.this,"loaded",Toast.LENGTH_SHORT).show();
                textView.setText("See video and win surprising amount of coin..!!");
                btnShow.setEnabled(true);
                btnShow.setAlpha(1);


            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem reward) {
                // Reward the user.
                //coin=coin+4;
                int max = POINT_VIDEO_MAX;
                int min = POINT_VIDEO_MIN;

                int randomNum=randomNumberInRange(min,max);
                desc="Offer coin";
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                Model model = new Model(desc, date, "" + randomNum,claim);
                mDatabase.child("users").child(MainActivity.mUserId).child("coin").push().setValue(model);
                Toast.makeText(Hot_offer.this, "Congratulation! you won " + randomNum + " coin", Toast.LENGTH_SHORT).show();
                setPoint();


            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                //    Toast.makeText(Hot_offer.this,"failed loaded"+i,Toast.LENGTH_SHORT).show();
                //  loadRewardedVideoAd();
            }
        };


        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);

        if (mRewardedVideoAd.isLoaded()) {

            btnShow.setEnabled(true);
            btnShow.setAlpha(1);
        } else {
            btnShow.setEnabled(false);
            btnShow.setAlpha(0);
        }


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showRewardedVideo(Hot_offer.this);


            }
        });
    }


    public static void showRewardedVideo(Context context) {

        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else if (!mRewardedVideoAd.isLoaded()) {

            loadRewardedVideoAd();
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setPoint();
        if (isNetworkAvailable() && mRewardedVideoAd.isLoaded()) {
            textView.setText("See video and win surprising amount of coin..!!");
            btnShow.setEnabled(true);
            btnShow.setAlpha(1);
        } else {
            textView.setText("Currently offer is not available.please try after sometime.");
            btnShow.setEnabled(false);
            btnShow.setAlpha(0);
        }
    }

}
