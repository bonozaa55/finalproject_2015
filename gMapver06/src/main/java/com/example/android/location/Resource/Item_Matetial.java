package com.example.android.location.Resource;

public class Item_Matetial {
	private String name;
	private int sell_price;
	private int buy_price;
	private int iconPath;
	private int Quantity;
	
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

	
	

	public int getQuantity() {
		return Quantity;
	}

	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	public Item_Matetial(String name, int iconPath, int quantity) {
		super();
		this.name = name;
		this.iconPath = iconPath;
		Quantity = quantity;
	}

	public int getIconPath() {
		return iconPath;
	}

	public void setIconPath(int iconPath) {
		this.iconPath = iconPath;
	}
	
}
