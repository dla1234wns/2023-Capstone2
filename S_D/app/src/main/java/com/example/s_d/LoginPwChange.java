package com.example.s_d;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class LoginPwChange extends AppCompatActivity {
    EditText LoginPwChange_Edit_Id, LoginPwChange_Edit_New_Pw, LoginPwChange_Edit_New_Pw_Check;
    AppCompatButton LoginPwChange_Check_Button;
    ImageButton LoginPwChange_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pw_change);

        //뒤로 가기 버튼
        LoginPwChange_Back = findViewById(R.id.LoginPwChange_back);
        LoginPwChange_Back.setOnClickListener(v -> onBackPressed() );

        LoginPwChange_Edit_Id = findViewById(R.id.LoginPwChange_edit_id);
        LoginPwChange_Edit_New_Pw = findViewById(R.id.LoginPwChange_edit_new_pw);
        LoginPwChange_Edit_New_Pw_Check = findViewById(R.id.LoginPwChange_edit_new_pw_check);
        LoginPwChange_Check_Button = findViewById(R.id.LoginPwChange_check_button);

        //비밀번호 변경 버튼 눌렀을때
        LoginPwChange_Check_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = LoginPwChange_Edit_Id.getText().toString();
                String input_New_pw = LoginPwChange_Edit_New_Pw.getText().toString();
                String input_New_pw_check = LoginPwChange_Edit_New_Pw_Check.getText().toString();

                if(inputId.isEmpty()){
                    Toast.makeText(LoginPwChange.this, "아이디를 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(input_New_pw.isEmpty()){
                    Toast.makeText(LoginPwChange.this, "새로운 비밀번호를 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(input_New_pw_check.isEmpty()){
                    Toast.makeText(LoginPwChange.this, "비밀번호를 다시 한번 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!input_New_pw.equals(input_New_pw_check)){
                    Toast.makeText(LoginPwChange.this, "두 비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String PwCheckUrl = "http://203.250.133.141:8080/user/user_info/" + inputId;
                ApiService PwCheckApiService = new ApiService();
                PwCheckApiService.getUrl(PwCheckUrl);

                String PwCheck = PwCheckApiService.getValue("user_pw");

                if(input_New_pw.equals(PwCheck)){
                    Toast.makeText(LoginPwChange.this, "바꾸려는 비밀번호가 \n현재 비밀번호와 같습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // url 작성
                String url = "http://203.250.133.141:8080/user/pw_update/" + inputId +  "/" + input_New_pw + "/" + input_New_pw_check;
                ApiService LoginPwChangeApiService = new ApiService();
                LoginPwChangeApiService.putUrl(url);

                if(LoginPwChangeApiService.getStatus() == 200){
                    Toast.makeText(LoginPwChange.this, "비밀번호 변경에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginPwChange.this, Login.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginPwChange.this, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        // 로그인 화면으로 이동
        startActivity(new Intent(LoginPwChange.this, Login.class));
        // 현재 액티비티 종료
        finish();
    }
}