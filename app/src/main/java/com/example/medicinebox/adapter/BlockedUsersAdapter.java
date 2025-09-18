package com.example.medicinebox.adapter;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicinebox.R;
import com.example.medicinebox.model.BlockedUsersModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.MyViewHolder> {
    Context context;
    ArrayList<BlockedUsersModel> list;


    public BlockedUsersAdapter(Context context, ArrayList<BlockedUsersModel> list){
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blocker_user_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BlockedUsersModel model = list.get(position);
        holder.email.setText(model.getEmail());
        holder.name.setText(model.getName());
        final String name = model.getName();
        final String email = model.getEmail();
        final String userId = model.getUserId();
        
        holder.addBtn.setOnClickListener(v->{showAddDialog(name,email,userId);});
        
    }

    @SuppressLint("SetTextI18n")
    private void showAddDialog(String name, String email, String userId) {
        Dialog addUserDialog = new Dialog(context);
        addUserDialog.setContentView(R.layout.add_dialog_layout);
        addUserDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addUserDialog.setCancelable(false);
        addUserDialog.show();

        AppCompatButton yesBtn, noBtn;
        TextView nameOfUserBlock = addUserDialog.findViewById(R.id.blockUserName_Textview);
        yesBtn = addUserDialog.findViewById(R.id.yes_Button);
        noBtn = addUserDialog.findViewById(R.id.no_Button);

        nameOfUserBlock.setText("Are you sure you want \nto unblock " + name + " ?");

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("users").child(userId);


        yesBtn.setOnClickListener(v->{
            userDB.child("isBlock").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "User " + name + " successfully blocked", Toast.LENGTH_LONG).show();
                        addUserDialog.dismiss();
                    } else {
                        Toast.makeText(context, "Failed to block User " + name, Toast.LENGTH_LONG).show();
                        Log.d("TAG", "failed to block user " + name);
                        addUserDialog.dismiss();
                    }
                }
            });
        });
        noBtn.setOnClickListener(v->{addUserDialog.dismiss();});

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email,name;
        ImageView addBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.email_TextView);
            name = itemView.findViewById(R.id.name_TextView);
            addBtn = itemView.findViewById(R.id.add_ImageView);
        }
    }
}
