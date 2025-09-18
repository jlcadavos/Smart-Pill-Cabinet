package com.example.medicinebox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medicinebox.utils.DateAndTimeUtils;
import com.example.medicinebox.utils.UserRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;
@RequiresApi(api = Build.VERSION_CODES.O)
public class Signup extends AppCompatActivity {
    TextView yesAccBtn;
    AppCompatEditText
            email,
            name,
            password,
            confirmPassword;

    ProgressBar progressBar;
    AppCompatButton signupBtn;
    private boolean passwordVisible;
    private boolean confirmPasswordVisible;
    UserRef userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initWidgets();
        userRef = new UserRef(this);
        setUpInterface();
        setUpButtons();
        setUpSignUp();
        passwordHideMethod();
    }
    private void setUpSignUp() {


        signupBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            signupBtn.setVisibility(View.GONE);

            String EMAIL = Objects.requireNonNull(email.getText()).toString();
            String NAME = Objects.requireNonNull(name.getText()).toString();
            String PASSWORD = Objects.requireNonNull(password.getText()).toString();
            String CONFIRM_PASSWORD = Objects.requireNonNull(confirmPassword.getText()).toString();

            if (EMAIL.isEmpty()){
                email.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()){
                email.setError("Enter valid email address");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (NAME.isEmpty()){
                name.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (PASSWORD.isEmpty()){
                password.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (CONFIRM_PASSWORD.isEmpty()){
                confirmPassword.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (!PASSWORD.equals(CONFIRM_PASSWORD)){
                password.setError("Password not match");
                confirmPassword.setError("Password not match");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else {
                registerUser(EMAIL, NAME, CONFIRM_PASSWORD);
            }
        });
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

        confirmPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= confirmPassword.getRight()-confirmPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = confirmPassword.getSelectionEnd();
                        if (confirmPasswordVisible){
                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmPasswordVisible = false;
                        }
                        else {

                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmPasswordVisible = true;

                        }
                        confirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }
    private void registerUser(String email, String name, String password) {


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            saveUser(email, name,password);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            signupBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Failed to create account, Please try again later", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "Failed to create accout: " + task.getException().getMessage());
                        }
                    }
                });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveUser(String email, String name, String password) {
        String userId = FirebaseAuth.getInstance().getUid();
        String access = userRef.getUserAccess();

        HashMap<String, Object> users = new HashMap<>();
        users.put("access", access);
        users.put("email", email);
        users.put("name", name);
        users.put("password", password);
        users.put("dateCreated", DateAndTimeUtils.getTime24HrsFormatAndDate());
        users.put("userId", userId);
        users.put("isBlocked", false);


        FirebaseFirestore.getInstance().collection("users").document(userId)
                .set(users)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to create account, Please try again later", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "Failed to save data of user up creating account: " + task.getException().getMessage());
                        }
                    }
                });

        HashMap<String,Object> usersToRTDB = new HashMap<>();

        usersToRTDB.put("userId", userId);
        usersToRTDB.put("isBlock", false);
        usersToRTDB.put("name", name);
        usersToRTDB.put("email", email);

        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("users");

        usersDB.child(userId).setValue(usersToRTDB)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("TAG", "User successfully added  to RTDB");
                        } else {
                            Log.d("TAG", "Failed to add user to RTDB");
                        }
                    }
                });
    }
    private void setUpButtons() {
        yesAccBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
        });


    }





    private void setUpInterface() {
        String access = userRef.getUserAccess();
        if(!access.isEmpty() && access != null){
            if(access.equals("admin")){
                Drawable newBackground = ContextCompat.getDrawable(this, R.drawable.custom_admin_primary_btn);
                signupBtn.setBackground(newBackground);
            } else {
                Drawable newBackground = ContextCompat.getDrawable(this, R.drawable.custom_primary_btn);
                signupBtn.setBackground(newBackground);
            }
        }
    }

    private void initWidgets() {

        yesAccBtn = findViewById(R.id.yesAccount_TextView);

        email = findViewById(R.id.email_Edittext);
        name = findViewById(R.id.name_Edittext);
        password = findViewById(R.id.password_Edittext);
        confirmPassword = findViewById(R.id.confirmPassword_Edittext);
        progressBar = findViewById(R.id.progressbar);
        signupBtn = findViewById(R.id.signup_Button);
    }
}