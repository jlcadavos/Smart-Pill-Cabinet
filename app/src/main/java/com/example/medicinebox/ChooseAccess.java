package com.example.medicinebox;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medicinebox.utils.UserRef;

public class ChooseAccess extends AppCompatActivity {
    CardView userBtn, adminBtn;
    UserRef userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_access);
        initWidgets();
        setUpButtons();
        userRef = new UserRef(this);
    }

    private void setUpButtons() {
        userBtn.setOnClickListener(v->{
            userRef.setUserAccess("user");
            startActivity(new Intent(getApplicationContext(), Login.class));
        });

        adminBtn.setOnClickListener(v->{
            userRef.setUserAccess("admin");
            startActivity(new Intent(getApplicationContext(), Login.class));
        });
    }

    private void initWidgets() {
        userBtn = findViewById(R.id.user_Cardview);
        adminBtn = findViewById(R.id.admin_Cardview);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }
}