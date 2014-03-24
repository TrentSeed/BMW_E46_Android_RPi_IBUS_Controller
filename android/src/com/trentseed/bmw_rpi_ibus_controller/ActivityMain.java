package com.trentseed.bmw_rpi_ibus_controller;

import com.trentseed.bmw_rpi_ibus_controller.AccessoryEngine.IEngineCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Activity that handles presents core functionality to user.
 * @author Trent
 */
public class ActivityMain extends Activity {
	
	// layout objects
	private AccessoryEngine mEngine = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_main);
		
		// get layout objects
		
		// bind click handlers to UI

		//mEngine.write(new byte[] { (byte) 6 });
		
		// connection with RPi
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d("BMW", "handling intent action: " + intent.getAction());
		if (mEngine == null) {
			mEngine = new AccessoryEngine(getApplicationContext(), mCallback);
		}
		mEngine.onNewIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		mEngine.onDestroy();
		mEngine = null;
		super.onDestroy();
	}

	private final IEngineCallback mCallback = new IEngineCallback() {
		@Override
		public void onDeviceDisconnected() {
			Log.d("BMW", "device physically disconnected");
			Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onConnectionEstablished() {
			Log.d("BMW", "device connected! ready to go!");
			Toast.makeText(getApplicationContext(), "Connected & Ready!", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onConnectionClosed() {
			Log.d("BMW", "connection closed");
			Toast.makeText(getApplicationContext(), "Conn. Closed", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onDataRecieved(byte[] data, int num) {
			Log.d("BMW", "received " + num + " bytes");
			Toast.makeText(getApplicationContext(), "Received " + num + " bytes", Toast.LENGTH_LONG).show();
		}
	};
	
}
