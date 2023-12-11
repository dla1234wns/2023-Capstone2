package com.example.s_d;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class DeviceAdd extends AppCompatActivity {

    AppCompatButton Signup_Button;
    TextInputEditText HardName, HardNumber;
    String user_id;
    int selectedOptionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_add);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.options, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = parentView.getItemAtPosition(position).toString();
                // 선택된 항목 활용
                if (selectedOption.equals("1(온도)")){
                    selectedOptionPosition = 1;
                }
                else if (selectedOption.equals("2(습도)")){
                    selectedOptionPosition = 2;
                }
                else if (selectedOption.equals("3(릴레이)")){
                    selectedOptionPosition = 3;
                }
                else{
                    selectedOptionPosition = 4;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때 처리
            }
        });

        Signup_Button = findViewById(R.id.signup_button);
        HardName = findViewById(R.id.hardname);
        HardNumber = findViewById(R.id.hardnumber);

        IdSave idsave = (IdSave) getApplication();

        if(idsave.getIsAutoLogin() == 1)
        {
            // SharedPreferences 객체 생성
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            // 사용자의 ID를 가져옴
            user_id = sharedPreferences.getString("user_id", "");
        }else{
            user_id = idsave.getUserId();
        }

        //기기 등록 버튼 눌렀을때
        Signup_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = HardName.getText().toString();
                String Number = HardNumber.getText().toString();

                if (selectedOptionPosition != AdapterView.INVALID_POSITION) {
                    //url 작성
                    ApiService RegistApiService = new ApiService();
                    String url = "http://203.250.133.141:8080/device/device_Add/" + user_id + "/" + selectedOptionPosition + "/" + Name + "/" + Number;
                    RegistApiService.postUrl(url);
                    Log.d("url",url);

                    if(RegistApiService.getStatus() == 200) {
                        //url 작성
                        Toast.makeText(DeviceAdd.this, "기기등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeviceAdd.this, MainActivity.class);
                        startActivity(intent);
                    } else{
                        Toast.makeText(DeviceAdd.this, "기기등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 선택된 항목이 없을 경우의 처리
                    Toast.makeText(DeviceAdd.this, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}