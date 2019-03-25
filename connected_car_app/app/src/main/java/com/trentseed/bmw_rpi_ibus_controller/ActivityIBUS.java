package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trentseed.bmw_rpi_ibus_controller.common.BluetoothInterface;
import com.trentseed.bmw_rpi_ibus_controller.common.IBUSPacket;

public class ActivityIBUS extends Activity {

	// layout objects
	ImageView ivBack;
	ImageView ivBusActivity;
	ListView lvBusEvents;
	TextView tvNoActivity;
	AdapterIBUS adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_ibus);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBack = findViewById(R.id.ivBack);
		ivBusActivity = findViewById(R.id.ivBusActivity);
		ivBusActivity.setVisibility(View.GONE);
		lvBusEvents = findViewById(R.id.lvBusEvents);
		tvNoActivity = findViewById(R.id.tvNoActivity);
		
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
		if(adapter.getCount() > 0) tvNoActivity.setVisibility(View.GONE);
		lvBusEvents.setOnItemClickListener(new AbsListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				IBUSPacket ibPacket = adapter.getItem(position);
				AlertDialog.Builder msgBuilder = new AlertDialog.Builder(ActivityIBUS.this);
				msgBuilder.setTitle("IBUS Packet");
				msgBuilder.setMessage("Data: " + ibPacket.raw + "\nASCII: " + ibPacket.getAsciiFromRaw());
				msgBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				msgBuilder.create().show();
				
			}
		});
	}
	
	/**
	 * Received an IBUSPacket update, refresh ListView
	 */
	public void receivedIBUSPacket(IBUSPacket ibPacket){
		adapter.notifyDataSetChanged();
		flashActivity();
	}
	
	/**
	 * Indicate IBUS activity on UI by "flashing" green indicator.
	 */
	private void flashActivity(){
		// fade in animation
		ivBusActivity.setVisibility(View.VISIBLE);
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new AccelerateInterpolator());
		fadeIn.setDuration(300);
		fadeIn.setAnimationListener(new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				// fade out animation
				Animation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator());
				fadeOut.setDuration(300);
				fadeOut.setAnimationListener(new AnimationListener(){
					public void onAnimationEnd(Animation animation){
						ivBusActivity.setVisibility(View.GONE);
					}
					public void onAnimationRepeat(Animation animation){ }
					public void onAnimationStart(Animation animation){ }
				});
				ivBusActivity.setVisibility(View.VISIBLE);
				ivBusActivity.startAnimation(fadeOut);
			}
			public void onAnimationRepeat(Animation animation){ }
			public void onAnimationStart(Animation animation){ }
		});
		ivBusActivity.startAnimation(fadeIn);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		BluetoothInterface.mActivity = this;
		BluetoothInterface.checkConnection();
	}
	
}
