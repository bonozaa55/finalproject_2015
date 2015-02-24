package com.example.android.location.Resource.Object;

import android.location.Location;

import com.metaio.sdk.jni.IGeometry;

/**
 * Created by Adisorn on 2/8/2015.
 */
public class ObjectDetail {
    Location location;
    private int remainingHP=0;
    private String key;
    IGeometry model;
    private int maxHP;
    public ObjectDetail(String key, IGeometry model) {
        this.key = key;
        this.model = model;
        this.remainingHP=ObjectDATA.getObjectDATAHashMap().get(key).getMaxHP();
        Location tLocation=ObjectDATA.getObjectDATAHashMap().get(key).getLocation();
        this.location=tLocation;
        this.maxHP=ObjectDATA.getObjectDATAHashMap().get(key).getMaxHP();
    }

    public int getMaxHP() {
        return maxHP;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRemainingHP() {
        return remainingHP;
    }

    public void setRemainingHP(int remainingHP) {
        this.remainingHP = remainingHP;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public IGeometry getModel() {
        return model;
    }

    public void setModel(IGeometry model) {
        this.model = model;
    }
}
