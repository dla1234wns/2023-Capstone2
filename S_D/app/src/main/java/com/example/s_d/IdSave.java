package com.example.s_d;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class IdSave extends Application {
    private String userId;
    private int isAutoLogin;

    @Override
    public void onCreate() {
        super.onCreate();
        loadSavedData(); // 저장된 데이터를 불러옴
    }

    public String getUserId() {
        Log.d("IdSave", "getUserId(): " + userId); // 로그 추가
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        saveData(); // 데이터를 저장
    }

    public int getIsAutoLogin() {
        return isAutoLogin;
    }

    public void setIsAutoLogin(int isAutoLogin) {
        this.isAutoLogin = isAutoLogin;
        saveData(); // 데이터를 저장
    }

    public void clearData() {
        userId = "";
        isAutoLogin = 0;
        saveData(); // 데이터를 저장
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", userId);
        editor.putInt("is_auto_login", isAutoLogin);
        editor.apply();
    }

    private void loadSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "");
        isAutoLogin = sharedPreferences.getInt("is_auto_login", 0);
    }
}