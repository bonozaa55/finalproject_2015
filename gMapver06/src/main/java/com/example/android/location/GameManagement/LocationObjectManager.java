package com.example.android.location.GameManagement;

import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.ObjectDetail;
import com.example.android.location.Resource.ObjectLoader;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IRadar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 1/30/2015.
 */
public class LocationObjectManager {

    private IRadar mRadar;
    private IMetaioSDKAndroid metaioSDK;
    private HashMap<String,ObjectDetail> modelList;

    public LocationObjectManager(IRadar mRadar, IMetaioSDKAndroid metaioSDK) {
        this.mRadar = mRadar;
        this.metaioSDK = metaioSDK;
        initResource();
    }

    public void setLocationBasedMonsterState(boolean parameter, ArrayList<ObjectDetail> object) {
        if (parameter) {
            GlobalResource.setGAME_STATE(GlobalResource.STATE_LOCATIONBASED);
            GameGenerator.setFoundedObjectLocation(object.get(0).getLocation());
            GameGenerator.setFoundedObjectDistance(object.get(0).getAcceptableDistance());
        } else {
            GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
        }
        if(object!=null)
            for(ObjectDetail t:object)
                setVisibilityLocationBasedMonster(parameter, t);
        else
            setVisibilityLocationBasedMonster(parameter, null);


        setGeneralState(parameter);
    }

    public void setVisibilityLocationBasedMonster(boolean parameter, ObjectDetail object) {
        //View motherView = mList.get(Constants.OVERLAY_LAYOUT);
        if (!parameter) {
            for (Map.Entry<String,ObjectDetail> t: modelList.entrySet())
                t.getValue().getModel().setVisible(false);
            mRadar.setVisible(false);
        } else {
            IGeometry temp=object.getModel();
            temp.setTransparency(0);
            temp.setVisible(true);
            temp.setPickingEnabled(true);
            mRadar.add(temp);
            mRadar.setVisible(true);
        }
    }

    public void initResource() {
        modelList= ObjectLoader.getObjectList();
    }

    public void setGeneralState(boolean parameter) {
        if (!parameter) {
            metaioSDK.pauseTracking();
        } else {
            metaioSDK.resumeTracking();
        }
    }
}
