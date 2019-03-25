package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trentseed.bmw_rpi_ibus_controller.common.IBUSWrapper;

public class FragmentKeysLock extends Fragment {
	
	ImageView imgBtnLock;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
    	View rootView = inflater.inflate(R.layout.keys_lock, container, false);
    	imgBtnLock = rootView.findViewById(R.id.imgBtnLock);
    	imgBtnLock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					IBUSWrapper.lockCar();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
        return rootView;
    }
}
