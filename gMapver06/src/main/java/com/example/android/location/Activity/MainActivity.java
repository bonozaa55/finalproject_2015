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
import android.os.Bundle;
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

import com.example.android.location.GameManagement.GameGenerator;
import com.example.android.location.GameManagement.HealManager;
import com.example.android.location.GameManagement.MapObjectManager;
import com.example.android.location.GameManagement.ObjectDetailManager;
import com.example.android.location.GameManagement.StoreManager;
import com.example.android.location.Interface.MyCraftMissionManager;
import com.example.android.location.Interface.TouchEffectView;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDetail;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Item.ItemsLoader;
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
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.ETRACKING_STATE;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.SystemInfo;
import com.metaio.tools.io.AssetsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ARViewActivity {

    public static final String FRAGTAG = "ImmersiveModeFragment";
    public static Vibrator mVibration;
    static Context applicationContext,this_Context;
    static TouchEffectView touchEffectView;
    static int count = 0, count2 = 0;
    static GameGenerator mGameGeneretor;
    static ImmersiveModeFragment immersiveModeFragment;
    private static TextView tBearing;
    private static double phoneHeading;
    SensorManager mSensorManager;
    int found = 0, SDK_ready = 0, index = -1;
    int ViewState = -1;
    ObjectDetailManager mObjectDetailManager;
    double mAccelCurrent;
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
    private boolean startCount = false;
    private int gameState = 0;
    private MapObjectManager mMapObjectManager;
    private HealManager mHealManager;

    public static double getPhoneHeading() {
        return MainActivity.phoneHeading;
    }

    public static Context getThisContext() {
        return applicationContext;
    }

    public static int getPlayerItemQuantity(HashMap<Integer, PlayerItem> playerItemHashMap, int id) {
        PlayerItem t = playerItemHashMap.get(id);
        int playerItemQuantity = 0;
        if (t != null)
            playerItemQuantity = t.getQuantity();
        return playerItemQuantity;
    }
/*
    private void checkDistanceToTarget()
    {

        // get tracing values for COS 1
        final TrackingValues tv = metaioSDK.getTrackingValues(1);

        // check if the current state is tracking
        // Note, you can use this mechanism also to detect if something is tracking or not.
        // (e.g. for triggering an action as soon as some target is visible on screen)

        if (tv.isTrackingState())
        {
            // get the translation part of the pose
            final Vector3d translation = tv.getTranslation();

            // calculate the distance as sqrt( x^2 + y^2 + z^2 )
            final float distanceToTarget = translation.norm();

            // define a threshold distance
            final float threshold = 800;

            // if we are already close to the model
            if (mIsCloseToModel)
            {
                // if our distance is larger than our threshold (+ a little)
                if (distanceToTarget > (threshold + 10))
                {
                    // we flip this variable again
                    mIsCloseToModel = false;
                    // and start the close_up animation
                    mMetaioMan.startAnimation("close_up", false);
                }
            }
            else
            {
                // we're not close yet, let's check if we are now
                if (distanceToTarget < threshold)
                {
                    // flip the variable
                    mIsCloseToModel = true;
                    // and start an animation
                    mMetaioMan.startAnimation("close_down", false);
                }
            }
        }
    }
    */

    public static void makeToastItem(int itemID, int quantity) {
        ItemDetail t = ItemsLoader.getItemList().get(itemID);
        View layout = GlobalResource.getListOfViews().get(Constants.TOAST_LAYOUT);
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(t.getIconResource());
        TextView text = (TextView) layout.findViewById(R.id.text);
        if (itemID == ItemsID.GOLD)
            text.setText("Receive " + t.getName() + " " + quantity + " gold!");
        else
            text.setText("Receive " + t.getName() + " " + quantity + " ea!");

        Toast toast = new Toast(applicationContext);
        toast.setGravity(Gravity.TOP, 50, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showHealingDialog() {
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
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setTitle("Healing Mode");

        dialog.findViewById(R.id.dialogBtnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mGameGeneretor.notifyEvent(GlobalResource.STATE_HEALING);
            }
        });

        dialog.findViewById(R.id.dialogBtnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mGameGeneretor.resetState(GlobalResource.STATE_DEAD);
            }
        });
        ((TextView) dialog.findViewById(R.id.dialogTxt)).setText("Do you want to healing again?");
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }


    public static void makeToast(String message) {
        View layout = GlobalResource.getListOfViews().get(Constants.TOAST_LAYOUT);
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.icon_star);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(applicationContext);
        toast.setGravity(Gravity.TOP, 50, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new FrameLayout(this));
        lReceiver = new LocationReceiver();
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
        initSensor();
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(mOriantationListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mAccelerometerListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        registerReceiver(lReceiver, new IntentFilter("Location"));
        startService(intent_location);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mOriantationListener);
        mSensorManager.unregisterListener(mAccelerometerListener);
        unregisterReceiver(lReceiver);
        stopService(intent_location);
        super.onPause();
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
        if (startCount)
            timeCount++;
        if (timeCount >= maxTime) {
            if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_GATHERING)
                metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
            timeCount = 0;
        }
        // checkDistanceToTarget();
    }

    public void initResource() {
        initInterface();
        Player player = new Player();
        /*
        Player.getPlayerItems().put(ItemsID.GOLD,new PlayerItem(ItemsID.GOLD,1000));
        Player.getPlayerItems().put(ItemsID.ORE,new PlayerItem(ItemsID.ORE,3));
        */
        applicationContext = getApplicationContext();
        this_Context=MainActivity.this;
        ItemsLoader itemsData = new ItemsLoader();
        SDK_ready = 1;
        mMapObjectManager = new MapObjectManager(metaioSDK);
        //  mLocationBasedManager = new LocationObjectManager(mRadar, metaioSDK);
        //  mMarkerObjectManager = new MarkerObjectManager(metaioSDK);
        mObjectDetailManager = new ObjectDetailManager(metaioSDK, mRadar, this);
        mHealManager = new HealManager();
        mGameGeneretor = new GameGenerator(this, metaioSDK, mMapObjectManager, mObjectDetailManager, map);

    }

    public void initInterface() {

        View mapLayout = mList.get(Constants.MAP_LAYOUT);
        final View overlayLayout = mList.get(Constants.OVERLAY_LAYOUT);
        View craftLayout = mList.get(2);
        final View storeLayout = mList.get(3);
        StoreManager mStoreManager = new StoreManager(this);


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
                2000,
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
        View potionInterface = overlayLayout.findViewById(R.id.overlay_potions_interface);
        potionInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View t = overlayLayout.findViewById(R.id.overlay_group_item);
                View t2 = overlayLayout.findViewById(R.id.overlay_group_quest);
                if (t.getVisibility() == View.INVISIBLE) {
                    t.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                } else {
                    t.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                }

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
        View questInterface = overlayLayout.findViewById(R.id.overlay_quest_interface);
        questInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeLayout.setVisibility(View.VISIBLE);
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

                    boolean x = check || (GlobalResource.getGAME_STATE() != GlobalResource.STATE_IDLE
                            && GlobalResource.getGAME_STATE() != GlobalResource.STATE_DEAD);
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

                    if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_DEAD)
                        mList.get(Constants.MAP_LAYOUT).findViewById(R.id.map_dead).setVisibility(View.VISIBLE);
                    if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_HEALING) {
                        mAccelCurrent = mHealManager.calculateShakeValue(event.values, mAccelCurrent);
                        mList.get(Constants.MAP_LAYOUT).findViewById(R.id.map_dead).setVisibility(View.GONE);
                    }
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
        //sLocation = "1";
        dlocation.add(new LLACoordinate(18.795526f, 98.953083f, 0, 0));
        dlocation.add(new LLACoordinate(18.796425f, 98.953134f, 0, 0));
        dlocation.add(new LLACoordinate(18.796535f, 98.952877f, 0, 0));
    }

    public void BackToNormal(View v) {
        mGameGeneretor.resetState(GlobalResource.STATE_GATHERING);
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

        ObjectLoader temp = new ObjectLoader(getApplicationContext(), metaioSDK, mRadar);
        temp.LoadARcontent();
        mRadar = GlobalResource.getRadar();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchEffectView.setGlowX(event.getX());
            touchEffectView.setGlowY(event.getY());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchEffectView.setVisibility(View.GONE);
        }
        return super.onTouch(v, event);
    }

    @Override
    protected void onGeometryTouched(IGeometry geometry) {
        int GAME_STATE = GlobalResource.getGAME_STATE();
        HashMap<Integer, PlayerItem> playerItemHashMap = Player.getPlayerItems();
        if (GAME_STATE == GlobalResource.STATE_GATHERING) {
            if (geometry.getName().equals("ore")) {
                int playerOreQuantity = getPlayerItemQuantity(playerItemHashMap, ItemsID.ORE);
                playerItemHashMap.put(ItemsID.ORE, new PlayerItem(ItemsID.ORE, playerOreQuantity + 1));
                makeToastItem(ItemsID.ORE, 1);
            }
            if (geometry.getName().equals("grass")) {
                int playerGrassQuantity = getPlayerItemQuantity(playerItemHashMap, ItemsID.GRASS);
                playerItemHashMap.put(ItemsID.GRASS, new PlayerItem(ItemsID.GRASS, playerGrassQuantity + 1));
                makeToastItem(ItemsID.GRASS, 1);
            }
            geometry.setVisible(false);
            mGameGeneretor.resetState(GAME_STATE);
        }
        if (GAME_STATE == GlobalResource.STATE_DEAD || GAME_STATE == GlobalResource.STATE_HEALING) {
            mGameGeneretor.checkHealingGeometryTouch(geometry);
        } else {
            touchEffectView.setVisibility(View.VISIBLE);
            mGameGeneretor.checkLocationGeometryTouch(geometry.getName(), GAME_STATE, mRadar, playerItemHashMap, mGameGeneretor);
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
                        mGameGeneretor.playerGetHit(100);
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
            /*
            if (mGameGeneretor.isHitting() && animationName.equals("loop")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGameGeneretor.playerGetHit();
                    }
                });
            }*/
            super.onAnimationEnd(geometry, animationName);
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