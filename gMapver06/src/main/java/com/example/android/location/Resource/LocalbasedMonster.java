package com.example.android.location.Resource;

import android.location.Location;


public class LocalbasedMonster {
	private Location location;
	private double initialAngle;
	private double radius;
	private double distance;
	
	public double getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public LocalbasedMonster(double lat,double lng, double initialAngle, double radius,double distance) {
		super();
		Location temp=new Location("");
		temp.setLatitude(lat);
		temp.setLongitude(lng);
		this.location = temp;
		this.initialAngle = initialAngle;
		this.radius = radius;
		this.distance=distance;
	}
	public double getInitialAngle() {
		return initialAngle;
	}
	public void setInitialAngle(double initialAngle) {
		this.initialAngle = initialAngle;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location position) {
		this.location = position;
	}
	
}
