package com.cash.spinningwheelandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static com.cash.spinningwheelandroid.Constant.AD_ID;
import static com.cash.spinningwheelandroid.Constant.DAILY_CHECK;
import static com.cash.spinningwheelandroid.Constant.INSTE_AD_ID;
import static com.cash.spinningwheelandroid.Constant.RATE_POINT;
import static com.cash.spinningwheelandroid.Constant.VIDEO_AD_ID;
import static com.cash.spinningwheelandroid.Constant.claim;

public class MainActivity extends AppCompatActivity implements SpinningWheelView.OnRotationListener<String> {

    private SpinningWheelView wheelView;
    private Button rotate;
    public static FirebaseUser mFirebaseUser;
    public static FirebaseAuth mFirebaseAuth;
    public static DatabaseReference mDatabase;
    public static String mUserId;
    Toolbar toolbar;
    public static String desc = "Spin wheel";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private InterstitialAd mInterstitialAd;
    SharedPreferences prefs = null;
    SharedPreferences prefs_daily = null;
    ImageView profileImgView;
    View header;
    TextView profilePicImgname, email;
    static TextView point;
    private AdView mAdView;

    static int coin = 0;
    int count = 0;
    public static Boolean offer = false;
    public static String sel_item;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheelView = (SpinningWheelView) findViewById(R.id.wheel);

        rotate = (Button) findViewById(R.id.rotate);
        point = (TextView) findViewById(R.id.point);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        wheelView.setItems(R.array.dummy);

        wheelView.setOnRotationListener(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
        prefs_daily = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

        setupnavigationDrawer();
        MobileAds.initialize(this, AD_ID);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(INSTE_AD_ID);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        progressDialog = new ProgressDialog(MainActivity.this);
        point.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(MainActivity.this, R.drawable.coins), null, null, null);


