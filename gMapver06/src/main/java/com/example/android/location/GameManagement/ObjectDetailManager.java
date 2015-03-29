package com.example.android.location.GameManagement;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Toast;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Object.ObjectDATA;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Util.Constants;
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
    Handler mHandler;
    HandlerThread mHandlerThread;

    public ObjectDetailManager(IMetaioSDKAndroid metaioSDK, IRadar mRadar, Context context) {
        this.metaioSDK = metaioSDK;
        this.mRadar = mRadar;
        this.context = context;
        initResource();
    }

    public void setGameState(boolean parameter, final ObjectGroup objectGroup, final int GAME_STATE) {
        GlobalResource.setGAME_STATE(GAME_STATE);
        setGeneralState(parameter);
        ObjectDATA t = ObjectDATA.getObjectDATAHashMap().get(objectGroup.getMainKey());
        if(t!=null) {
            GameGenerator.setFoundedObjectDistance(t.getAcceptableDistance());
            GameGenerator.setFoundedObjectLocation(t.getLocation());
        }
        switch (GAME_STATE) {
            case GlobalResource.STATE_MARKER:
                ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(objectGroup.getMainKey());
                MainActivity.makeToast("BOSS!!", Toast.LENGTH_LONG);
                String filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + objectDATA.getMarkerPath() + ".xml");
                setConfig(filePath);
                loadMarkerModel(objectGroup);
                break;
            case GlobalResource.STATE_LOCATIONBASED:
                MainActivity.makeToast("Location monster",Toast.LENGTH_LONG);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        metaioSDK.setTrackingConfiguration("GPS", false);
                    }
                },100);
                loadLocalModel(objectGroup);
                break;
            case GlobalResource.STATE_METEOR:
                MainActivity.makeToast("Meteor coming!!",Toast.LENGTH_LONG);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        metaioSDK.setTrackingConfiguration("ORIENTATION");
                    }
                },100);
                loadMeteorModel(objectGroup);
                break;
            case GlobalResource.STATE_DEAD:
                MainActivity.makeToast("You are dead, Please go to heal station to refill it",Toast.LENGTH_LONG);
                String markerPath = ObjectDATA.getObjectDATAHashMap().get(ObjectID.BOTTLE).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                setConfig(filePath);
                loadDeadResource();
                break;
            case GlobalResource.STATE_HEALING:
                MainActivity.makeToast("Healing",Toast.LENGTH_LONG);
                markerPath = ObjectDATA.getObjectDATAHashMap().get(ObjectID.BOTTLE).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                setConfig(filePath);
                loadDeadResource();
                break;
            case GlobalResource.STATE_MISSION:
                MainActivity.makeToast("The Old man",Toast.LENGTH_LONG);
                new Mission_ONE().resetStateToMission();
                break;
            case GlobalResource.STATE_GATHERING:
                MainActivity.makeToast("Gathering",Toast.LENGTH_LONG);
                setCollectingState(true, objectGroup);
                setGatheringConfig();
                break;
            case GlobalResource.STATE_FISHING:
                MainActivity.makeToast("Fishing",Toast.LENGTH_LONG);
                markerPath = ObjectDATA.getObjectDATAHashMap().get(ObjectID.WATER_VALVE).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                setConfig(filePath);
                loadFishingResource();
                break;
            case GlobalResource.STATE_SHOPPING:
                MainActivity.makeToast("SHOP",Toast.LENGTH_LONG);
                markerPath = ObjectDATA.getObjectDATAHashMap().get(ObjectID.SHOP_MAN).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                setConfig(filePath);
                loadShopResource(objectGroup);
                break;
            case GlobalResource.STATE_PETTING:
                MainActivity.makeToast("PET",Toast.LENGTH_LONG);
                markerPath = ObjectDATA.getObjectDATAHashMap().get(ObjectID.PET_V1).getMarkerPath();
                filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                        "TrackingConfig/Assets/MarkerConfig_" + markerPath + ".xml");
                setConfig(filePath);
                loadPetResource();
                break;
            case GlobalResource.STATE_MIST:
                MainActivity.makeToast("MIST ZONE",Toast.LENGTH_LONG);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        metaioSDK.setTrackingConfiguration("GPS", false);
                    }
                },100);
                loadLocalModel(objectGroup);
                break;
        }
    }

    public void setGatheringConfig(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
            }
        },100);
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

    public void loadPetResource(){
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_PET).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setPickingEnabled(true);
            if (!t.getKey().split("_")[1].equals(PettingManager.getPetID())) {
                tt.setVisible(false);
            } else {
                if(!Player.isIsGetPet()) {
                    tt.setTranslation(new Vector3d(400,0,-555));
                    tt.startAnimation("loop", true);
                }else
                    tt.setTranslation(new Vector3d(0,0,-555));
                tt.setVisible(true);
            }
        }
        GlobalResource.getListOfViews().get(Constants.PET_LAYOUT).setVisibility(View.VISIBLE);
    }

    private void loadFishingResource() {
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_FISHING).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setPickingEnabled(true);
            if (!t.getKey().split("_")[1].equals(ObjectID.WATER_VALVE))
                tt.setVisible(false);
             else {
                tt.setPickingEnabled(true);
                tt.setVisible(true);
            }
        }
    }

    private void loadMeteorModel(ObjectGroup objectGroup) {
        HashMap<String, ObjectDetail> temp = objectGroup.getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setPickingEnabled(true);
            tt.setAnimationSpeed(50);
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
            tt.setVisible(true);
        }
    }

    void loadDeadResource() {
        setVisibilityHealObject();
    }

    void setVisibilityHealObject() {
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.BOTTLE).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
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
            tt.setAnimationSpeed(200);
            tt.startAnimation("pri_loop",true);
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
            tt.setRotation(new Rotation(0, 0, 0));
        }
        GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT)
                .findViewById(R.id.backToNormal).setVisibility(View.GONE);
        GlobalResource.getListOfViews().get(Constants.PET_LAYOUT).setVisibility(View.GONE);
    }

    public void setVisibilityCollectItem(boolean parameter, ObjectGroup object) {
        View motherView = GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT);
        if (parameter) {
            for (Map.Entry<String, ObjectDetail> t : object.getObjectDetailList().entrySet()) {
                IGeometry tt = t.getValue().getModel();
                tt.setVisible(true);
                tt.setFadeInTime(1);
                tt.setPickingEnabled(true);
            }
            motherView.findViewById(R.id.backToNormal).setVisibility(View.VISIBLE);
        } else
            motherView.findViewById(R.id.backToNormal).setVisibility(View.GONE);
    }

    public void loadShopResource(ObjectGroup mModelList){
        HashMap<String, ObjectDetail> temp = mModelList.getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setVisible(true);
            tt.setPickingEnabled(true);
        }
    }

    public void setCollectingState(boolean parameter, ObjectGroup object) {
        if (!parameter) {
            GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
        } else
            GlobalResource.setGAME_STATE(GlobalResource.STATE_GATHERING);
        setVisibilityCollectItem(parameter, object);
        setGeneralState(parameter);
    }

    void setConfig(final String markerPath){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                metaioSDK.setTrackingConfiguration(markerPath);
            }
        }, 100);
    }

    void initResource() {
        mHandlerThread = new HandlerThread("ObjectDetailThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

    }
}
