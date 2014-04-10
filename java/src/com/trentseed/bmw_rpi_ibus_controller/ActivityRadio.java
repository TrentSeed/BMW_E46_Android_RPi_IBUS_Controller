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
		
	}
	
}
