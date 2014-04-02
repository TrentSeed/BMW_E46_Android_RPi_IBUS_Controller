package com.trentseed.bmw_rpi_ibus_controller;

import java.util.Locale;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity that handles presents core functionality to user.
 * @author Trent
 */
public class ActivityMain extends Activity {
	
	// layout objects
	private ImageView ivBmwEmblem;
	private TextView tvBtnRadio;
	private TextView tvBtnWindows;
	private TextView tvBtnIBUS;
	private TextView tvBtnStatus;
	private TextView tvBtnLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_main);
		BluetoothInterface.mActivity = this;
		
		// initialize bluetooth
		if(BluetoothInterface.mBluetoothAdapter == null){
			BluetoothInterface.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			BluetoothInterface.ConnectToRaspberryPi();
		}
		
		// get layout objects
		ivBmwEmblem = (ImageView) findViewById(R.id.ivBMWEmblem);
		tvBtnRadio = (TextView) findViewById(R.id.tvBtnRadio);
		tvBtnWindows = (TextView) findViewById(R.id.tvBtnWindows);
		tvBtnIBUS = (TextView) findViewById(R.id.tvBtnIBUS);
		tvBtnStatus = (TextView) findViewById(R.id.tvBtnStatus);
		tvBtnLocation = (TextView) findViewById(R.id.tvBtnLocation);
		
		// bind click handlers to layout objects
		ivBmwEmblem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// go to next music track
				IBUSWrapper.nextTrack(ActivityMain.this);
			}
		});
		tvBtnLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// launch Google Maps
				String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 0.0f, 0.0f);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				ActivityMain.this.startActivity(intent);
			}
		});
		tvBtnRadio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent launchRadio = new Intent(ActivityMain.this, ActivityRadio.class);
				startActivity(launchRadio);
			}
		});
		tvBtnWindows.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent launchWindows = new Intent(ActivityMain.this, ActivityWindows.class);
				startActivity(launchWindows);				
			}
		});
		tvBtnStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent launchStatus = new Intent(ActivityMain.this, ActivityStatus.class);
				startActivity(launchStatus);				
			}
		});
		tvBtnIBUS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent launchIBUS = new Intent(ActivityMain.this, ActivityIBUS.class);
				startActivity(launchIBUS);				
			}
		});
	
		// check if bluetooth enabled (prompt to enable)
		if (!BluetoothInterface.mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, 100);
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		BluetoothInterface.mActivity = this;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			BluetoothInterface.Disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
