package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.ImageReference;
import android.util.Log;

public class IBUSGridPagerAdapter extends FragmentGridPagerAdapter {

	/* PAGE IDS */
	public static int PAGE_NONE = 0;
	public static int PAGE_WELCOME = 10;
	public static int PAGE_ABOUT = 11;
	public static int PAGE_DRIVER_LANDING = 20;
	public static int PAGE_DRIVER_UP = 21;
	public static int PAGE_DRIVER_DOWN = 22;
	public static int PAGE_PASSENGER_LANDING = 30;
	public static int PAGE_PASSENGER_UP = 31;
	public static int PAGE_PASSENGER_DOWN = 32;
	public static int PAGE_REAR_LEFT_LANDING = 40;
	public static int PAGE_REAR_LEFT_UP = 41;
	public static int PAGE_REAR_LEFT_DOWN = 42;
	public static int PAGE_REAR_RIGHT_LANDING = 50;
	public static int PAGE_REAR_RIGHT_UP = 51;
	public static int PAGE_REAR_RIGHT_DOWN = 52;
	public static int PAGE_LOCKS_LANDING = 60;
	public static int PAGE_LOCKS_LOCK = 61;
	public static int PAGE_LOCKS_UNLOCK = 62;
	
    private final Context mContext;

    public IBUSGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        Log.d("SAMPLE", String.valueOf(PAGES[0].length));
    }
    
    // A simple container for static data in each page
    private static class Page {
    	int page_id;
    	
        public Page(int page_id){
        	this.page_id = page_id;
        }
    }

    // Create a static set of pages in a 2D array
    private final Page[][] PAGES = {
    		{ new Page(PAGE_WELCOME), new Page(PAGE_ABOUT) },
    		{ new Page(PAGE_DRIVER_LANDING), new Page(PAGE_DRIVER_UP), new Page(PAGE_DRIVER_DOWN) },
    		{ new Page(PAGE_PASSENGER_LANDING), new Page(PAGE_PASSENGER_UP), new Page(PAGE_PASSENGER_DOWN) },
    		{ new Page(PAGE_REAR_LEFT_LANDING), new Page(PAGE_REAR_LEFT_UP), new Page(PAGE_REAR_LEFT_DOWN) },
    		{ new Page(PAGE_REAR_RIGHT_LANDING), new Page(PAGE_REAR_RIGHT_UP), new Page(PAGE_REAR_RIGHT_DOWN) },
    		{ new Page(PAGE_LOCKS_LANDING), new Page(PAGE_LOCKS_LOCK), new Page(PAGE_LOCKS_UNLOCK) }
    };
    
    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
       
        Fragment newFrag = null;
        
        if (page.page_id == PAGE_WELCOME){
        	ActivityMain.parent_page_id = PAGE_WELCOME;
        	newFrag = new FragmentWelcome();
        }else if (page.page_id == PAGE_ABOUT){
        	newFrag = new FragmentAbout();
        }else if (page.page_id == PAGE_DRIVER_LANDING){
        	ActivityMain.parent_page_id = PAGE_DRIVER_LANDING;
        	newFrag = new FragmentWindowFrontLeft();
        }else if (page.page_id == PAGE_DRIVER_UP){
        	newFrag = new FragmentWindowFrontLeftActionUp();
        }else if (page.page_id == PAGE_DRIVER_DOWN){
        	newFrag = new FragmentWindowFrontLeftActionDown();
        }else if (page.page_id == PAGE_PASSENGER_LANDING){
        	ActivityMain.parent_page_id = PAGE_PASSENGER_LANDING;
        	newFrag = new FragmentWindowFrontRight();
        }else if (page.page_id == PAGE_PASSENGER_UP){
        	newFrag = new FragmentWindowFrontRightActionUp();
        }else if (page.page_id == PAGE_PASSENGER_DOWN){
        	newFrag = new FragmentWindowFrontRightActionDown();
        }else if (page.page_id == PAGE_REAR_LEFT_LANDING){
        	ActivityMain.parent_page_id = PAGE_REAR_LEFT_LANDING;
        	newFrag = new FragmentWindowBackLeft();
        }else if (page.page_id == PAGE_REAR_LEFT_UP){
        	newFrag = new FragmentWindowBackLeftActionUp();
        }else if (page.page_id == PAGE_REAR_LEFT_DOWN){
        	newFrag = new FragmentWindowBackLeftActionDown();
        }else if (page.page_id == PAGE_REAR_RIGHT_LANDING){
        	ActivityMain.parent_page_id = PAGE_REAR_RIGHT_LANDING;
        	newFrag = new FragmentWindowBackRight();
        }else if (page.page_id == PAGE_REAR_RIGHT_UP){
        	newFrag = new FragmentWindowBackRightActionUp();
        }else if (page.page_id == PAGE_REAR_RIGHT_DOWN){
        	newFrag = new FragmentWindowBackRightActionDown();
        }else if (page.page_id == PAGE_LOCKS_LANDING){
        	ActivityMain.parent_page_id = PAGE_LOCKS_LANDING;
        	newFrag = new FragmentKeys();
        }else if (page.page_id == PAGE_LOCKS_LOCK){
        	newFrag = new FragmentKeysLock();
        }else if (page.page_id == PAGE_LOCKS_UNLOCK){
        	newFrag = new FragmentKeysUnlock();
        }else{
        	ActivityMain.parent_page_id = PAGE_WELCOME;
        	newFrag = new FragmentWelcome();
        }

        return newFrag;
    }

    // Obtain the background image for the page at the specified position
    @Override
    public ImageReference getBackground(int row, int column) {
    	return ImageReference.NONE;
    }
    
    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return PAGES.length;
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }

}
