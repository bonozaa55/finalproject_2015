package com.example.android.location.Resource;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IRadar;

import java.util.ArrayList;

/**
 * Created by Adisorn on 1/16/2015.
 */
public class GlobalResource {
    private static IGeometry mLocationModel;
    private static IGeometry mMarkerModel;
    private static IGeometry mStoreModel;
    private static IRadar mRadar;
    private static ArrayList<IGeometry> CollectingModelPack;

    public static ArrayList<IGeometry> getCollectingModelPack() {
        return CollectingModelPack;
    }

    public static void setCollectingModelPack(ArrayList<IGeometry> collectingModelPack) {
        CollectingModelPack = collectingModelPack;
    }



    public static IRadar getmRadar() {
        return mRadar;
    }

    public static void setmRadar(IRadar mRadar) {
        GlobalResource.mRadar = mRadar;
    }


    public static IGeometry getmLocationModel() {
        return mLocationModel;
    }

    public static void setmLocationModel(IGeometry mLocationModel) {
        GlobalResource.mLocationModel = mLocationModel;
    }


    public static IGeometry getmMarkerModel() {
        return mMarkerModel;
    }

    public static void setmMarkerModel(IGeometry mMarkerModel) {
        GlobalResource.mMarkerModel = mMarkerModel;
    }



    public static IGeometry getmStoreModel() {
        return mStoreModel;
    }

    public static void setmStoreModel(IGeometry mStoreModel) {
        GlobalResource.mStoreModel = mStoreModel;
    }


}
