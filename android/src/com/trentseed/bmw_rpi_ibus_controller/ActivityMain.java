package com.trentseed.bmw_rpi_ibus_controller;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

/**
 * Activity that handles presents core functionality to user.
 * @author Trent
 *
 */
public class ActivityMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
	}
	
}
