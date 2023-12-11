package com.example.s_d;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private ArrayList<DeviceDataModel> dataList;
    private OnItemClickListener onItemClickListener;

    // 인터페이스 정의: 아이템 클릭 이벤트 처리
    public interface OnItemClickListener {
        void onItemClick(DeviceDataModel device, int position);
    }

    // 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    // 생성자
    public DeviceAdapter(ArrayList<DeviceDataModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeviceDataModel data = dataList.get(position);

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(data, position);
                }
            }
        });

        holder.textDeviceNo.setText(data.getDevice_no());
        holder.textDeviceType.setText(data.getDevice_type());
        holder.textDeviceName.setText(data.getDevice_name());
        holder.textDeviceSN.setText(data.getDevice_sn());
        holder.textDeviceStatus.setText(data.getDevice_status());
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textDeviceNo;
        TextView textDeviceType;
        TextView textDeviceName;
        TextView textDeviceSN;
        TextView textDeviceStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            textDeviceNo = itemView.findViewById(R.id.textDeviceNo);
            textDeviceType = itemView.findViewById(R.id.textDeviceType);
            textDeviceName = itemView.findViewById(R.id.textDeviceName);
            textDeviceSN = itemView.findViewById(R.id.textDeviceSN);
            textDeviceStatus = itemView.findViewById(R.id.textDeviceStatus);
        }
    }
}
