package com.example.medicinebox.adapter;

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

import androidx.appcompat.widget.AppCompatButton;

import androidx.recyclerview.widget.RecyclerView;


import com.example.medicinebox.R;
import com.example.medicinebox.model.AllUsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;


public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.MyViewHolder> {
    Context context;
    ArrayList<AllUsersModel> list;


    public AllUserAdapter(Context context, ArrayList<AllUsersModel> list){
        this.context = context;
        this.list = list;


    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_user_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AllUsersModel model = list.get(position);

        holder.email.setText(model.getEmail());
        holder.name.setText(model.getName());
        final String name = model.getName();
        final String email = model.getEmail();
        final String userId = model.getUserId();
        holder.blockBtn.setOnClickListener(v->{showDeleteDialog(name,email,userId);});
    }

    private void showDeleteDialog(String name, String email, String userId) {
        Dialog blockUserDialog = new Dialog(context);
        blockUserDialog.setContentView(R.layout.block_user_dialog);
        blockUserDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blockUserDialog.setCancelable(false);
        blockUserDialog.show();

        AppCompatButton yesBtn, noBtn;
        TextView nameOfUserBlock = blockUserDialog.findViewById(R.id.blockUserName_Textview);
        yesBtn = blockUserDialog.findViewById(R.id.yes_Button);
        noBtn = blockUserDialog.findViewById(R.id.no_Button);

        nameOfUserBlock.setText("Are you sure you want \nto block " + name + " ?");

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("users").child(userId);


        yesBtn.setOnClickListener(v->{
            userDB.child("isBlock").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "User " + name + " successfully blocked", Toast.LENGTH_LONG).show();
                        blockUserDialog.dismiss();
                    } else {
                        Toast.makeText(context, "Failed to block User " + name, Toast.LENGTH_LONG).show();
                        Log.d("TAG", "failed to block user " + name);
                        blockUserDialog.dismiss();
                    }
                }
            });
        });
        noBtn.setOnClickListener(v->{blockUserDialog.dismiss();});

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email,name;
        ImageView blockBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.email_TextView);
            name = itemView.findViewById(R.id.name_TextView);
            blockBtn = itemView.findViewById(R.id.block_ImageView);
        }
    }
}
