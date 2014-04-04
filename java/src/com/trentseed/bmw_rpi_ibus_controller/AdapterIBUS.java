package com.trentseed.bmw_rpi_ibus_controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Adapter that is used to display IBUS activity
 * @author Trent Seed
 */
public class AdapterIBUS extends BaseAdapter{
	
	//instance variables
	public ActivityIBUS thisActivity;
	
	public AdapterIBUS(ActivityIBUS thisActivity){
		//store context and determine enroll status
		this.thisActivity = thisActivity;
	}
	
	public int getCount() {
		return BluetoothInterface.mArrayListIBUSActivity.size();
	}

	public IBUSPacket getItem(int position) {
		return BluetoothInterface.mArrayListIBUSActivity.get(position);
	}

	public long getItemId(int position) {
		return BluetoothInterface.mArrayListIBUSActivity.get(position).hashCode();
	}
	
	@Override
	public void notifyDataSetChanged(){
		super.notifyDataSetChanged();
		
		if(this.getCount() == 0){
			thisActivity.tvNoActivity.setVisibility(View.VISIBLE);
		}else{
			thisActivity.tvNoActivity.setVisibility(View.GONE);			
		}
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// get this object
		IBUSPacket ibPacket = BluetoothInterface.mArrayListIBUSActivity.get(position);
		
		// create field view
		LinearLayout viewContainer = new LinearLayout(thisActivity);
		viewContainer.setOrientation(LinearLayout.HORIZONTAL);
		viewContainer.setGravity(Gravity.LEFT);
		viewContainer.setPadding(40, 20, 20, 0);
		
		// packet image view
		float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80 ,thisActivity.getResources().getDisplayMetrics());
		ImageView ivPacket = new ImageView(thisActivity);
		ivPacket.setImageResource(R.drawable.packet);
		GridView.LayoutParams imgParams = new GridView.LayoutParams((int)height, (int)height);
		ivPacket.setLayoutParams(imgParams);
		ivPacket.setPadding(10, 10, 10, 10);
		
		// label
		TextView labelKey = new TextView(thisActivity);
		String labelText = "Source: " + ibPacket.source_id + " (" + ibPacket.getSourceName() + ") @ " 
						+ AdapterIBUS.getDate(Long.parseLong(ibPacket.timestamp), "hh:mm") + "\n"
						+ "Destination: " + ibPacket.destination_id + " (" + ibPacket.getDestinationName() + ")\n"
						+ "Datagram: " + ibPacket.raw;
		labelKey.setText(labelText);
		labelKey.setTextColor(Color.WHITE);
		GridView.LayoutParams params = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, (int)height, 1);
		labelKey.setGravity(Gravity.CENTER_VERTICAL);
		labelKey.setPadding(40, 0, 0, 0);
		labelKey.setLayoutParams(params);
		labelKey.setTextSize(18.0f);
		
		// timestamp
		TextView tvTimestamp = new TextView(thisActivity);
		String labelTimestamp = ibPacket.timestamp;
		tvTimestamp.setText(labelTimestamp);
		tvTimestamp.setTextColor(Color.WHITE);
		GridView.LayoutParams paramsTimestamp = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, (int)height, 1);
		tvTimestamp.setGravity(Gravity.CENTER_VERTICAL);
		tvTimestamp.setPadding(40, 0, 0, 0);
		tvTimestamp.setLayoutParams(paramsTimestamp);
		tvTimestamp.setTextSize(20.0f);
		
		// add sub-views to container view
		viewContainer.addView(ivPacket);
		viewContainer.addView(labelKey);
		//viewContainer.addView(tvTimestamp);
		return viewContainer;
	}
	
	/**
	 * Return date in specified format.
	 * @param milliSeconds Date in milliseconds
	 * @param dateFormat Date format 
	 * @return String representing date in specified format
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(milliSeconds);
	    return formatter.format(calendar.getTime());
	}
}
