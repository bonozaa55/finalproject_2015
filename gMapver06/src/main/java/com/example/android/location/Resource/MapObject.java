package com.example.android.location.Resource;

import android.location.Location;

import com.metaio.sdk.jni.IGeometry;

/**
 * Created by Adisorn on 1/16/2015.
 */
public class MapObject {
    private Location location;
    private double initialAngle;
    private double acceptableAngle;
    private double acceptableDistance;
    private IGeometry model;

    public MapObject(IGeometry model) {
        this.model = model;
    }

    public IGeometry getModel() {
        return model;
    }

    public void setModel(IGeometry model) {
        this.model = model;
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
