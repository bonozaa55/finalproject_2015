package com.example.android.location.Resource;

import android.content.Context;
import android.location.Location;
import android.util.Log;

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
    private IGeometry gLocalMonster, gMarkerMonster, gStoreObject;
    private static HashMap<String,ObjectDetail> objectList;

    public ObjectLoader(Context appContext, IMetaioSDKAndroid metaioSDK, IRadar radar) {
        this._this = appContext;
        this._metaioSDK = metaioSDK;
        this._radar = radar;
        createObject();
    }

    public static HashMap<String, ObjectDetail> getObjectList() {
        return objectList;
    }

    public static void setObjectList(HashMap<String, ObjectDetail> objectList) {
        ObjectLoader.objectList = objectList;
    }

    public void createObject(){
        objectList=new HashMap<String, ObjectDetail>();
        objectList.put(ObjectID.ONE_EYE_1,new ObjectDetail("monster_one_eye",150));
        objectList.put(ObjectID.ONE_EYE_2,new ObjectDetail("monster_one_eye",150));
        objectList.put(ObjectID.ONE_EYE_3,new ObjectDetail("monster_one_eye",150));
        objectList.put(ObjectID.PRISONER_1,new ObjectDetail("prisoner",100));
        objectList.put(ObjectID.PRISONER_2,new ObjectDetail("prisoner",100));
        objectList.put(ObjectID.PRISONER_3,new ObjectDetail("prisoner",100));
        Location temp=new Location("");
        temp.setLatitude(18.796425);
        temp.setLongitude(98.953134);
        objectList.put(ObjectID.BOSS,new ObjectDetail(temp, 130f, 45, 15,"monster_loop",400, "ENG"));

    }
    public void LoadARcontent() {

        ArrayList<IGeometry> mapObject = new ArrayList<IGeometry>();
        ArrayList<IGeometry> markerObject = new ArrayList<IGeometry>();
        ArrayList<IGeometry> locationBasedObject = new ArrayList<IGeometry>();

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

            for (Map.Entry<String, ObjectDetail> t:objectList.entrySet()){
                String filePath = AssetsManager.getAssetPath(_this, "ModelAsset/Assets/" + t.getValue().getModelString() + ".md2");
                IGeometry temp = _metaioSDK.createGeometry(filePath);
                ObjectDetail objectDetail=objectList.get(t.getKey());
                temp.setScale(0.6f);
                temp.setVisible(false);
                temp.setName(t.getKey());
                objectDetail.setModel(temp);
                objectList.put(t.getKey(),objectDetail);
            }

            ///load model for collecting function
            String mapObjectString[] = {"ore", "bush"};
            for (String t : mapObjectString) {
                String filepath = AssetsManager.getAssetPath(_this, "ModelAsset/Assets/" + t + ".md2");
                tresure = _metaioSDK.createGeometry(filepath);
                tresure.setScale(0.05f);
                tresure.setVisible(false);
                tresure.setFadeInTime(1);
                tresure.setName(t);
                mapObject.add(tresure);
                GlobalResource.setMapObjectModelList(mapObject);
            }



        } catch (Exception e) {
            Log.i("ERROR", "Loader geometry ERROR");
        }
    }
}
