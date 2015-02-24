package com.example.android.location.GameManagement;

import android.content.Context;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Object.ObjectDATA;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IRadar;
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
        switch (GAME_STATE) {
            case GlobalResource.STATE_MARKER:
                ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(objectGroup.getMainKey());
                MainActivity.makeToast("BOSS!!");
                String filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TutorialDynamicModels/Assets/MarkerConfig_" + objectDATA.getMarkerPath() + ".xml");
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
                        "TutorialDynamicModels/Assets/MarkerConfig_" + markerPath + ".xml");
                metaioSDK.setTrackingConfiguration(filePath);
                loadDeadResource();
                break;
            case GlobalResource.STATE_HEALING:
                setVisibilityHealObject();
                break;
            default:
                break;
        }
        GlobalResource.setGAME_STATE(GAME_STATE);
        setGeneralState(parameter);
    }

    void setGameState(boolean parameter) {
        GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
        hideAllModel();
        setGeneralState(parameter);
    }

    public void setGeneralState(boolean parameter) {
        if (!parameter) {
            metaioSDK.pauseTracking();
        } else {
            metaioSDK.resumeTracking();
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
        GlobalResource.setGAME_STATE(GlobalResource.STATE_DEAD);
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
        }
    }

    public void initResource() {


    }
}
