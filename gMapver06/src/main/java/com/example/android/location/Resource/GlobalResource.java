package com.example.android.location.Resource;

import android.view.View;

import com.example.android.location.Resource.Mission.CraftMission;
import com.example.android.location.Resource.Player.Player;
import com.metaio.sdk.jni.IRadar;

import java.util.ArrayList;

/**
 * Created by Adisorn on 1/16/2015.
 */
public class GlobalResource {
    private static IRadar radar;
    private static Player player;
    private static ArrayList<CraftMission> craftMissionList;
    private static ArrayList<View> listOfViews;
    private static int GAME_STATE=0;
    private static int MISSION_STATE=0;

    public static final int STATE_IDLE=0;
    public static final int STATE_LOCATIONBASED=2;
    public static final int STATE_GATHERING=1;
    public static final int STATE_MARKER=3;
    public static final int STATE_METEOR=4;
    public static final int STATE_HEALING=5;
    public static final int STATE_DEAD=6;
    public static final int STATE_MISSION=7;
    public static final int STATE_FISHING=8;
    public static final int STATE_PETTING=9;
    public static final int STATE_SHOPPING=10;
    public static final int STATE_MIST=11;

    public static int getMISSION_STATE() {
        return MISSION_STATE;
    }

    public static void setMISSION_STATE(int MISSION_STATE) {
        GlobalResource.MISSION_STATE = MISSION_STATE;
    }

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




}
