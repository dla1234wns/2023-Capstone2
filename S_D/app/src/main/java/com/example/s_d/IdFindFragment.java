package com.example.s_d;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class IdFindFragment extends Fragment {

    EditText IdFindFragment_Edit_Name, IdFindFragment_Mail, IdFindFragment_Mail_Cn;
    AppCompatButton IdFindFragment_Check_Button, IdFindFragment_Mail_Cn_Button, IdFindFragment_Mail_Cn_Check_Button;
    Dialog IdFindFragment_Find_id, IdFindFragment_No_Id_Dialog;

    private boolean IdFindFragment_isMailCnConfirmed = false; // 인증번호 받기 버튼 눌림 여부를 나타내는 변수
    private boolean IdFindFragment_isCnChecked = false; // 인증번호 확인 버튼 눌림 여부를 나타내는 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_id_find_fragment, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Id 찾기 확인 다이어로그 초기화
        IdFindFragment_Find_id = new Dialog(getContext());       // Dialog 초기화
        IdFindFragment_Find_id.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        IdFindFragment_Find_id.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 다이얼로그 배경 투명하게 설정
        IdFindFragment_Find_id.setContentView(R.layout.id_find_dialog);             // xml 레이아웃 파일과 연결

        IdFindFragment_Edit_Name = view.findViewById(R.id.IdFindFragment_edit_name);
        IdFindFragment_Mail = view.findViewById(R.id.IdFindFragment_mail);
        IdFindFragment_Mail_Cn = view.findViewById(R.id.IdFindFragment_mail_cn);
        IdFindFragment_Mail_Cn_Button = view.findViewById(R.id.IdFindFragment_mail_cn_button);
        IdFindFragment_Mail_Cn_Check_Button = view.findViewById(R.id.IdFindFragment_mail_cn_check_button);
        IdFindFragment_Check_Button = view.findViewById(R.id.IdFindFragment_check_button);

        //아이디 찾기 버튼을 눌렀을때
        IdFindFragment_Check_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = IdFindFragment_Edit_Name.getText().toString();
                String Email = IdFindFragment_Mail.getText().toString();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(getContext());

                if(Name.isEmpty()){
                    Toast.makeText(getContext(), "이름을 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 인증번호 받기 버튼을 누르지 않았을 경우
                if (!IdFindFragment_isMailCnConfirmed) {
                    Toast.makeText(getContext(), "인증번호를 받은 후 확인 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 인증번호 확인 버튼을 누르지 않았을 경우
                if (!IdFindFragment_isCnChecked) {
                    Toast.makeText(getContext(), "인증번호를 확인 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // url 작성
                ApiService IdFindApiService = new ApiService();
                String url = "http://203.250.133.141:8080/user/id_find/" + Name + "/" + Email;
                IdFindApiService.getUrl(url);

                if (IdFindApiService.getStatus() == 200) {
                    showFindIdDialog(IdFindApiService.getValue("user_id"));

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
        IdFindFragment_Mail_Cn_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String Mail = IdFindFragment_Mail.getText().toString();

                // CustomDialog 객체 생성
                CustomDialog dialog = new CustomDialog(getContext());

                // 이메일 형식 체크
                if (!isValidEmail(Mail)) {
                    // 메시지 설정
                    String message = "올바른 이메일 형식이 아닙니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    IdFindFragment_isMailCnConfirmed = false;
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
                    IdFindFragment_isMailCnConfirmed = true;
                }else{
                    // 메시지 설정
                    String message = "서버에 문제가 생겼거나 네트워크를 확인해주세요.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    IdFindFragment_isMailCnConfirmed = false;
                }
            }
        });
        //인증번호 확인 버튼 구현
        IdFindFragment_Mail_Cn_Check_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 내용 변수에 저장
                String Mail = IdFindFragment_Mail.getText().toString();
                String Cn = IdFindFragment_Mail_Cn.getText().toString();

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
                    IdFindFragment_isCnChecked = true;
                }else{
                    // 메시지 설정
                    String message = "인증 실패했습니다.";
                    dialog.setMessage(message);

                    // 다이얼로그 보여주기
                    dialog.show();
                    IdFindFragment_isCnChecked = false;
                }
            }
        });
    }
    public void showFindIdDialog(String Id){
        IdFindFragment_Find_id.show();
        // 다이얼로그에 텍스트 설정하기
        TextView dialogText = IdFindFragment_Find_id.findViewById(R.id.dialog_id);
        dialogText.setText(Id);

        // 로그인 버튼
        Button LoginBtn = IdFindFragment_Find_id.findViewById(R.id.id_find_to_login_Btn);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
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