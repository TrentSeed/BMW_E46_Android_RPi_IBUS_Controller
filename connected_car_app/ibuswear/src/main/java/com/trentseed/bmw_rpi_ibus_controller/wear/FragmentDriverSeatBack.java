package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentDriverSeatBack extends Fragment {

    ImageView imgBtnDown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ActivityMain.parent_page_id = IBUSGridPagerAdapter.PAGE_DRIVER_SEAT_BACK;
        View rootView = inflater.inflate(R.layout.window_action_down, container, false);
        imgBtnDown = (ImageView) rootView.findViewById(R.id.btn_down);
        imgBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    IBUSWrapper.moveDriverSeat(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }
}
