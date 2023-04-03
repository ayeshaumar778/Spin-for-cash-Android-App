package com.cash.spinningwheelandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class Contact extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getBackground().setAlpha(0);

    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
