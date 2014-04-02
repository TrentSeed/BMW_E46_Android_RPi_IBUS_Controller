package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

public class ActivityIBUS extends Activity {

	// layout objects
	private ImageView ivBack;
	private ListView lvBusEvents;
	public AdapterIBUS adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_ibus);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBack = (ImageView) findViewById(R.id.ivBack);
		lvBusEvents = (ListView) findViewById(R.id.lvBusEvents);
		
		// set click handlers
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// create and set adapter
		adapter = new AdapterIBUS(this);
		lvBusEvents.setAdapter(adapter);
	}
	
}
