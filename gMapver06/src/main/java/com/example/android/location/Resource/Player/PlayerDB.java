package com.example.android.location.Resource.Player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.android.location.Resource.GlobalResource;

import java.util.HashMap;

/**
 * Created by Adisorn on 3/30/2015.
 */
public class PlayerDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB1";
    private static final String TABLE_PLAYER_ITEM = "Player_Item";
    private static final String TABLE_PLAYER_INFO = "Player_Info";
    private static final String TABLE_EQUIPMENT = "Equipment_info";

    public PlayerDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL("CREATE TABLE " + TABLE_PLAYER_ITEM +
                "(ItemID INTEGER PRIMARY KEY," +
                " Quantity INTEGER);");

        db.execSQL("CREATE TABLE " + TABLE_PLAYER_INFO +
                "(PlayerID INTEGER PRIMARY KEY," +
                " Remaining_HP INTEGER," +
                " Max_HP INTEGER," +
                " Get_Pet TEXT(100)," +
                " ATK_ITEM_ID INTEGER," +
                " DEF_ITEM_ID INTEGER," +
                " Mission_STATE INTEGER" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_EQUIPMENT +
                "(Equipment_ID INTEGER PRIMARY KEY," +
                " LV INTEGER," +
                " ATK_DMG INTEGER," +
                " DEF_DMG INTEGER," +
                " Initial_Cost INTEGER" +
                ");");
        Log.d("CREATE TABLE", "Create Table Successfully.");
    }

    public long updatePlayerData() {
        try {
            String PlayerID = "1";
            String Remaining_HP = Player.getHp() + "";
            String Max_HP = Player.getMaxHP() + "";
            String Get_Pet = Player.isGetPet + "";
            String Mission_STATE = GlobalResource.getMISSION_STATE() + "";
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data
            ContentValues Val = new ContentValues();
            Val.put("PlayerID", PlayerID);
            Val.put("Remaining_HP", Remaining_HP);
            Val.put("Max_HP", Max_HP);
            Val.put("Get_Pet", Get_Pet);
            Val.put("Mission_STATE", Mission_STATE);
            long rows = db.update(TABLE_PLAYER_INFO, Val, " PlayerID = ?",
                    new String[]{String.valueOf(PlayerID)});
            db.close();
            return rows; // return rows updated.

        } catch (Exception e) {
            return -1;
        }
    }

    public long resetAllPlayerItem(){
        try {
            SQLiteStatement insertCmd;
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            String strSQL = "UPDATE " + TABLE_PLAYER_ITEM+ " SET Quantity = 0";
            db.execSQL(strSQL);
            return 1;
        }catch (Exception e){
            return -1;
        }

    }

    public long updatePlayerEquipment(String ATK_ITEM_ID, String DEF_ITEM_ID) {
        try {
            String PlayerID = "1";
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data
            ContentValues Val = new ContentValues();
            Val.put("PlayerID", PlayerID);
            Val.put("ATK_ITEM_ID", ATK_ITEM_ID);
            Val.put("DEF_ITEM_ID", DEF_ITEM_ID);
            long rows = db.update(TABLE_PLAYER_INFO, Val, " PlayerID = ?",
                    new String[]{String.valueOf(PlayerID)});
            db.close();
            return rows; // return rows updated.

        } catch (Exception e) {
            return -1;
        }
    }

    public long insertPlayerData(String PlayerID, String Remaining_HP, String Max_HP, String Get_Pet
            , String ATK_ITEM_ID, String DEF_ITEM_ID, String Mission_STATE) {
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            ContentValues Val = new ContentValues();
            Val.put("PlayerID", PlayerID);
            Val.put("Remaining_HP", Remaining_HP);
            Val.put("Max_HP", Max_HP);
            Val.put("Get_Pet", Get_Pet);
            Val.put("ATK_ITEM_ID", ATK_ITEM_ID);
            Val.put("DEF_ITEM_ID", DEF_ITEM_ID);
            Val.put("Mission_STATE", Mission_STATE);
            long rows = db.insert(TABLE_PLAYER_INFO, null, Val);
            db.close();
            return rows; // return rows inserted.

        } catch (Exception e) {
            return -1;
        }
    }

    public String[] SelectPlayerData() {
        try {
            String PlayerID = "1";
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data
            Cursor cursor = db.query(TABLE_PLAYER_INFO, new String[]{"*"},
                    "PlayerID=?",
                    new String[]{String.valueOf(PlayerID)}, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getColumnCount()];
                    arrData[0] = cursor.getString(0);
                    arrData[1] = cursor.getString(1);
                    arrData[2] = cursor.getString(2);
                    arrData[3] = cursor.getString(3);
                    arrData[4] = cursor.getString(4);
                    arrData[5] = cursor.getString(5);
                    arrData[6] = cursor.getString(6);
                }
            }
            cursor.close();
            db.close();
            return arrData;

        } catch (Exception e) {
            return null;
        }
    }

    public long updatePlayerItem(String ITEM_ID, String Quantity) {
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data
            ContentValues Val = new ContentValues();
            Val.put("ItemID", ITEM_ID);
            Val.put("Quantity", Quantity);
            long rows = db.update(TABLE_PLAYER_ITEM, Val, " ItemID = ?",
                    new String[]{String.valueOf(ITEM_ID)});
            db.close();
            return rows; // return rows updated.

        } catch (Exception e) {
            return -1;
        }
    }

    public long insertPlayerItem(String ItemID, String Quantity) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            ContentValues Val = new ContentValues();
            Val.put("ItemID", ItemID);
            Val.put("Quantity", Quantity);
            long rows = db.insert(TABLE_PLAYER_ITEM, null, Val);
            db.close();
            return rows; // return rows inserted.

        } catch (Exception e) {
            return -1;
        }
    }

    public HashMap<Integer, PlayerItem> selectAllPlayerItems() {
        try {
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase();
            String strSQL = "SELECT  * FROM " + TABLE_PLAYER_ITEM;
            Cursor cursor = db.rawQuery(strSQL, null);
            HashMap<Integer, PlayerItem> playerItemHashMap = new HashMap<Integer, PlayerItem>();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        int itemID = Integer.parseInt(cursor.getString(0));
                        int quantity = Integer.parseInt(cursor.getString(1));
                        if (quantity > 0) {
                            PlayerItem playerItem = new PlayerItem(itemID, quantity);
                            playerItemHashMap.put(itemID, playerItem);
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            db.close();
            return playerItemHashMap;

        } catch (Exception e) {
            return null;
        }
    }

    public long insertPlayerEquipment(String Equipment_ID, String LV,String ATK_DMG,String DEF_DMG,String Initial_Cost) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            ContentValues Val = new ContentValues();
            Val.put("Equipment_ID", Equipment_ID);
            Val.put("LV", LV);
            Val.put("ATK_DMG", ATK_DMG);
            Val.put("DEF_DMG", DEF_DMG);
            Val.put("Initial_Cost", Initial_Cost);
            long rows = db.insert(TABLE_EQUIPMENT, null, Val);
            db.close();
            return rows; // return rows inserted.

        } catch (Exception e) {
            return -1;
        }
    }

    public long updatePlayerEquipmentData(String Equipment_ID, String LV,String ATK_DMG,String DEF_DMG,String Initial_Cost) {
        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data
            ContentValues Val = new ContentValues();
            Val.put("Equipment_ID", Equipment_ID);
            Val.put("LV", LV);
            Val.put("ATK_DMG", ATK_DMG);
            Val.put("DEF_DMG", DEF_DMG);
            Val.put("Initial_Cost", Initial_Cost);
            long rows = db.update(TABLE_EQUIPMENT, Val, " Equipment_ID = ?",
                    new String[]{String.valueOf(Equipment_ID)});
            db.close();
            return rows; // return rows updated.

        } catch (Exception e) {
            return -1;
        }
    }

    public String[] selectPlayerEquipmentData(String Equipment_ID) {
        try {
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data
            Cursor cursor = db.query(TABLE_EQUIPMENT, new String[]{"*"},
                    "Equipment_ID=?",
                    new String[]{String.valueOf(Equipment_ID)}, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getColumnCount()];
                    arrData[0] = cursor.getString(0);
                    arrData[1] = cursor.getString(1);
                    arrData[2] = cursor.getString(2);
                    arrData[3] = cursor.getString(3);
                    arrData[4] = cursor.getString(4);
                }
            }
            cursor.close();
            db.close();
            return arrData;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }


}
