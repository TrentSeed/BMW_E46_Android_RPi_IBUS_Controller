package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trentseed.bmw_rpi_ibus_controller.common.BluetoothInterface;
import com.trentseed.bmw_rpi_ibus_controller.common.VoiceCommand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    TextView tvDateTime;

    BroadcastReceiver _broadcastReceiver;
    SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat _sdfWatchDate = new SimpleDateFormat("MM/dd");

    private static final int SPEECH_REQUEST_CODE = 0;

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
        tvDateTime = findViewById(R.id.tvDateTime);
		
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
				// launch Google Maps
				String uri = "http://maps.google.com/maps";
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				ActivityMain.this.startActivity(intent);
			}
		});
		ivBtnRadio.setOnClickListener(new View.OnClickListener() {
                @Override
			public void onClick(View v) {
                // launch Pandora Radio
                Intent launchPlay = getPackageManager().getLaunchIntentForPackage("com.pandora.android");
                if(launchPlay != null) startActivity(launchPlay);
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
				// launch Google Play Music
                Intent launchPlay = getPackageManager().getLaunchIntentForPackage("com.google.android.music");
                if(launchPlay != null)startActivity(launchPlay);
			}
		});
        ivBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });
        ivBtnGear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - add settings support, launching legacy activity until then
                Intent launchWindows = new Intent(ActivityMain.this, ActivityIBUS.class);
                startActivity(launchWindows);
            }
        });
	
		// check if bluetooth enabled (prompt to enable)
        refreshConnectingStatus();

        // set the date and time
        setDateTime();

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

    @Override
    public void onStart() {
        super.onStart();
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    setDateTime();
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

	public void setDateTime(){
	    Date now = new Date();
        String date = _sdfWatchDate.format(now);
        String time = _sdfWatchTime.format(now);
        tvDateTime.setText(time + "\n" + date);
    }

    /**
     * Create an intent that can start the Speech Recognizer activity
     */
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            String result = VoiceCommand.processSpokenText(spokenText);
            showToast(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class PerformBackgroundConnect extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("BMW", "Checking connection in doInBackground");
            BluetoothInterface.checkConnection();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("BMW", "connection onPostExecute");
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
     * Display toast message for Toast.LENGTH_SHORT period. Uses the fragments
     * activity for context.
     * @param message content to display in toast message
     */
    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
