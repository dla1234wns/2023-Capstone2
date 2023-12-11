package com.example.s_d;

public class DeviceDataModel {
    private int device_id;
    private String device_no;
    private String device_type;
    private String device_name;
    private String device_sn;
    private String device_status;



    public int getDevice_id() {
        return device_id;
    }
    public String getDevice_no() {
        return device_no;
    }
    public String getDevice_type() {
        return device_type;
    }
    public String getDevice_name() {
        return device_name;
    }
    public String getDevice_sn() {
        return device_sn;
    }
    public String getDevice_status() {return device_status;}

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }
    public void setDevice_no(String device_no) {
        this.device_no = device_no;
    }
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }
    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }
    public void setDevice_status(String device_status) {
        this.device_status = device_status;
    }
}
