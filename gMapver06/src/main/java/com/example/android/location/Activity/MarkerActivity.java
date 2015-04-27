package com.example.android.location.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.BounceAnimation;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.FlipHorizontalAnimation;
import com.easyandroidanimations.library.FlipVerticalAnimation;
import com.easyandroidanimations.library.ParallelAnimator;
import com.easyandroidanimations.library.RotationAnimation;
import com.easyandroidanimations.library.ScaleInAnimation;
import com.easyandroidanimations.library.ScaleOutAnimation;
import com.easyandroidanimations.library.ShakeAnimation;
import com.example.android.location.GameManagement.GameGenerator;
import com.example.android.location.GameManagement.ZoomViewManager;
import com.example.android.location.Interface.MarkerFragment;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.MarkerMode.ImageObject;
import com.example.android.location.Util.ImmersiveModeFragment;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.GestureHandlerAndroid;
import com.metaio.sdk.jni.GestureHandler;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 4/6/2015.
 */
public class MarkerActivity extends ARViewActivity {
    public static final int CAMERA_LAYOUT = 0;
    public static final int ZOOM_LAYOUT = 1;
    public static final int SCORE_LAYOUT = 2;


    public static final int MARKER_CAT = 2;
    public static final int MARKER_BOSS = 7;
    public static final int MARKER_TOILET = 6;
    public static final int MARKER_INFORMATION = 4;
    public static final int MARKER_AJK = 3;
    public static final int MARKER_PAPER_CUP = 5;
    public static final int MARKER_STORE = 1;


    static ImageObject touchImageObject;
    int sdkReady = 0, count = 0;
    public static Vibrator mVibration;
    SensorManager mSensorManager;
    private SensorEventListener mAccelerometerListener;
    private MetaioSDKCallbackHandler mCallbackHandler;
    static ZoomViewManager mZoomViewManager;
    static public HashMap<Integer, ImageObject> mImageObject;
    static CountDownTimer mCountDownTimer;
    private GestureHandlerAndroid mGestureHandler;

    private static ArrayList<View> mViewList;
    public static final String FRAGTAG = "ImmersiveModeFragment";
    IGeometry mGeometry[];

    public static HashMap<Integer, ImageObject> getmImageObject() {
        return mImageObject;
    }

    public static ImageObject getTouchImageObject() {
        return touchImageObject;
    }

    public static ZoomViewManager getmZoomViewManager() {
        return mZoomViewManager;
    }

    public static void setmZoomViewManager(ZoomViewManager mZoomViewManager) {
        MarkerActivity.mZoomViewManager = mZoomViewManager;
    }

    public static void setTouchImageObject(ImageObject touchImageObject) {
        MarkerActivity.touchImageObject = touchImageObject;
    }

