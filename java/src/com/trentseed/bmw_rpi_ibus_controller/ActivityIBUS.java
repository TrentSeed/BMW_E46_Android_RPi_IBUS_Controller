package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityIBUS extends Activity {

	// layout objects
	private ImageView ivBack;
	private ImageView ivBusActivity;
	private ListView lvBusEvents;
	public TextView tvNoActivity;
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
		ivBusActivity = (ImageView) findViewById(R.id.ivBusActivity);
		ivBusActivity.setVisibility(View.GONE);
		lvBusEvents = (ListView) findViewById(R.id.lvBusEvents);
		tvNoActivity = (TextView) findViewById(R.id.tvNoActivity);
		
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
	}
	
	/**
	 * Received an IBUSPacket, store in cache and update ListView
	 */
	public void receivedIBUSPacket(IBUSPacket ibPacket){
		// add packet to local buffer (IBUS activity displays buffer)
		int max_buffer = 50;
		BluetoothInterface.mArrayListIBUSActivity.add(0, ibPacket);
		if(BluetoothInterface.mArrayListIBUSActivity.size() > max_buffer){
			BluetoothInterface.mArrayListIBUSActivity.remove(BluetoothInterface.mArrayListIBUSActivity.size()-1);
		}
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
	
}
