package com.cash.spinningwheelandroid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Splash extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1000;
    public FirebaseUser mFirebaseUser;
    public FirebaseAuth mFirebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        ConnectivityManager ConnectionManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    if (mFirebaseUser == null) {
                        Intent inst = new Intent(Splash.this, Login.class);
                        inst.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(inst);
                        finish();
                    } else {
                        Intent intent = new Intent(Splash.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }

            }, SPLASH_TIME_OUT);


        }else {


            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "No Network connection..!!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = getIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
            snackbar.show();
        }
        }
}
