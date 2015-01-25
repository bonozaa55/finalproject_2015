package com.example.android.location.Resource;

public class Item_Upgradeable {
	private String name;
	private int sell_price;
	private int buy_price;
	private int atttack;
	public Item_Upgradeable(String name, int sell_price, int buy_price,
			int atttack, int defence, int upgraded_level, int upgrade_cost,
			int iconPath) {
		super();
		this.name = name;
		this.sell_price = sell_price;
		this.buy_price = buy_price;
		this.atttack = atttack;
		this.defence = defence;
		this.upgraded_level = upgraded_level;
		this.upgrade_cost = upgrade_cost;
		this.iconPath = iconPath;
	}

	public int getUpgrade_cost() {
		return upgrade_cost;
	}

	public void setUpgrade_cost(int upgrade_cost) {
		this.upgrade_cost = upgrade_cost;
	}

	private int defence;
	private int upgraded_level;
	private int upgrade_cost;
	private int iconPath;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSell_price() {
		return sell_price;
	}

	public void setSell_price(int sell_price) {
		this.sell_price = sell_price;
	}

	public int getBuy_price() {
		return buy_price;
	}

	public void setBuy_price(int buy_price) {
		this.buy_price = buy_price;
	}

	public int getAtttack() {
		return atttack;
	}

	public void setAtttack(int atttack) {
		this.atttack = atttack;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getUpgraded_level() {
		return upgraded_level;
	}

	public void setUpgraded_level(int upgraded_level) {
		this.upgraded_level = upgraded_level;
	}

	public int getIconPath() {
		return iconPath;
	}

	public void setIconPath(int iconPath) {
		this.iconPath = iconPath;
	}
	
}
