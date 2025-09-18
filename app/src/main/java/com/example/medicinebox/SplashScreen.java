package com.example.medicinebox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medicinebox.utils.UserRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        UserRef userRef = new UserRef(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    String userId = FirebaseAuth.getInstance().getUid();
                    FirebaseFirestore.getInstance().collection("users")
                            .document(userId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        String access = documentSnapshot.getString("access");
                                        userRef.setUserAccess(access);
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                    } else {
                                        Toast.makeText(getApplicationContext(), "There's an error, Please try again", Toast.LENGTH_LONG).show();
                                        finishAffinity();
                                    }
                                }
                            });
                } else {
                    startActivity(new Intent(getApplicationContext(), ChooseAccess.class));
                }

            }
        }, 3000);

    }
}