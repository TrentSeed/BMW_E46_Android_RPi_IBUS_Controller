package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentWindowActionDown extends Fragment {
	
	ImageView imgBtnDown;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	// Inflate the layout for this fragment
    	View rootView = inflater.inflate(R.layout.window_action_down, container, false);
    	imgBtnDown = (ImageView) rootView.findViewById(R.id.btn_down);
    	imgBtnDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					if(ActivityMain.parent_page_id == IBUSGridPagerAdapter.PAGE_DRIVER_LANDING){
						IBUSWrapper.windowDriverFront(true);
					}else if(ActivityMain.parent_page_id == IBUSGridPagerAdapter.PAGE_PASSENGER_LANDING){
						IBUSWrapper.windowPassengerFront(true);
					}else if(ActivityMain.parent_page_id == IBUSGridPagerAdapter.PAGE_REAR_LEFT_LANDING){
						IBUSWrapper.windowDriverRear(true);
					}else if(ActivityMain.parent_page_id == IBUSGridPagerAdapter.PAGE_REAR_RIGHT_LANDING){
						IBUSWrapper.windowPassengerRear(true);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
        return rootView;
    }
}
