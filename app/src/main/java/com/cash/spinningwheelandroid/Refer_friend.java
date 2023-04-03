package com.cash.spinningwheelandroid;

/**
 * Created by Hetal on 20-Apr-18.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Hetal on 20-Apr-18.
 */

public class Refer_friend extends AppCompatActivity {

    Button invite;
    Toolbar toolbar;
    TextView refer_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refer_friend);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        invite=(Button)findViewById(R.id.invite);

        refer_code=(TextView) findViewById(R.id.txtrefer);



        Query query = FirebaseDatabase.getInstance().getReference().child("register").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("code");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              refer_code.setText(dataSnapshot.getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String shareBody = "https://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I have earned cash using "+getResources().getString(R.string.app_name)+" app.you can also earn by downloading app from below link and enter refferal code while login-"+refer_code.getText().toString()+ " \n" + shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Invite Friend Using"));
                } catch (Exception e) {
                    Log.e("Sahare error", e.getMessage());
                }

            }
        });

       /* verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = refer_code.getText().toString().trim();

                if (text.compareTo("") != 0) {

                    FirebaseDatabase.getInstance().getReference().child("register").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean exist = false;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                if (postSnapshot.getValue().toString().equals(refer_code.getText().toString())) {
                                    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                    Model model = new Model("Refferal code", date, "" + 100);
                                    FirebaseDatabase.getInstance().getReference().child("users").child(postSnapshot.getKey()).child("coin").push().setValue(model);
                                    Toast.makeText(Refer_friend.this,"Your Refferal code is send successfully",Toast.LENGTH_SHORT).show();
                                    exist = true;
                                    break;
                                }

                            }
                            if (exist == false) {
                                Toast.makeText(Refer_friend.this, "Please Enter Correct code..", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(Refer_friend.this, "Please Enter Refferal code..", Toast.LENGTH_SHORT).show();


                }
            }
        });
*/
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
