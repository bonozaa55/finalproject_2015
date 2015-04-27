package com.example.android.location.GameManagement;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.android.location.Interface.PageIndicator;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Object.ObjectDATA;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Util.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 4/21/2015.
 */
public class InstructionManager {
    static int STATE = 0;
    PageIndicator mPageIndicator;
    ViewPager mViewPager;
    HashMap<Integer,Integer[]> mImageList;
    HashMap<Integer, Circle> mCircleHashMap;
    HashMap<Integer, Marker> mMarkerHashMap;
    HashMap<Integer, Polygon> mPolygonHashMap;
    GoogleMap mGoogleMap;
    Integer[] markerKeyList;

    public InstructionManager(GoogleMap map) {
        this.mGoogleMap = map;
        initResource();
    }

    public static int getSTATE() {
        return STATE;
    }

    public static void setSTATE(int STATE) {
        InstructionManager.STATE = STATE;
    }

    public void changeInstructionView(int STATE) {
        InstructionManager.STATE = STATE;
        mPageIndicator.setTotalNoOfDots(mImageList.get(STATE).length);
        mPageIndicator.setActiveDot(0);
        MyPageAdapter t=new MyPageAdapter();
        mViewPager.setAdapter(t);
        t.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);

    }

    public void highLightArea() {
        mPolygonHashMap.get(GlobalResource.STATE_LOCATIONBASED).setVisible(true);
        mPolygonHashMap.get(GlobalResource.STATE_MIST).setVisible(true);
        for (Map.Entry<Integer, Circle> t : mCircleHashMap.entrySet())
            t.getValue().setVisible(true);
        if (STATE == GlobalResource.STATE_LOCATIONBASED)
            mPolygonHashMap.get(GlobalResource.STATE_LOCATIONBASED).setVisible(true);
        else if (STATE == GlobalResource.STATE_AREA2) {
            mCircleHashMap.get(GlobalResource.STATE_FISHING);
            mCircleHashMap.get(GlobalResource.STATE_FISHING).setVisible(true);
            mCircleHashMap.get(GlobalResource.STATE_PETTING);
            mCircleHashMap.get(GlobalResource.STATE_PETTING).setVisible(true);
            mCircleHashMap.get(GlobalResource.STATE_SHOPPING);
            mCircleHashMap.get(GlobalResource.STATE_SHOPPING).setVisible(true);
            mPolygonHashMap.get(GlobalResource.STATE_MIST).setVisible(true);
        }else if (STATE == GlobalResource.STATE_MIST) {
            mPolygonHashMap.get(GlobalResource.STATE_MIST).setVisible(true);
        }
        else {
            mCircleHashMap.get(STATE).setVisible(true);
            mMarkerHashMap.get(STATE).setVisible(true);
        }
    }

    public void showMarker(int MISSION_STATE) {
        int size = 0;
        switch (MISSION_STATE) {
            case Mission_ONE.STATE_GET_MISSION:
                size = 0;
                setSTATE(GlobalResource.STATE_MISSION);
                highLightArea();
                break;
            case Mission_ONE.STATE_GO_TO_BOSS:
                size = 1;
                setSTATE(GlobalResource.STATE_MARKER);
                highLightArea();
                break;
            case Mission_ONE.STATE_GO_TO_HEAL:
                setSTATE(GlobalResource.STATE_HEALING);
                highLightArea();
                size = 2;
                break;
            case Mission_ONE.STATE_GO_TO_AREA1:
                setSTATE(GlobalResource.STATE_LOCATIONBASED);
                highLightArea();
                break;
            case Mission_ONE.STATE_GO_TO_AREA2_AND_FISHING:
                size = 5;
                setSTATE(GlobalResource.STATE_AREA2);
                highLightArea();
                break;
        }

        for (int i=0;i<=size;i++){
            mMarkerHashMap.get(markerKeyList[i]).setVisible(true);
        }
    }


    private void createMapObject() {

        HashMap<String, ObjectGroup> t1 = ObjectLoader.getObjectGroupList();
        for (Map.Entry<String, ObjectGroup> t : t1.entrySet()) {
            HashMap<String, ObjectDetail> t0 = t.getValue().getObjectDetailList();
            for (Map.Entry<String, ObjectDetail> t2 : t0.entrySet()) {
                Location tLocation = t2.getValue().getLocation();
                if (tLocation != null) {
                    String key[] = t2.getKey().split("_");
                    int icon = ObjectDATA.getObjectDATAHashMap().get(key[1]).getIcon();
                    int state = ObjectDATA.getObjectDATAHashMap().get(key[1]).getState();
                    MarkerOptions temp = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(icon))
                            .anchor(0.0f, 0.0f)
                            .visible(true)
                            .position(new LatLng(tLocation.getLatitude(), tLocation.getLongitude()));
                    mMarkerHashMap.put(state, mGoogleMap.addMarker(temp));

                    CircleOptions circleOptions = new CircleOptions()
                            .center(new LatLng(tLocation.getLatitude(), tLocation.getLongitude()))
                            .strokeColor(Color.argb(150, 192, 39, 25))
                            .fillColor(Color.argb(150, 242, 89, 75))
                            .radius(20)
                            .visible(false);
                    mCircleHashMap.put(state, mGoogleMap.addCircle(circleOptions));
                }
            }
        }
        Polygon polygon = mGoogleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(18.795526, 98.953083), new LatLng(18.795526 + 0.00095, 98.953083)
                        , new LatLng(18.795526 + 0.00095, 98.953083 + 0.000206), new LatLng(18.795526, 98.953083 + 0.000206))
                .strokeColor(Color.argb(150, 192, 39, 25))
                .fillColor(Color.argb(150, 242, 89, 75))
                .visible(false));
        mPolygonHashMap.put(GlobalResource.STATE_LOCATIONBASED, polygon);

        Polygon polygon2 = mGoogleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(18.793860, 98.950866), new LatLng(18.793860 + 0.000463, 98.950866)
                        , new LatLng(18.793860 + 0.000463, 98.950866 + 0.001099), new LatLng(18.793860, 98.950866 + 0.001099))
                .strokeColor(Color.argb(150, 192, 39, 25))
                .fillColor(Color.argb(150, 242, 89, 75))
                .visible(false));
        mPolygonHashMap.put(GlobalResource.STATE_MIST, polygon2);

    }


    public void initResource() {
        markerKeyList = new Integer[]{GlobalResource.STATE_MISSION, GlobalResource.STATE_MARKER, GlobalResource.STATE_HEALING
                , GlobalResource.STATE_FISHING, GlobalResource.STATE_PETTING,GlobalResource.STATE_SHOPPING};
        mImageList = new HashMap<>();
        mPolygonHashMap = new HashMap<>();
        mMarkerHashMap = new HashMap<>();
        mCircleHashMap = new HashMap<>();

        mImageList.put(GlobalResource.STATE_LOCATIONBASED,new Integer[]{R.drawable.ins_gathering,R.drawable.ins_zombie,R.drawable.ins_meteor});//location base 3 scene
        mImageList.put(GlobalResource.STATE_MISSION,new Integer[]{R.drawable.ins_interface_description,R.drawable.ins_find_marker,R.drawable.ins_oldman});//
        mImageList.put(GlobalResource.STATE_HEALING,new Integer[]{R.drawable.ins_heal});
        mImageList.put(GlobalResource.STATE_SHOPPING,new Integer[]{R.drawable.ins_shop});
        mImageList.put(GlobalResource.STATE_FISHING,new Integer[]{R.drawable.ins_fishing,R.drawable.ins_howtofishing});
        mImageList.put(GlobalResource.STATE_MARKER,new Integer[]{R.drawable.ins_attack_def,R.drawable.ins_boss});//
        mImageList.put(GlobalResource.STATE_DEAD,new Integer[]{R.drawable.ins_heal});
        mImageList.put(GlobalResource.STATE_PETTING,new Integer[]{R.drawable.ins_petting,R.drawable.ins_howtopet,R.drawable.ins_fishing,R.drawable.ins_howtofishing});
        mImageList.put(GlobalResource.STATE_MIST,new Integer[]{R.drawable.ins_mist});
        mImageList.put(GlobalResource.STATE_AREA2,new Integer[]{R.drawable.ins_shop,R.drawable.ins_petting,R.drawable.ins_mist});

        View instructionPage = GlobalResource.getListOfViews().get(Constants.HELP_LAYOUT);
        mPageIndicator = (PageIndicator) instructionPage.findViewById(R.id.pageIndicator);
        mPageIndicator.setTotalNoOfDots(0);
        mPageIndicator.setActiveDot(0);
        mPageIndicator.setDotSpacing(5);
        mViewPager = (ViewPager) instructionPage.findViewById(R.id.viewPager);
        //mViewPager.setAdapter(new MyPageAdapter());

        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                mPageIndicator.setActiveDot(pos);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        createMapObject();
    }

    class MyPageAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(View container, int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int resId = R.layout.overlay_dot; // layout for page one
            View view = inflater.inflate(resId, null);
            ((ViewPager) container).addView(view, 0);
            ImageView t = (ImageView) view.findViewById(R.id.dot_image);
            t.setImageResource(mImageList.get(STATE)[position]);
            return view;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImageList.get(STATE).length;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        public Parcelable saveState() {
            return null;
        }
        // public int getItemPosition(Object object) { return POSITION_NONE; }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

    }
}
