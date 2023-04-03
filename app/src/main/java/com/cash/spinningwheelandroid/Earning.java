package com.cash.spinningwheelandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.cash.spinningwheelandroid.Constant.claim;
import static com.cash.spinningwheelandroid.Wallet.coins;
import static com.cash.spinningwheelandroid.Wallet.redeem;

public class Earning extends Fragment {

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    public DatabaseReference mDatabase;
    public String mUserId;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter adapter;
    int coin = 0;
    ProgressDialog Asycdialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.recycleview, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycleview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();
        Asycdialog = new ProgressDialog(getContext());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Asycdialog.setMessage("Loading...");
        Asycdialog.show();

        coin = 0;


        Query query = mDatabase.child("users").child(mUserId).child("coin").orderByChild("status").equalTo(claim);
        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(query, Model.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Model, ReminderViewHolder>(options) {
            @NonNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_single, parent, false);

                return new ReminderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, final int position, @NonNull final Model model) {

                holder.setDesc(model.getDesc());
                holder.setDate(model.getDate());
                holder.setCoin("" + model.getCoin());


            }

        };

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        Asycdialog.dismiss();
        adapter.startListening();
        redeem.setOnClickListener(okListener);

    }

    private View.OnClickListener okListener = new View.OnClickListener() {

        public void onClick(View v) {
            Intent i = new Intent(getContext(), Payment.class);
            i.putExtra("coin", Integer.parseInt(coins.getText().toString()));
            startActivity(i);
        }

    };


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView date, coin, desc;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            coin = (TextView) itemView.findViewById(R.id.coin);
            desc = (TextView) itemView.findViewById(R.id.desc);
        }

        public void setDate(String title) {
            date.setText(title);
        }

        public void setCoin(String description) {
            coin.setText(description);
        }

        public void setDesc(String description) {
            desc.setText(description);
        }


    }

}