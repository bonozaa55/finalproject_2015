package com.example.android.location.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.Camera.CameraInfo;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.location.GameManagement.FishingManager;
import com.example.android.location.GameManagement.GameGenerator;
import com.example.android.location.GameManagement.HealManager;
import com.example.android.location.GameManagement.Mission_ONE;
import com.example.android.location.GameManagement.MistManager;
import com.example.android.location.GameManagement.MyItemManager;
import com.example.android.location.GameManagement.ObjectDetailManager;
import com.example.android.location.GameManagement.PettingManager;
import com.example.android.location.GameManagement.StoreManager;
import com.example.android.location.Interface.MyCraftMissionManager;
import com.example.android.location.Interface.TouchEffectView;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDATA;
import com.example.android.location.Resource.Item.ItemDetail;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Resource.Player.PlayerItem;
import com.example.android.location.Util.BackgroundLocationService;
import com.example.android.location.Util.Constants;
import com.example.android.location.Util.ImmersiveModeFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.GestureHandlerAndroid;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.ETRACKING_STATE;
import com.metaio.sdk.jni.GestureHandler;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.SystemInfo;
import com.metaio.tools.io.AssetsManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ARViewActivity {

    public static final String FRAGTAG = "ImmersiveModeFragment";
    public static Vibrator mVibration;
    static Context applicationContext, this_Context;
    static TouchEffectView touchEffectView;
    static int count = 0, count2 = 0;
    static GameGenerator mGameGeneretor;
    static ImmersiveModeFragment immersiveModeFragment;
    static Mission_ONE mMissionOne;
    static Toast mToast;
    static MediaPlayer mMediaPlayer, mMediaPlayerEffect;
    private static TextView tBearing;
    private static double phoneHeading;
    SensorManager mSensorManager;
    int found = 0, SDK_ready = 0, index = -1;
    int ViewState = -1;
    ObjectDetailManager mObjectDetailManager;
    double mAccelCurrent;
    FishingManager mFishingManager;
    private Button resumeMapButton;
    private LocationReceiver lReceiver;
    private Intent intent_location;
    private Location cLocation;
    private String sLocation = "";
    private GoogleMap map;
    private float mDeclination;
    private ArrayList<LLACoordinate> dlocation = new ArrayList<LLACoordinate>();
    private IRadar mRadar;
    private SensorEventListener mOriantationListener, mAccelerometerListener;
    private ArrayList<View> mList = new ArrayList<View>();
    private int mlist_size = 3;
    private MetaioSDKCallbackHandler mCallbackHandler;
    private int timeCount = 0;
    private int maxTime = 200;
    private boolean startCount = false,doubleBackToExitPressedOnce=false;
    private int gameState = 0;
    private HealManager mHealManager;
    private GestureHandlerAndroid mGestureHandler;
    private static PettingManager mPettingManager;
    private MistManager mMistManager;
    private MyItemManager mMyItemManager;

    public static double getPhoneHeading() {
        return MainActivity.phoneHeading;
    }

    public static Context getThisContext() {
        return applicationContext;
    }

    public static Context getActivityContext() {
        return this_Context;
    }

    public static GameGenerator getmGameGeneretor() {
        return mGameGeneretor;
    }

    public static void makeToastItem(int itemID, int quantity) {
        ItemDetail t = ItemDATA.getItemList().get(itemID);
        View layout = GlobalResource.getListOfViews().get(Constants.TOAST_LAYOUT);
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(t.getIconResource());
        TextView text = (TextView) layout.findViewById(R.id.text);
        if (itemID == ItemsID.GOLD)
            text.setText("Receive " + t.getName() + " " + quantity + " gold!");
        else
            text.setText("Receive " + t.getName() + " " + quantity + " ea!");
        Toast toast = new Toast(applicationContext);
        toast.setGravity(Gravity.TOP, 0, -200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }


    public static void showSimpleDialog(String title, String Text) {
        final Dialog dialog = new Dialog(this_Context);
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
                if(GlobalResource.getGAME_STATE()==GlobalResource.STATE_PETTING)
                    mPettingManager.resumeAnimation();
            }
        });
        ((TextView) dialog.findViewById(R.id.dialogTxt)).setText(Text);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }
    public static void makeToast(String message,int length) {
        View layout = GlobalResource.getListOfViews().get(Constants.TOAST_LAYOUT);
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.icon_star);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        mToast.setGravity(Gravity.TOP, 0, -200);
        mToast.setDuration(length);
        mToast.setView(layout);
        mToast.show();
    }

    public static void stopPlaying() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static void playSound(String sound, boolean isLoop) {
        stopPlaying();
        mMediaPlayer = new MediaPlayer();
        try {
            FileInputStream fis = new FileInputStream(AssetsManager.getAssetPath(this_Context, "SoundEffect1/Assets/" + sound));
            mMediaPlayer.setDataSource(fis.getFD());
            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(0.2f, 0.2f);
            mMediaPlayer.setLooping(isLoop);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    public static void stopPlayingEffect() {
        if (mMediaPlayerEffect != null) {
            mMediaPlayerEffect.stop();
            mMediaPlayerEffect.release();
            mMediaPlayerEffect = null;
        }
    }

    public static void playSoundEffect(String sound) {
        stopPlayingEffect();
        mMediaPlayerEffect = new MediaPlayer();
        try {
            FileInputStream fis = new FileInputStream(AssetsManager.getAssetPath(this_Context, "SoundEffect1/Assets/" + sound));
            mMediaPlayerEffect.setDataSource(fis.getFD());
            mMediaPlayerEffect.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayerEffect.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new FrameLayout(this));

        //mGestureCallback= new MetaioGestureHandlerCallback();
        initOther();
        initSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(mOriantationListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mAccelerometerListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mMediaPlayer = new MediaPlayer();
        lReceiver = new LocationReceiver();
        registerReceiver(lReceiver, new IntentFilter("Location"));
        startService(intent_location);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mOriantationListener);
        mSensorManager.unregisterListener(mAccelerometerListener);
        unregisterReceiver(lReceiver);
        stopService(intent_location);
        mMediaPlayer.stop();
       // mGameGeneretor.resetState();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallbackHandler.delete();
        mCallbackHandler = null;
    }

    @Override
    public void onDrawFrame() {
        super.onDrawFrame();
        if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_FISHING)
            mFishingManager.checkDistanceToTarget();
        if (startCount)
            timeCount++;
        if (timeCount >= maxTime) {
            if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_GATHERING)
                mObjectDetailManager.setGatheringConfig();
            timeCount = 0;
        }
        // checkDistanceToTarget();
    }

    void initOther() {
        intent_location = new Intent(this, BackgroundLocationService.class);

        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            immersiveModeFragment = new ImmersiveModeFragment();
            transaction.add(immersiveModeFragment, FRAGTAG);
            transaction.commit();
        }

        try {
            AssetsManager.extractAllAssets(this, true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCallbackHandler = new MetaioSDKCallbackHandler();
    }

    public void initResource() {
        initOther();
        applicationContext = getApplicationContext();
        this_Context = this;
        mMyItemManager = new MyItemManager();
        SDK_ready = 1;

        initInterface();

        mObjectDetailManager = new ObjectDetailManager(metaioSDK, mRadar, this);
        mHealManager = new HealManager();
        mFishingManager = new FishingManager(metaioSDK, mSurfaceView, mGestureHandler);
        mPettingManager = new PettingManager(mObjectDetailManager);
        StoreManager mStoreManager = new StoreManager(this, mMyItemManager);
        mMistManager = new MistManager();


        mGameGeneretor = new GameGenerator(this, metaioSDK, mObjectDetailManager, mFishingManager, mMistManager, map);

        mMissionOne = new Mission_ONE(mGameGeneretor, metaioSDK, applicationContext);


        mMissionOne.startMission();
        GlobalResource.setGAME_STATE(GlobalResource.STATE_IDLE);
        mToast = new Toast(applicationContext);
    }

    public void initInterface() {

        View mapLayout = mList.get(Constants.MAP_LAYOUT);
        final View overlayLayout = mList.get(Constants.OVERLAY_LAYOUT);
        View craftLayout = mList.get(Constants.MY_CRAFT_DETIAL_LAYOUT);
        final View storeLayout = mList.get(3);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setZoomControlsEnabled(false);
        LatLng mapCenter = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 18f));
        CameraPosition t0 = new CameraPosition.Builder()
                .target(mapCenter)
                .zoom(18f)
                .build();

        map.animateCamera(
                CameraUpdateFactory.newCameraPosition(t0),
                3000,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        sLocation = "1";
                    }

                    @Override
                    public void onCancel() {
                    }
                }
        );

        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                sLocation = "2";
            }
        });
        TextView t = (TextView) mapLayout.findViewById(R.id.bearing_text);
        t.setVisibility(View.GONE);
        Button b = (Button) mapLayout.findViewById(R.id.switch_location);
        b.setVisibility(View.VISIBLE);
        resumeMapButton = (Button) mapLayout.findViewById(R.id.toggleZoom);
        tBearing = (TextView) findViewById(R.id.bearing_text);
        tBearing.setVisibility(View.VISIBLE);
        AddAllMarker();
        TextView potionText = (TextView) overlayLayout.findViewById(R.id.overlay_potions_count);
        potionText.setText(GameGenerator.getPlayerItemQuantity(ItemsID.POTION) + "");
        View potionInterface = overlayLayout.findViewById(R.id.overlay_potions_interface);
        potionInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGameGeneretor.restoreHPbyItems();

                /*View t = overlayLayout.findViewById(R.id.overlay_group_item);
                View t2 = overlayLayout.findViewById(R.id.overlay_group_quest);
                if (t.getVisibility() == View.INVISIBLE) {
                    t.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                } else {
                    t.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                }
                */


            }
        });
        ImageView interfaceCraftQuest = (ImageView) overlayLayout.findViewById(R.id.overlay_craft_quest_interface);

        interfaceCraftQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.get(2).setVisibility(View.VISIBLE);
                // mMyCraftMissionManager.checkCraftingMaterial();
            }
        });
        MyCraftMissionManager test = new MyCraftMissionManager(craftLayout, getSupportFragmentManager());

    }

    public void initSensor() {
        mVibration = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mOriantationListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    float[] mRotationMatrix = new float[16];
                    SensorManager.getRotationMatrixFromVector(mRotationMatrix,
                            event.values);
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(mRotationMatrix, orientation);
                    float bearing = (float) (Math.toDegrees(orientation[0]) + mDeclination);
                    phoneHeading = bearing;
                    if (SDK_ready == 1)
                        tBearing.setText(bearing + "");
                    updateCamera(bearing + 90);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };
        mAccelerometerListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                        && mList.size() == mlist_size && (ViewState == Constants.MAP_VISIBLE
                        || ViewState == Constants.metaio_VISIBLE || ViewState == -1)) {
                    count++;
                    float z_value = event.values[2];
                    float x_value = Math.abs(event.values[0]);
                    // Log.i("www",z_value+" "+y_value+" "+x_value);
                    //boolean check=x_value<1&&y_value<1&&z_value<8;
                    boolean check = z_value < 8 && x_value > 3;
                    // show map only a phone flip up

                    boolean x = check || (GlobalResource.getGAME_STATE() != GlobalResource.STATE_IDLE);
                    //Log.i("www",x+"");
                    if (count > 20) {//camera
                        if (x) {
                            mList.get(Constants.MAP_LAYOUT).setVisibility(View.GONE);
                            mList.get(Constants.OVERLAY_LAYOUT).setVisibility(View.VISIBLE);
                            ViewState = Constants.metaio_VISIBLE;
                        } else {// map
                            mList.get(Constants.OVERLAY_LAYOUT).setVisibility(View.GONE);
                            mList.get(Constants.MAP_LAYOUT).setVisibility(View.VISIBLE);
                            ViewState = Constants.MAP_VISIBLE;
                        }
                        count = 0;
                    }
                    if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_HEALING)
                        mAccelCurrent = mHealManager.calculateShakeValue(event.values, mAccelCurrent);
                    if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_FISHING)
                        mFishingManager.calculateHook(event.values, mAccelCurrent);

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };
    }

    /**
     * ******************************
     */


    public void AddAllMarker() {

        dlocation.add(new LLACoordinate(18.794803, 98.950988, 0, 0));//pet

        //dlocation.add(new LLACoordinate(18.796474, 98.952519, 0, 0));//boss
        /*
        dlocation.add(new LLACoordinate(18.795396, 98.951926, 0, 0));//mission
        dlocation.add(new LLACoordinate(18.796474, 98.952519, 0, 0));//boss
        dlocation.add(new LLACoordinate(18.796568, 98.951905, 0, 0));//heal
        dlocation.add(new LLACoordinate(18.795396, 98.951926, 0, 0));//mission
        dlocation.add(new LLACoordinate(18.795122, 98.951545, 0, 0));//fishing
        dlocation.add(new LLACoordinate(18.794803, 98.950988, 0, 0));//pet
        dlocation.add(new LLACoordinate(18.795396, 98.951926, 0, 0));//mission
        dlocation.add(new LLACoordinate(18.796474, 98.952519, 0, 0));//boss
        dlocation.add(new LLACoordinate(18.795396, 98.951926, 0, 0));//mission
        */

/*
        dlocation.add(new LLACoordinate(18.794280, 98.950866, 0, 0));//area2
        dlocation.add(new LLACoordinate(18.794803, 98.950988, 0, 0));//pet
        dlocation.add(new LLACoordinate(18.795122, 98.951545, 0, 0));//fishing
        dlocation.add(new LLACoordinate(18.795893, 98.951211, 0, 0));//shop
        dlocation.add(new LLACoordinate(18.796568, 98.951905, 0, 0));//heal

        dlocation.add(new LLACoordinate(18.796474, 98.952519, 0, 0));//boss
        dlocation.add(new LLACoordinate(18.795526f, 98.953083f, 0, 0));//area
        dlocation.add(new LLACoordinate(18.795396, 98.951926, 0, 0));//mission
        */

        LLACoordinate t = new LLACoordinate(cLocation.getLatitude(), cLocation.getLongitude(), 0, 0);
        dlocation.add(t);
    }

    public void BackToNormal(View v) {
        mGameGeneretor.resetState();
    }

    public void SwitchLocation(View v) {
        index++;
        index = index % dlocation.size();
    }

    public void ToggleZoom(View v) {
        sLocation = "1";
        resumeMapButton.setVisibility(View.GONE);
        CameraPosition pos = CameraPosition
                .builder()
                .target(new LatLng(cLocation.getLatitude(), cLocation
                        .getLongitude())).zoom(18f)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    private void updateCamera(float bearing) {
        if (sLocation != "" && ViewState == Constants.MAP_VISIBLE) {
            LatLng currentLocation = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
            if (map.getCameraPosition().zoom == 18f && sLocation != "2") {
                CameraPosition pos = CameraPosition
                        .builder()
                        .target(currentLocation).bearing(bearing).zoom(18f)
                        .build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                sLocation = "1";
            } else {
                resumeMapButton.setVisibility(View.VISIBLE);
                sLocation = "";
            }
        }
    }

    @Override
    protected int getGUILayout() {
        // TODO Auto-generated method stub
        return 0;
        // return R.layout.map_layout;
    }

    @Override
    protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
        // TODO Auto-generated method stub
        return mCallbackHandler;
    }

    @Override
    protected void loadContents() {

        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                ObjectLoader temp = new ObjectLoader(getApplicationContext(), metaioSDK, mRadar);
                temp.LoadARcontent();
                mRadar = GlobalResource.getRadar();

                int mGestureMask = GestureHandler.GESTURE_ROTATE;
                mGestureHandler = new GestureHandlerAndroid(metaioSDK, mGestureMask);
                ObjectGroup t1 = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_FISHING);
                for (Map.Entry<String, ObjectDetail> t : t1.getObjectDetailList().entrySet()) {
                    if (t.getKey().split("_")[1].equals(ObjectID.WATER_VALVE))
                        mGestureHandler.addObject(t.getValue().getModel(), 1);
                }
                mGestureHandler.setRotationAxis('y');

            }
        });


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (SDK_ready == 1) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchEffectView.setGlowX(event.getX());
                touchEffectView.setGlowY(event.getY());
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                touchEffectView.setVisibility(View.GONE);
            }
            if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_FISHING) {
                mFishingManager.checkFishingOnTouch(v, event);
            }
            mGestureHandler.onTouch(v, event);
        }
        return super.onTouch(v, event);
    }


    @Override
    protected void onGeometryTouched(IGeometry geometry) {

        int GAME_STATE = GlobalResource.getGAME_STATE();
        HashMap<Integer, PlayerItem> playerItemHashMap = Player.getPlayerItems();
        if (GAME_STATE == GlobalResource.STATE_GATHERING) {
            mGameGeneretor.checkGatheringGeometryTouch(geometry.getName(), playerItemHashMap);
        } else if (GAME_STATE == GlobalResource.STATE_DEAD || GAME_STATE == GlobalResource.STATE_HEALING) {
            mGameGeneretor.stopTimer();
            mGameGeneretor.checkHealingGeometryTouch(geometry, GAME_STATE);
        } else if (GAME_STATE == GlobalResource.STATE_MISSION) {
            mMissionOne.checkMissionState();
            mGameGeneretor.stopTimer();
        } else if (GAME_STATE == GlobalResource.STATE_FISHING) {
            mFishingManager.checkFishingOnGeometryTouch(geometry, playerItemHashMap, mGameGeneretor);
            mGameGeneretor.stopTimer();
        } else if (GAME_STATE == GlobalResource.STATE_SHOPPING) {
            mList.get(Constants.STORE_LAYOUT).setVisibility(View.VISIBLE);
            mGameGeneretor.stopTimer();
        } else if (GAME_STATE == GlobalResource.STATE_PETTING) {
            mGameGeneretor.stopTimer();
            mPettingManager.checkGeometryPetTouch(geometry);
        } else {
            if (GlobalResource.getMISSION_STATE() >= Mission_ONE.STATE_WIN_BOSS
                    && GlobalResource.getGAME_STATE() == GlobalResource.STATE_MARKER) {
                mMissionOne.bossFear();
                mGameGeneretor.stopTimer();
            } else {
                touchEffectView.setVisibility(View.VISIBLE);
                mGameGeneretor.checkLocationGeometryTouch(geometry.getName(), GAME_STATE, mRadar, playerItemHashMap, mGameGeneretor);
            }
        }
    }

    public void onButtonClick(View v) {
        mList.get(2).setVisibility(View.GONE);
        ViewState = -1;
    }

    @Override
    protected void startCamera() {
        // Select the back facing camera by default
        final int cameraIndex = SystemInfo
                .getCameraIndex(CameraInfo.CAMERA_FACING_BACK);

        metaioSDK.startCamera(cameraIndex, 1024, 576);
    }

    public class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            double Lng, Lat, Alt;
            Lat = intent.getDoubleExtra("Latitude", 0);
            Lng = intent.getDoubleExtra("Longitude", 0);
            Alt = intent.getDoubleExtra("Altitude", 0);

            if (SDK_ready == 0 && mList.size() == mlist_size
                    && cLocation != null) {
                initResource();
            }
            cLocation = new Location("");
            cLocation.setLatitude(Lat);
            cLocation.setLongitude(Lng);
            if (dlocation.size() != 0 && index != -1) {
                double lat = dlocation.get(index).getLatitude();
                double lng = dlocation.get(index).getLongitude();
                cLocation.setLatitude(lat);
                cLocation.setLongitude(lng);
                mSensors.setManualLocation(new LLACoordinate(lat, lng, 0, 0));
            }
            //bearing
            GeomagneticField field = new GeomagneticField((float) Lat,
                    (float) Lng, (float) Alt, System.currentTimeMillis());
            mDeclination = field.getDeclination();

            //check map object location
            if (SDK_ready == 1) {
                mGameGeneretor.gameChecking(cLocation);
            }

        }
    }


    final class MetaioSDKCallbackHandler extends IMetaioSDKCallback {


        @Override
        public void onSDKReady() {
            super.onSDKReady();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mList.size() == 0) {

                        // add map layout
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.map_layout, null);
                        mList.add(mGUIView);
                        mGUIView.setVisibility(View.GONE);
                        addContentView(mGUIView, new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT));
                        //camera overlay
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.overlay_camera_layout, null);
                        mList.add(mGUIView);
                        addContentView(mGUIView, new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT));
                        mGUIView.setVisibility(View.GONE);
                        //craft detail View
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.mycraft_main, null);
                        mList.add(mGUIView);
                        Resources r = getResources();
                        float px_w = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, r.getDisplayMetrics());
                        float px_h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, r.getDisplayMetrics());
                        FrameLayout.LayoutParams temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);
                        touchEffectView = (TouchEffectView) mList.get(Constants.OVERLAY_LAYOUT).findViewById(R.id.touch_effect);

                        //store view
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.overlay_store, null);
                        mList.add(mGUIView);
                        px_w = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 580, r.getDisplayMetrics());
                        px_h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);

                        //store craft
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.overlay_craft_recipe, null);
                        mList.add(mGUIView);
                        px_w = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, r.getDisplayMetrics());
                        px_h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, r.getDisplayMetrics());
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);
                        // toast layout
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
                        mList.add(layout);
                        //heal layout
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.overlay_healing, null);
                        mList.add(mGUIView);
                        px_w = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 390, r.getDisplayMetrics());
                        px_h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 269, r.getDisplayMetrics());
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);
                        //buy potion layout
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.dialog_buying, null);
                        mList.add(mGUIView);
                        px_w = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 640, r.getDisplayMetrics());
                        px_h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, r.getDisplayMetrics());
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);
                        //pet layout
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.petting_layout, null);
                        mList.add(mGUIView);
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);

                        //Myitem Layout
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.overlay_myitem_list, null);
                        mList.add(mGUIView);
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);

                        //MyEquipment Layout
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.overlay_my_equip_list, null);
                        mList.add(mGUIView);
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);

                        //selling Layout
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.dialog_selling, null);
                        mList.add(mGUIView);
                        temp = new FrameLayout.LayoutParams((int) px_w, (int) px_h);
                        temp.gravity = Gravity.CENTER;
                        addContentView(mGUIView, temp);
                        mGUIView.setVisibility(View.GONE);

                        //set global
                        mlist_size = mList.size();
                        GlobalResource.setListOfViews(mList);
                    }
                }
            });

        }

        @Override
        public void onTrackingEvent(TrackingValuesVector trackingValues) {
            found = 0;
            for (int i = 0; i < trackingValues.size(); i++) {
                final TrackingValues v = trackingValues.get(i);
                found = v.getCoordinateSystemID();
                Log.i("kak", "Tracking state for COS " + v.getCoordinateSystemID() + " is " + v.getState() + found);
                if (v.getState().compareTo(ETRACKING_STATE.ETS_INITIALIZED) == 0)
                    startCount = true;
                if (v.getState().compareTo(ETRACKING_STATE.ETS_FOUND) == 0) {
                    if (gameState <= 2 && gameState > 0) {
                        mVibration.vibrate(100);
                        gameState += 2;
                    }
                    if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_MARKER) {
                        /*
                        Map<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_BOSS).getObjectDetailList();
                        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
                            t.getValue().getModel().setScale(0.2f);
                            t.getValue().getModel().startAnimation("loop", true);
                        }
                        */

                        //t.setScale(0.3f);
                        //t.setAnimationSpeed(200);
                        //t.startAnimation("loop");
                    }
                    if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_GATHERING) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mGameGeneretor.startCountdownTimer(10000);
                            }
                        });
                    }
                    startCount = false;
                    timeCount = 0;
                }
            }
        }

        @Override
        public void onAnimationEnd(final IGeometry geometry, final String animationName) {

            if ((animationName.equals("falling") || animationName.equals("falling_right")
                    || animationName.equals("falling_left"))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGameGeneretor.playerGetHit(100, true);
                        mGameGeneretor.checkMeteor(geometry);
                    }
                });
            }
            if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_MARKER) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGameGeneretor.getBossManager().checkOnAnimationEnd(animationName, geometry, mGameGeneretor);
                    }
                });
            }
            if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_MIST) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMistManager.onMistModelAnimationEnd(animationName, geometry);
                    }
                });
            }
            /*
            if (mGameGeneretor.isHitting() && animationName.equals("loop")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGameGeneretor.playerGetHit();
                    }
                });
            }*/

        }

        @Override
        public void onInstantTrackingEvent(boolean success, String file) {
            if (success) {
                MetaioDebug
                        .log("MetaioSDKCallbackHandler.onInstantTrackingEvent: "
                                + file);
                metaioSDK.setTrackingConfiguration(file);
                //mMapObjectManager.setVisibilityCollectItem(true);
            } else {
                MetaioDebug.log(Log.ERROR,
                        "Failed to create instant tracking configuration!");
            }
        }
    }
}