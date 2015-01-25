package com.example.android.location.Resource;

import java.util.ArrayList;

public class User_data {
	private int gold;
	private ArrayList<Item_Matetial> item_matetial;
	private ArrayList<Item_Upgradeable> item_Upgradeables;
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public ArrayList<Item_Matetial> getItem_matetial() {
		return item_matetial;
	}
	public void setItem_matetial(ArrayList<Item_Matetial> item_matetial) {
		this.item_matetial = item_matetial;
	}
	public ArrayList<Item_Upgradeable> getItem_Upgradeables() {
		return item_Upgradeables;
	}
	public void setItem_Upgradeables(ArrayList<Item_Upgradeable> item_Upgradeables) {
		this.item_Upgradeables = item_Upgradeables;
	}
	public User_data(int gold, ArrayList<Item_Matetial> item_matetial,
			ArrayList<Item_Upgradeable> item_Upgradeables) {
		super();
		this.gold = gold;
		this.item_matetial = item_matetial;
		this.item_Upgradeables = item_Upgradeables;
	}
	
}
