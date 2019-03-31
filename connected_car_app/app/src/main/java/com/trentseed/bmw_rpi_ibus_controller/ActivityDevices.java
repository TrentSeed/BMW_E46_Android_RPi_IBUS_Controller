package com.trentseed.bmw_rpi_ibus_controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.trentseed.bmw_rpi_ibus_controller.common.BluetoothInterface;
import com.trentseed.bmw_rpi_ibus_controller.common.IBUSWrapper;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityDevices extends Activity {
	
	ImageView ivBack;
	RecyclerView rvDevices;
    AdapterDevices adapterDevices;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		onNewIntent(getIntent());
		setContentView(R.layout.activity_devices);
		BluetoothInterface.mActivity = this;

        // data to populate the RecyclerView with
        ArrayList<AdapterDevices.Device> deviceNames = this.getDevicesForAdapter();

		// get layout objects
		ivBack = findViewById(R.id.ivBack);
		rvDevices = findViewById(R.id.rvDevices);

        // set up the RecyclerView
        rvDevices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterDevices = new AdapterDevices(this, deviceNames);
        rvDevices.setAdapter(adapterDevices);

		// set click handlers
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		BluetoothInterface.mActivity = this;
		// BluetoothInterface.checkConnection();  // TODO uncomment
	}

    /**
     * Prepares a Device object for each device to display for interaction.
     * @return
     */
	public ArrayList<AdapterDevices.Device> getDevicesForAdapter(){
        ArrayList<AdapterDevices.Device> devices = new ArrayList<>();
        devices.add(getDeviceLocks());
        devices.add(getInteriorLights());
        devices.add(getVolume());
        devices.add(getDriverWindow());
        devices.add(getPassengerWindow());
        devices.add(getDriverRearWindow());
        devices.add(getPassengerRearWindow());
        devices.add(getDriverSeat());
        devices.add(getSunroof());
        devices.add(testGetNextSong());
        return devices;
    }

    /** Returns a Device object for the vehicle locks. **/
    public AdapterDevices.Device getDeviceLocks(){
	    AdapterDevices.Device device = new AdapterDevices.Device("Locks");
	    device.icon = R.drawable.icon_lock;
	    device.imageAction1 = R.drawable.unlock;
        device.imageAction2 = R.drawable.lock;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.unlockCar();
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.lockCar();
            }
        };
	    return device;
    }

    /** TODO remove this method, testing next track. **/
    public AdapterDevices.Device testGetNextSong(){
        AdapterDevices.Device device = new AdapterDevices.Device("Song Control");
        device.icon = R.drawable.icon_volume;
        device.imageAction1 = R.drawable.arrow_left;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.nextTrack(ActivityDevices.this);
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.nextTrack(ActivityDevices.this);
            }
        };
        return device;
    }

    /** Returns a Device object for the vehicle locks. **/
    public AdapterDevices.Device getInteriorLights(){
        AdapterDevices.Device device = new AdapterDevices.Device("Interior Lights");
        device.icon = R.drawable.icon_lights;
        device.imageAction1 = R.drawable.btn_on;
        device.imageAction2 = R.drawable.btn_off;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.turnInteriorLightsOn();
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.turnInteriorLightsOff();
            }
        };
        return device;
    }

    /** Returns a Device object for the vehicle locks. **/
    public AdapterDevices.Device getVolume(){
        AdapterDevices.Device device = new AdapterDevices.Device("Volume");
        device.icon = R.drawable.icon_volume;
        device.imageAction1 = R.drawable.arrow_up;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.volumeUp();
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.volumeDown();
            }
        };
        return device;
    }

    /** Returns a Device object for the driver window. **/
    public AdapterDevices.Device getDriverWindow(){
        AdapterDevices.Device device = new AdapterDevices.Device("Driver Window");
        device.icon = R.drawable.icon_window_left;
        device.imageAction1 = R.drawable.arrow_up;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.windowDriverFront(false);
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.windowDriverFront(true);
            }
        };
        return device;
    }

    /** Returns a Device object for the passenger window. **/
    public AdapterDevices.Device getPassengerWindow(){
        AdapterDevices.Device device = new AdapterDevices.Device("Psngr Window");
        device.icon = R.drawable.icon_window_right;
        device.imageAction1 = R.drawable.arrow_up;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.windowPassengerFront(false);
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.windowPassengerFront(true);
            }
        };
        return device;
    }

    /** Returns a Device object for the driver rear window. **/
    public AdapterDevices.Device getDriverRearWindow(){
        AdapterDevices.Device device = new AdapterDevices.Device("Driver Rear Window");
        device.icon = R.drawable.icon_window_left;
        device.imageAction1 = R.drawable.arrow_up;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.windowDriverRear(false);
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.windowDriverRear(true);
            }
        };
        return device;
    }

    /** Returns a Device object for the passenger rear window. **/
    public AdapterDevices.Device getPassengerRearWindow(){
        AdapterDevices.Device device = new AdapterDevices.Device("Psngr Rear Window");
        device.icon = R.drawable.icon_window_right;
        device.imageAction1 = R.drawable.arrow_up;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.windowPassengerRear(false);
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.windowPassengerRear(true);
            }
        };
        return device;
    }

    /** Returns a Device object for the driver seat motion control. **/
    public AdapterDevices.Device getDriverSeat(){
        AdapterDevices.Device device = new AdapterDevices.Device("Driver Seat");
        device.icon = R.drawable.icon_driverseat;
        device.imageAction1 = R.drawable.arrow_up;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.moveDriverSeat(false);
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.moveDriverSeat(true);
            }
        };
        return device;
    }

    /** Returns a Device object for the sunroof. **/
    public AdapterDevices.Device getSunroof(){
        AdapterDevices.Device device = new AdapterDevices.Device("Sunroof");
        device.icon = R.drawable.icon_sunroof;
        device.imageAction1 = R.drawable.arrow_up;
        device.imageAction2 = R.drawable.arrow_down;
        device.onClickListenerAction1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                IBUSWrapper.toggleSunroof(false);
            }
        };
        device.onClickListenerAction2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IBUSWrapper.toggleSunroof(true);
            }
        };
        return device;
    }
	
}
