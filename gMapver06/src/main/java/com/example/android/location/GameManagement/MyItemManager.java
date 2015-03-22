package com.example.android.location.GameManagement;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.Interface.MyItemGridAdapter;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDATA;
import com.example.android.location.Resource.Item.ItemDetail;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Resource.Player.PlayerItem;
import com.example.android.location.Util.Constants;

/**
 * Created by Adisorn on 3/21/2015.
 */
public class MyItemManager {
    PlayerItem myPlayerItem;
    boolean isOnStore;

    public MyItemManager() {
        initResource();
    }

    public void setOnStore(boolean isOnStore) {
        this.isOnStore = isOnStore;
    }

    void initResource() {
        initDATA();


        final View myItemLayout = GlobalResource.getListOfViews().get(Constants.MY_ITEMS_LAYOUT);
        View overlayLayout = GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT);
        final View myEquipmentLayout = GlobalResource.getListOfViews().get(Constants.MY_EQUIPMENT_LAYOUT);


        GridView myItemGrid = (GridView) myItemLayout.findViewById(R.id.myitem_grid);
        final MyItemGridAdapter gridAdapter = new MyItemGridAdapter(MainActivity.getActivityContext());

        myItemGrid.setAdapter(gridAdapter);
        overlayLayout.findViewById(R.id.overlay_myItem_interface).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridAdapter.updatePlayerItem();
                myItemLayout.setVisibility(View.VISIBLE);
            }
        });
        myItemLayout.findViewById(R.id.myitem_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemLayout.setVisibility(View.GONE);
            }
        });

        //equipment
        overlayLayout.findViewById(R.id.overlay_equipment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEquipmentLayout.setVisibility(View.VISIBLE);
                updateEquipmentDATA();
            }
        });
        myEquipmentLayout.findViewById(R.id.myequip_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEquipmentLayout.setVisibility(View.GONE);
            }
        });

        //selling
        final View sellingItemLayout = GlobalResource.getListOfViews().get(Constants.SELL_ITEM_LAYOUT);
        final TextView price = (TextView) sellingItemLayout.findViewById(R.id.selling_price);
        final ImageView icon = (ImageView) sellingItemLayout.findViewById(R.id.selling_icon);
        final NumberPicker numberPicker = (NumberPicker) sellingItemLayout.findViewById(R.id.selling_number);
        final View sellingOk = sellingItemLayout.findViewById(R.id.selling_ok);
        final View sellingCancel = sellingItemLayout.findViewById(R.id.selling_cancle_order);

        sellingItemLayout.findViewById(R.id.selling_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellingItemLayout.setVisibility(View.GONE);

            }
        });
        myItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isOnStore) {
                    myPlayerItem = gridAdapter.getItem(position);
                    icon.setImageResource(ItemDATA.getItemList().get(myPlayerItem.getId()).getIconResource());
                    numberPicker.setMaxValue(myPlayerItem.getQuantity());
                    numberPicker.setMinValue(1);
                    numberPicker.setValue(1);
                    price.setText("100");
                    sellingItemLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int value = newVal;
                int playerItemQuantity = myPlayerItem.getQuantity();
                if (newVal > playerItemQuantity) {
                    picker.setValue(oldVal);
                    value = oldVal;
                }
                price.setText((value * 100) + "");
            }
        });

        sellingOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemQuantity = myPlayerItem.getQuantity();
                int pickValue = numberPicker.getValue();
                GameGenerator.setPlayerItem(ItemsID.GOLD, pickValue * 100, false);
                if (itemQuantity == numberPicker.getValue())
                    Player.getPlayerItems().remove(myPlayerItem.getId());
                else
                    GameGenerator.setPlayerItem(myPlayerItem.getId(), -pickValue, false);
                gridAdapter.updatePlayerItem();
                sellingItemLayout.setVisibility(View.GONE);
                StoreManager.getTextPlayerGold().setText(Player.getPlayerItems().get(ItemsID.GOLD).getQuantity()+"");
                int potionRemain=GameGenerator.getPlayerItemQuantity(ItemsID.POTION);
                if(myPlayerItem.getId()==ItemsID.POTION)
                    StoreManager.getPlayerPotionItem().setText(potionRemain+"");

            }
        });
        sellingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellingItemLayout.setVisibility(View.GONE);

            }
        });
    }

    void updateEquipmentDATA() {
        View myEquipmentLayout = GlobalResource.getListOfViews().get(Constants.MY_EQUIPMENT_LAYOUT);
        TextView atkName = (TextView) myEquipmentLayout.findViewById(R.id.my_equip_atk_name);
        TextView atkDetail = (TextView) myEquipmentLayout.findViewById(R.id.my_equip_atk_detail);
        TextView atkDmg = (TextView) myEquipmentLayout.findViewById(R.id.my_equip_atk_dmg);
        ImageView atkIcon = (ImageView) myEquipmentLayout.findViewById(R.id.my_equip_atk_icon);
        TextView defName = (TextView) myEquipmentLayout.findViewById(R.id.my_equip_def_name);
        TextView defDetail = (TextView) myEquipmentLayout.findViewById(R.id.my_equip_def_detail);
        TextView defDmg = (TextView) myEquipmentLayout.findViewById(R.id.my_equip_def_dmg);
        ImageView defIcon = (ImageView) myEquipmentLayout.findViewById(R.id.my_equip_def_icon);


        ItemDetail defItem = ItemDATA.getItemList().get(getDefEquipment());
        ItemDetail atkItem = ItemDATA.getItemList().get(getAtkEquipment());
        atkName.setText(atkItem.getName());
        atkDetail.setText(atkItem.getDetail());
        atkDmg.setText(atkItem.getAtkDMG() + "(" + atkItem.getLv() + ")");
        atkIcon.setImageResource(atkItem.getIconResource());

        defName.setText(defItem.getName());
        defDetail.setText(defItem.getDetail());
        defDmg.setText(defItem.getDefDMG() + "(" + defItem.getLv() + ")");
        defIcon.setImageResource(defItem.getIconResource());
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
        if (Player.getPlayerEquipment().containsKey(ItemsID.ATK_HAND))
            atkID = ItemsID.ATK_HAND;
        else
            atkID = ItemsID.ATK_SLINK_SHOT;
        return atkID;
    }

    void initDATA() {
        ItemDATA itemsData = new ItemDATA();
        Player player = new Player();
        //Player.setAtkDmg(50);
        Player.getPlayerItems().put(ItemsID.GRASS, new PlayerItem(ItemsID.GRASS, 3));
        Player.getPlayerItems().put(ItemsID.ORE, new PlayerItem(ItemsID.ORE, 3));
        Player.getPlayerItems().put(ItemsID.POTION, new PlayerItem(ItemsID.POTION, 10));
        Player.getPlayerItems().put(ItemsID.GOLD, new PlayerItem(ItemsID.GOLD, 7500));
        Player.getPlayerItems().put(ItemsID.FISH, new PlayerItem(ItemsID.FISH, 15));
        Player.getPlayerItems().put(ItemsID.MUMMY_PIECE, new PlayerItem(ItemsID.MUMMY_PIECE, 5));
        Player.getPlayerItems().put(ItemsID.EGG, new PlayerItem(ItemsID.EGG, 3));


        Player.getPlayerEquipment().put(ItemsID.DEF_OLD_SHIRT, new PlayerItem(ItemsID.DEF_OLD_SHIRT, 1));
        Player.getPlayerEquipment().put(ItemsID.ATK_HAND, new PlayerItem(ItemsID.ATK_HAND, 1));
        Player.setAtkDmg(ItemDATA.getItemList().get(getAtkEquipment()).getAtkDMG());
        Player.setDefDmg(ItemDATA.getItemList().get(getDefEquipment()).getDefDMG());

    }
}
