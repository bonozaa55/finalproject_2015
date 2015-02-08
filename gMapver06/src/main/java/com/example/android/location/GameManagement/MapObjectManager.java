package com.example.android.location.GameManagement;

import android.view.View;

import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.MapObject;
import com.example.android.location.Util.Constants;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;

import java.util.ArrayList;

/**
 * Created by Adisorn on 1/30/2015.
 */


public class MapObjectManager {

    private ArrayList<View> listOfViews;
    private ArrayList<IGeometry> listMapObject;
    private IMetaioSDKAndroid metaioSDK;

    public MapObjectManager(IMetaioSDKAndroid metaioSDK) {
        this.metaioSDK = metaioSDK;
        initResource();
    }

    public void setVisibilityCollectItem(boolean parameter,MapObject object) {
        View motherView = listOfViews.get(Constants.OVERLAY_LAYOUT);
        if (parameter) {
            object.getModel().setVisible(parameter);
            motherView.findViewById(R.id.backToNormal).setVisibility(View.VISIBLE);
        } else {
            for (IGeometry t : listMapObject)
                t.setVisible(parameter);
            motherView.findViewById(R.id.backToNormal).setVisibility(View.GONE);
        }


    }

    public void setCollectingState(boolean parameter,MapObject object) {
        if (!parameter) {
            GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
        } else
            GlobalResource.setGAME_STATE(GlobalResource.STATE_GATHERING);
        setVisibilityCollectItem(parameter,object);
        setGeneralState(parameter);
    }

    public void initResource() {
        listOfViews = GlobalResource.getListOfViews();
        listMapObject = GlobalResource.getMapObjectModelList();
    }

    public void setGeneralState(boolean parameter) {
        if (!parameter) {
            metaioSDK.pauseTracking();
        } else {
            metaioSDK.resumeTracking();
        }
    }
}
