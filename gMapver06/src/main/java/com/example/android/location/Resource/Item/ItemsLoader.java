package com.example.android.location.Resource.Item;

import com.example.android.location.R;

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
        itemList.put(ItemsID.GOLD,new ItemDetail(ItemsID.GOLD,"Gold","", R.drawable.icon_gold));
        itemList.put(ItemsID.GRASS,new ItemDetail(ItemsID.GRASS,"Grass","",R.drawable.icon_grass3));
        itemList.put(ItemsID.ORE,new ItemDetail(ItemsID.ORE,"Ore","",R.drawable.icon_ore3));
    }

    public static HashMap<Integer, ItemDetail> getItemList() {
        return itemList;
    }

}
