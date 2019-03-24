package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class ActivityWindows extends Activity {
	
	// layout objects
	private ImageView ivBack;
	private Button btnDriverFrontUp;
	private Button btnDriverFrontDown;
	private Button btnDriverRearUp;
	private Button btnDriverRearDown;
	private Button btnPassengerFrontUp;
	private Button btnPassengerFrontDown;
	private Button btnPassengerRearUp;
	private Button btnPassengerRearDown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_windows);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBack = (ImageView) findViewById(R.id.ivBack);
		btnDriverFrontUp = (Button) findViewById(R.id.btnDriverFrontUp);
		btnDriverFrontDown = (Button) findViewById(R.id.btnDriverFrontDown);
		btnDriverRearUp = (Button) findViewById(R.id.btnDriverRearUp);
		btnDriverRearDown = (Button) findViewById(R.id.btnDriverRearDown);
		btnPassengerFrontUp = (Button) findViewById(R.id.btnPassengerFrontUp);
		btnPassengerFrontDown = (Button) findViewById(R.id.btnPassengerFrontDown);
		btnPassengerRearUp = (Button) findViewById(R.id.btnPassengerRearUp);
		btnPassengerRearDown = (Button) findViewById(R.id.btnPassengerRearDown);
		
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