    public static ArrayList<View> getmViewList() {
        return mViewList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOther();
        setContentView(new FrameLayout(this));
        initSensor();
        initImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mCallbackHandler=new MetaioSDKCallbackHandler();
        mSensorManager.registerListener(mAccelerometerListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mAccelerometerListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallbackHandler.delete();
        mCallbackHandler = null;
    }

    void initImage() {
        mImageObject.put(R.id.marker1, ImageObject.createImageObject(R.drawable.store_image, R.drawable.store_image_blur
                , R.id.marker1, 0.6f, "D", 1, new Vector3d(0, 0, -180)));
        mImageObject.put(R.id.marker2, ImageObject.createImageObject(R.drawable.cat_image, R.drawable.cat_image_blur
                , R.id.marker2, 0.6f, "K", 2, new Vector3d(0, 0, 0)));
        mImageObject.put(R.id.marker3, ImageObject.createImageObject(R.drawable.f_image, R.drawable.f_image_blur
                , R.id.marker3, 0.6f, "bomb_boss", 3, new Vector3d(0, 0, -100)));
        mImageObject.put(R.id.marker4, ImageObject.createImageObject(R.drawable.infomation_image, R.drawable.infomation_image_blur
                , R.id.marker4, 0.6f, "A", 4, new Vector3d(0, 0, 0)));
        mImageObject.put(R.id.marker5, ImageObject.createImageObject(R.drawable.paper_cup_image, R.drawable.paper_cup_image_blur
                , R.id.marker5, 0.6f, "J", 5, new Vector3d(0, 0, 0)));
        mImageObject.put(R.id.marker6, ImageObject.createImageObject(R.drawable.toilet_image, R.drawable.toilet_image_blur
                , R.id.marker6, 0.6f, "J", 6, new Vector3d(0, 0, 0)));
        mImageObject.put(R.id.marker7, ImageObject.createImageObject(R.drawable.boss_image, R.drawable.boss_image_blur
                , R.id.marker7, 0.6f, "H", 7, new Vector3d(0, 0, 0)));

    }

    void initResource() {
        mZoomViewManager = new ZoomViewManager(mViewList.get(ZOOM_LAYOUT));
        initInterface();

    }


    void initInterface() {
        View cameraView = mViewList.get(CAMERA_LAYOUT);
        ViewPager mPager = (ViewPager) cameraView.findViewById(R.id.pager);
        MarkerFragmentAdapter mPagerAdapter = new MarkerFragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


        mViewList.get(SCORE_LAYOUT).findViewById(R.id.back_to_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewList.get(ZOOM_LAYOUT).findViewById(R.id.container2).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (touchImageObject.getHitCount() < 5 && !touchImageObject.isGetItem()) {
                    touchImageObject.setHitCount(touchImageObject.getHitCount() + 1);
                }
                mZoomViewManager.showImage();
                return true;
            }
        });

    }

    void initOther() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ImmersiveModeFragment immersiveModeFragment = new ImmersiveModeFragment();
            transaction.add(immersiveModeFragment, FRAGTAG);
            transaction.commit();
        }
        try {
            AssetsManager.extractAllAssets(this, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ZoomViewManager.setContext(this);
        mCallbackHandler = new MetaioSDKCallbackHandler();
        mViewList = new ArrayList<View>();
        mImageObject = new HashMap<Integer, ImageObject>();
        int mGestureMask = GestureHandler.GESTURE_DRAG;
        mGestureHandler = new GestureHandlerAndroid(metaioSDK, mGestureMask);

    }

    public void initSensor() {
        mVibration = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mAccelerometerListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && sdkReady == 1) {
                    count++;
                    float z_value = event.values[2];
                    float x_value = Math.abs(event.values[0]);
                    boolean check = z_value < 8 && x_value > 3;
                    // show map only a phone flip up
                    if (count > 20) {//camera
                        if (check) {
                            mViewList.get(CAMERA_LAYOUT).setVisibility(View.GONE);
                        } else {// map
                            mViewList.get(CAMERA_LAYOUT).setVisibility(View.VISIBLE);
                        }
                        count = 0;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v, event);
        mGestureHandler.onTouch(v, event);
        return true;
    }

    public static void startAnimation(final View rootView) {
        /*if (mCountDownTimer != null)
            mCountDownTimer.cancel();

        if (mItemList.size() != 0) {

            mCountDownTimer = new CountDownTimer(30200, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int index = GameGenerator.randInt(0, mItemList.size() - 1);
                    final View v = rootView.findViewById(mItemList.get(index));
                    int random = GameGenerator.randInt(0, 0);
                    if (random == 0)
                        new RotationAnimation(v).setPivot(RotationAnimation.PIVOT_CENTER).animate();
                    if (random == 1)
                        new ShakeAnimation(v).setNumOfShakes(3)
                                .setDuration(Animation.DURATION_SHORT)
                                .animate();
                    if (random == 2)
                        new FlipVerticalAnimation(v).animate();
                    if (random == 3)
                        new FlipHorizontalAnimation(v).animate();
                    if (random == 0)
                        new BounceAnimation(v).setNumOfBounces(3).animate();

                }

                @Override
                public void onFinish() {
                    startAnimation(rootView);
                }
            }.start();
        }*/

    }


    public static boolean checkClearAll() {
        int score = 0;
        for (Map.Entry<Integer, ImageObject> t : mImageObject.entrySet()) {
            score += t.getValue().getHitCount();
            if (!t.getValue().isGetItem())
                return false;
        }
        View scoreLayout = mViewList.get(SCORE_LAYOUT);
        scoreLayout.setVisibility(View.VISIBLE);
        TextView t = (TextView) scoreLayout.findViewById(R.id.score);
        t.setText("Your score: " + score);
        GlobalResource.setClearMarkerMission(true);
        return true;
    }

    @Override
    protected int getGUILayout() {
        return 0;
    }

    @Override
    protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
        return mCallbackHandler;
    }

    @Override
    protected void loadContents() {
        String filePath = AssetsManager.getAssetPath(getApplicationContext(),
                "TrackingConfig/Floor4_5/MarkerConfig_Floor4_5.xml");
        metaioSDK.setTrackingConfiguration(filePath);
        int i = 0;
        mGeometry = new IGeometry[8];
        for (Map.Entry<Integer, ImageObject> t : mImageObject.entrySet()) {
            String filepath = AssetsManager.getAssetPath(this, "ModelAsset/Assets/" + t.getValue().getModelFilePath() + ".md2");
            IGeometry temp = metaioSDK.createGeometry(filepath);
            temp.setName(t.getKey() + "");
            if (t.getValue().isGetItem())
                temp.setCoordinateSystemID(MARKER_AJK);
            else
              temp.setCoordinateSystemID(t.getValue().getCos());
            temp.setScale(t.getValue().getModelSize());
            temp.setTranslation(t.getValue().getOffset());
            temp.setVisible(true);
            temp.setScale(0.4f);
            mGestureHandler.addObject(temp, t.getValue().getCos());
            mGeometry[t.getValue().getCos()] = temp;
        }
    }

    //AJKJDH
    // infor-paper-cat-toilet-store-boss
    // infor-toilet-cat-paper-store-boss
    boolean checkLocation() {
        float translationX[] = new float[8];
        for (Map.Entry<Integer, ImageObject> t : mImageObject.entrySet()) {
            int index = t.getValue().getCos();
            translationX[index] = mGeometry[index].getTranslation().getX();
            if (!t.getValue().isGetItem() && t.getKey().intValue() != R.id.marker3)
                return false;
        }
        Log.i("www", translationX[MARKER_INFORMATION] + " " + translationX[MARKER_PAPER_CUP] + " " + translationX[MARKER_CAT] + " " +
                translationX[MARKER_TOILET] + " " + translationX[MARKER_STORE] + " " + translationX[MARKER_BOSS]);
        boolean condition1 = translationX[MARKER_INFORMATION] < translationX[MARKER_PAPER_CUP]
                && translationX[MARKER_PAPER_CUP] < translationX[MARKER_CAT] && translationX[MARKER_CAT] < translationX[MARKER_TOILET]
                && translationX[MARKER_TOILET] < translationX[MARKER_STORE] && translationX[MARKER_STORE] < translationX[MARKER_BOSS];
        boolean condition2 = translationX[MARKER_INFORMATION] < translationX[MARKER_TOILET]
                && translationX[MARKER_TOILET] < translationX[MARKER_CAT] && translationX[MARKER_CAT] < translationX[MARKER_PAPER_CUP]
                && translationX[MARKER_PAPER_CUP] < translationX[MARKER_STORE] && translationX[MARKER_STORE] < translationX[MARKER_BOSS];
        if (condition1 || condition2)
            return true;
        else
            return false;
    }

    void bossCongrats() {
        for(int i=1;i<8;i++)
            mGeometry[i].setVisible(false);
        mGeometry[MARKER_AJK].setTranslation(new Vector3d(0,0,0));
        mGeometry[MARKER_AJK].setAnimationSpeed(100);
        mGeometry[MARKER_AJK].setVisible(true);
        mGeometry[MARKER_AJK].startAnimation("attack");

    }

    void showSimpleDialog(String title, String Text) {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        dialog.setContentView(R.layout.dialog_ok);
        dialog.setTitle(title);

        dialog.findViewById(R.id.dialogBtnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.dialogTxt)).setText(Text);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    int countObjectFound() {
        int objectFound = 0;
        for (Map.Entry<Integer, ImageObject> t : mImageObject.entrySet())
            if (t.getValue().isGetItem())
                objectFound++;
        return objectFound;
    }

    @Override
    protected void onGeometryTouched(final IGeometry geometry) {

        if (geometry.getCoordinateSystemID() == MARKER_AJK) {
            if (geometry.getName().equals(R.id.marker3 + "")) {
                int objectFound=countObjectFound();
                if (objectFound < 6)
                    showSimpleDialog("BOSS", "You have not enough materials! go back and find it");
                if (!checkLocation() && objectFound >= 6) {
                    showSimpleDialog("BOSS", "You arrange wrong position!");
                    geometry.setTranslation(new Vector3d(4 * 300, 0, 0));
                    int i = -3;
                    for (Map.Entry<Integer, ImageObject> t : mImageObject.entrySet()) {
                        if (t.getValue().isGetItem()) {
                            IGeometry temp = mGeometry[t.getValue().getCos()];
                            temp.setVisible(true);
                            temp.setTranslation(new Vector3d(i * 300, 0, 0));
                            i++;
                        }
                    }
                }
                if (checkLocation()) {
                    bossCongrats();
                    ImageObject t = mImageObject.get(Integer.parseInt(geometry.getName()));
                    t.setIsGetItem(true);
                }
            }

        } else {
            geometry.setVisible(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageObject t = mImageObject.get(Integer.parseInt(geometry.getName()));
                    t.setIsGetItem(true);
                    ImageView t1 = (ImageView) mViewList.get(CAMERA_LAYOUT).findViewById(t.getThumbImageID());
                    t1.setBackgroundResource(t.getImageID());
                    t1.setImageResource(R.drawable.cancel);
                }
            });
        }
        geometry.setCoordinateSystemID(MARKER_AJK);

    }


    final class MetaioSDKCallbackHandler extends IMetaioSDKCallback {

        @Override
        public void onSDKReady() {
            super.onSDKReady();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mViewList.size() == 0) {
                        //camera overlay
                        mGUIView = View.inflate(MarkerActivity.this,
                                R.layout.marker_camera_view_pager, null);
                        mViewList.add(mGUIView);
                        addContentView(mGUIView, new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
                        mGUIView.setVisibility(View.GONE);

                        //zoom overlay
                        mGUIView = View.inflate(MarkerActivity.this,
                                R.layout.zoom_layout, null);
                        mViewList.add(mGUIView);
                        addContentView(mGUIView, new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
                        mGUIView.setVisibility(View.GONE);

                        //score overlay
                        mGUIView = View.inflate(MarkerActivity.this,
                                R.layout.marker_score_layout, null);
                        mViewList.add(mGUIView);
                        addContentView(mGUIView, new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
                        mGUIView.setVisibility(View.GONE);

                        //set global
                        sdkReady = 1;
                        Log.i("www", "sdkReady");
                        initResource();

                    }
                }
            });

        }

        @Override
        public void onTrackingEvent(TrackingValuesVector trackingValues) {
            int found;
            for (int i = 0; i < trackingValues.size(); i++) {
                final TrackingValues v = trackingValues.get(i);
                found = v.getCoordinateSystemID();
                Log.i("kak", "Tracking state for COS " + v.getCoordinateSystemID() + " is " + v.getState() + found);

            }
        }

        @Override
        public void onAnimationEnd(final IGeometry geometry, final String animationName) {
            if (animationName.equals("attack"))
                geometry.startAnimation("attack_back");

            if (animationName.equals("attack_back"))
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkClearAll();
                    }
                });


        }
    }

    public class MarkerFragmentAdapter extends FragmentStatePagerAdapter {
        int NUM_PAGES = 1;

        public MarkerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new MarkerFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }
}
