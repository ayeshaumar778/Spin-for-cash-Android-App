package com.cash.spinningwheelandroid;

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

public class Withdraw extends Fragment {

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    public DatabaseReference mDatabase;
    public String mUserId;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter adapter;


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


        return v;
    }




    @Override
    public void onStart() {
        super.onStart();


        Query query =  mDatabase.child("users").child(mUserId).child("payment");
        FirebaseRecyclerOptions<Payment_model> options =
                new FirebaseRecyclerOptions.Builder<Payment_model>()
                        .setQuery(query, Payment_model.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Payment_model, ReminderViewHolder>(options) {
            @NonNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_single, parent, false);

                return new ReminderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, final int position, @NonNull final Payment_model model) {

                holder.setDesc("Withdrawn Money");
                holder.setDate(model.getDate());
                holder.setCoin(""+model.getRs());
            }

        };

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }


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