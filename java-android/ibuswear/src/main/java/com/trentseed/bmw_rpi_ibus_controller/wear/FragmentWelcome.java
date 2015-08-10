package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentWelcome extends Fragment {

	ImageView imgBtnClose;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	// perform check if connected to bmw car (debug purposes)
		//BluetoothInterface.checkConnection();
    	
        // Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.welcome, container, false);
		imgBtnClose = (ImageView) rootView.findViewById(R.id.imgBtnEmblem);
		imgBtnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothInterface.disconnect();
				getActivity().finish();
			}
		});
        return rootView;
    }
}
