package com.example.s_d;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class DeleteDevice extends AppCompatActivity {

    ImageButton Delete_Device_Back;

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private ArrayList<DeviceDataModel> dataList;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);

        IdSave idSave = (IdSave) getApplication();
        userId = idSave.getUserId();

        Delete_Device_Back = findViewById(R.id.delete_device_back);

        //뒤로 가기 버튼
        Delete_Device_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeleteDevice.this, Setting.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.delete_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        adapter = new DeviceAdapter(dataList);

        // 어댑터에 클릭 리스너 설정
        adapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DeviceDataModel device, int position) {
                // 아이템 클릭 시 수행할 작업 구현
                handleItemClick(device);
            }
        });

        recyclerView.setAdapter(adapter);

        String User_url = "http://203.250.133.141:8080/user/user_info/" + userId;
        ApiService UserInfoApiService = new ApiService();
        UserInfoApiService.getUrl(User_url);

        String Count = UserInfoApiService.getValue("HDCount");

        // url 작성
        String Device_url = "http://203.250.133.141:8080/device/device_Info/" + userId;
        ApiService DeviceInfoApiService = new ApiService();
        DeviceInfoApiService.getUrl(Device_url);

        String device_no, device_type, device_name, device_sn, device_status;

        if(DeviceInfoApiService.getStatus() == 200)
        {
            for(int i = 0; i<Integer.parseInt(Count); i++)
            {
                DeviceDataModel device = new DeviceDataModel();

                device_no = Integer.toString(i);
                device_type = DeviceInfoApiService.getValue("device_no" + i);
                device_name = DeviceInfoApiService.getValue("device_name" + i);
                device_sn = DeviceInfoApiService.getValue("device_sn" + i);
                device_status = DeviceInfoApiService.getValue("device_value" + i);

                device.setDevice_no(device_no);
                device.setDevice_type(device_type);
                device.setDevice_name(device_name);
                device.setDevice_sn(device_sn);
                device.setDevice_status(device_status);
                dataList.add(device);
            }
            // 데이터 변경을 알림
            adapter.notifyDataSetChanged();
        }
    }
    // 장치 삭제 메서드
    private void handleItemClick(DeviceDataModel device) {
        // 이곳에서 클릭한 아이템에 대한 작업을 수행
        String device_sn = device.getDevice_sn();

        // 예: 기기 삭제 API 호출
        String deleteDeviceUrl = "http://203.250.133.141:8080/device/device_delete/" + userId + "/" + device_sn;
        ApiService deleteDeviceApiService = new ApiService();
        deleteDeviceApiService.deleteUrl(deleteDeviceUrl);

        if (deleteDeviceApiService.getStatus() == 200) {
            Toast.makeText(DeleteDevice.this, "기기가 삭제됐습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteDevice.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(DeleteDevice.this, "기기 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}