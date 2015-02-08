package com.example.android.location.GameManagement;

import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.android.location.Interface.CraftMissionAdapter;
import com.example.android.location.R;
import com.example.android.location.Resource.CraftMission;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.MaterialRequired;
import com.example.android.location.Resource.ItemsID;

import java.util.ArrayList;

/**
 * Created by Adisorn on 2/7/2015.
 */
public class StoreManager {
    private Context context;
    ArrayList<CraftMission> craftMissionList;
    public StoreManager(Context context) {
        this.context = context;
        initUI();
    }

    void initUI(){
        // get the listview
        final View motherView=GlobalResource.getListOfViews().get(3);
        final View storeCraftLayout= GlobalResource.getListOfViews().get(4);

        ExpandableListView expListView = (ExpandableListView) storeCraftLayout.findViewById(R.id.expandableListView);
        createCraftMission();
        CraftMissionAdapter listAdapter = new CraftMissionAdapter(context, craftMissionList);
        expListView.setAdapter(listAdapter);

        View storeCraftInterface=motherView.findViewById(R.id.store_craft);
        final View storeCraftCancel=motherView.findViewById(R.id.store_cancle);
        View craftRecipe=storeCraftLayout.findViewById(R.id.craft_recipe_cancle);
        craftRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeCraftLayout.setVisibility(View.GONE);
            }
        });
        storeCraftCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                motherView.setVisibility(View.GONE);
            }
        });
        storeCraftInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeCraftLayout.setVisibility(View.VISIBLE);
            }
        });


    }


    void createCraftMission(){
        craftMissionList=new ArrayList<CraftMission>();
        ArrayList<MaterialRequired> materialRequiredList=new ArrayList<MaterialRequired>();
        ArrayList<MaterialRequired> materialRequiredList2=new ArrayList<MaterialRequired>();
        MaterialRequired item1=new MaterialRequired(ItemsID.ORE,3);
        MaterialRequired item2=new MaterialRequired(ItemsID.GOLD,1000);
        MaterialRequired item4=new MaterialRequired(ItemsID.GOLD,1500);
        MaterialRequired item3=new MaterialRequired(ItemsID.GRASS,2);
        materialRequiredList.add(item1);
        materialRequiredList.add(item2);
        materialRequiredList.add(item3);
        materialRequiredList2.add(item1);
        materialRequiredList2.add(item4);
        craftMissionList.add(new CraftMission(0,"Craft1",materialRequiredList,"nothing",R.drawable.bag_64px));
        craftMissionList.add(new CraftMission(0,"Craft2",materialRequiredList2,"nothing",R.drawable.book_64px));
        craftMissionList.add(new CraftMission(0,"Craft3",materialRequiredList,"nothing",R.drawable.browser_64px));
        GlobalResource.setCraftMissionList(craftMissionList);
    }
}
