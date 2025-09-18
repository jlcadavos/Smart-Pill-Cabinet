package com.example.medicinebox;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medicinebox.utils.UserRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {
    UserRef userRef;
    AppCompatButton loginBtn;
    TextView noAccBtn;
    AppCompatEditText
            email,
            password;
    ProgressBar progressBar;

    private boolean passwordVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initWidgets();
        userRef = new UserRef(this);
        setUpInterface();
        setUpButtons();
        passwordHideMethod();

    }
    @SuppressLint("ClickableViewAccessibility")
    private void passwordHideMethod() {
        password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = password.getSelectionEnd();
                        if (passwordVisible){
                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else {

                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;

                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }
    private void setUpButtons() {
        noAccBtn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), Signup.class));
        });
        loginBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);

            String EMAIL = Objects.requireNonNull(email.getText()).toString();
            String PASSWORD = Objects.requireNonNull(password.getText()).toString();

            if (EMAIL.isEmpty()) {
                email.setError("Enter email");

                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);

            } else if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
                email.setError("Enter valid email");

                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
            } else if (PASSWORD.isEmpty()) {
                password.setError("Enter password");

                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
            } else {
                signInUser(EMAIL, PASSWORD);
            }
        });
    }
    private void signInUser(String email, String password) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String userId = FirebaseAuth.getInstance().getUid();
                            validateUserBasedOnAccess(userId);

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to log in " + task.getException().getMessage(), Toast.LENGTH_LONG).show();;
                            progressBar.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    private void validateUserBasedOnAccess(String userId) {

        FirebaseFirestore.getInstance().collection("users").document(userId)
                        .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            String accessFromDB = documentSnapshot.getString("access").toLowerCase();
                                            String access = userRef.getUserAccess().toLowerCase();
                                            if(!access.isEmpty() && access != null && !accessFromDB.isEmpty() && accessFromDB != null){
                                                if(access.equals(accessFromDB)){

                                                    saveUsersDataToSystem(userId);

                                                    if(access.equals("user")){
                                                        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("users")
                                                                .child(userId);

                                                                userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                       if(snapshot.exists()){
                                                                           boolean isBlock = (boolean) snapshot.child("isBlock").getValue();

                                                                           if(isBlock){
                                                                               if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                                                                   FirebaseAuth.getInstance().signOut();
                                                                               }
                                                                               Toast.makeText(getApplicationContext(), "Failed to log in ", Toast.LENGTH_LONG).show();;
                                                                               progressBar.setVisibility(View.GONE);
                                                                               loginBtn.setVisibility(View.VISIBLE);
                                                                           } else {
                                                                               Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();
                                                                               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                               startActivity(intent);
                                                                               finish();
                                                                           }
                                                                       } else {
                                                                           if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                                                               FirebaseAuth.getInstance().signOut();
                                                                           }
                                                                           Toast.makeText(getApplicationContext(), "Failed to log in ", Toast.LENGTH_LONG).show();;
                                                                           progressBar.setVisibility(View.GONE);
                                                                           loginBtn.setVisibility(View.VISIBLE);
                                                                       }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                    } else{
                                                        Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                } else {
                                                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                                        FirebaseAuth.getInstance().signOut();
                                                    }
                                                    Toast.makeText(getApplicationContext(), "Failed to log in ", Toast.LENGTH_LONG).show();;
                                                    progressBar.setVisibility(View.GONE);
                                                    loginBtn.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        } else {
                                            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                            Toast.makeText(getApplicationContext(), "Failed to log in " + task.getException().getMessage(), Toast.LENGTH_LONG).show();;
                                            progressBar.setVisibility(View.GONE);
                                            loginBtn.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
    }
    private void saveUsersDataToSystem(String userId){
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            userRef.setUserAccess(documentSnapshot.getString("access"));
                        } else {
                            Log.d("TAG", "Failed to fetch user details");
                        }
                    }
                });
    }
    private void setUpInterface() {
        String access = userRef.getUserAccess();
        if(!access.isEmpty() && access != null){
            if(access.equals("admin")){
                Drawable newBackground = ContextCompat.getDrawable(this, R.drawable.custom_admin_primary_btn);
                loginBtn.setBackground(newBackground);
                noAccBtn.setVisibility(GONE);

            } else {
                Drawable newBackground = ContextCompat.getDrawable(this, R.drawable.custom_primary_btn);
                loginBtn.setBackground(newBackground);
                noAccBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initWidgets() {

        noAccBtn = findViewById(R.id.dontHaveAccount_TextView);
        loginBtn = findViewById(R.id.login_Button);

        email = findViewById(R.id.email_Edittext);
        password = findViewById(R.id.password_Edittext);
        progressBar = findViewById(R.id.progressbar);

    }
    @Override
    public void onBackPressed() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Exit the app completely
            finish(); // Closes all activities in the task
        } else {
            // Proceed with normal back navigation
            super.onBackPressed();
        }
    }
}