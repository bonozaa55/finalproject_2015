package com.example.android.location.Resource.Player;

import com.example.android.location.GameManagement.GameGenerator;
import com.example.android.location.Resource.Mission.CraftMission;
import com.example.android.location.Resource.Mission.MaterialRequired;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Adisorn on 1/31/2015.
 */
public class Player {
    static int hp;
    static int maxHP;
    static int atkDmg;
    static int defDmg;
    private static HashMap<Integer,PlayerItem> playerItems;
    private static HashMap<Integer,PlayerItem> playerEquipment;
    private static ArrayList<CraftMission> myCraftMission;

    public Player() {
        defDmg=10;
        atkDmg=10;
        hp=1000;
        maxHP=1000;
        playerItems=new HashMap<Integer, PlayerItem>();
        playerEquipment=new HashMap<Integer, PlayerItem>();
    }

    public static void removeMaterialRequired(ArrayList<MaterialRequired> t){
        for(MaterialRequired t1:t)
            GameGenerator.setPlayerItem(t1.getItemID(),t1.getQuantity(),false);
    }



    public static int getDefDmg() {
        return defDmg;
    }

    public static void setDefDmg(int defDmg) {
        Player.defDmg = defDmg;
    }

    public static HashMap<Integer, PlayerItem> getPlayerEquipment() {
        return playerEquipment;
    }

    public static void setPlayerEquipment(HashMap<Integer, PlayerItem> playerEquipment) {
        Player.playerEquipment = playerEquipment;
    }

    public static int getMaxHP() {
        return maxHP;
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
