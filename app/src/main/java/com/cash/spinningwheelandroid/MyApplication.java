package com.cash.spinningwheelandroid;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;

/**
 * Created by Hetal on 15-Sep-17.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseApp.initializeApp(MyApplication.this);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}