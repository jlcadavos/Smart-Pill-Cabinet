package com.example.medicinebox;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medicinebox.utils.DateAndTimeUtils;
import com.example.medicinebox.utils.UserRef;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MainActivity extends AppCompatActivity {
    ImageView logoutBtn;
    RelativeLayout servo1CardTitle,servo2CardTitle,servo3CardTitle;
    Toolbar toolbar;
    UserRef userRef;
    AppCompatButton manageUserBtn;
    ImageView servo1SetAlarmBtn, servo2SetAlarmBtn, servo3SetAlarmBtn;
    ImageView servo1RemoveAlarmBtn, servo2RemoveAlarmBtn, servo3RemoveAlarmBtn;

    LinearLayout servo1NullLayout,servo2NullLayout,servo3NullLayout;
    TextView servo1AlarmTime, servo2AlarmTime, servo3AlarmTime;
    private Handler handler;
    private Runnable runnable;
    private AlertDialog servoAlertDIalog;
     Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        userRef = new UserRef(this);
        handler = new Handler();
        initWidgets();
        setUpButtons();
        setUpServos();
        setUpInterface();
        createNotification();
        setUpRingtone();
    }
    private void setUpServos() {
        setUpServo1();
        setUpServo2();
        setUpServosButtons();
    }
    private void setUpServosButtons() {
        servo1SetAlarmBtn.setOnClickListener(v->{
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .setTitleText("Set Alarm for Servo 1")
                    .build();

            timePicker.addOnPositiveButtonClickListener(v1->{
                // Retrieve selected hour and minute
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Format time to 12-hour format with AM/PM
                String formattedTime = formatTime(hour, minute);
                String clock = DateAndTimeUtils.getDateWithWordFormat() + " " + formattedTime;
                String id = generateId();
                Log.d("TAG", "SETTING ALARM");
                setAlarm(clock, id,"servo1", formattedTime);
            });

            timePicker.show(getSupportFragmentManager(), "tag");
        });

        servo1RemoveAlarmBtn.setOnClickListener(v->{
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("servo1");

            db.child("clock").setValue("");
            db.child("time").setValue("");
            db.child("currentAlarmId").setValue("");
        });

        servo2SetAlarmBtn.setOnClickListener(v->{
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .setTitleText("Set Alarm for Servo 2")
                    .build();

            timePicker.addOnPositiveButtonClickListener(v1->{
                // Retrieve selected hour and minute
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Format time to 12-hour format with AM/PM
                String formattedTime = formatTime(hour, minute);
                String clock = DateAndTimeUtils.getDateWithWordFormat() + " " + formattedTime;
                String id = generateId();
                Log.d("TAG", "SETTING ALARM");
                setAlarm(clock, id,"servo2", formattedTime);
            });

            timePicker.show(getSupportFragmentManager(), "tag");
        });

        servo2RemoveAlarmBtn.setOnClickListener(v->{
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("servo2");

            db.child("clock").setValue("");
            db.child("time").setValue("");
            db.child("currentAlarmId").setValue("");
        });
    }

    private void setUpServo1() {
        DatabaseReference servo1DB = FirebaseDatabase.getInstance().getReference("servo1");

        servo1DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String id = snapshot.child("currentAlarmId").getValue().toString();
                    String clock = snapshot.child("clock").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();

                    if(!clock.isEmpty() && clock != null ){
                        servo1RemoveAlarmBtn.setVisibility(View.VISIBLE);
                        servo1AlarmTime.setText("Alarms at " + time);

                        if(!id.isEmpty() && id != null){
                            setCurrentAlarm(id, "servo1");
                        }

                    } else {
                        Log.d("TAG", "CLOCK IS NULL");
                        servo1RemoveAlarmBtn.setVisibility(View.GONE);
                        servo1AlarmTime.setText("No Alarm is set");
                    }

                } else {
                    Log.d("TAG", "Servo 1 doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch Servo 1");
            }
        });
    }

    private void setUpServo2() {
        DatabaseReference servo1DB = FirebaseDatabase.getInstance().getReference("servo2");

        servo1DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String id = snapshot.child("currentAlarmId").getValue().toString();
                    String clock = snapshot.child("clock").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();

                    if(!clock.isEmpty() && clock != null ){
                        servo2RemoveAlarmBtn.setVisibility(View.VISIBLE);
                        servo2AlarmTime.setText("Alarms at " + time);

                        if(!id.isEmpty() && id != null){
                            setCurrentAlarm(id,"servo2");
                        }

                    } else {
                        Log.d("TAG", "CLOCK IS NULL");
                        servo2RemoveAlarmBtn.setVisibility(View.GONE);
                        servo2AlarmTime.setText("No Alarm is set");
                    }


                } else {
                    Log.d("TAG", "Servo 1 doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch Servo 1");
            }
        });
    }
    private void setCurrentAlarm(String id,String servoName) {
        DatabaseReference currentAlarm = FirebaseDatabase.getInstance().getReference(servoName).child("alarms").child(id);

        currentAlarm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String clock = snapshot.child("clock").getValue().toString();
                    boolean isAlarmStop = (boolean) snapshot.child("alarmStop").getValue();


                    if(!isAlarmStop && !clock.isEmpty() && clock != null){
                        trackAlarmTime(clock,id, servoName);
                    }
                } else {
                    Log.d("TAG", "Failed to fetch current alarm");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch current alarm" + error.getMessage());
            }
        });
    }

    private void trackAlarmTime(String clock,String id,String servoName) {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // Check if the alarm time is now or past
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.getDefault());
                    Date alarmDate = sdf.parse(clock);
                    if (alarmDate != null) {
                        long alarmTimeInMillis = alarmDate.getTime();
                        long currentTime = System.currentTimeMillis();

                        if (alarmTimeInMillis <= currentTime) {
                            Log.d("TAG", "TIME IS PAST OR NOW");
                            // Trigger alarm if condition is true
                            showDialogForServo1(id,servoName);
                            playRingtone();
                            getNotify(servoName);
                            setServoSpin(servoName,id);
                            handler.removeCallbacks(runnable); // Stop further checks after triggering
                        } else {
                            Log.d("TAG", "Alarm time is in the future. Waiting...");
                            handler.postDelayed(this, 30000); // Check again after 1 minute
                        }
                    }
                } catch (ParseException e) {
                    Log.e("TAG", "Date parsing error: " + e.getMessage());
                }
            }
        };

        handler.post(runnable); // Start the loop
    }

    private void setServoSpin(String servoName,String id) {

        DatabaseReference currentAlarmDB = FirebaseDatabase.getInstance().getReference(servoName).child("alarms").child(id);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(servoName);
        currentAlarmDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    long servoSpin = (long) snapshot.child("servoSpinned").getValue();

                    if(servoSpin == 0){
                        Log.d("TAG", "CHANGING STATUS TO 1");
                        db.child("status").setValue(1);
                    }
                } else {
                    Log.d("TAG", "Servo " + servoName + "does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch " +servoName);
            }
        });
    }

    private void showDialogForServo1(String id,String servoName){
        String name = "";
        if(servoName.equals("servo1")){
            name = "Servo 1";
        } else {
            name = "Servo 2";
        }
        // Show Alarm Dialog
        if (servoAlertDIalog == null || !servoAlertDIalog.isShowing()) {
            servoAlertDIalog = new AlertDialog.Builder(this)
                    .setTitle(name + " Alarm")
                    .setMessage("The alarm is ringing. Stop it?")
                    .setCancelable(false) // Prevent dismissal by tapping outside
                    .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Stop the alarm and dismiss dialog
                            stopAlarm(id,servoName);


                            Toast.makeText(getApplicationContext(), "Alarm Stopped!", Toast.LENGTH_SHORT).show();
                            servoAlertDIalog = null; // Reset dialog reference after dismissal
                        }
                    })
                    .show();
        }
    }
    private void setAlarm(String clock, String id, String servo,String time) {
        DatabaseReference servoDB = FirebaseDatabase.getInstance().getReference(servo);

        servoDB.child("clock").setValue(clock);
        servoDB.child("currentAlarmId").setValue(id);
        servoDB.child("time").setValue(time);

        HashMap<String,Object> clockData = new HashMap<>();
        clockData.put("id", id);
        clockData.put("alarmStop", false);
        clockData.put("clock", clock);
        clockData.put("time", time);
        clockData.put("servoSpinned", 0);

        servoDB.child("status").setValue(0);
        servoDB.child("alarms").child(id).setValue(clockData);
    }

    private void setUpRingtone(){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, soundUri);
    }
    private void playRingtone() {

        if(ringtone != null && !ringtone.isPlaying()){
            ringtone.play();
            ringtone.setLooping(true);
            Log.d("TAG", "RINGTONE PLAYED");
        }

    }
    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("mb",
                    "Medicine Box", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 1000, 200, 340});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    private void getNotify(String servo) {
        int id;
        String name = "";
        if(servo.equals("servo1")){
          id = 0;
          name = "Servo 1";
        } else if (servo.equals("servo2")){
            id = 1;
            name = "Servo 2";
        }else {
            id = 2;
            name = "Servo 3";
        }

        String alertMessage =name +  " is Alarming";
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "mb")
                .setContentTitle("Medicine Box")
                .setSmallIcon(R.drawable.ic_logo)
                .setAutoCancel(true)
                .setContentText(alertMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(id, builder.build());
    }


    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop(); // Stop ringtone
         
        }
    }



    private void stopAlarm(String id,String servoName) {

       if(servoAlertDIalog != null){

           DatabaseReference currentAlarm = FirebaseDatabase.getInstance().getReference(servoName).child("alarms").child(id);
           currentAlarm.child("alarmStop").setValue(true);


           servoAlertDIalog.hide();

           String time = DateAndTimeUtils.getTimeWithAMAndPM();
           String clock = DateAndTimeUtils.getNextDayWithWordFormat() + " " + time;
           String generateId = generateId();

           setAlarm(clock, generateId,servoName,time);
           stopRingtone();
       }
    }



    private String generateId (){
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String randStr = "";
        Random random = new Random();
        int idLength = 10;
        for(int i = 0; i <idLength; i++){
            randStr +=charSet.charAt(random.nextInt(charSet.length()));
        }
        return randStr;
    }

    private String formatTime(int hour, int minute) {
        // Determine AM or PM
        String period = (hour >= 12) ? "PM" : "AM";

        // Convert to 12-hour format
        hour = (hour > 12) ? (hour - 12) : hour;
        hour = (hour == 0) ? 12 : hour; // Handle midnight (0:00)

        // Format time with leading zeros if needed
        return String.format("%02d:%02d %s", hour, minute, period);
    }

    @SuppressLint("ResourceAsColor")
    private void setUpInterface() {
        String access = userRef.getUserAccess();
        if(!access.isEmpty() && access != null){
            if(access.equals("admin")){
                int color = ContextCompat.getColor(this, R.color.adminPrimary);
                toolbar.setBackgroundColor(color);
                servo1CardTitle.setBackgroundColor(color);
                servo2CardTitle.setBackgroundColor(color);
                servo3CardTitle.setBackgroundColor(color);
                manageUserBtn.setVisibility(View.VISIBLE);
            } else {
                int color = ContextCompat.getColor(this, R.color.userPrimary);
                toolbar.setBackgroundColor(color);
                servo1CardTitle.setBackgroundColor(color);
                servo2CardTitle.setBackgroundColor(color);
                servo3CardTitle.setBackgroundColor(color);
                manageUserBtn.setVisibility(View.GONE);
            }
        }
    }

    private void setUpButtons() {
        logoutBtn.setOnClickListener(v->{logout();});
        manageUserBtn.setOnClickListener(v->{startActivity(new Intent(this, Users.class));});
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), ChooseAccess.class));
    }

    private void initWidgets() {
        logoutBtn = findViewById(R.id.logout_Imageview);
        toolbar = findViewById(R.id.toolbar);
        servo1CardTitle = findViewById(R.id.servo1_cardTitle);
        servo2CardTitle = findViewById(R.id.servo2_cardTitle);
        servo3CardTitle = findViewById(R.id.servo3_cardTitle);
        manageUserBtn = findViewById(R.id.managerUser_Button);

        servo1SetAlarmBtn = findViewById(R.id.servo1_edit_Imageview);
        servo2SetAlarmBtn = findViewById(R.id.servo2_edit_Imageview);
        servo3SetAlarmBtn = findViewById(R.id.servo3_edit_Imageview);

        servo1RemoveAlarmBtn = findViewById(R.id.servo1_remove_Imageview);
        servo2RemoveAlarmBtn = findViewById(R.id.servo2_remove_Imageview);
        servo3RemoveAlarmBtn = findViewById(R.id.servo3_remove_Imageview);

        servo1NullLayout = findViewById(R.id.servo1_AlarmNull_Layout);
        servo2NullLayout = findViewById(R.id.servo2_AlarmNull_Layout);
        servo3NullLayout = findViewById(R.id.servo3_AlarmNull_Layout);

        servo1AlarmTime = findViewById(R.id.servo1_TextView);
        servo2AlarmTime = findViewById(R.id.servo2_TextView);
        servo3AlarmTime = findViewById(R.id.servo3_TextView);
    }
    @Override
    public void onBackPressed() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Exit the app completely
            finishAffinity(); // Closes all activities in the task
        } else {
            // Proceed with normal back navigation
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone();
    }


}