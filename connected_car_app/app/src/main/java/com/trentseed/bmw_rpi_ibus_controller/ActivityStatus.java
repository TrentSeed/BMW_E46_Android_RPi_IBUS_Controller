package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.trentseed.bmw_rpi_ibus_controller.common.BluetoothInterface;

public class ActivityStatus extends Activity {
	
	ImageView ivBack;
	TextView tvStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_status);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBack = findViewById(R.id.ivBack);
		tvStatus = findViewById(R.id.tvStatus);
		updateStatusText();
		
		// set click handlers
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		BluetoothInterface.mActivity = this;
		BluetoothInterface.checkConnection();
		updateStatusText();
	}
	
	private void updateStatusText(){
		if(BluetoothInterface.isConnected()){
			tvStatus.setText("Connected to Raspberry Pi via Bluetooth");
		}else{
			tvStatus.setText("Not Connected");
		}
	}
	
}
