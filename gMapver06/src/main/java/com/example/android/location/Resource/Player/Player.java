package com.example.android.location.Resource.Player;

import com.example.android.location.GameManagement.GameGenerator;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDATA;
import com.example.android.location.Resource.Item.ItemsID;
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
    static boolean isGetPet;
    private static HashMap<Integer, PlayerItem> playerItems;
    private static HashMap<Integer, PlayerItem> playerEquipment;
    private static ArrayList<CraftMission> myCraftMission;

    public Player(String[] playerData) {
        /*
        Val.put("PlayerID", PlayerID);
        Val.put("Remaining_HP", Remaining_HP);
        Val.put("Max_HP", Max_HP);
        Val.put("Get_Pet", Get_Pet);
        Val.put("ATK_ITEM_ID", ATK_ITEM_ID);
        Val.put("DEF_ITEM_ID", DEF_ITEM_ID);
        Val.put("Mission_STATE", Mission_STATE);
        */
        isGetPet=false;
        if(playerData[3].equals("true"))
            isGetPet = true;
        hp = Integer.parseInt(playerData[1]);
        maxHP = Integer.parseInt(playerData[2]);
        GlobalResource.setMISSION_STATE(Integer.parseInt(playerData[6]));
        playerItems = new HashMap<Integer, PlayerItem>();

        playerEquipment = new HashMap<Integer, PlayerItem>();

        playerEquipment.put(Integer.parseInt(playerData[4]), new PlayerItem(Integer.parseInt(playerData[4]), 1));
        playerEquipment.put(Integer.parseInt(playerData[5]), new PlayerItem(Integer.parseInt(playerData[5]), 1));

        atkDmg = ItemDATA.getItemList().get(Integer.parseInt(playerData[4])).getAtkDMG();
        defDmg = ItemDATA.getItemList().get(Integer.parseInt(playerData[5])).getDefDMG();
    }

    public static boolean isIsGetPet() {
        return isGetPet;
    }

    public static void setIsGetPet(boolean isGetPet) {
        Player.isGetPet = isGetPet;
    }

    public static void removeMaterialRequired(ArrayList<MaterialRequired> t) {
        for (MaterialRequired t1 : t)
            GameGenerator.setPlayerItem(t1.getItemID(), t1.getQuantity(), false);
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

    int getDefEquipment() {
        int defID;
        if (Player.getPlayerEquipment().containsKey(ItemsID.DEF_NICE_SHIRT))
            defID = ItemsID.DEF_NICE_SHIRT;
        else
            defID = ItemsID.DEF_OLD_SHIRT;
        return defID;
    }

    int getAtkEquipment() {
        int atkID;
        if (Player.getPlayerEquipment().containsKey(ItemsID.ATK_SLINK_SHOT))
            atkID = ItemsID.ATK_SLINK_SHOT;
        else
            atkID = ItemsID.ATK_HAND;
        return atkID;
    }
}
