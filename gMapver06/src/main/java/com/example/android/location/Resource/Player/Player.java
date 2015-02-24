package com.example.android.location.Resource.Player;

import com.example.android.location.Resource.Mission.CraftMission;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Adisorn on 1/31/2015.
 */
public class Player {
    static int hp;
    static int atkDmg;
    private static HashMap<Integer,PlayerItem> playerItems;
    private static ArrayList<CraftMission> myCraftMission;

    public Player() {
        atkDmg=10;
        hp=1000;
        playerItems=new HashMap<Integer, PlayerItem>();
    }

    public static int getHp() {
        return hp;
    }

    public static void setHp(int hp) {
        Player.hp = hp;
    }

    public static int getAtkDmg() {
        return atkDmg;
    }

    public static void setAtkDmg(int atkDmg) {
        Player.atkDmg = atkDmg;
    }

    public static HashMap<Integer, PlayerItem> getPlayerItems() {
        return playerItems;
    }

    public static void setPlayerItems(HashMap<Integer, PlayerItem> playerItems) {
        Player.playerItems = playerItems;
    }

    public static ArrayList<CraftMission> getMyCraftMission() {
        return myCraftMission;
    }

    public static void setMyCraftMission(ArrayList<CraftMission> myCraftMission) {
        Player.myCraftMission = myCraftMission;
    }
}
