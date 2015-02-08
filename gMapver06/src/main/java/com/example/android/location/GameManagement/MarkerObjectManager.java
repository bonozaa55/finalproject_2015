package com.example.android.location.GameManagement;

import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.ObjectDetail;
import com.example.android.location.Resource.ObjectLoader;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 1/30/2015.
 */
public class MarkerObjectManager {

    IMetaioSDKAndroid metaioSDK;
    private ArrayList<IGeometry> markerModelList;
    private HashMap<String,ObjectDetail> objectDetailHashMap;

    public MarkerObjectManager(IMetaioSDKAndroid metaioSDK) {
        this.metaioSDK = metaioSDK;
        initResource();
    }

    public void setMarkerMonsterState(boolean parameter, ObjectDetail object) {
        if (parameter) {
            loadMarkerMonster(object);
            GameGenerator.setFoundedObjectDistance(object.getAcceptableDistance());
            GameGenerator.setFoundedObjectLocation(object.getLocation());
            GlobalResource.setGAME_STATE(GlobalResource.STATE_MARKER);
        } else {
            GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
            unLoadMarkerMonster();
        }
        setGeneralState(parameter);
    }

    private void loadMarkerMonster(ObjectDetail mMonster) {
        IGeometry temp = mMonster.getModel();
        temp.setTransparency(0);
        temp.setPickingEnabled(true);
        temp.setTranslation(new Vector3d(0, -20f, -200f));
        temp.setVisible(true);
    }

    private void unLoadMarkerMonster() {
        for(Map.Entry<String,ObjectDetail> t :objectDetailHashMap.entrySet())
            t.getValue().getModel().setVisible(false);
    }

    public void setGeneralState(boolean parameter) {
        if (!parameter) {
            metaioSDK.pauseTracking();
        } else {
            metaioSDK.resumeTracking();
        }
    }

    public void initResource() {
        objectDetailHashMap= ObjectLoader.getObjectList();
        markerModelList = GlobalResource.getMarkerObjectModelList();
    }
}
