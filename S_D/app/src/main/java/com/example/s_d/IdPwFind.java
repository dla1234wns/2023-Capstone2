package com.example.s_d;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class IdPwFind extends AppCompatActivity {
    private FrameLayout fragment_Container;
    private AppCompatButton IdPwFind_Id_Find_Button, IdPwFind_Pw_Find_Button;
    ImageButton IdPwFind_Find_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_pw_find);

        //뒤로 가기 버튼
        IdPwFind_Find_Back = findViewById(R.id.IdPwFind_back);
        IdPwFind_Find_Back.setOnClickListener(v -> onBackPressed() );

        // FragmentContainer 초기화
        fragment_Container = findViewById(R.id.fragment_container);

        // 버튼 초기화
        IdPwFind_Id_Find_Button = findViewById(R.id.IdPwFind_id_find_button);
        IdPwFind_Pw_Find_Button = findViewById(R.id.IdPwFind_pw_find_button);
        // idFindButton 클릭 시 IdFindFragment를 띄웁니다.
        IdPwFind_Id_Find_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                IdFindFragment idFindFragment = new IdFindFragment();
                fragmentTransaction.replace(R.id.fragment_container, idFindFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        // pwFindButton 클릭 시 PwFindFragment를 띄웁니다.
        IdPwFind_Pw_Find_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                PwFindFragment pwFindFragment = new PwFindFragment();
                fragmentTransaction.replace(R.id.fragment_container, pwFindFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
    @Override
    public void onBackPressed() {
        // 로그인 화면으로 이동
        startActivity(new Intent(IdPwFind.this, Login.class));
        // 현재 액티비티 종료
        finish();
    }
}