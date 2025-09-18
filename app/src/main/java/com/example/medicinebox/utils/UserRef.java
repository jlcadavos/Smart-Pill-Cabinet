package com.example.medicinebox.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserRef {
    Context context;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public static final String PREF_NAME = "USER_ACCESS";

    public UserRef(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }
    public void  setUserAccess(String userAccess){
        editor.putString("userAccess", userAccess);
        editor.apply();
    }

    public String getUserAccess(){
        return sharedPreferences.getString("userAccess", "user");
    }
}
