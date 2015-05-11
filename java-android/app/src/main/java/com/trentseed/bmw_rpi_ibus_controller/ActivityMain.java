package com.trentseed.bmw_rpi_ibus_controller;

import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
	private TextView tvBtnMedia;
	private TextView tvBtnLocation;
    private ProgressBar pbConnecting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_main);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBmwEmblem = (ImageView) findViewById(R.id.ivBMWEmblem);
		tvBtnRadio = (TextView) findViewById(R.id.tvBtnRadio);
		tvBtnWindows = (TextView) findViewById(R.id.tvBtnWindows);
		tvBtnIBUS = (TextView) findViewById(R.id.tvBtnIBUS);
		tvBtnMedia = (TextView) findViewById(R.id.tvBtnMedia);
		tvBtnLocation = (TextView) findViewById(R.id.tvBtnLocation);
        pbConnecting = (ProgressBar) findViewById(R.id.pbBluetoothConnecting);
		
		// bind click handlers to layout objects
		ivBmwEmblem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				// launch Screen Off (third-party app used to turn off the screen)
//				Intent screenOff = getPackageManager().getLaunchIntentForPackage("com.cillinsoft.scrnoff");
//				if(screenOff != null) startActivity(screenOff);

                if(!BluetoothInterface.isConnected() && !BluetoothInterface.isConnecting) new PerformBackgroundConnect().execute();

			}
		});
		tvBtnLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// launch Google Maps (using street and city provided by BMW GPS System)
				String uri = "http://maps.google.co.in/maps?q=" + IBUSWrapper.gpsStreet + ", " + IBUSWrapper.gpsCity;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				if(intent != null) ActivityMain.this.startActivity(intent);
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
		tvBtnMedia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder chooseMediaSource = new AlertDialog.Builder(ActivityMain.this);
				chooseMediaSource.setTitle("Media Source");
				chooseMediaSource.setPositiveButton("Music", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// launch Google Play Music
						Intent launchPlay = getPackageManager().getLaunchIntentForPackage("com.google.android.music");
						if(launchPlay != null)startActivity(launchPlay);
					}
				});
				chooseMediaSource.setNegativeButton("Video", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// launch Network Places (third-party app I use to access in-car SMB media server)
						Intent launchNetworkPlaces = getPackageManager().getLaunchIntentForPackage("net.mori.androsamba");
						if(launchNetworkPlaces != null) startActivity(launchNetworkPlaces);
					}
				});
				chooseMediaSource.create().show();
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
        refreshConnectingStatus();
		if (BluetoothInterface.mBluetoothAdapter!=null && !BluetoothInterface.mBluetoothAdapter.isEnabled()) {
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

        if(!BluetoothInterface.isConnected()) new PerformBackgroundConnect().execute();
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

    private class PerformBackgroundConnect extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            BluetoothInterface.checkConnection();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            BluetoothInterface.isConnecting = false;
            refreshConnectingStatus();
            if(BluetoothInterface.isConnected()){
                Toast.makeText(ActivityMain.this, "Connected!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ActivityMain.this, "Unable to connect via bluetooth :(", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            BluetoothInterface.isConnecting = true;
            refreshConnectingStatus();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    /**
     * If Bluetooth connection is being established, show loader.
     */
    public void refreshConnectingStatus(){
        if (BluetoothInterface.isConnecting()){
            pbConnecting.setVisibility(View.VISIBLE);
        }else{
            pbConnecting.setVisibility(View.GONE);
        }
    }
	
	/**
	 * Updates the UI with latest location information provided by GPS
	 */
	public void updateLocationOnScreen(){
		tvBtnLocation.setText(IBUSWrapper.gpsStreet + "\n" + IBUSWrapper.gpsCity);
	}
	
	/**
	 * Received an IBUSPacket, determine if interesting
	 */
	public void receivedIBUSPacket(IBUSPacket ibPacket){
		// navigation GPS information
		if(ibPacket.source_id.equals("7f") && ibPacket.raw.substring(2,4).equals("23") && ibPacket.destination_id.equals("c8")){
			Log.d("BMW", "Received GPS packet" +  ibPacket.length);
			if(IBUSWrapper.gpsIsCityToggle){
				IBUSWrapper.gpsCity = ibPacket.getAsciiFromRaw();
				IBUSWrapper.gpsIsCityToggle = false;
			}else{
				IBUSWrapper.gpsStreet = ibPacket.getAsciiFromRaw();
				IBUSWrapper.gpsIsCityToggle = true;
			}
			updateLocationOnScreen();
		}
	}
}
