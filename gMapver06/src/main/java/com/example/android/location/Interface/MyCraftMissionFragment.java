/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.location.Interface;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDetail;
import com.example.android.location.Resource.Item.ItemsLoader;
import com.example.android.location.Resource.Mission.CraftMission;
import com.example.android.location.Resource.Mission.MaterialRequired;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Resource.Player.PlayerItem;

import java.util.ArrayList;
import java.util.HashMap;


public class MyCraftMissionFragment extends android.support.v4.app.Fragment {
    public static final String ARG_PAGE = "page";
    private int mPageNumber;

    public MyCraftMissionFragment() {
    }

    public static MyCraftMissionFragment create(int pageNumber) {
        MyCraftMissionFragment fragment = new MyCraftMissionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static void UpdateUI(CraftMission craftMission, View rootView) {

        LinearLayout requiredLayout = (LinearLayout) rootView.findViewById(R.id.mycraft_require_list);
        requiredLayout.removeAllViews();
        ArrayList<MaterialRequired> itemsRequired = craftMission.getMaterialRequireList();
        HashMap<Integer, PlayerItem> playerItemHashMap = Player.getPlayerItems();
        int complete=3;
        for (MaterialRequired t : itemsRequired) {
            ItemDetail item = ItemsLoader.getItemList().get(t.getItemID());
            PlayerItem temp = playerItemHashMap.get(t.getItemID());
            TextView tv = new TextView(MainActivity.getThisContext());
            int playerItem = 0;
            if (temp != null) {
                playerItem = temp.getQuantity();
            }
            tv.setTextColor(Color.BLACK);
            if (playerItem >= t.getQuantity())
                tv.setText("\t\t" + item.getName());
            else {
                complete=2;
                tv.setText("\t\t" + item.getName() + "(" + (t.getQuantity() - playerItem) + ")");
                tv.setTextColor(tv.getTextColors().withAlpha(128));
            }
            requiredLayout.addView(tv);
        }
        craftMission.setMissionStatus(complete);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.mycraft_item, container, false);
        // Set the title view to show the page number.
        final CraftMission craftMission = MyCraftMissionManager.getMyCraftMissionList().get(mPageNumber);
        TextView headerTitle = (TextView) rootView.findViewById(R.id.mycraft_title);
        TextView detailTitle = (TextView) rootView.findViewById(R.id.mycraft_title2);
        TextView detail = (TextView) rootView.findViewById(R.id.mycraft_detail);
        ImageView img = (ImageView) rootView.findViewById(R.id.mycraft_img);
        View getItButton = rootView.findViewById(R.id.mycraft_get_it);
        UpdateUI(craftMission, rootView);

        detail.setText(craftMission.getProperty());
        headerTitle.setText(craftMission.getName());
        detailTitle.setText(craftMission.getName());
        img.setImageResource(craftMission.getImgResource());
        getItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUI(craftMission,rootView);
                /*
                if (craftMission.getMissionStatus()==3) {
                    Player.setAtkDmg(50);
                    MainActivity.makeToast("Crafting Successful!");

                } else {
                    MainActivity.makeToast("Go out and find materials!!");
                }*/
            }
        });

        rootView.findViewById(R.id.mycraft_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalResource.getListOfViews().get(2).setVisibility(View.GONE);
            }
        });
        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */

}
