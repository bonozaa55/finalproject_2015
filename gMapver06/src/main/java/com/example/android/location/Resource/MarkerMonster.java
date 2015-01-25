package com.example.android.location.Resource;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MarkerMonster {
	private Location location;
	private String imagePath;
	private double initialAngle;
	private double radius;
	private double distance;
	
	
	
	public MarkerMonster(double lat,double lng, double initialAngle, double radius,
			double distance,String imagePath) {
		super();
		Location temp=new Location("");
		temp.setLatitude(lat);
		temp.setLongitude(lng);
		this.location = temp;
		this.initialAngle = initialAngle;
		this.radius = radius;
		this.imagePath=imagePath;
		this.distance = distance;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public double getInitialAngle() {
		return initialAngle;
	}
	public void setInitialAngle(double initialAngle) {
		this.initialAngle = initialAngle;
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
}
