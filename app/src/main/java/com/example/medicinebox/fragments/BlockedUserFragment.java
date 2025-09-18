package com.example.medicinebox.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.medicinebox.R;
import com.example.medicinebox.adapter.AllUserAdapter;
import com.example.medicinebox.adapter.BlockedUsersAdapter;
import com.example.medicinebox.model.AllUsersModel;
import com.example.medicinebox.model.BlockedUsersModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class BlockedUserFragment extends Fragment {

    BlockedUsersAdapter adapter;
    ArrayList<BlockedUsersModel> list;
    RecyclerView recyclerView;
    RelativeLayout noDataLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blocked_user, container, false);
        initWidgets(view);
        setUpRecyclerView();
        noDataLayout.setVisibility(View.GONE);
        return view;
    }

    public void setUpRecyclerView() {
        list = new ArrayList<>();
        adapter = new BlockedUsersAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("users");

        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    list.clear();
                    boolean noDataForBlockedUsers = true;
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                        String userId = Objects.requireNonNull(dataSnapshot.child("userId").getValue()).toString();
                        boolean isBlock = (boolean) dataSnapshot.child("isBlock").getValue();

                        if(isBlock){
                            noDataForBlockedUsers = false;
                            list.add(new BlockedUsersModel(email, name, userId));
                        }
                    }

                    if(adapter != null){
                        adapter.notifyDataSetChanged();
                    }

                    if(noDataForBlockedUsers){
                        noDataLayout.setVisibility(View.VISIBLE);
                    } else {
                        noDataLayout.setVisibility(View.GONE);
                    }
                } else{
                    noDataLayout.setVisibility(View.VISIBLE);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch users");
            }
        });


    }
    private void initWidgets(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        noDataLayout = view.findViewById(R.id.noData_Layout);
    }
}