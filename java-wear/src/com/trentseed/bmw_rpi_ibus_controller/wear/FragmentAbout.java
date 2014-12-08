package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentAbout extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	
    	// perform check if connected to bmw car (debug purposes)
		BluetoothInterface.checkConnection();
    	
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.about, container, false);
    }
}
