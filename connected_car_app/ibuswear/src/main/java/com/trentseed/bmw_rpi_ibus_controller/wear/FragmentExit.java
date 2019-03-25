package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.trentseed.bmw_rpi_ibus_controller.common.BluetoothInterface;

public class FragmentExit extends Fragment {

    ImageView imgBtnClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.exit, container, false);
        imgBtnClose = rootView.findViewById(R.id.imgBtnExit);
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
