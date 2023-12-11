package com.example.s_d;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageButton Device_Add, Setting;

    String userId;

    int check = 0;

    long recordedTimeMillis;

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private ArrayList<DeviceDataModel> dataList;

    private Handler handler = new Handler();
    private final int delay = 2000; // 1초 delay

    private static final String NOTIFICATION_MESSAGE = "리드 스위치가 켜져있습니다.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordedTimeMillis = System.currentTimeMillis();

        Log.d("recordedTimeMillis", String.valueOf(recordedTimeMillis));

        IdSave idSave = (IdSave) getApplication();
        userId = idSave.getUserId();

        // 기기 추가 버튼
        Device_Add = findViewById(R.id.device_add);
        Device_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeviceAdd.class);
                startActivity(intent);
            }
        });

        // 설정 버튼
        Setting = findViewById(R.id.setting);
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycleView);
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

        // Set up a repeating task to update device_value every second
        handler.postDelayed(new Runnable() {
            public void run() {
                updateDeviceValues(userId);
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
    private void showNotification(String message) {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("message", message);
        startService(serviceIntent);
    }

    private void updateDeviceValues(String userId) {
        // url 작성
        String User_url = "http://203.250.133.141:8080/user/user_info/" + userId;
        ApiService UserInfoApiService = new ApiService();
        UserInfoApiService.getUrl(User_url);

        String Count = UserInfoApiService.getValue("HDCount");

        // url 작성
        String Device_url = "http://203.250.133.141:8080/device/device_Info/" + userId;
        ApiService DeviceInfoApiService = new ApiService();
        DeviceInfoApiService.getUrl(Device_url);

        String device_no, device_type, device_name, device_sn, device_status;

        if (DeviceInfoApiService.getStatus() == 200) {
            for (int i = 0; i < Integer.parseInt(Count); i++) {
                device_no = Integer.toString(i + 1);
                device_type = DeviceInfoApiService.getValue("device_no" + i);
                device_name = DeviceInfoApiService.getValue("device_name" + i);
                device_sn = DeviceInfoApiService.getValue("device_sn" + i);
                device_status = DeviceInfoApiService.getValue("device_value" + i);

                // Check if dataList already contains a device with the same device_sn
                boolean found = false;
                for (DeviceDataModel existingDevice : dataList) {
                    if (existingDevice.getDevice_sn().equals(device_sn)) {
                        // Update only device_value (assuming it's an integer)
                        if (device_status != null) {
                            int updatedValue = Integer.parseInt(device_status);
                            existingDevice.setDevice_status(String.valueOf(updatedValue));
                        }
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // If the device_sn was not found in dataList, add a new device
                    DeviceDataModel device = new DeviceDataModel();
                    device.setDevice_no(device_no);
                    device.setDevice_type(device_type);
                    device.setDevice_name(device_name);
                    device.setDevice_sn(device_sn);
                    device.setDevice_status(device_status);
                    dataList.add(device);
                }
                long Time = System.currentTimeMillis() - recordedTimeMillis;
                Log.d("Time123213213", String.valueOf(Time));
                // 추가: 타입이 4이면서 상태가 1인 장치를 검사하여 노티피케이션을 표시
                if (check == 0) {
                    if (device_type.equals("4") && device_status.equals("1")){
                        if (System.currentTimeMillis() - recordedTimeMillis >= 300000) {
                            Time = System.currentTimeMillis() - recordedTimeMillis;
                            Log.d("Time123213213", String.valueOf(Time));
                            // 팝업 알림 표시
                            showNotification(NOTIFICATION_MESSAGE);
                            Toast.makeText(MainActivity.this, "리드 스위치가 켜져있습니다.", Toast.LENGTH_SHORT).show();
                            check = 1;
                        }
                    }
                }

            }
            // 데이터 변경을 알림
            adapter.notifyDataSetChanged();
        }
    }
    // 센서 변경 메소드
    private void handleItemClick(DeviceDataModel device) {
        // 이곳에서 클릭한 아이템에 대한 작업을 수행
        String device_sn = device.getDevice_sn();
        String device_status = device.getDevice_status();

        int on = 1, off = 0;
        String S_on = String.valueOf(on), S_off = String.valueOf(off);

        if ("3".equals(device.getDevice_type()) && device_status.equals(S_on))
        {
            // 예: 센서 변경 API 호출
            String SensorChangeUrl = "http://203.250.133.141:8080/device/switch_change/" + userId + "/" + device_sn + "/" + S_off;
            ApiService SensorChangeApiService = new ApiService();
            SensorChangeApiService.putUrl(SensorChangeUrl);

            check = 0;
            if (SensorChangeApiService.getStatus() == 200) {
                Toast.makeText(MainActivity.this, "상태값이 변경됐습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "상태값이 변경 안됐습니다..", Toast.LENGTH_SHORT).show();
            }
        }
        else if ("3".equals(device.getDevice_type()) && device_status.equals(S_off))
        {
            // 예: 센서 변경 API 호출
            String SensorChangeUrl = "http://203.250.133.141:8080/device/switch_change/" + userId + "/" + device_sn + "/" + S_on;
            ApiService SensorChangeApiService = new ApiService();
            SensorChangeApiService.putUrl(SensorChangeUrl);

            if (SensorChangeApiService.getStatus() == 200) {
                Toast.makeText(MainActivity.this, "상태값이 변경됐습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "상태값이 변경 안됐습니다..", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "type가 3인것만 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}