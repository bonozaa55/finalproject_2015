package com.example.android.location.Resource;

import android.location.Location;

/**
 * Created by Adisorn on 1/16/2015.
 */
public class MapObject {
    private Location location;
    private double initialAngle;
    private double acceptableAngle;
    private double acceptableDistance;

    public MapObject(Location location,double acceptableAngle, double acceptableDistance, double initialAngle) {
        this.acceptableAngle = acceptableAngle;
        this.acceptableDistance = acceptableDistance;
        this.initialAngle = initialAngle;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getInitialAngle() {
        return initialAngle;
    }

    public void setInitialAngle(double initialAngle) {
        this.initialAngle = initialAngle;
    }

    public double getAcceptableAngle() {
        return acceptableAngle;
    }

    public void setAcceptableAngle(double acceptableAngle) {
        this.acceptableAngle = acceptableAngle;
    }

    public double getAcceptableDistance() {
        return acceptableDistance;
    }

    public void setAcceptableDistance(double acceptableDistance) {
        this.acceptableDistance = acceptableDistance;
    }
}
