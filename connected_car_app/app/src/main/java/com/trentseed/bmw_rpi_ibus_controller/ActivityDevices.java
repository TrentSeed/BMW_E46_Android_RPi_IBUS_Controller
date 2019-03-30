package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.trentseed.bmw_rpi_ibus_controller.common.BluetoothInterface;
import com.trentseed.bmw_rpi_ibus_controller.common.IBUSWrapper;

public class ActivityDevices extends Activity {
	
	ImageView ivBack;
	Button btnDriverFrontUp;
	Button btnDriverFrontDown;
	Button btnDriverRearUp;
	Button btnDriverRearDown;
	Button btnPassengerFrontUp;
	Button btnPassengerFrontDown;
	Button btnPassengerRearUp;
	Button btnPassengerRearDown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_devices);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBack = findViewById(R.id.ivBack);
		btnDriverFrontUp = findViewById(R.id.btnDriverFrontUp);
		btnDriverFrontDown = findViewById(R.id.btnDriverFrontDown);
		btnDriverRearUp = findViewById(R.id.btnDriverRearUp);
		btnDriverRearDown = findViewById(R.id.btnDriverRearDown);
		btnPassengerFrontUp = findViewById(R.id.btnPassengerFrontUp);
		btnPassengerFrontDown = findViewById(R.id.btnPassengerFrontDown);
		btnPassengerRearUp = findViewById(R.id.btnPassengerRearUp);
		btnPassengerRearDown = findViewById(R.id.btnPassengerRearDown);
		
		// set click handlers
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnDriverFrontUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowDriverFront(false);
			}
		});
		btnDriverFrontDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowDriverFront(true);
			}
		});
		btnDriverRearUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowDriverRear(false);
			}
		});
		btnDriverRearDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowDriverRear(true);
			}
		});
		btnPassengerFrontUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowPassengerFront(false);
			}
		});
		btnPassengerFrontDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowPassengerFront(true);
			}
		});
		btnPassengerRearUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowPassengerRear(false);
			}
		});
		btnPassengerRearDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.windowPassengerRear(true);
			}
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		BluetoothInterface.mActivity = this;
		BluetoothInterface.checkConnection();
	}
	
}
