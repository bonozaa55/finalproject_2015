package com.example.android.location.Resource.Mission;

import java.util.ArrayList;

/**
 * Created by Adisorn on 2/7/2015.
 */
public class CraftMission {
    private int missionStatus;
    private String name;
    private ArrayList<MaterialRequired> materialRequireList;
    private String property;
    private int ImgResource;

    public CraftMission(int missionStatus, String name, ArrayList<MaterialRequired> materialRequireList, String property, int imgResource) {
        this.missionStatus = missionStatus;
        this.name = name;
        this.materialRequireList = materialRequireList;
        this.property = property;
        ImgResource = imgResource;
    }

    public ArrayList<MaterialRequired> getMaterialRequireList() {
        return materialRequireList;
    }

    public void setMaterialRequireList(ArrayList<MaterialRequired> materialRequireList) {
        this.materialRequireList = materialRequireList;
    }

    public int getMissionStatus() {
        return missionStatus;
    }

    public void setMissionStatus(int missionStatus) {
        this.missionStatus = missionStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getImgResource() {
        return ImgResource;
    }

    public void setImgResource(int imgResource) {
        ImgResource = imgResource;
    }
}
