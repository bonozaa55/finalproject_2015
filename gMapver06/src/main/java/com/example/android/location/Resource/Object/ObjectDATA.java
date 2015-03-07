package com.example.android.location.Resource.Object;

import android.location.Location;

import java.util.HashMap;

/**
 * Created by Adisorn on 2/14/2015.
 */
public class ObjectDATA {
    public static HashMap<String,ObjectDATA> objectDATAHashMap;
    private double initialAngle;
    private double acceptableAngle;
    private double acceptableDistance;
    private int maxHP;
    private String markerPath;
    private int goldDrop=200;
    private String modelString;
    private Location location=null;

    public static void LoadObjectDATA(){
        Location temp=new Location("");
        objectDATAHashMap=new HashMap<String, ObjectDATA>();
        objectDATAHashMap.put(ObjectID.ONE_EYE,new ObjectDATA(150,"monster_one_eye",300));
        objectDATAHashMap.put(ObjectID.PRISONER,new ObjectDATA(100,"prisoner",200));
        temp.setLatitude(18.796551);
        temp.setLongitude(98.952543);
        //objectDATAHashMap.put(ObjectID.TENTACLE,new ObjectDATA(temp,135,45,15,400,"ENG",400,"monster_loop"));
        objectDATAHashMap.put(ObjectID.METEOR,new ObjectDATA(50,"meteor",100));
        objectDATAHashMap.put(ObjectID.ELEPHANT,new ObjectDATA(temp,135,45,15,2000,"ENG",1000,"bomb_boss"));
        objectDATAHashMap.put(ObjectID.ELEPHANT_UNDERLING_L,new ObjectDATA(500,"bomb_monster_L",100));
        objectDATAHashMap.put(ObjectID.ELEPHANT_UNDERLING_R,new ObjectDATA(500,"bomb_monster_R",100));
        temp=new Location("");
        temp.setLatitude(18.795516);
        temp.setLongitude(98.952832);
        objectDATAHashMap.put(ObjectID.BOTTLE,new ObjectDATA(temp,135,45,15,0,"ELE",0,"bottle"));
        temp=new Location("");
        temp.setLatitude(18.795798);
        temp.setLongitude(98.952797);
        objectDATAHashMap.put(ObjectID.OLD_MAN_KARN,new ObjectDATA(temp,135,45,15,0,"ONE",0,"monster_one_eye"));
        objectDATAHashMap.put(ObjectID.GRASS,new ObjectDATA(0,"grass",0));
        objectDATAHashMap.put(ObjectID.STONE,new ObjectDATA(0,"ore",0));

    }

    public ObjectDATA(int maxHP, String modelString, int goldDrop) {
        this.maxHP = maxHP;
        this.modelString = modelString;
        this.goldDrop = goldDrop;
    }

    public ObjectDATA(Location location,double initialAngle, double acceptableAngle, double acceptableDistance, int maxHP, String markerPath, int goldDrop, String modelString) {
        this.initialAngle = initialAngle;
        this.acceptableAngle = acceptableAngle;
        this.acceptableDistance = acceptableDistance;
        this.maxHP = maxHP;
        this.markerPath = markerPath;
        this.goldDrop = goldDrop;
        this.modelString = modelString;
        this.location=location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static HashMap<String, ObjectDATA> getObjectDATAHashMap() {
        return objectDATAHashMap;
    }

    public static void setObjectDATAHashMap(HashMap<String, ObjectDATA> objectDATAHashMap) {
        ObjectDATA.objectDATAHashMap = objectDATAHashMap;
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

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

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

    public String getModelString() {
        return modelString;
    }

    public void setModelString(String modelString) {
        this.modelString = modelString;
    }
}