        Hot_offer.mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);


        try {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mUserId = mFirebaseUser.getUid();
        } catch (Exception e) {

        }


        if (mFirebaseUser == null) {
            Intent homeIntent1 = new Intent(MainActivity.this, Login.class);
            finish();
            startActivity(homeIntent1);
        } else {

            if (navigationView != null) {

                header = navigationView.getHeaderView(0);

                profileImgView = (ImageView) header.findViewById(R.id.profilePicImg);

                Glide.with(this)
                        .load(mFirebaseUser.getPhotoUrl())
                        .into(profileImgView);

                profilePicImgname = (TextView) header.findViewById(R.id.profilePicImgname);
                email = (TextView) header.findViewById(R.id.email);
                profilePicImgname.setText(mFirebaseUser.getDisplayName());
                email.setText(mFirebaseUser.getEmail());

                setPoint();

                profileImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent homeIntent1 = new Intent(MainActivity.this, Login.class);
                        startActivity(homeIntent1);

                    }
                });


                rotate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // max angle 50
                        // duration 40 second
                        // every 50 ms rander rotation

                        desc="Spin wheel";
                        mFirebaseUser = mFirebaseAuth.getCurrentUser();
                        if (mFirebaseUser != null) {

                            int i = randomNumberInRange(4000, 10000);
                            System.out.println("random.."+i);

                            wheelView.rotate(50, i, 50);
                        } else {
                            Intent homeIntent1 = new Intent(MainActivity.this, Login.class);

                            startActivity(homeIntent1);
                        }
                    }
                });


            }
        }


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //   Toast.makeText(MainActivity.this,"ad loaded",Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad finishes loading.
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                // Toast.makeText(MainActivity.this,"faild"+errorCode,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    public static int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
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

        if (!item.equalsIgnoreCase("Oops!")) {
            sel_item = item;

            try {


                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(item + " coins ")
                        .setMessage("Please claim to add coins into wallet!!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Claim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mFirebaseUser != null) {

                                    if (isNetworkAvailable()) {
                                        if (mInterstitialAd.isLoaded()) {

                                            showad(item);

                                        } else {
                                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
                                    Intent inst = new Intent(MainActivity.this, Login.class);
                                    startActivity(inst);
                                    finish();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            } catch (Exception e) {
            }
        } else {
            Toast.makeText(MainActivity.this, "Try Again!", Toast.LENGTH_LONG).show();
        }
    }


    public void showad(String item) {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            desc="Spin wheel";
            Model model = new Model(desc, date, item, claim);
            mDatabase.child("users").child(mUserId).child("coin").push().setValue(model);


            setPoint();
            Toast.makeText(MainActivity.this, "Coins Added Successfully!", Toast.LENGTH_LONG).show();

        }
    }

    public static void loadRewardedVideoAd() {
        Hot_offer.mRewardedVideoAd.loadAd(VIDEO_AD_ID, new AdRequest.Builder().build());
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupnavigationDrawer() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.lucky: {
                        drawer.closeDrawers();
                        ;
                        Intent tran = new Intent(MainActivity.this, Lucky_no.class);
                        startActivity(tran);
                        break;
                    }
                    case R.id.wallet: {
                        drawer.closeDrawers();
                        ;
                        Intent tran = new Intent(MainActivity.this, Wallet.class);
                        startActivity(tran);
                        break;
                    }
                    case R.id.offer: {
                        drawer.closeDrawers();
                        ;
                        Intent tran = new Intent(MainActivity.this, Hot_offer.class);
                        startActivity(tran);

                        break;
                    }

                    case R.id.daily: {

                        drawer.closeDrawers();

                        final String d = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        DatabaseReference reference = mDatabase.child("users").child(mUserId);

                        Query query = reference.child("coin").orderByChild("date").equalTo(d);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot appleSnapshot) {

                                if (appleSnapshot.getValue() != null) {

                                    Query query2 = appleSnapshot.getRef().orderByChild("desc").equalTo("Daily checked In");

                                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {

                                                desc = "Spin wheel";
                                                Toast.makeText(MainActivity.this, "you have already checked in today!!", Toast.LENGTH_SHORT).show();
                                            } else {

                                                desc = "Daily checked In";
                                                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                Model model = new Model(desc, date, "" + DAILY_CHECK, claim);
                                                mDatabase.child("users").child(mUserId).child("coin").push().setValue(model);
                                                setPoint();
                                                Toast.makeText(MainActivity.this, "Congratulation you will get daily checked point!", Toast.LENGTH_LONG).show();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    desc = "Daily checked In";
                                    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                    Model model = new Model(desc, date, "" + DAILY_CHECK, claim);
                                    mDatabase.child("users").child(mUserId).child("coin").push().setValue(model);
                                    setPoint();
                                    Toast.makeText(MainActivity.this, "Congratulation you will get daily checked point!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;
                    }
                    case R.id.share: {
                        drawer.closeDrawers();
                        ;
                        share();
                        break;
                    }
                    case R.id.refer: {
                        drawer.closeDrawers();
                        ;
                        Intent tran = new Intent(MainActivity.this, Refer_friend.class);
                        startActivity(tran);
                        break;
                    }
                    case R.id.rate: {
                        drawer.closeDrawers();
                        ;
                        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("register");
                        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("rate")) {

                                    if ((dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rate").getValue()).equals(false)) {
                                        // Do first run stuff here then set 'firstrun' as false
                                        // using the following line to edit/commit prefs
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                        desc = "App rate";
                                        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                        Model model = new Model(desc, date, "" + RATE_POINT, claim);
                                        mDatabase.child("users").child(mUserId).child("coin").push().setValue(model);
                                        Toast.makeText(MainActivity.this, "Congratulation you will get rate point!", Toast.LENGTH_LONG).show();


                                        FirebaseDatabase.getInstance().getReference().child("register").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rate").setValue(true);

                                    } else {
                                        desc = "Spin wheel";
                                        Toast.makeText(MainActivity.this, "You have already rated..!!", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    if (prefs.getBoolean("firstrun", true)) {
                                        // Do first run stuff here then set 'firstrun' as false
                                        // using the following line to edit/commit prefs

                                        desc = "App rate";
                                        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                        Model model = new Model(desc, date, "" + RATE_POINT, claim);
                                        mDatabase.child("users").child(mUserId).child("coin").push().setValue(model);
                                        Toast.makeText(MainActivity.this, "Congratulation you will get rate point!", Toast.LENGTH_LONG).show();

                                        prefs.edit().putBoolean("firstrun", false).commit();
                                    }
                                    dataSnapshot.getRef().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rate").setValue(false);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        break;
                    }
                    case R.id.more: {
                        drawer.closeDrawers();
                        ;
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        break;

                    }
                    case R.id.contact: {
                        drawer.closeDrawers();
                        ;
                        Intent homeIntent1 = new Intent(MainActivity.this, Contact.class);
                        startActivity(homeIntent1);
                        break;

                    }

                    case R.id.privacy: {
                        drawer.closeDrawers();
                        ;
                        Intent homeIntent1 = new Intent(MainActivity.this, Privacy.class);
                        startActivity(homeIntent1);
                        break;

                    }

                }

                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public String getModifiedeDate(Long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);

        String date = DateFormat.format("dd-MM-yyyy", cal).toString();

        /*Calendar now = Calendar.getInstance();

        date = "";
        if (now.get(Calendar.DATE) == cal.get(Calendar.DATE) ) {
            // return "Today " + DateFormat.format(timeFormatString, smsTime);

        }
*/
        return date;
    }


    public void share() {
        try {
            String shareBody = "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I have earned cash using this app.you can also earn download app - \n" + shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share Using"));
        } catch (Exception e) {
            Log.e("Sahare error", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPoint();
        loadRewardedVideoAd();
    }
}
