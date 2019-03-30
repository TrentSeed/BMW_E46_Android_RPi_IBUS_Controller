package com.trentseed.bmw_rpi_ibus_controller;

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
import android.widget.Toast;

import com.trentseed.bmw_rpi_ibus_controller.common.BluetoothInterface;
import com.trentseed.bmw_rpi_ibus_controller.common.IBUSWrapper;
import com.trentseed.bmw_rpi_ibus_controller.common.IBUSPacket;

/**
 * Activity that handles presents core functionality to user.
 * @author Trent
 */
public class ActivityMain extends Activity {

	ImageView ivBmwEmblem;
	ImageView ivBtnRadio;
	ImageView ivBtnDevices;
	ImageView ivBtnMaps;
	ImageView ivBtnMedia;
	ImageView ivBtnGear;
	ImageView ivBtnVoice;
    ProgressBar pbConnecting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_main);
		BluetoothInterface.mActivity = this;
		
		// get layout objects
		ivBmwEmblem = findViewById(R.id.ivBMWEmblem);
		ivBtnRadio = findViewById(R.id.ivBtnRadio);
		ivBtnDevices = findViewById(R.id.ivBtnDevices);
		ivBtnMaps = findViewById(R.id.ivBtnMaps);
		ivBtnMedia = findViewById(R.id.ivBtnMedia);
		ivBtnVoice = findViewById(R.id.ivBtnMic);
        ivBtnGear = findViewById(R.id.ivBtnGear);
        pbConnecting = findViewById(R.id.pbBluetoothConnecting);
		
		// bind click handlers to layout objects
		ivBmwEmblem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if(!BluetoothInterface.isConnected() && !BluetoothInterface.isConnecting){
                    new PerformBackgroundConnect().execute();
                }
			}
		});
		ivBtnMaps.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// launch Google Maps (using street and city provided by BMW GPS System)
				String uri = "http://maps.google.com/maps";
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				ActivityMain.this.startActivity(intent);
			}
		});
		ivBtnRadio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent launchRadio = new Intent(ActivityMain.this, ActivityRadio.class);
				startActivity(launchRadio);
			}
		});
		ivBtnDevices.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent launchWindows = new Intent(ActivityMain.this, ActivityDevices.class);
				startActivity(launchWindows);				
			}
		});
		ivBtnMedia.setOnClickListener(new View.OnClickListener() {
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
        ivBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                Intent launchWindows = new Intent(ActivityMain.this, ActivityIBUS.class);
                startActivity(launchWindows);
            }
        });
        ivBtnGear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                Intent launchWindows = new Intent(ActivityMain.this, ActivityIBUS.class);
                startActivity(launchWindows);
            }
        });
	
		// check if bluetooth enabled (prompt to enable)
        refreshConnectingStatus();
		if (BluetoothInterface.mBluetoothAdapter!=null && !BluetoothInterface.mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, 100);
		}
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
//		tvBtnLocation.setText(IBUSWrapper.gpsStreet + "\n" + IBUSWrapper.gpsCity);
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
//			updateLocationOnScreen();
		}
	}
}
