package com.trentseed.bmw_rpi_ibus_controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class AdapterDevices extends RecyclerView.Adapter<AdapterDevices.ViewHolder> {

    private List<Device> mData;
    private LayoutInflater mInflater;

    AdapterDevices(Context context, List<Device> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.devices_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device device = mData.get(position);

        // bind text values
        holder.deviceName.setText(device.name);

        // bind image resources
        if (device.icon > 0) {
            holder.deviceIcon.setImageResource(device.icon);
        }
        if (device.imageAction1 > 0) {
            holder.deviceAction1.setImageResource(device.imageAction1);
        }
        if (device.imageAction2 > 0) {
            holder.deviceAction2.setImageResource(device.imageAction2);
        }

        // bind click events
        if (device.onClickListenerAction1 != null){
            holder.deviceAction1.setOnClickListener(device.onClickListenerAction1);
        }
        if (device.onClickListenerAction2 != null){
            holder.deviceAction2.setOnClickListener(device.onClickListenerAction2);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceName;
        ImageView deviceIcon;
        ImageView deviceAction1;
        ImageView deviceAction2;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.tvDeviceName);
            deviceIcon = itemView.findViewById(R.id.ivDeviceIcon);
            deviceAction1 = itemView.findViewById(R.id.ivDeviceAction1);
            deviceAction2 = itemView.findViewById(R.id.ivDeviceAction2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("BMW", "onclick occurred in viewholder");
        }

    }

    public static class Device{
        int icon;
        String name;
        int imageAction1;
        int imageAction2;
        View.OnClickListener onClickListenerAction1;
        View.OnClickListener onClickListenerAction2;

        public Device(String name){
            this.name = name;
        }
    }
}
