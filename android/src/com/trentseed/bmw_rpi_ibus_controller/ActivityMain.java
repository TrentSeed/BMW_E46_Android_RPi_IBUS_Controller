package com.trentseed.bmw_rpi_ibus_controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	
	// bluetooth objects
	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;
	InputStream mBluetoothInputStream;
	OutputStream mBluetoothOutputStream;
	UUID serviceUUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
	String remoteAddress = "5C:AC:4C:C8:E2:7E";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_main);
		
		// get bluetooth objects
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
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
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, 100);
		}
		
		// show paired devices
		/*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		        Toast.makeText(ActivityMain.this, device.getName() + "\n" + device.getAddress(), Toast.LENGTH_SHORT).show();
		    }
		}*/
		
		try{
			// connect to device and get input stream
			mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(remoteAddress);
			mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(serviceUUID);
			mBluetoothSocket.connect();
			mBluetoothInputStream = mBluetoothSocket.getInputStream();
			mBluetoothOutputStream = mBluetoothSocket.getOutputStream();
			Toast.makeText(ActivityMain.this, "Socket connection aquired...", Toast.LENGTH_LONG).show();
			
			// start listening for data on new thread
			new ConnectedThread().start();
		}catch(Exception e){
			Toast.makeText(ActivityMain.this, "IBUS Bluetooth Server is not running...", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mBluetoothSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Thread that handles Bluetooth RFCOMM reading
	 */
	private class ConnectedThread extends Thread {
	 
	    public ConnectedThread() { }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];
	        int bytes = 0;
	        
	        while (true) {
	            try {
	                bytes = mBluetoothInputStream.read(buffer);
	                if(bytes > 0){
	                	Log.d("BMW", "Data in " + String.valueOf(bytes));
	                	
	                	//perform command
	                	ActivityMain.this.runOnUiThread(new Runnable() {
	                	    @Override
	                	    public void run() {
	                	        IBUSWrapper.nextTrack(ActivityMain.this);
	                	    }
	                	});
	                }
	            } catch (IOException e) {
	            	Log.d("BMW", "Error reading: " + String.valueOf(e.getLocalizedMessage()));
	                break;
	            }
	        }
	    }
	}
	
}
