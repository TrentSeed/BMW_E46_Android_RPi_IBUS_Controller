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
	
	// class objects
	public static boolean gpsIsCityToggle = true;
	public static String gpsCity = "";
	public static String gpsStreet = "";
	
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
			BluetoothInterface.connectToRaspberryPi();
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
				IBUSWrapper.toggleMode(ActivityMain.this);
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
		
		// reset navigation on screen location
		updateLocationOnScreen();
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
			BluetoothInterface.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the UI with latest location information provided by GPS
	 */
	public void updateLocationOnScreen(){
		tvBtnLocation.setText(gpsStreet + "\n" + gpsCity);
	}
	
	/**
	 * Received an IBUSPacket, determine if interesting
	 */
	public void receivedIBUSPacket(IBUSPacket ibPacket){
		// navigation GPS information
		if(ibPacket.source_id.equals("7f") && ibPacket.destination_id.equals("c8")){
			if(gpsIsCityToggle){
				gpsCity = getAsciiFromHex(ibPacket.data);
				gpsIsCityToggle = false;
			}else{
				gpsStreet = getAsciiFromHex(ibPacket.data);
				gpsIsCityToggle = true;
			}
			updateLocationOnScreen();
		}
	}
	
	/**
	 * Parses hex string and returns character representation
	 * @param hex
	 * @return
	 * @see http://stackoverflow.com/a/4785776/714666
	 */
	public String getAsciiFromHex(String hex){
		StringBuilder output = new StringBuilder();
	    for (int i = 0; i < hex.length(); i+=2) {
	        String str = hex.substring(i, i+2);
	        output.append((char)Integer.parseInt(str, 16));
	    }
	    return output.toString();
	}
}
