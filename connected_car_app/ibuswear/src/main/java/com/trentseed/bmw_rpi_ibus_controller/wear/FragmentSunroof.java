package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSunroof extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	ActivityMain.parent_page_id = IBUSGridPagerAdapter.PAGE_SUNROOF_LANDING;
        return inflater.inflate(R.layout.sunroof, container, false);
    }
}
