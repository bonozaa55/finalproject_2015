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
    private int zOffset=0;
    private int xOffset=0;
    private int yOffset=0;
    private float maxSize;
    private float size;
    private Location location=null;

    public static void LoadObjectDATA(){

        objectDATAHashMap=new HashMap<String, ObjectDATA>();
        objectDATAHashMap.put(ObjectID.ONE_EYE,new ObjectDATA(150,"monster_one_eye",300,0.6f));
        objectDATAHashMap.put(ObjectID.PRISONER,new ObjectDATA(100,"prisoner2",200,0.6f));
        objectDATAHashMap.put(ObjectID.METEOR,new ObjectDATA(50,"meteor",100,0,0,-200,0.5f));
        objectDATAHashMap.put(ObjectID.GRASS,new ObjectDATA(0,"grass",0,0.05f));
        objectDATAHashMap.put(ObjectID.STONE,new ObjectDATA(0,"ore",0,0.05f));
        Location temp=new Location("");
        temp.setLatitude(18.796474);
        temp.setLongitude(98.952519);
        //objectDATAHashMap.put(ObjectID.TENTACLE,new ObjectDATA(temp,135,45,15,400,"ENG",400,"monster_loop"));
        objectDATAHashMap.put(ObjectID.ELEPHANT,new ObjectDATA(temp,135,45,15,2000,"BOSS",1000,"bomb_boss",100,0,-200,0.6f));
        objectDATAHashMap.put(ObjectID.ELEPHANT_UNDERLING_L,new ObjectDATA(500,"bomb_monster_L",100,100,0,-200,0.6f));
        objectDATAHashMap.put(ObjectID.ELEPHANT_UNDERLING_R,new ObjectDATA(500,"bomb_monster_R",100,100,0,-200,0.6f));

        temp=new Location("");
        temp.setLatitude(18.796568);
        temp.setLongitude(98.951905);
        objectDATAHashMap.put(ObjectID.BOTTLE,new ObjectDATA(temp,135,45,15,0,"Healing",0,"bottle",0,0,50,0.53f));

        temp=new Location("");
        temp.setLatitude(18.795122);
        temp.setLongitude(98.951545);
        objectDATAHashMap.put(ObjectID.WATER_VALVE,new ObjectDATA(temp,135,45,15,0,"Fishing",0,"valve",
                0,0,0,0.5f));
        objectDATAHashMap.put(ObjectID.FISHING_ROD,new ObjectDATA(0,"fishing2",0,
                200,-1500,0,0.6f));
        objectDATAHashMap.put(ObjectID.TENDON,new ObjectDATA(0,"tendon2",0,
                200,-1500,0,0.6f));
        objectDATAHashMap.put(ObjectID.FISH,new ObjectDATA(0,"fish2",0,
                200,-1500,0,0.6f));

        temp=new Location("");
        temp.setLatitude(18.795396);
        temp.setLongitude(98.951926);
        objectDATAHashMap.put(ObjectID.THE_OLD_MAN,new ObjectDATA(temp,135,45,15,0,"Mission",0,"monster_one_eye"
                ,0,0,-500,0.35f));

        temp=new Location("");
        temp.setLatitude(18.795893);
        temp.setLongitude(98.951211);
        objectDATAHashMap.put(ObjectID.SHOP_MAN,new ObjectDATA(temp,135,45,15,0,"Shop",0,"monster_one_eye"
                ,0,0,-690,0.4f));

        temp=new Location("");
        temp.setLatitude(18.794803);
        temp.setLongitude(98.950988);
        objectDATAHashMap.put(ObjectID.PET_V1,new ObjectDATA(temp,135,45,15,0,"Pet",0,"prisoner2"
                ,0,0,-555,0.3f,0.4f));
        objectDATAHashMap.put(ObjectID.PET_V2,new ObjectDATA(100,"bomb_boss",200,0,0,-555,0.4f,0.5f));
        objectDATAHashMap.put(ObjectID.PET_V3,new ObjectDATA(100,"monster_one_eye",200,0,0,-555,0.4f,0.5f));

    }

    public ObjectDATA(int maxHP, String modelString, int goldDrop,float size) {
        this.maxHP = maxHP;
        this.modelString = modelString;
        this.goldDrop = goldDrop;
        this.size=size;
    }
    public ObjectDATA(int maxHP, String modelString, int goldDrop,int xOffset,int yOffset,int zOffset,float size) {
        this.maxHP = maxHP;
        this.modelString = modelString;
        this.goldDrop = goldDrop;
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        this.zOffset=zOffset;
        this.size=size;
    }
    public ObjectDATA(int maxHP, String modelString, int goldDrop,int xOffset,int yOffset,int zOffset,float size,float maxSize) {
        this.maxHP = maxHP;
        this.modelString = modelString;
        this.goldDrop = goldDrop;
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        this.zOffset=zOffset;
        this.size=size;
        this.maxSize=maxSize;
    }

    public ObjectDATA(Location location,double initialAngle, double acceptableAngle, double acceptableDistance, int maxHP
            , String markerPath, int goldDrop, String modelString,int xOffset,int yOffset,int zOffset,float size) {
        this.initialAngle = initialAngle;
        this.acceptableAngle = acceptableAngle;
        this.acceptableDistance = acceptableDistance;
        this.maxHP = maxHP;
        this.markerPath = markerPath;
        this.goldDrop = goldDrop;
        this.modelString = modelString;
        this.location=location;
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        this.zOffset=zOffset;
        this.size=size;
    }
    public ObjectDATA(Location location,double initialAngle, double acceptableAngle, double acceptableDistance, int maxHP
            , String markerPath, int goldDrop, String modelString,int xOffset,int yOffset,int zOffset,float size,float maxSize) {
        this.initialAngle = initialAngle;
        this.acceptableAngle = acceptableAngle;
        this.acceptableDistance = acceptableDistance;
        this.maxHP = maxHP;
        this.markerPath = markerPath;
        this.goldDrop = goldDrop;
        this.modelString = modelString;
        this.location=location;
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        this.zOffset=zOffset;
        this.size=size;
        this.maxSize=maxSize;
    }

    public float getMaxSize() {
        return maxSize;
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
    public double getInitialAngle() {
        return initialAngle;
    }
    public double getAcceptableAngle() {
        return acceptableAngle;
    }
    public double getAcceptableDistance() {
        return acceptableDistance;
    }
    public int getMaxHP() {
        return maxHP;
    }
    public String getMarkerPath() {
        return markerPath;
    }
    public int getGoldDrop() {
        return goldDrop;
    }
    public String getModelString() {
        return modelString;
    }
    public int getzOffset() {
        return zOffset;
    }
    public int getxOffset() {
        return xOffset;
    }
    public int getyOffset() {
        return yOffset;
    }

    public float getSize() {
        return size;
    }
}
