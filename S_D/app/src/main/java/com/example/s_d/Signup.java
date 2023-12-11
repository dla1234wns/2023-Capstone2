package com.example.s_d;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class Signup extends AppCompatActivity {
    ImageButton Signup_Back;
    AppCompatButton Signup_Button, Signup_Id_Check_Button, Signup_Mail_Cn_Button, Signup_Mail_Cn_Check_Button;
    EditText Signup_Id, Signup_Pw, Signup_Pw2, Signup_Name, Signup_Mail, Signup_Mail_Cn;
    ImageView Signup_Pw_Check_Icon, Signup_Pw2_Check_Icon, Signup_Pw_No_Icon, Signup_Pw2_No_Icon;
    String EMAIL, pwcheck, pwcheck2;

    private boolean Signup_isIdChecked = false; // 아이디 중복 확인 버튼 눌림 여부를 나타내는 변수
    private boolean Signup_isMailCnConfirmed = false; // 인증번호 받기 버튼 눌림 여부를 나타내는 변수
    private boolean Signup_isCnChecked = false; // 인증번호 확인 버튼 눌림 여부를 나타내는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //뒤로 가기 버튼
        Signup_Back = findViewById(R.id.signup_back);
        Signup_Back.setOnClickListener(v -> onBackPressed() );

        Signup_Id = findViewById(R.id.signup_id);
        Signup_Pw = findViewById(R.id.signup_pw);
        Signup_Pw2 = findViewById(R.id.signup_pw2);
        Signup_Name = findViewById(R.id.signup_name);
        Signup_Mail = findViewById(R.id.signup_mail);
        Signup_Mail_Cn = findViewById(R.id.signup_mail_cn);

        Signup_Id_Check_Button = findViewById(R.id.signup_id_check_button);
        Signup_Mail_Cn_Button = findViewById(R.id.signup_mail_cn_button);
        Signup_Mail_Cn_Check_Button = findViewById(R.id.signup_mail_cn_check_button);
        Signup_Button = findViewById(R.id.signup_button);

        Signup_Pw_Check_Icon = findViewById(R.id.signup_pw_check_icon);
        Signup_Pw_No_Icon = findViewById(R.id.signup_pw_no_icon);
        Signup_Pw2_Check_Icon = findViewById(R.id.signup_pw2_check_icon);
        Signup_Pw2_No_Icon = findViewById(R.id.signup_pw2_no_icon);

        TextWatcher pwTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 이 메소드는 텍스트가 변경되기 전에 호출됩니다.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 이 메소드는 텍스트가 변경될 때마다 호출됩니다.
                pwcheck = Signup_Pw.getText().toString();
                pwcheck2 = Signup_Pw2.getText().toString();

                if (pwcheck.equals(pwcheck2)) {
                    Signup_Pw_Check_Icon.setVisibility(View.VISIBLE);
                    Signup_Pw_No_Icon.setVisibility(View.INVISIBLE);
                    Signup_Pw2_Check_Icon.setVisibility(View.VISIBLE);
                    Signup_Pw2_No_Icon.setVisibility(View.INVISIBLE);
                } else {
                    Signup_Pw_Check_Icon.setVisibility(View.INVISIBLE);
                    Signup_Pw_No_Icon.setVisibility(View.VISIBLE);
                    Signup_Pw2_Check_Icon.setVisibility(View.INVISIBLE);
                    Signup_Pw2_No_Icon.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // 이 메소드는 텍스트가 변경된 후에 호출됩니다.
            }
        };

        // TextWatcher 등록
        Signup_Pw.addTextChangedListener(pwTextWatcher);
        Signup_Pw2.addTextChangedListener(pwTextWatcher);

        Signup_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String ID = Signup_Id.getText().toString();
                String PW = Signup_Pw.getText().toString();
                String NAME = Signup_Name.getText().toString();
                EMAIL = Signup_Mail.getText().toString();

                // ID 중복 확인 버튼을 누르지 않았을 경우
                if (!Signup_isIdChecked) {
                    Toast.makeText(Signup.this, "아이디 중복 확인을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!pwcheck.equals(pwcheck2)){
                    Toast.makeText(Signup.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(NAME.isEmpty()){
                    Toast.makeText(Signup.this, "이름을 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 인증번호 받기 버튼을 누르지 않았을 경우
                if (!Signup_isMailCnConfirmed) {
                    Toast.makeText(Signup.this, "인증번호를 받은 후 확인 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 인증번호 확인 버튼을 누르지 않았을 경우
                if (!Signup_isCnChecked) {
                    Toast.makeText(Signup.this, "인증번호를 확인 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }



                // url 작성
                String url = "http://203.250.133.141:8080/user/regist/" + ID + "/" + PW + "/" + NAME + "/" + EMAIL;
                ApiService SignupApiService = new ApiService();
                SignupApiService.postUrl(url);


                if(SignupApiService.getStatus() == 200 && pwcheck.equals(pwcheck2)){
                    Toast.makeText(Signup.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Signup.this, Login.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(Signup.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ID 중복 확인 버튼 구현
        Signup_Id_Check_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String IDcheck = Signup_Id.getText().toString();
                Log.e("ddd", IDcheck);
                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(Signup.this);

                // url 작성
                ApiService IDcheckApiService = new ApiService();
                String url = "http://203.250.133.141:8080/user/id_check/" + IDcheck;
                IDcheckApiService.getUrl(url);

                if(IDcheckApiService.getStatus() == 200){
                    // 메시지 설정
                    String message = "사용 가능한 아이디입니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    Signup_isIdChecked = true; // 아이디 중복 확인을 완료했음을 표시
                }else{
                    // 메시지 설정
                    String message = "중복된 아이디입니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    Signup_isIdChecked = false;
                }
            }
        });
        //인증번호 받기 버튼 구현
        Signup_Mail_Cn_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String Mail = Signup_Mail.getText().toString();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(Signup.this);
                // 이메일 형식 체크
                if (!isValidEmail(Mail)) {
                    // 메시지 설정
                    String message = "올바른 이메일 형식이 아닙니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    Signup_isMailCnConfirmed = false;
                    return;
                }

                // url 작성
                ApiService MailApiService = new ApiService();
                String url = "http://203.250.133.141:8080/user/get_certification_number/" + Mail;
                MailApiService.postUrl(url);

                String key = MailApiService.getKey("signup_mail_error");

                if(MailApiService.getStatus() == 200){
                    // 메시지 설정
                    String message = "이메일로 인증번호가\n발송되었습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    Signup_isMailCnConfirmed = true;
                }else {
                    // 메시지 설정
                    String message = "다시 입력해주세요.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    Signup_isMailCnConfirmed = false;
                }
            }
        });
        //인증번호 확인 버튼 구현
        Signup_Mail_Cn_Check_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String Mail = Signup_Mail.getText().toString();
                String Cn = Signup_Mail_Cn.getText().toString();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(Signup.this);

                // url 작성
                ApiService CnApiService = new ApiService();
                String url = "http://203.250.133.141:8080/user/check_verification/" + Mail + "/" + Cn;
                CnApiService.getUrl(url);

                if(CnApiService.getStatus() == 200){
                    // 메시지 설정
                    String message = "인증 됐습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    Signup_isCnChecked = true;
                }else{
                    // 메시지 설정
                    String message = "인증 실패했습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    Signup_isCnChecked = false;
                }
            }
        });
    }
    // 이메일 형식 체크 메소드
    private boolean isValidEmail(String email) {
        // 이메일 형식 검사하는 정규표현식
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // 정규표현식과 일치하는지 확인
        return email.matches(emailPattern);
    }
}