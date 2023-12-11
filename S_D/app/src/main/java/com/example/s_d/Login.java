package com.example.s_d;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {
    TextInputEditText Login_Id, Login_Pw;
    TextView Login_Button, Login_Id_Pw_Find, Login_Signup;
    CheckBox Auto_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login_Id = findViewById(R.id.Login_id);
        Login_Pw = findViewById(R.id.Login_pw);
        Login_Button = findViewById(R.id.Login_button);
        Login_Id_Pw_Find = findViewById(R.id.Login_id_pw_find);
        Login_Signup = findViewById(R.id.Login_signup);
        Auto_Login = findViewById(R.id.auto_login);

        IdSave idSave = (IdSave) getApplication();
        String userId = idSave.getUserId();
        int isAutoLogin = idSave.getIsAutoLogin();

        // 자동 로그인 체크 여부 및 저장된 사용자 ID 확인
        if (isAutoLogin == 1 && !userId.isEmpty()) {
            // 자동 로그인 체크되어 있고, 저장된 사용자 ID가 있는 경우
            // MainActivity로 이동
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        }

        // 로그인 버튼
        Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = Login_Id.getText().toString();
                String inputPw = Login_Pw.getText().toString();
                boolean isAutoLogin = Auto_Login.isChecked();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(Login.this);

                // url 작성
                String url = "http://203.250.133.141:8080/user/login/" + inputId + "/" +  inputPw;
                ApiService loginApiService = new ApiService();
                loginApiService.getUrl(url);

                String id_error = loginApiService.getKey("login_id_pw_error");
                String pw_error = loginApiService.getKey("login_pw_error");

                if (isAutoLogin) {
                    // Auto_Login이 체크되었을 때 로그인 정보를 저장
                    idSave.setIsAutoLogin(1);
                } else {
                    idSave.setIsAutoLogin(0);
                }

                if(loginApiService.getStatus() == 200){
                    idSave.setUserId(inputId);
                    Toast.makeText(Login.this, "로그인 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                }else if(id_error == "login_id_pw_error") {
                    // url 만들어지고 ID 잘못될때 출력
                    // 메시지 설정
                    String message = "잘못된 아이디입니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                }else if(pw_error == "login_pw_error") {
                    // url 만들어지고 PW 잘못될때 출력
                    // 메시지 설정
                    String message = "잘못된 비밀번호입니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                }else if(inputId.trim().equals("")) {
                    // Id 빈값일 경우
                    // 메시지 설정
                    String message = "아이디를 입력해주세요.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                }else if(inputPw.trim().equals("")) {
                    // Pw 빈값인 경우
                    // 메시지 설정
                    String message = "비밀번호를 입력해주세요.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                }else {
                    // 메시지 설정
                    String message = "서버에 문제가 생겼거나 네트워크를 확인해주세요.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                }
            }
        });

        Login_Id_Pw_Find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ID/PW 찾기 화면으로 이동
                Intent intent = new Intent(Login.this, IdPwFind.class);
                startActivity(intent);
            }
        });

        Login_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });
    }
}