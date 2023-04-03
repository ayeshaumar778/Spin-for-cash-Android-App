package com.cash.spinningwheelandroid;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cash.spinningwheelandroid.Constant.AD_ID;
import static com.cash.spinningwheelandroid.Constant.INSTE_AD_ID;
import static com.cash.spinningwheelandroid.Constant.Lucky_NO;
import static com.cash.spinningwheelandroid.Constant.claim;
import static com.cash.spinningwheelandroid.MainActivity.randomNumberInRange;

public class Lucky_no extends AppCompatActivity implements SpinningWheelView_small.OnRotationListener<String> {

    private SpinningWheelView_small wheelView;
    private Button rotate, lucky;
    public static FirebaseUser mFirebaseUser;
    public static FirebaseAuth mFirebaseAuth;
    public static DatabaseReference mDatabase;
    public static String mUserId;
    Toolbar toolbar;
    public static String desc = "Spin wheel";
    private InterstitialAd mInterstitialAd;
    ImageView profileImgView;
    View header;
    TextView no;
    static TextView point;
    private AdView mAdView;
    static int coin = 0;
    int count = 0;
    Boolean offer = false;
    public static String sel_item;

    ProgressDialog progressDialog;
    static final String[] numbers = new String[]{
            "200", "7", "10",
            "300", "8", "5",
            "500", "Oops!", "9", "100", "250", "3", "1", "400"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky);

        wheelView = (SpinningWheelView_small) findViewById(R.id.wheel);
        lucky = (Button) findViewById(R.id.lucky);
        rotate = (Button) findViewById(R.id.rotate);
        point = (TextView) findViewById(R.id.point);
        no = (TextView) findViewById(R.id.txtno);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        wheelView.setItems(R.array.dummy);
        wheelView.setOnRotationListener(this);
        progressDialog = new ProgressDialog(Lucky_no.this);

        MobileAds.initialize(this, AD_ID);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(INSTE_AD_ID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        point.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(Lucky_no.this, R.drawable.coins), null, null, null);

        try {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mUserId = mFirebaseUser.getUid();
        } catch (Exception e) {

        }
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // max angle 50
                // duration 40 second
                // every 50 ms rander rotation
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {

                    if (no.getText().toString().equals(""))
                        Toast.makeText(Lucky_no.this, "Please select lucky number", Toast.LENGTH_SHORT).show();
                    else {
                        int i = randomNumberInRange(4000, 10000);
                        wheelView.rotate(50, i, 50);
                    }
                } else {
                    Intent homeIntent1 = new Intent(Lucky_no.this, Login.class);

                    startActivity(homeIntent1);
                }
            }
        });

        lucky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // max angle 50
                // duration 40 second
                // every 50 ms rander rotation
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {

                    final Dialog dialog = new Dialog(Lucky_no.this);
                    dialog.setTitle("Select a Lucky No.");
                    dialog.setContentView(R.layout.grid);
                    dialog.show();

                    GridView gridView = (GridView) dialog.findViewById(R.id.calendar_grid);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lucky_no.this, R.layout.grid_item, numbers);

                    gridView.setAdapter(adapter);


                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            no.setText(numbers[position]);
                            dialog.dismiss();
                        }
                    });

                }
            }
        });

    }


        public static void setPoint() {

            if (mFirebaseUser != null) {
                mUserId = mFirebaseUser.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference();


                mDatabase.child("users").child(mUserId).child("coin").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {

                            if (appleSnapshot.hasChild("status")) {
                                Query applesQuery = mDatabase.child("users").child(mUserId).child("coin").orderByChild("status").equalTo(claim);

                                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        coin = 0;
                                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {

                                            try {
                                                Model model = appleSnapshot.getValue(Model.class);
                                                coin = coin + Integer.parseInt(model.getCoin());
                                                point.setText("" + coin);
                                            } catch (Exception e) {
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });


                            } else {

                                appleSnapshot.getRef().child("status").setValue(Constant.claim);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        }




    @Override
    public void onRotation() {
        Log.d("XXXX", "On Rotation");
    }

    @Override
    public void onStopRotation(final String item) {

        if (item.equalsIgnoreCase(no.getText().toString())) {
            try {

                new android.app.AlertDialog.Builder(Lucky_no.this)
                        .setTitle(Lucky_NO + " coins ")
                        .setMessage("Please claim to add coins into wallet!!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Claim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mFirebaseUser != null) {

                                    if (isNetworkAvailable()) {
                                        if (mInterstitialAd.isLoaded()) {

                                            showad(item);

                                        } else {

                                            progressDialog.setMessage("Claiming..");
                                            progressDialog.show();

                                        }
                                    } else {

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
                                } else {
                                    Intent inst = new Intent(Lucky_no.this, Login.class);
                                    startActivity(inst);
                                    finish();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }catch (Exception e){}

        } else {
            try {
                new AlertDialog.Builder(Lucky_no.this)
                        .setTitle("Sorry..! you loose ")
                        .setMessage("Try again!!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();
            }catch (Exception e){}


        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showad(String item) {
        if (mInterstitialAd.isLoaded()) {

            mInterstitialAd.show();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            desc = "Lucky Number";
            Model model = new Model(desc, date,""+ Lucky_NO,claim);
            mDatabase.child("users").child(mUserId).child("coin").push().setValue(model);
            desc = "Spin wheel";
            setPoint();
            Toast.makeText(Lucky_no.this, "Coins Added Successfully!", Toast.LENGTH_LONG).show();

        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        setPoint();
    }


    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
