package com.example.android.location.GameManagement;

import android.content.Context;
import android.view.View;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Object.ObjectDATA;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Util.Constants;
import com.example.android.location.Util.Mission_ONE;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 2/13/2015.
 */
public class ObjectDetailManager {
    IMetaioSDKAndroid metaioSDK;
    IRadar mRadar;
    Context context;

    public ObjectDetailManager(IMetaioSDKAndroid metaioSDK, IRadar mRadar, Context context) {
        this.metaioSDK = metaioSDK;
        this.mRadar = mRadar;
        this.context = context;
        initResource();
    }

    public void setGameState(boolean parameter, ObjectGroup objectGroup, int GAME_STATE) {
        GlobalResource.setGAME_STATE(GAME_STATE);
        setGeneralState(parameter);
        switch (GAME_STATE) {
            case GlobalResource.STATE_MARKER:
                ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(objectGroup.getMainKey());
                MainActivity.makeToast("BOSS!!");
                String filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + objectDATA.getMarkerPath() + ".xml");
                metaioSDK.setTrackingConfiguration(filePath);
                loadMarkerModel(objectGroup);
                ObjectDATA t = ObjectDATA.getObjectDATAHashMap().get(objectGroup.getMainKey());
                GameGenerator.setFoundedObjectDistance(t.getAcceptableDistance());
                GameGenerator.setFoundedObjectLocation(t.getLocation());
                break;
            case GlobalResource.STATE_LOCATIONBASED:
                MainActivity.makeToast("Location monster");
                metaioSDK.setTrackingConfiguration("GPS", false);
                loadLocalModel(objectGroup);
                break;
            case GlobalResource.STATE_METEOR:
                MainActivity.makeToast("Meteor coming!!");
                metaioSDK.setTrackingConfiguration("ORIENTATION");
                loadMeteorModel(objectGroup);
                break;
            case GlobalResource.STATE_DEAD:
                MainActivity.makeToast("You are dead, Please go to heal station to refill it");
                String markerPath=ObjectDATA.getObjectDATAHashMap().get(ObjectID.BOTTLE).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                metaioSDK.setTrackingConfiguration(filePath);
                loadDeadResource();
                break;
            case GlobalResource.STATE_HEALING:
                MainActivity.makeToast("Healing");
                markerPath=ObjectDATA.getObjectDATAHashMap().get(ObjectID.BOTTLE).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                metaioSDK.setTrackingConfiguration(filePath);
                loadDeadResource();
                break;
            case GlobalResource.STATE_MISSION:
                MainActivity.makeToast("The Old man");
                new Mission_ONE().resetStateToMission();
                break;
            case GlobalResource.STATE_GATHERING:
                MainActivity.makeToast("Gathering");
                setCollectingState(true, objectGroup);
                metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
                break;
            case GlobalResource.STATE_FISHING:
                MainActivity.makeToast("Fishing");
                markerPath=ObjectDATA.getObjectDATAHashMap().get(ObjectID.WATER_VALVE).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                metaioSDK.setTrackingConfiguration(filePath);
                loadFishingResource();
                break;
        }

    }

    void setGameState(boolean parameter) {
        GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
        hideAllModel();
        setGeneralState(parameter);
    }

    public void setGeneralState(boolean parameter) {
        if (!parameter) {
            metaioSDK.setTrackingConfiguration("DUMMY");
            metaioSDK.pauseTracking();
        } else {
            metaioSDK.resumeTracking();
        }
    }

    private  void loadFishingResource(){
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_FISHING).getObjectDetailList();
        for(Map.Entry<String, ObjectDetail> t : temp.entrySet()){
            IGeometry tt=t.getValue().getModel();
            tt.setPickingEnabled(true);
            if(!t.getKey().split("_")[1].equals(ObjectID.WATER_VALVE)){
                //tt.setRelativeToScreen(IGeometry.ANCHOR_BC);
                tt.setVisible(false);
                tt.setTranslation(new Vector3d(200,-1500,0));
                // tt.setRotation(new Rotation((float)(Math.PI/180f*20),0,0));
            }else {
                tt.setVisible(true);
                tt.setPickingEnabled(true);
            }
        }
    }

    private void loadMeteorModel(ObjectGroup objectGroup) {
        HashMap<String, ObjectDetail> temp = objectGroup.getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setPickingEnabled(true);
            tt.setScale(0.5f);
            tt.setAnimationSpeed(50);
            tt.setTranslation(new Vector3d(0, 0, 200));
            tt.setVisible(true);
            tt.startAnimation("falling");
        }
    }

    private void loadMarkerModel(ObjectGroup mModelList) {
        HashMap<String, ObjectDetail> temp = mModelList.getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setAnimationSpeed(100);
            tt.setPickingEnabled(true);
            tt.setTranslation(new Vector3d(0, 0, -200f));
            tt.setVisible(true);
        }
    }

    void loadDeadResource(){
        setVisibilityHealObject();
    }

    void setVisibilityHealObject(){
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.BOTTLE).getObjectDetailList();
        for(Map.Entry<String, ObjectDetail> t : temp.entrySet()){
            IGeometry tt=t.getValue().getModel();
            tt.setVisible(true);
            tt.setPickingEnabled(true);
        }
    }

    private void loadLocalModel(ObjectGroup mModelList) {
        HashMap<String, ObjectDetail> temp = mModelList.getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setVisible(true);
            tt.setPickingEnabled(true);
            mRadar.add(tt);
        }
        mRadar.setVisible(true);
    }

    private void hideAllModel() {
        mRadar.setVisible(false);
        HashMap<String, ObjectDetail> temp = GameGenerator.getObjectGroup().getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setVisible(false);
            tt.setPickingEnabled(false);
            tt.stopAnimation();
            tt.setRotation(new Rotation(0,0,0));
        }
        GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT)
                .findViewById(R.id.backToNormal).setVisibility(View.GONE);
    }

    public void setVisibilityCollectItem(boolean parameter,ObjectGroup object) {
        View motherView = GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT);
        if (parameter) {
            for(Map.Entry<String,ObjectDetail> t:object.getObjectDetailList().entrySet()){
                IGeometry tt=t.getValue().getModel();
                tt.setScale(0.05f);
                tt.setVisible(true);
                tt.setFadeInTime(1);
                tt.setPickingEnabled(true);
            }
            motherView.findViewById(R.id.backToNormal).setVisibility(View.VISIBLE);
        } else
            motherView.findViewById(R.id.backToNormal).setVisibility(View.GONE);
    }

    public void setCollectingState(boolean parameter,ObjectGroup object) {
        if (!parameter) {
            GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
        } else
            GlobalResource.setGAME_STATE(GlobalResource.STATE_GATHERING);
        setVisibilityCollectItem(parameter,object);
        setGeneralState(parameter);
    }
    void initResource(){

    }
}
