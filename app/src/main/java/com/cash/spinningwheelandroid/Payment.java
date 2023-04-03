package com.cash.spinningwheelandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.cash.spinningwheelandroid.Constant.claim;
import static com.cash.spinningwheelandroid.Constant.currency;
import static com.cash.spinningwheelandroid.MainActivity.point;
import static com.cash.spinningwheelandroid.Wallet.setCoins;

public class Payment extends AppCompatActivity {

    RadioGroup payment;
    RadioButton radioButton;
    EditText account;
    Button submit;
    TextView coin, rs;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    public DatabaseReference mDatabase;
    public String mUserId;
    Toolbar toolbar;
    int r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        payment = (RadioGroup) findViewById(R.id.payment);
        account = (EditText) findViewById(R.id.account);
        submit = (Button) findViewById(R.id.submit);
        coin = (TextView) findViewById(R.id.coin);
        rs = (TextView) findViewById(R.id.rs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getBackground().setAlpha(0);

        Bundle b = getIntent().getExtras();
        int i = b.getInt("coin");

        r = i / 100;

        rs.setText(r +" "+ currency);

        ((RadioButton) payment.getChildAt(0)).setChecked(true);
        try {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mUserId = mFirebaseUser.getUid();
        } catch (Exception e) {
            Intent homeIntent1 = new Intent(Payment.this, Login.class);
            startActivity(homeIntent1);
        }


        coin.setText("" + i);

        payment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (r >= 20) {
                    if (account.getText().toString().equals(""))
                        Toast.makeText(Payment.this, "Please enter Account Detail..", Toast.LENGTH_SHORT).show();
                    else {

                        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        String pay = ((RadioButton) findViewById(payment.getCheckedRadioButtonId())).getText().toString();
                        Payment_model model = new Payment_model(rs.getText().toString(), pay, account.getText().toString(), date);

                        String key=mDatabase.push().getKey();
                        mDatabase.child("users").child(mUserId).child("payment").child(key).setValue(model);


                        Toast.makeText(Payment.this, "Payment Request Send Successfully!", Toast.LENGTH_LONG).show();
                        DeletFromFirebase(key);
                        coin.setText("" + 0);

                        rs.setText("0 "+ currency);
                        setCoins(0);
                        point.setText("0");

                    }
                } else {
                    try {
                        new AlertDialog.Builder(Payment.this)
                                .setTitle(" Not enough coins..!! ")
                                .setMessage("you must have at least 2000 coin to get 20 Rs!!")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", null).show();

                    } catch (Exception e) {
                    }
                }
                r=0;

                Wallet.adapter.notifyDataSetChanged();
                account.setText("");
            }
        });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void DeletFromFirebase(final String key) {
        if (mFirebaseUser != null) {


            mUserId = mFirebaseUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            Query applesQuery = mDatabase.child("users").child(mUserId).child("coin").orderByChild("status").equalTo(claim);


            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {

                        appleSnapshot.getRef().child("payment").setValue(key);
                        appleSnapshot.getRef().child("status").setValue(Constant.request);


                    }

                    MainActivity.setPoint();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });














          /*  mUserId = mFirebaseUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            Query applesQuery = mDatabase.child("users").child(mUserId).child("coin");

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {

                        appleSnapshot.getRef().child("status").setValue(Constant.request);

                    }

                    MainActivity.setPoint();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });*/
        }

    }
}
