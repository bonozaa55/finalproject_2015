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

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.android.location.R;
import com.example.android.location.Resource.Mission.CraftMission;

import java.util.ArrayList;

/**
 * Demonstrates a "screen-slide" animation using a {@link android.support.v4.view.ViewPager}. Because {@link android.support.v4.view.ViewPager}
 * automatically plays such an animation when calling {@link android.support.v4.view.ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see MyCraftMissionFragment
 */
public class MyCraftMissionManager {
    private ViewPager mPager;
    private static ScreenSlidePagerAdapter mPagerAdapter;
    private static ArrayList<CraftMission> myCraftMissionList;

    public MyCraftMissionManager(View motherView, FragmentManager fm) {
        myCraftMissionList=new ArrayList<CraftMission>();
        mPager = (ViewPager) motherView.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(fm);
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MyCraftMissionFragment.UpdateUI(myCraftMissionList.get(position), mPager.getRootView());
            }
        });
    }

    public static ArrayList<CraftMission> getMyCraftMissionList() {
        return myCraftMissionList;
    }

    public static void setMyCraftMissionList(ArrayList<CraftMission> myCraftMissionList) {
        mPagerAdapter.notifyDataSetChanged();
        MyCraftMissionManager.myCraftMissionList = myCraftMissionList;
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }



        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            /*if(position==1)
                //do stuff
            */
            return MyCraftMissionFragment.create(position);
        }

        @Override
        public int getCount() {
            return myCraftMissionList.size();
        }
    }
}
