package com.example.s_d;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {
    TextView User_Name_View, User_Id_View, User_Pw_View, User_Email_View, User_Hard_Check_View;
    TextView Delete_Hard, Delete_User, LogOut, Pw_Change;
    String User_Name, Id, Pw, Email, Check, user_id;
    ImageButton Setting_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        User_Name_View = findViewById(R.id.user_name_view);
        User_Id_View = findViewById(R.id.user_id_view);
        User_Pw_View = findViewById(R.id.user_pw_view);
        User_Email_View = findViewById(R.id.user_email_view);
        User_Hard_Check_View = findViewById(R.id.user_hard_check_view);
        Pw_Change = findViewById(R.id.pw_change);
        Delete_Hard = findViewById(R.id.delete_hard);
        Delete_User = findViewById(R.id.delete_user);
        LogOut = findViewById(R.id.logout);
        Setting_Back = findViewById(R.id.Setting_back);

        //뒤로 가기 버튼
        Setting_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this, MainActivity.class);
                startActivity(intent);
            }
        });

        IdSave idSave = (IdSave) getApplication();
        int isAutoLogin = idSave.getIsAutoLogin();

        if(isAutoLogin == 1)
        {
            // 사용자의 ID를 가져옴
            user_id = idSave.getUserId();

            Log.d("Login", "Auto Login - user_id from SharedPreferences: " + user_id); // 로그 추가
        }else{
            user_id = idSave.getUserId();
            Log.d("Login", "user_id from IdSave: " + user_id); // 로그 추가
        }

        // url 작성
        ApiService UserInfoApiService = new ApiService();
        String Info_url = "http://203.250.133.141:8080/user/user_info/" + user_id;
        UserInfoApiService.getUrl(Info_url);

        User_Name = UserInfoApiService.getValue("user_name");
        Id = UserInfoApiService.getValue("user_id");
        Pw = UserInfoApiService.getValue("user_pw");
        Email = UserInfoApiService.getValue("user_email");
        Check = UserInfoApiService.getValue("HDCount");
        // 비밀번호 길이만큼 * 문자 저장
        String maskedPw;

        maskedPw = new String(new char[Pw.length()]).replace("\0", "*");
        Log.d("masked",maskedPw);

        User_Name_View.setText(User_Name);
        User_Id_View.setText(Id);
        User_Pw_View.setText(maskedPw);
        User_Email_View.setText(Email);
        User_Hard_Check_View.setText(Check);

        Pw_Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, LoginPwChange.class);
                startActivity(intent);
            }
        });

        Delete_Hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, DeleteDevice.class);
                startActivity(intent);
            }
        });

        Delete_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService UserDeleteApiService = new ApiService();
                String User_Delete_url = "http://203.250.133.141:8080/user/user_delete/" + Id + "/" + Pw;
                UserDeleteApiService.deleteUrl(User_Delete_url);
                Log.d("url",User_Delete_url);

                if(UserDeleteApiService.getStatus() == 200) {
                    Toast.makeText(Setting.this, "계정이 삭제 됐습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Setting.this, Login.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(Setting.this, "계정 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idSave.clearData();
                // 로그인 화면으로 이동
                Intent intent = new Intent(Setting.this, Login.class);
                startActivity(intent);
            }
        });
    }
}