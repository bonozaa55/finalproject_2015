package com.example.android.location.Resource;

import android.content.Context;
import android.util.Log;

import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import java.util.ArrayList;

/**
 * Created by Adisorn on 1/16/2015.
 */
public class ResourceManager{
    Context _this;
    IMetaioSDKAndroid _metaioSDK;
    IRadar _radar;
    int factor = 300;
    private IGeometry[] mMushroom = new IGeometry[3];
    private IGeometry[] mFlower = new IGeometry[2];
    private IGeometry[] mGrass = new IGeometry[12];
    private IGeometry tresure;
    private IGeometry gLocalMonster, gMarkerMonster, gStoreObject;

    public ResourceManager(Context appContext, IMetaioSDKAndroid metaioSDK, IRadar radar) {

        this._this = appContext;
        this._metaioSDK = metaioSDK;
        this._radar = radar;
    }

    public void LoadARcontent() {

        ArrayList<IGeometry> temp = new ArrayList<IGeometry>();
        _metaioSDK.setLLAObjectRenderingLimits(0,200);
        _metaioSDK.setRendererClippingPlaneLimits(10, 100000);

        _radar = _metaioSDK.createRadar();

        _radar.setBackgroundTexture(AssetsManager.getAssetPath(
                _this,
                "ModelAsset/Assets/radar.png"));
        _radar.setObjectsDefaultTexture(AssetsManager.getAssetPath(
                _this,
                "ModelAsset/Assets/yellow.png"));
        _radar.setRelativeToScreen(IGeometry.ANCHOR_TL);
        _radar.setVisible(false);
        GlobalResource.setmRadar(_radar);
        // //////load local monster
        LLACoordinate location = new LLACoordinate(0, 0, 0, 0);
        String metaioManModel = AssetsManager.getAssetPath(
                _this,
                "ModelAsset/Assets/metaioman.md2");
        if (metaioManModel != null) {
            gLocalMonster = _metaioSDK.createGeometry(metaioManModel);
            if (gLocalMonster != null) {
                gLocalMonster.setTranslationLLA(location);
                gLocalMonster.setScale(100);
                gLocalMonster.setVisible(false);
                GlobalResource.setmLocationModel(gLocalMonster);
                _radar.add(gLocalMonster);
            } else {
                MetaioDebug.log(Log.ERROR, "Error loading geometry: "
                        + metaioManModel);
            }
        }
        // //load marker monster
        String filepath = AssetsManager.getAssetPath(_this,
                "ModelAsset/Assets/monster2.md2");
        if (filepath != null) {
            gMarkerMonster = _metaioSDK.createGeometry(filepath);
            if (gMarkerMonster != null) {
                gMarkerMonster.setScale(0.3f);
                gMarkerMonster.setCoordinateSystemID(1);
                gMarkerMonster.setTranslation(new Vector3d(0, 0, -2000.0f));
                gMarkerMonster.setVisible(false);
                gMarkerMonster.setAnimationSpeed(50);
                GlobalResource.setmMarkerModel(gMarkerMonster);
            } else
                MetaioDebug.log(Log.ERROR, "Error loading geometry: "
                        + filepath);
        }
        // // load store object
        filepath = AssetsManager.getAssetPath(_this,
                "ModelAsset/Assets/f1.zip");
        if (filepath != null) {
            gStoreObject = _metaioSDK.createGeometry(filepath);
            if (gStoreObject != null) {
                gStoreObject.setScale(100f);
                gStoreObject.setCoordinateSystemID(1);
                //gStoreObject.setRotation(new Rotation((float) Math.PI / 2, 0f,
                //	0));
                gStoreObject.setTranslation(new Vector3d(0, -500.0f, 0));
                gStoreObject.setVisible(false);
                GlobalResource.setmStoreModel(gStoreObject);
            } else
                MetaioDebug.log(Log.ERROR, "Error loading geometry: "
                        + filepath);
        }
        ///load model for collecting function
        try {
            for (int i = 0; i < mFlower.length; i++) {
                String modelPath = AssetsManager.getAssetPath(
                        _this,
                        "ModelAsset/Assets/f1.zip");
                mFlower[i] = _metaioSDK.createGeometry(modelPath);
                mFlower[i].setScale(4f);
                mFlower[i].setRotation(new Rotation(0f, 0f,
                        (float) (Math.PI * Math.random())));
                mFlower[i].setVisible(false);
                mFlower[i].setTranslation(new Vector3d(
                        (float) (Math.random() - 0.5) * factor,
                        (float) (Math.random() - 0.5) * factor, 0));
                temp.add(mFlower[i]);
            }
            // mushroom
            for (int i = 0; i < mMushroom.length; i++) {
                String modelPath = AssetsManager.getAssetPath(
                        _this,
                        "ModelAsset/Assets/mushroom1.zip");
                mMushroom[i] = _metaioSDK.createGeometry(modelPath);
                mMushroom[i].setScale(24f);
                // mFlower[i].setRotation(new Rotation(0f, 0f, rFlower[i]));
                mMushroom[i].setVisible(false);
                mMushroom[i].setTranslation(new Vector3d(
                        (float) (Math.random() - 0.5) * factor,
                        (float) (Math.random() - 0.5) * factor, 0));
                temp.add(mMushroom[i]);
            }
            // grass
            for (int i = 0; i < mGrass.length; i++) {
                String modelPath = AssetsManager.getAssetPath(
                        _this,
                        "ModelAsset/Assets/grass1.zip");
                mGrass[i] = _metaioSDK.createGeometry(modelPath);
                mGrass[i].setScale(8f);
                mGrass[i].setRotation(new Rotation(0f, 0f,
                        (float) (Math.PI * Math.random())));
                mGrass[i].setVisible(false);
                mGrass[i].setTranslation(new Vector3d(
                        (float) ((Math.random() - 0.5) * factor),
                        (float) ((Math.random() - 0.5) * factor), 0));
                temp.add(mGrass[i]);
            }
            String x = AssetsManager.getAssetPath(
                    _this,
                    "ModelAsset/Assets/tresure.zip");
            tresure = _metaioSDK.createGeometry(x);
            //tresure.setRotation(new Rotation(0f, 0f,(float)Math.PI/4));
            //tresure.setScale(new Vector3d(250f, 250f, 32f));
            tresure.setScale(50f);
            tresure.setVisible(false);
            tresure.setFadeInTime(501);
            temp.add(tresure);
            GlobalResource.setCollectingModelPack(temp);
        } catch (Exception e) {
            MetaioDebug.log(Log.ERROR,
                    "Error loading geometry: " + e.getMessage());
        }
    }
}
