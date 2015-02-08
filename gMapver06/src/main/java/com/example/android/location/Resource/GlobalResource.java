package com.example.android.location.Resource;

import android.view.View;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IRadar;

import java.util.ArrayList;

/**
 * Created by Adisorn on 1/16/2015.
 */
public class GlobalResource {
    private static IRadar radar;
    private static Player player;
    private static ArrayList<CraftMission> craftMissionList;
    private static ArrayList<IGeometry> mapObjectModelList;
    private static ArrayList<IGeometry> markerObjectModelList;
    private static ArrayList<IGeometry> locationBasedObjectModelList;
    private static ArrayList<View> listOfViews;
    private static int GAME_STATE=0;
    public static final int STATE_IDLE=0;
    public static final int STATE_LOCATIONBASED=2;
    public static final int STATE_GATHERING=1;
    public static final int STATE_MARKER=3;

    public static ArrayList<CraftMission> getCraftMissionList() {
        return craftMissionList;
    }

    public static void setCraftMissionList(ArrayList<CraftMission> craftMissionList) {
        GlobalResource.craftMissionList = craftMissionList;
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setPlayer(Player player) {
        GlobalResource.player = player;
    }

    public static IRadar getRadar() {
        return radar;
    }

    public static void setRadar(IRadar radar) {
        GlobalResource.radar = radar;
    }

    public static int getGAME_STATE() {
        return GAME_STATE;
    }

    public static void setGAME_STATE(int GAME_STATE) {
        GlobalResource.GAME_STATE = GAME_STATE;
    }

    public static ArrayList<View> getListOfViews() {
        return listOfViews;
    }

    public static void setListOfViews(ArrayList<View> listOfViews) {
        GlobalResource.listOfViews = listOfViews;
    }

    public static ArrayList<IGeometry> getLocationBasedObjectModelList() {
        return locationBasedObjectModelList;
    }

    public static void setLocationBasedObjectModelList(ArrayList<IGeometry> locationBasedObjectModelList) {
        GlobalResource.locationBasedObjectModelList = locationBasedObjectModelList;
    }

    public static ArrayList<IGeometry> getMapObjectModelList() {
        return mapObjectModelList;
    }

    public static void setMapObjectModelList(ArrayList<IGeometry> mapObjectModelList) {
        GlobalResource.mapObjectModelList = mapObjectModelList;
    }

    public static ArrayList<IGeometry> getMarkerObjectModelList() {
        return markerObjectModelList;
    }

    public static void setMarkerObjectModelList(ArrayList<IGeometry> markerObjectModelList) {
        GlobalResource.markerObjectModelList = markerObjectModelList;
    }




}
