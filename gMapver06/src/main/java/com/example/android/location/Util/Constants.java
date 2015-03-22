package com.example.android.location.Util;

import android.widget.TextView;

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
	public static final int MAP_VISIBLE = 0;
	public static final int metaio_VISIBLE = 2;
    //layout
    public static final int MAP_LAYOUT=0;
    public static final int OVERLAY_LAYOUT=1;
    public static final int MY_CRAFT_DETIAL_LAYOUT=2;
    public static final int STORE_LAYOUT=3;
    public static final int CRAFT_RECIPE_LAYOUT=4;
    public static final int TOAST_LAYOUT=5;
    public static final int HEAL_LAYOUT=6;
    public static final int BUY_POTION_LAYOUT=7;
    public static final int PET_LAYOUT=8;
    public static final int MY_ITEMS_LAYOUT=9;
    public static final int MY_EQUIPMENT_LAYOUT=10;
    public static final int SELL_ITEM_LAYOUT=11;

    public static TextView User_gold;

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