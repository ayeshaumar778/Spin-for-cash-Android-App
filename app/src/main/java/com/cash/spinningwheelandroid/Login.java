package com.cash.spinningwheelandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cash.spinningwheelandroid.Constant.REFER_POINT;
import static com.cash.spinningwheelandroid.Constant.claim;


/**
 * Created by Hetal on 12-Sep-17.
 */

public class Login extends AppCompatActivity {

    public GoogleApiClient googleApiClient;
    String f_name, f_id;
    Toolbar mtoolbar;
    public static final int RequestSignInCode = 7;
    private ProgressDialog pDialog;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    LoginButton loginButton;
    Button before_action;
    Button btnlogout;
    TextView email, name, loginby;
    FirebaseAuth mFirebaseAuth;
    SignInButton signInGoogle;
    EditText refer;
    Boolean refercode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        signInGoogle = (SignInButton) findViewById(R.id.btnGoogle);
        refer = (EditText) findViewById(R.id.refer);

        mCallbackManager = CallbackManager.Factory.create();
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnlogout = (Button) findViewById(R.id.btnlogout);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        before_action = (Button) findViewById(R.id.before_action);
        loginby = (TextView) findViewById(R.id.name);
        name = (TextView) findViewById(R.id.p_name);
        email = (TextView) findViewById(R.id.p_id);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        btnlogout.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(Login.this,R.drawable.ic_input_black_24dp), null, null, null);
        name.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(Login.this,R.drawable.ic_account_box_black_24dp), null, null, null);

        // Creating and Configuring Google Sign In object.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if (mFirebaseUser != null) {
            loginButton.setVisibility(View.GONE);
            signInGoogle.setVisibility(View.GONE);
            btnlogout.setVisibility(View.VISIBLE);
            loginby.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            refer.setVisibility(View.GONE);
            String provider = "" + mFirebaseUser.getProviders();
            if (provider.equalsIgnoreCase("[facebook.com]"))
                loginby.setText("Facebook");
            else
                loginby.setText("Google");

            email.setText("" + mFirebaseAuth.getCurrentUser().getEmail());
            name.setText("" + mFirebaseAuth.getCurrentUser().getDisplayName());


        } else {
            refer.setVisibility(View.VISIBLE);
            btnlogout.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            loginby.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }


        mAuth = FirebaseAuth.getInstance();
        googleApiClient = new GoogleApiClient.Builder(Login.this)
                .enableAutoManage(Login.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        before_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = refer.getText().toString().trim();

                if (text.compareTo("") != 0) {

                    refercode = true;
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("register");

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Boolean exist = false;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                if (postSnapshot.child("code").getValue().toString().equals(refer.getText().toString())) {
                                    exist = true;
                                    loginButton.performClick();

                                }
                            }
                            if (exist == false) {
                                refer.setText("");
                                Toast.makeText(Login.this, "Please Enter Correct code..", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    refercode = false;
                    loginButton.performClick();

                }


            }
        });
        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String text = refer.getText().toString().trim();

                if (text.compareTo("") != 0) {

                    refercode = true;
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("register");

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                    Boolean exist = false;



                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                         if (postSnapshot.child("code").getValue().toString().equals(refer.getText().toString())) {
                                                exist = true;
                                                UserSignInMethod();

                                              }
                                              }
                                    if (exist == false) {
                                        refer.setText("");
                                        Toast.makeText(Login.this, "Please Enter Correct code..", Toast.LENGTH_SHORT).show();
                                    }

                                }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    refercode = false;
                    UserSignInMethod();

                }
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {


                }
            }
        };
        accessTokenTracker.startTracking();


        //handle facebook
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                pDialog = new ProgressDialog(Login.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
                handleFacebookAccessToken(loginResult.getAccessToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                try {
                                    //login success  get name and facebook id
                                    f_id = object.getString("id");
                                    f_name = object.getString("name");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(Login.this)
                        .setTitle("Logout")
                        .setMessage("Do you really want to Logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                Toast.makeText(Login.this, "You have successfully logged out.", Toast.LENGTH_SHORT).show();

                                finish();
                                Intent homeIntent = new Intent(Login.this, Login.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homeIntent);


                                startActivity(getIntent());


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();
            }
        });


    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                    Toast.makeText(Login.this, "login successfully…", Toast.LENGTH_LONG).show();


                    final String code = randomAlphaNumeric(8);
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("register");

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rate").setValue(false);
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("code").setValue(code);
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        if (refercode == true) {

                                            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                            Model model = new Model("Refferal code", date, "" +REFER_POINT,claim);
                                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("coin").push().setValue(model);

                                            FirebaseDatabase.getInstance().getReference().child("register").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").setValue(true);
                                        }
                                    }
                                });

                            } else {
                                Boolean first = false;
                                if (refercode == true) {

                                    if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("first"))
                                        if ((dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").getValue()).equals(false)) {
                                            first = true;
                                            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                            Model model = new Model("Refferal code", date, "" + REFER_POINT,claim);
                                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("coin").push().setValue(model);

                                            FirebaseDatabase.getInstance().getReference().child("register").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").setValue(true);

                                        }
                                    if (first == false) {
                                        refer.setText("");
                                        Toast.makeText(Login.this, "You have used Refferal code..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void UserSignInMethod() {
        pDialog = new ProgressDialog(Login.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(true);
        pDialog.show();

        // Passing Google Api Client into Intent.
        Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(AuthIntent, RequestSignInCode);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //google login
        if (requestCode == RequestSignInCode) {

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()) {

                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                FirebaseUserAuth(googleSignInAccount);
            }

        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);


        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(Login.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {

                        if (AuthResultTask.isSuccessful()) {
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                            Toast.makeText(Login.this, "login successfully…", Toast.LENGTH_LONG).show();


                            final String code = randomAlphaNumeric(8);
                            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("register");

                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {



                                    if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rate").setValue(false);
                                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("code").setValue(code);
                                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                if (refercode == true) {

                                                    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                    Model model = new Model("Refferal code", date, "" + REFER_POINT,claim);
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("coin").push().setValue(model);

                                                    FirebaseDatabase.getInstance().getReference().child("register").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").setValue(true);
                                                }
                                            }
                                        });

                                    } else {
                                        Boolean first = false;
                                        if (refercode == true) {

                                            if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("first"))
                                                if ((dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").getValue()).equals(false)) {
                                                    first = true;
                                                    String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                    Model model = new Model("Refferal code", date, "" + REFER_POINT,claim);
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("coin").push().setValue(model);

                                                    FirebaseDatabase.getInstance().getReference().child("register").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first").setValue(true);

                                                }
                                            if (first == false) {
                                                refer.setText("");
                                                Toast.makeText(Login.this, "You have used Refferal code..", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Login.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}

