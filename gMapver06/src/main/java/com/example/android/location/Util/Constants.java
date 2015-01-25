package com.example.android.location.Util;

import android.widget.TextView;

import com.example.android.location.Resource.Item_Matetial;
import com.example.android.location.Resource.Item_Upgradeable;
import com.example.android.location.Resource.User_data;

import java.util.ArrayList;

public final class Constants {

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	private static final int UPDATE_INTERVAL_IN_SECONDS = 2;
	// Update frequency in milliseconds
	public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final long FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	public static final long FASTEST_INTERVAL =(long) (MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS);
	// Stores the lat / long pairs in a text file
	public static final String LOCATION_FILE = "sdcard/location.txt";
	// Stores the connect / disconnect data in a text file
	public static final String LOG_FILE = "sdcard/log.txt";
	public static final int ITEM_TYPE_MATERIAL = 1;
	public static final int ITEM_TYPE_UPGRADEABLE = 2;
	public static final int MAP_VISIBLE = 0;
	public static final int metaio_VISIBLE = 2;
	public static final int SHOP_VISIBLE = 3;
    public static final int MAP_LAYOUT=0;
    public static final int OVERLAY_LAYOUT=1;
	public static TextView User_gold;
	public static User_data User_global = new User_data(5000000,
			new ArrayList<Item_Matetial>(), new ArrayList<Item_Upgradeable>());

	public static User_data getUser_global() {
		return User_global;
	}

	public static void setUser_global(User_data user_global) {
		User_global = user_global;
	}

	public static TextView getUser_gold() {
		return User_gold;
	}

	public static void setUser_gold(TextView user_gold) {
		User_gold = user_gold;
	}

	/**
	 * Suppress default constructor for noninstantiability
	 */
	private Constants() {
		throw new AssertionError();
	}


}