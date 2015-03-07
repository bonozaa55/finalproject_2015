package com.example.android.location.Resource.Object;

import android.content.Context;
import android.util.Log;

import com.example.android.location.Resource.GlobalResource;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IRadar;
import com.metaio.tools.io.AssetsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 1/16/2015.
 */
public class ObjectLoader {
    Context _this;
    IMetaioSDKAndroid _metaioSDK;
    IRadar _radar;
    private IGeometry tresure;
    private static HashMap<String,ObjectGroup> objectGroupList;

    public ObjectLoader(Context appContext, IMetaioSDKAndroid metaioSDK, IRadar radar) {
        this._this = appContext;
        this._metaioSDK = metaioSDK;
        this._radar = radar;
        createObject();
    }

    public static HashMap<String, ObjectGroup> getObjectGroupList() {
        return objectGroupList;
    }

    public static void setObjectGroupList(HashMap<String, ObjectGroup> objectGroupList) {
        ObjectLoader.objectGroupList = objectGroupList;
    }

    public void createObject(){
        ObjectDATA.LoadObjectDATA();
        objectGroupList=new HashMap<String, ObjectGroup>();
        objectGroupList.put(ObjectID.GROUP_BOSS,new ObjectGroup(new String[]{ObjectID.ELEPHANT,ObjectID.ELEPHANT_UNDERLING_L
        ,ObjectID.ELEPHANT_UNDERLING_R},ObjectID.ELEPHANT));

        objectGroupList.put(ObjectID.GROUP_ONE_EYE,new ObjectGroup(new String[]{ObjectID.PRISONER
                ,ObjectID.PRISONER,ObjectID.PRISONER}));
        objectGroupList.put(ObjectID.GROUP_PRISONER,new ObjectGroup(new String[]{ObjectID.PRISONER
                ,ObjectID.PRISONER,ObjectID.PRISONER}));
        objectGroupList.put(ObjectID.GROUP_METEOR,new ObjectGroup(new String[]{ObjectID.METEOR},ObjectID.METEOR));
        objectGroupList.put(ObjectID.BOTTLE,new ObjectGroup(new String[]{ObjectID.BOTTLE},ObjectID.BOTTLE));
        objectGroupList.put(ObjectID.OLD_MAN_KARN,new ObjectGroup(new String[]{ObjectID.OLD_MAN_KARN},ObjectID.OLD_MAN_KARN));
        objectGroupList.put(ObjectID.STONE,new ObjectGroup(new String[]{ObjectID.STONE,ObjectID.STONE,ObjectID.STONE}));
        objectGroupList.put(ObjectID.GRASS,new ObjectGroup(new String[]{ObjectID.GRASS,ObjectID.GRASS,ObjectID.GRASS}));
    }
    public void LoadARcontent() {

        ArrayList<IGeometry> mapObject = new ArrayList<IGeometry>();

        try {
            _metaioSDK.setLLAObjectRenderingLimits(0, 200);
            _metaioSDK.setRendererClippingPlaneLimits(10, 100000);
            _radar = _metaioSDK.createRadar();
            _radar.setBackgroundTexture(AssetsManager.getAssetPath(_this, "ModelAsset/Assets/radar.png"));
            _radar.setObjectsDefaultTexture(AssetsManager.getAssetPath(_this, "ModelAsset/Assets/yellow.png"));
            _radar.setRelativeToScreen(IGeometry.ANCHOR_TL);
            _radar.setVisible(false);
            GlobalResource.setRadar(_radar);
            // //////load local monster

            for (Map.Entry<String, ObjectGroup> t:objectGroupList.entrySet()){
                HashMap<String,ObjectDetail> objectDetailHashMap=new HashMap<String, ObjectDetail>();
                int i=0;
                for (String objectID:t.getValue().getKeyList()){
                    String name=t.getKey()+"_"+objectID+"_"+i;
                    HashMap<String,ObjectDATA> objectDATAHashMap=ObjectDATA.getObjectDATAHashMap();
                    String filePath = AssetsManager.getAssetPath(_this, "ModelAsset/Assets/" +
                            objectDATAHashMap.get(objectID).getModelString() + ".md2");
                    IGeometry temp = _metaioSDK.createGeometry(filePath);
                    temp.setScale(0.6f);
                    temp.setVisible(false);
                    temp.setName(name);
                    ObjectDetail objectDetail=new ObjectDetail(objectID,temp);
                    objectDetailHashMap.put(name,objectDetail);
                    i++;
                }
                t.getValue().setObjectDetailList(objectDetailHashMap);
            }

            ///load model for collecting function



        } catch (Exception e) {
            Log.i("ERROR", "Loader geometry ERROR");
        }
    }
}
