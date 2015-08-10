package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class FragmentWelcome extends Fragment {

	ImageView imgBtnReconnect;
    ProgressBar pbConnectionLoader;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        // Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.welcome, container, false);
        pbConnectionLoader = (ProgressBar) rootView.findViewById(R.id.pbConnectionLoader);
        imgBtnReconnect = (ImageView) rootView.findViewById(R.id.imgBtnEmblem);
        imgBtnReconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startReconnect();
			}
		});
        return rootView;
    }

    /**
     * Starts the bluetooth connection process in a background thread.
     */
    public void startReconnect(){
        new ConnectViaBluetooth().execute();
    }

    /**
     * AsyncTask to perform the bluetooth connection.
     * Displays a progress bar loader to indicate progress.
     */
    private class ConnectViaBluetooth extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            BluetoothInterface.checkConnection();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pbConnectionLoader.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            pbConnectionLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
