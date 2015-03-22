package com.example.android.location.GameManagement;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Resource.Player.PlayerItem;
import com.metaio.sdk.GestureHandlerAndroid;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.Vector3d;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 3/8/2015.
 */
public class FishingManager {
    static double rotateAngle = 0;
    SurfaceView mSurfaceView;
    GestureHandlerAndroid mGestureHandler;
    boolean allowRotating = true, mIsCloseToModel = false;
    boolean[] quadrantTouched;
    double startAngle;
    boolean isOpenValve = false, checkHook = false;
    IGeometry mFish, mTendon;
    IMetaioSDKAndroid metaioSDK;
    CountDownTimer mCountDownTimer;
    Handler mHandler;
    HandlerThread mHandlerThread;

    public FishingManager(IMetaioSDKAndroid metaioSDK, SurfaceView mSurfaceView, GestureHandlerAndroid mGestureHandler) {
        this.metaioSDK = metaioSDK;
        this.mSurfaceView = mSurfaceView;
        this.mGestureHandler = mGestureHandler;
        initResource();
    }

    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
    public void resetRotateAngle(){
        rotateAngle=0;
    }
    public void rotateGeometry(double angle) {
        if (angle > 0) {
            IGeometry t = mGestureHandler.getAllObjects().get(0);
            rotateAngle = rotateAngle + angle;
            t.setRotation(new Rotation(0, (float) (rotateAngle * Math.PI / 180), 0));
            if (360 * 3 - rotateAngle < 30) {
                isOpenValve = true;
                t.setPickingEnabled(false);
                t.setVisible(false);
                MainActivity.makeToast("Open the valve, Finish!");
            }
        }
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - (mSurfaceView.getWidth() / 2d);
        double y = mSurfaceView.getHeight() - yTouch - (mSurfaceView.getHeight() / 2d);
        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

            case 2:
            case 3:
                return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);

            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

            default:
                // ignore, does not happen
                return 0;
        }
    }

    public void checkFishingOnTouch(View v, MotionEvent event) {
        if (!isOpenValve) {
            int dialerWidth = v.getWidth();
            int dialerHeight = v.getHeight();
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    // reset the touched quadrants
                    for (int i = 0; i < quadrantTouched.length; i++) {
                        quadrantTouched[i] = false;
                    }
                    allowRotating = false;
                    startAngle = getAngle(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    double currentAngle = getAngle(event.getX(), event.getY());
                    rotateGeometry((float) (startAngle - currentAngle));
                    startAngle = currentAngle;
                    break;

                case MotionEvent.ACTION_UP:
                    allowRotating = true;
                    break;

            }
            // set the touched quadrant to true
            quadrantTouched[getQuadrant(event.getX() - (dialerWidth / 2), dialerHeight - event.getY() - (dialerHeight / 2))] = true;
        }
    }

    public double calculateHook(float[] values, double mAccelCurrent) {
        double mAccelLast, mAccel = 0;
        double x = values[0];
        double y = values[1];
        double z = values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = Math.sqrt(z*z+y*y);
        double delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        mAccel *= 0.2;
        mAccel = Math.abs(Math.round(mAccel));
        if (mAccel > 3 && checkHook) {
            checkHook = false;
            mFish.setVisible(true);
            mTendon.stopAnimation();
            mCountDownTimer.cancel();
        }
        return mAccelCurrent;
    }

    public void checkFishingOnGeometryTouch(IGeometry geometry, HashMap<Integer, PlayerItem> playerItemHashMap
            , GameGenerator mGameGenerator) {
        if (geometry.equals(mFish)) {
            int playerFish = mGameGenerator.getPlayerItemQuantity( ItemsID.FISH);
            playerItemHashMap.put(ItemsID.FISH, new PlayerItem(ItemsID.FISH, playerFish + 1));
            MainActivity.makeToastItem(ItemsID.FISH, 1);
            mFish.setVisible(false);
            generateTendonVibrate();
            //mGameGenerator.resetState();
        }
    }

    public void checkDistanceToTarget() {
        final TrackingValues tv = metaioSDK.getTrackingValues(1);
        if (tv.isTrackingState()) {
            final Vector3d translation = tv.getTranslation();
            final float distanceToTarget = translation.norm();
            final float threshold = 3900;
            if (mIsCloseToModel) {
                if (distanceToTarget > (threshold + 10) && isOpenValve) {
                    mIsCloseToModel = false;
                    ObjectGroup t = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_FISHING);
                    for (Map.Entry<String, ObjectDetail> t1 : t.getObjectDetailList().entrySet()) {
                        IGeometry temp = t1.getValue().getModel();
                        if (t1.getKey().split("_")[1].equals(ObjectID.FISHING_ROD) ||
                                t1.getKey().split("_")[1].equals(ObjectID.TENDON)
                                ) {
                            temp.setVisible(true);
                        } else {
                            temp.setVisible(false);
                        }
                    }
                    isOpenValve=false;
                    generateTendonVibrate();
                }
            } else {
                if (distanceToTarget < threshold) {
                    mIsCloseToModel = true;
                }
            }
        }
    }

    void generateTendonVibrate() {
        int randomValue = (int) (Math.random() * 4500) + 500;
        mHandler=new Handler(mHandlerThread.getLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkHook = true;
                mTendon.startAnimation("vibrate", true);
                mCountDownTimer = new CountDownTimer(5000, 5000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        mTendon.stopAnimation();
                        if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_FISHING) {
                            generateTendonVibrate();
                        }
                    }
                }.start();
            }
        }, randomValue);
    }

    void initResource() {
        mHandlerThread = new HandlerThread("FishingThread");
        mHandlerThread.start();
        HashMap<String, ObjectDetail> t0 = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_FISHING).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : t0.entrySet()) {
            if (t.getKey().split("_")[1].equals(ObjectID.FISH))
                mFish = t.getValue().getModel();
            if (t.getKey().split("_")[1].equals(ObjectID.TENDON)) {
                mTendon = t.getValue().getModel();
                mTendon.setAnimationSpeed(100);
            }

        }
        quadrantTouched = new boolean[]{false, false, false, false, false};
    }
}
