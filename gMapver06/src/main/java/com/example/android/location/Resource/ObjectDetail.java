package com.example.android.location.Resource;

import android.location.Location;

import com.metaio.sdk.jni.IGeometry;

/**
 * Created by Adisorn on 2/8/2015.
 */
public class ObjectDetail {
    private Location location;
    private double initialAngle=0;
    private double acceptableAngle=0;
    private double acceptableDistance=0;
    private IGeometry model;
    private String modelString;
    private int remainingHP=0;
    private int maxHP=0;
    private String markerPath;
    private int goldDrop=200;

    public String getMarkerPath() {
        return markerPath;
    }

    public void setMarkerPath(String markerPath) {
        this.markerPath = markerPath;
    }

    public int getGoldDrop() {
        return goldDrop;
    }

    public void setGoldDrop(int goldDrop) {
        this.goldDrop = goldDrop;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public ObjectDetail(String modelString) {
        this.modelString = modelString;
    }

    public ObjectDetail(String modelString,int maxHP) {
        this.modelString = modelString;
        this.maxHP=maxHP;
        this.remainingHP=maxHP;
    }


    public ObjectDetail(Location location, double initialAngle, double acceptableAngle, double acceptableDistance, String modelString,int maxHP,String markerPath) {
        this.location = location;
        this.maxHP=maxHP;
        this.remainingHP=maxHP;
        this.initialAngle = initialAngle;
        this.acceptableAngle = acceptableAngle;
        this.acceptableDistance = acceptableDistance;
        this.modelString = modelString;
        this.markerPath=markerPath;
    }

    public int getRemainingHP() {
        return remainingHP;
    }

    public void setRemainingHP(int remainingHP) {
        this.remainingHP = remainingHP;
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

    public IGeometry getModel() {
        return model;
    }

    public void setModel(IGeometry model) {
        this.model = model;
    }

    public String getModelString() {
        return modelString;
    }

    public void setModelString(String modelString) {
        this.modelString = modelString;
    }
}
