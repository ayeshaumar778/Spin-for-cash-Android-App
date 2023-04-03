package com.cash.spinningwheelandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Privacy extends AppCompatActivity {

    Toolbar toolbar;
    TextView text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        text=(TextView) findViewById(R.id.txtPrivacy);
        text.setText("Add your privacy policy text here");
        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Privacy Policy");

    }
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
