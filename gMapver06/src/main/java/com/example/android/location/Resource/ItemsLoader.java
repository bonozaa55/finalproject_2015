package com.example.android.location.Resource;

import java.util.HashMap;

/**
 * Created by Adisorn on 2/8/2015.
 */
public class ItemsLoader {
    public static HashMap<Integer,ItemDetail> itemList;

    public ItemsLoader() {
        assignItemData();
    }

    void assignItemData(){
        itemList=new HashMap<Integer, ItemDetail>();
        itemList.put(ItemsID.GOLD,new ItemDetail(ItemsID.GOLD,"Gold",""));
        itemList.put(ItemsID.GRASS,new ItemDetail(ItemsID.GRASS,"Grass",""));
        itemList.put(ItemsID.ORE,new ItemDetail(ItemsID.ORE,"Ore",""));
    }

    public static HashMap<Integer, ItemDetail> getItemList() {
        return itemList;
    }

    public static void setItemList(HashMap<Integer, ItemDetail> itemList) {
        ItemsLoader.itemList = itemList;
    }
}
