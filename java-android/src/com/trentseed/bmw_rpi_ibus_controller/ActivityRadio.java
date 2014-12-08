package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class ActivityRadio extends Activity {

	// layout objects
	private ImageView ivBack;
	private Button btnAccept;
	private Button btnVolUp;
	private Button btnVolDown;
	private Button btnRadioPower;
	private Button btnDriverSeatForward;
	private Button btnDriverSeatBack;
	private Button btnSunroofOpen;
	private Button btnSunroofClose;
	private Button btnLock;
	private Button btnUnlock;
	private Button btnMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_radio);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBack = (ImageView) findViewById(R.id.ivBack);
		btnAccept = (Button) findViewById(R.id.btnAccept);
		btnVolUp = (Button) findViewById(R.id.btnVolUp);
		btnVolDown = (Button) findViewById(R.id.btnVolDown);
		btnRadioPower = (Button) findViewById(R.id.btnRadioPower);
		btnDriverSeatForward = (Button) findViewById(R.id.btnDriverSeatForward);
		btnDriverSeatBack = (Button) findViewById(R.id.btnDriverSeatBack);
		btnSunroofOpen = (Button) findViewById(R.id.btnSunroofOpen);
		btnSunroofClose = (Button) findViewById(R.id.btnSunroofClose);
		btnLock = (Button) findViewById(R.id.btnLock);
		btnUnlock = (Button) findViewById(R.id.btnUnlock);
		btnMode = (Button) findViewById(R.id.btnMode);
		
		// set click handlers
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.pressAccept();
			}
		});
		btnVolUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.volumeUp();
			}
		});
		btnVolDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.volumeDown();				
			}
		});
		btnRadioPower.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.toggleRadioPower();				
			}
		});
		btnMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.toggleMode(ActivityRadio.this);
			}
		});
		btnDriverSeatForward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.moveDriverSeat(true);				
			}
		});
		btnDriverSeatBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.moveDriverSeat(false);				
			}
		});
		btnLock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.lockCar();
			}
		});
		btnUnlock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.unlockCar();
			}
		});
		btnSunroofOpen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.toggleSunroof(true);
			}
		});
		btnSunroofClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IBUSWrapper.toggleSunroof(false);
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
