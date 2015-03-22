package com.example.android.location.Resource.Item;

import com.example.android.location.R;

import java.util.HashMap;

/**
 * Created by Adisorn on 2/8/2015.
 */
public class ItemDATA {
    public static HashMap<Integer,ItemDetail> itemList;

    public ItemDATA() {
        assignItemData();
    }

    void assignItemData(){
        itemList=new HashMap<Integer, ItemDetail>();
        itemList.put(ItemsID.GOLD,new ItemDetail(ItemsID.GOLD,"Gold","", R.drawable.icon_gold));
        itemList.put(ItemsID.GRASS,new ItemDetail(ItemsID.GRASS,"Grass","",R.drawable.icon_grass3));
        itemList.put(ItemsID.ORE,new ItemDetail(ItemsID.ORE,"Ore","",R.drawable.icon_ore3));
        itemList.put(ItemsID.FISH,new ItemDetail(ItemsID.FISH,"Fish","",R.drawable.fish_icon));
        itemList.put(ItemsID.FISHING_LINE,new ItemDetail(ItemsID.FISHING_LINE,"Fishing Line","",R.drawable.fishing_line));
        itemList.put(ItemsID.POTION,new ItemDetail(ItemsID.POTION,"Red Potion","",R.drawable.potion_icon));
        itemList.put(ItemsID.ATK_HAND,new ItemDetail(ItemsID.ATK_HAND,"มือเปล่า","อุปกรณ์โจมตีที่ติดตัวผู้เล่นมาตั้งแต่กำเนิด"
                ,R.drawable.hand_icon,10,0,200));
        itemList.put(ItemsID.ATK_SLINK_SHOT,new ItemDetail(ItemsID.ATK_SLINK_SHOT,"หนังสติ๊กทรงพลัง"
                ,"อุปกรณ์โจมตีระยะไกลที่ทำจากวัสดุหายาก จึงทำให้มีความรุนแรงในการโจมตีสูง",R.drawable.stick,50,0,500));
        itemList.put(ItemsID.DEF_OLD_SHIRT,new ItemDetail(ItemsID.DEF_OLD_SHIRT,"เสื้อตัวเก่า"
                ,"อุปกรณป้องกันที่ติดตัวผู้เล่นมาตั้งแต่กำเนิด",R.drawable.icon_heart,0,10,200));
        itemList.put(ItemsID.DEF_NICE_SHIRT,new ItemDetail(ItemsID.DEF_NICE_SHIRT,"เสื้อจอมยุทธ"
                ,"อุปกรณป้องกันที่ได้รับจากภารกิจซึ่งมีความทนทานสูง",R.drawable.armor_icon1,0,50,500));
        itemList.put(ItemsID.EGG,new ItemDetail(ItemsID.EGG,"Egg","",R.drawable.egg));
        itemList.put(ItemsID.MUMMY_PIECE,new ItemDetail(ItemsID.MUMMY_PIECE,"Mummy soul","",R.drawable.mummy));
    }

    public static HashMap<Integer, ItemDetail> getItemList() {
        return itemList;
    }

}
