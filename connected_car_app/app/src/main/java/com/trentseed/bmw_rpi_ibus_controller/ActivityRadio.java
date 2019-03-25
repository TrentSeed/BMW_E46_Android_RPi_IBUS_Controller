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

public class ActivityRadio extends Activity {

	ImageView ivBack;
	Button btnAccept;
	Button btnVolUp;
	Button btnVolDown;
	Button btnRadioPower;
	Button btnDriverSeatForward;
	Button btnDriverSeatBack;
	Button btnSunroofOpen;
	Button btnSunroofClose;
	Button btnLock;
	Button btnUnlock;
	Button btnMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_radio);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBack = findViewById(R.id.ivBack);
		btnAccept = findViewById(R.id.btnAccept);
		btnVolUp = findViewById(R.id.btnVolUp);
		btnVolDown = findViewById(R.id.btnVolDown);
		btnRadioPower = findViewById(R.id.btnRadioPower);
		btnDriverSeatForward = findViewById(R.id.btnDriverSeatForward);
		btnDriverSeatBack = findViewById(R.id.btnDriverSeatBack);
		btnSunroofOpen = findViewById(R.id.btnSunroofOpen);
		btnSunroofClose = findViewById(R.id.btnSunroofClose);
		btnLock = findViewById(R.id.btnLock);
		btnUnlock = findViewById(R.id.btnUnlock);
		btnMode = findViewById(R.id.btnMode);
		
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
