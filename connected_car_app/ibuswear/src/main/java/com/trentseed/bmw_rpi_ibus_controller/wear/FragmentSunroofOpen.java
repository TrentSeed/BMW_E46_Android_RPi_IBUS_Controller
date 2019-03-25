package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trentseed.bmw_rpi_ibus_controller.common.IBUSWrapper;

public class FragmentSunroofOpen extends Fragment {

    ImageView imgBtnUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ActivityMain.parent_page_id = IBUSGridPagerAdapter.PAGE_SUNROOF_OPEN;
        View rootView = inflater.inflate(R.layout.window_action_up, container, false);
        imgBtnUp = rootView.findViewById(R.id.btn_up);
        imgBtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    IBUSWrapper.toggleSunroof(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }
}
