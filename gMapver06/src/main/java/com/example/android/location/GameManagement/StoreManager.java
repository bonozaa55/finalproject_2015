package com.example.android.location.GameManagement;

import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.Interface.CraftMissionAdapter;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDATA;
import com.example.android.location.Resource.Item.ItemDetail;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Mission.CraftMission;
import com.example.android.location.Resource.Mission.MaterialRequired;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Util.Constants;

import java.util.ArrayList;

/**
 * Created by Adisorn on 2/7/2015.
 */
public class StoreManager {
    ArrayList<CraftMission> craftMissionList;
    final MyItemManager mItemManager;
    private Context context;
    private static TextView textPlayerGold;
    private static TextView playerPotionItem;

    public StoreManager(Context context, MyItemManager mItemManager) {
        this.mItemManager = mItemManager;
        this.context = context;
        initUI();
    }

    public static TextView getPlayerPotionItem() {
        return playerPotionItem;
    }

    public static TextView getTextPlayerGold() {
        return textPlayerGold;
    }

    void initUI() {
        // get the listview
        final View storeLayout = GlobalResource.getListOfViews().get(Constants.STORE_LAYOUT);
        final View storeCraftLayout = GlobalResource.getListOfViews().get(Constants.CRAFT_RECIPE_LAYOUT);
        final View buyingPotionLayout = GlobalResource.getListOfViews().get(Constants.BUY_POTION_LAYOUT);
        final View myEquipmentLayout = GlobalResource.getListOfViews().get(Constants.MY_EQUIPMENT_LAYOUT);

        ExpandableListView expListView = (ExpandableListView) storeCraftLayout.findViewById(R.id.expandableListView);
        createCraftMission();
        CraftMissionAdapter listAdapter = new CraftMissionAdapter(context, craftMissionList);
        expListView.setAdapter(listAdapter);
        final View storeCraftCancel = storeLayout.findViewById(R.id.store_cancle);
        storeCraftCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeLayout.setVisibility(View.GONE);
                mItemManager.setOnStore(false);
            }
        });
        playerPotionItem = (TextView) GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT)
                .findViewById(R.id.overlay_potions_count);
        final int playerGold = GameGenerator.getPlayerItemQuantity(ItemsID.GOLD);
        textPlayerGold = (TextView) storeLayout.findViewById(R.id.store_gold);
        textPlayerGold.setText(playerGold + "");

        View craftRecipe = storeCraftLayout.findViewById(R.id.craft_recipe_cancle);
        craftRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeCraftLayout.setVisibility(View.GONE);
            }
        });
        View buyingPotion = storeLayout.findViewById(R.id.store_buy_potion);
        View buyingCancel = buyingPotionLayout.findViewById(R.id.buying_cancel);
        final NumberPicker buyingNumber = (NumberPicker) buyingPotionLayout.findViewById(R.id.buying_number);
        final TextView buyingPrice = (TextView) buyingPotionLayout.findViewById(R.id.buying_price);
        View buyingOk = buyingPotionLayout.findViewById(R.id.selling_ok);
        View buyingCancelOrder = buyingPotionLayout.findViewById(R.id.buying_cancle_order);

        buyingNumber.setMaxValue(99);
        buyingNumber.setMinValue(0);
        buyingNumber.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        buyingNumber.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int value = newVal;
                int playerGold = GameGenerator.getPlayerItemQuantity(ItemsID.GOLD);
                if (newVal * 100 > playerGold) {
                    picker.setValue(oldVal);
                    value = oldVal;
                }
                buyingPrice.setText((value * 100) + "");
            }
        });
        buyingNumber.setValue(0);
        buyingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyingPotionLayout.setVisibility(View.GONE);
            }
        });
        buyingPotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyingPotionLayout.setVisibility(View.VISIBLE);
            }
        });
        buyingOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int playerPotion = GameGenerator.getPlayerItemQuantity(ItemsID.POTION) + buyingNumber.getValue();
                int playerGold = GameGenerator.getPlayerItemQuantity(ItemsID.GOLD) - (buyingNumber.getValue() * 100);
                GameGenerator.setPlayerItem(ItemsID.POTION,buyingNumber.getValue(),false);
                GameGenerator.setPlayerItem(ItemsID.GOLD,- (buyingNumber.getValue() * 100),false);
                textPlayerGold.setText(playerGold + "");
                playerPotionItem.setText(playerPotion + "");
                buyingNumber.setValue(0);
                buyingPrice.setText(0 + "");
                buyingPotionLayout.setVisibility(View.GONE);
            }
        });
        buyingCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyingPotionLayout.setVisibility(View.GONE);
                buyingNumber.setValue(0);
            }
        });

        //Upgrade
        final View upgradeAtk = myEquipmentLayout.findViewById(R.id.my_equip_cost_atk_layout);
        final View upgradeDef = myEquipmentLayout.findViewById(R.id.my_equip_cost_def_layout);
        final View playerGoldLayout = myEquipmentLayout.findViewById(R.id.my_equip_gold_layout);
        final TextView playerGoldTextView = (TextView) myEquipmentLayout.findViewById(R.id.my_equip_gold);

        storeLayout.findViewById(R.id.store_upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeAtk.setVisibility(View.VISIBLE);
                upgradeDef.setVisibility(View.VISIBLE);
                playerGoldLayout.setVisibility(View.VISIBLE);
                myEquipmentLayout.setVisibility(View.VISIBLE);
                playerGoldTextView.setText(Player.getPlayerItems().get(ItemsID.GOLD).getQuantity() + "");
                generateAtkUpgradeCost();
                generateDefUpgradeCost();
                mItemManager.updateEquipmentDATA();
            }
        });
        myEquipmentLayout.findViewById(R.id.myequip_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeAtk.setVisibility(View.GONE);
                upgradeDef.setVisibility(View.GONE);
                playerGoldLayout.setVisibility(View.GONE);
                myEquipmentLayout.setVisibility(View.GONE);
            }
        });

        upgradeAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerGold(playerGoldTextView, generateAtkUpgradeCost(), getAtkID());
                mItemManager.updateEquipmentDATA();
                generateAtkUpgradeCost();
            }
        });

        upgradeDef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerGold(playerGoldTextView, generateDefUpgradeCost(), getDefID());
                mItemManager.updateEquipmentDATA();
                generateDefUpgradeCost();
            }
        });

        //sell item
        final View myItemLayout = GlobalResource.getListOfViews().get(Constants.MY_ITEMS_LAYOUT);
        storeLayout.findViewById(R.id.store_sell_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemLayout.setVisibility(View.VISIBLE);
                mItemManager.updateGridItem();
                mItemManager.setOnStore(true);
            }
        });
    }

    int getAtkID() {
        int atkID;
        if (Player.getPlayerEquipment().containsKey(ItemsID.ATK_HAND))
            atkID = ItemsID.ATK_HAND;
        else
            atkID = ItemsID.ATK_SLINK_SHOT;
        return atkID;
    }

    int getDefID() {
        int defID;
        if (Player.getPlayerEquipment().containsKey(ItemsID.DEF_OLD_SHIRT))
            defID = ItemsID.DEF_OLD_SHIRT;
        else
            defID = ItemsID.DEF_NICE_SHIRT;
        return defID;
    }

    int generateAtkUpgradeCost() {
        ItemDetail t = ItemDATA.getItemList().get(getAtkID());
        int cost = 0;
        if (t.getLv() < 5)
            cost = t.getInitialCost() + (int) (37 * Math.pow(t.getLv(), 2));
        View myEquipmentLayout = GlobalResource.getListOfViews().get(Constants.MY_EQUIPMENT_LAYOUT);
        ((TextView) myEquipmentLayout.findViewById(R.id.my_equip_cost_atk)).setText("(" + cost + ")");
        return -cost;
    }

    int generateDefUpgradeCost() {
        ItemDetail t = ItemDATA.getItemList().get(getDefID());
        int cost = 0;
        if (t.getLv() < 5)
            cost = t.getInitialCost() + (int) (37 * Math.pow(t.getLv(), 2));
        View myEquipmentLayout = GlobalResource.getListOfViews().get(Constants.MY_EQUIPMENT_LAYOUT);
        ((TextView) myEquipmentLayout.findViewById(R.id.my_equip_cost_def)).setText("(" + cost + ")");
        return -cost;
    }

    void updatePlayerGold(TextView v, int cost, int ID) {
        int playerGold = Player.getPlayerItems().get(ItemsID.GOLD).getQuantity() + cost;
        if (playerGold >= 0) {
            GameGenerator.setPlayerItem(ItemsID.GOLD, cost, false);
            v.setText(playerGold + "");
            ItemDetail t = ItemDATA.getItemList().get(ID);
            if (t.getLv() <= 4) {
                int lv=t.getLv() + 1;
                t.setLv(lv);
                if (t.getAtkDMG() > 0)
                    t.setAtkDMG(t.getAtkDMG() + 5);
                else
                    t.setDefDMG(t.getDefDMG() + 5);
                Player.setAtkDmg(t.getAtkDMG());
                Player.setDefDmg(t.getDefDMG());
                MainActivity.getPlayerDB().updatePlayerEquipmentData(ID+"",lv+"",Player.getAtkDmg()+""
                        ,Player.getDefDmg()+"",cost+"");
            }
        }
    }

    void createCraftMission() {
        craftMissionList = new ArrayList<CraftMission>();
        ArrayList<MaterialRequired> materialRequiredList = new ArrayList<MaterialRequired>();
        ArrayList<MaterialRequired> materialRequiredList2 = new ArrayList<MaterialRequired>();


        materialRequiredList.add(new MaterialRequired(ItemsID.ORE, 3));
        materialRequiredList.add(new MaterialRequired(ItemsID.GOLD, 1500));
        materialRequiredList.add(new MaterialRequired(ItemsID.GRASS, 2));

        materialRequiredList2.add(new MaterialRequired(ItemsID.GOLD, 1500));
        materialRequiredList2.add(new MaterialRequired(ItemsID.EGG, 3));
        materialRequiredList2.add(new MaterialRequired(ItemsID.MUMMY_PIECE, 5));
        craftMissionList.add(new CraftMission(0, "หนังสติ๊กทรงพลัง", materialRequiredList, "หนังสติ๊กที่ทำจากหญ้าหลอม " +
                "และหินแกรนิตจึงทำให้มีความแข็งแรงและทนทาน แถมยังมีพลังทำลายล้างสูง ,ATK+50", R.drawable.stick));
        craftMissionList.add(new CraftMission(0, "เสื้อจอมยุทธ", materialRequiredList2, "สุดยอดเสื้อที่ทำจากไข่ลึกลับและวิญญาณของมัมมี่"
                , R.drawable.armor_icon1));
        GlobalResource.setCraftMissionList(craftMissionList);
    }
}
