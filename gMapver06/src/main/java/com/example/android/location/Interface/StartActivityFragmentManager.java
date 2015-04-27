package com.example.android.location.Interface;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.example.android.location.Activity.LocationActivity;
import com.example.android.location.Activity.MarkerActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.Mission.CraftMission;

import java.util.ArrayList;

/**
 * Created by Adisorn on 4/6/2015.
 */
public class StartActivityFragmentManager {
    private static ViewPager mPager;
    private static StartActivityFragmentAdapter mPagerAdapter;
    public static final int MARKER_INTENT=0;
    public static final int LOCATION_INTENT=2;
    public static final String ACTION="action";
    static Context mContext;
    public StartActivityFragmentManager(View motherView, FragmentManager fm,Context mContext) {
        StartActivityFragmentManager.mContext=mContext;
        mPager = (ViewPager) motherView.findViewById(R.id.pager);
        mPagerAdapter = new StartActivityFragmentAdapter(fm);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        //ImageView t= (ImageView) motherView.findViewById(R.id.image);
       // mPager.setPageTransformer(false,new ParallaxPageTransformer(t));


    }

    public static void setAdapterPage(int page){
        mPager.setCurrentItem(page,true);
    }

    public static void startActivity(int intent,int Arg){
        Intent t=new Intent(mContext, MarkerActivity.class);
        if(intent==LOCATION_INTENT)
            t=new Intent(mContext,LocationActivity.class);
        t.putExtra(ACTION,Arg);
        mContext.startActivity(t);

    }

    public class StartActivityFragmentAdapter extends FragmentStatePagerAdapter {
        int NUM_PAGES = 3;

        public StartActivityFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1)
                return new StartFragment();
            else if (position == MARKER_INTENT)
                return SecondFragment.create(MARKER_INTENT);
            else
                return SecondFragment.create(LOCATION_INTENT);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}



