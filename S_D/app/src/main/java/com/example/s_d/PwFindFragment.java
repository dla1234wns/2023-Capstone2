package com.example.s_d;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class PwFindFragment extends Fragment {
    EditText PwFindFragment_Edit_Id, PwFindFragment_Edit_Name, PwFindFragment_Mail, PwFindFragment_Mail_Cn;
    AppCompatButton PwFindFragment_Mail_Cn_Button, PwFindFragment_Mail_Cn_Check_Button, PwFindFragment_Check_Button;

    private boolean PwFindFragment_isMailCnConfirmed = false; // 인증번호 받기 버튼 눌림 여부를 나타내는 변수
    private boolean PwFindFragment_isCnChecked = false; // 인증번호 확인 버튼 눌림 여부를 나타내는 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pw_find_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PwFindFragment_Edit_Id = view.findViewById(R.id.PwFindFragment_edit_id);
        PwFindFragment_Edit_Name = view.findViewById(R.id.PwFindFragment_edit_name);
        PwFindFragment_Mail = view.findViewById(R.id.PwFindFragment_mail);
        PwFindFragment_Mail_Cn = view.findViewById(R.id.PwFindFragment_mail_cn);

        PwFindFragment_Mail_Cn_Button = view.findViewById(R.id.PwFindFragment_mail_cn_button);
        PwFindFragment_Mail_Cn_Check_Button = view.findViewById(R.id.PwFindFragment_mail_cn_check_button);
        PwFindFragment_Check_Button = view.findViewById(R.id.PwFindFragment_check_button);

        //비밀번호 찾기 버튼 눌렀을때
        PwFindFragment_Check_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Id = PwFindFragment_Edit_Id.getText().toString();
                String Name = PwFindFragment_Edit_Name.getText().toString();
                String Email = PwFindFragment_Mail.getText().toString();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(getContext());

                if(Id.isEmpty()){
                    Toast.makeText(getContext(), "아이디를 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Name.isEmpty()){
                    Toast.makeText(getContext(), "이름을 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 인증번호 받기 버튼을 누르지 않았을 경우
                if (!PwFindFragment_isMailCnConfirmed) {
                  Toast.makeText(getContext(), "인증번호를 받은 후 확인 해주세요.", Toast.LENGTH_SHORT).show();
                  return;
                }
                // 인증번호 확인 버튼을 누르지 않았을 경우
                if (!PwFindFragment_isCnChecked) {
                  Toast.makeText(getContext(), "인증번호를 확인 해주세요.", Toast.LENGTH_SHORT).show();
                  return;
                }

                // url 작성
                String url = "http://203.250.133.141:8080/user/user_info/" + Id;
                ApiService PwFindApiService = new ApiService();
                PwFindApiService.getUrl(url);

                String id_Check = PwFindApiService.getValue("user_id");
                String name_Check = PwFindApiService.getValue("user_name");
                String email_Check = PwFindApiService.getValue("user_email");

                if (Id.equals(id_Check) && Name.equals(name_Check) && Email.equals(email_Check) && PwFindApiService.getStatus() == 200) {
                    Intent intent = new Intent(getActivity(), LoginPwChange.class);
                    startActivity(intent);
                } else {
                    // 메시지 설정
                    String message = "사용자의 입력이 잘못되었습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                }
            }
        });
        //인증번호 받기 버튼 구현
        PwFindFragment_Mail_Cn_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String Mail = PwFindFragment_Mail.getText().toString();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(getContext());

                // 이메일 형식 체크
                if (!isValidEmail(Mail)) {
                    // 메시지 설정
                    String message = "올바른 이메일 형식이 아닙니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    PwFindFragment_isMailCnConfirmed = false;
                    return;
                }

                // url 작성
                ApiService MailApiService = new ApiService();
                String url = "http://203.250.133.141:8080/user/get_certification_number/" + Mail;
                MailApiService.postUrl(url);

                if(MailApiService.getStatus() == 200){
                    // 메시지 설정
                    String message = "이메일로 인증번호가\n발송되었습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    PwFindFragment_isMailCnConfirmed = true;
                }else{
                    // 메시지 설정
                    String message = "이메일이 틀렸습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    PwFindFragment_isMailCnConfirmed = false;
                }
            }
        });
        //인증번호 확인 버튼 구현
        PwFindFragment_Mail_Cn_Check_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String Mail = PwFindFragment_Mail.getText().toString();
                String Cn = PwFindFragment_Mail_Cn.getText().toString();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(getContext());

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
                    PwFindFragment_isCnChecked = true;
                }else{
                    // 메시지 설정
                    String message = "인증 실패했습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    PwFindFragment_isCnChecked = false;
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