package com.example.android.location.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.hardware.Camera.CameraInfo;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.location.Interface.CustomView;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.LocalbasedMonster;
import com.example.android.location.Resource.MapObject;
import com.example.android.location.Resource.MarkerMonster;
import com.example.android.location.Resource.ResourceManager;
import com.example.android.location.Util.BackgroundLocationService;
import com.example.android.location.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.ETRACKING_STATE;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.SystemInfo;
import com.metaio.tools.io.AssetsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends ARViewActivity{

    static CustomView touchEffectView;


    static boolean dialogState = false,geometryTouch=false;
    private static TextView tBearing;
    public static Vibrator mVibration;
    DialogInterface.OnClickListener dialogClickListener;
    HashMap<String, String> markersIDAndType = new HashMap<String, String>();
    SensorManager mSensorManager;
    ArrayList<MarkerMonster> markerMonster = new ArrayList<MarkerMonster>();
    ArrayList<LocalbasedMonster> localbaseMonster = new ArrayList<LocalbasedMonster>();
    ArrayList<MapObject> mapCollectableItemList = new ArrayList<MapObject>();
    float phone_heading;
    static int count = 0,hitPoint=100;
    int layoutID = R.layout.overlay_camera_layout;
    int found = 0, SDK_ready = 0, index = -1;
    IGeometry gLocalMonster;
    IGeometry gMarkerMonster;
    IGeometry gStoreObject;
    LocalbasedMonster foundedLocalMonster;
    MarkerMonster foundedMarkerMonster;
    boolean pause = true;
    int ViewState = -1;
    private Button resumeMapButton;
    private LocationReceiver lReceiver;
    private Intent intent_location;
    private Location cLocation;
    private String sLocation = "";
    private GoogleMap map;
    private float mDeclination;
    private boolean hRotation = true;
    private ArrayList<LLACoordinate> dlocation = new ArrayList<LLACoordinate>();
    private ArrayList<IGeometry> CollectingModelPack;
    private IRadar mRadar;
    private SensorEventListener mOriantationListener, mAccelerometerListener;
    private ArrayList<View> mList = new ArrayList<View>();
    private int mlist_size = 3;
    private MetaioSDKCallbackHandler mCallbackHandler;
    private int timeCount = 0;
    private int maxTime = 200;
    private boolean startCount = false, collectItemState = false, locationBasedMonsterState = false,markerBasedMonsterState=false;
    private int gameState = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new FrameLayout(this));
        lReceiver = new LocationReceiver();
        intent_location = new Intent(this, BackgroundLocationService.class);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            if(collectItemState)
                metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
            if(markerBasedMonsterState)
                touchEffectView.setVisibility(View.GONE);
            timeCount = 0;
        }
        // checkDistanceToTarget();
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
                    phone_heading = bearing;
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
                        && mList.size() == mlist_size
                        && (ViewState == Constants.MAP_VISIBLE
                        || ViewState == Constants.metaio_VISIBLE || ViewState == -1)) {
                    count++;
                    float z_value = event.values[2];
                    // Log.i("kak",
                    // show map only a phone flip up
                    /*Log.i("kak", event.values[0] + " " + event.values[1] + " "
                            + event.values[2]);*/
                    if (count > 20) {
                        if (z_value >= 8 && !collectItemState) {// map
                            mList.get(Constants.OVERLAY_LAYOUT).setVisibility(View.GONE);
                            mList.get(Constants.MAP_LAYOUT).setVisibility(View.VISIBLE);
                            ViewState = Constants.MAP_VISIBLE;
                            if (pause) {
                                metaioSDK.pauseTracking();
                                pause = false;
                            }
                        } else {
                            mList.get(Constants.MAP_LAYOUT).setVisibility(View.GONE);
                            mList.get(Constants.OVERLAY_LAYOUT).setVisibility(View.VISIBLE);
                            ViewState = Constants.metaio_VISIBLE;
                            if (!pause) {
                                metaioSDK.resumeTracking();
                                pause = true;
                            }
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

    /**
     * ******************************
     */
    public void AddAllMarker() {

        map.clear();


        sLocation = "1";
        LocalbasedMonster tMonster = new LocalbasedMonster(18.796535, 98.952877, -130f, 30, 15);
        /*LocalbasedMonster tMonster2 = new LocalbasedMonster(18.796526,
                98.952473, -130f, 30, 20);*/
        /*
         * LocalbasedMonster tMonster3 = new LocalbasedMonster(18.799983,
		 * 98.9597227, -130f, 30, 20);
		 */
        localbaseMonster.add(tMonster);
        //localbaseMonster.add(tMonster2);
        // localbaseMonster.add(tMonster3);
        MarkerMonster t1 = new MarkerMonster(18.796425, 98.953134, 130f, 45,
                15, "ENG");
        /*MarkerMonster t1 = new MarkerMonster(18.799983, 98.9597227, -130f, 45,
                20, "ENG");*/
        markerMonster.add(t1);
        MarkerMonster t2 = new MarkerMonster(18.795768, 98.952795, 130f, 45,
                20, "SPIC");
        markerMonster.add(t2);
        for (LocalbasedMonster t : localbaseMonster)
            addMarker(t.getLocation(), R.drawable.monster2, "Monster");
        for (MarkerMonster t : markerMonster)
            addMarker(t.getLocation(), R.drawable.monster_marker_icon,
                    "Monster");
        Location temp = new Location("");
        temp.setLatitude(18.795749);
        temp.setLongitude(98.952889);
        /*
         * temp.setLatitude(18.799983); temp.setLongitude(98.9597227);
		 */
        addMarker(temp, R.drawable.store2, "Store");
        // 18.795726f,98.952469f,0.000886f,0.00015f
        dlocation.add(new LLACoordinate(18.795526f, 98.953083f, 0, 0));
        dlocation.add(new LLACoordinate(18.796425f,98.953134f, 0, 0));
        dlocation.add(new LLACoordinate(18.796535f, 98.952877f, 0, 0));



       /*
        temp = new Location("");
        temp.setLatitude(18.795946);
        temp.setLongitude(98.952497);
        MapObject t = new MapObject(temp, 30, 20, 180);
        mapCollectableItemList.add(t);
        addMarker(temp, R.drawable.treasure_icon, "x");*/

    }
    public void addMarker(Location location, int icon, String type) {
        MarkerOptions temp = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .anchor(0.0f, 0.0f)
                .flat(true)
                .position(new LatLng(location.getLatitude(), location.getLongitude()));
        Marker x = map.addMarker(temp);
        markersIDAndType.put(x.getId(), type);
    }
    private void FoundMarkerMonster(final MarkerMonster mMonster) {
        String filepath = AssetsManager.getAssetPath( getApplicationContext(),"TutorialDynamicModels/Assets/MarkerConfig_"
                        + mMonster.getImagePath() + ".xml");
        metaioSDK.setTrackingConfiguration(filepath);
        setMarkerMonsterState(true);
        loadMarkerMonster(mMonster);
        foundedMarkerMonster=mMonster;
    }

    private void FoundLocalMonster(LocalbasedMonster lMonster) {
        metaioSDK.setTrackingConfiguration("GPS", false);
        setLocaltioBasedMonsterState(true);
        loadLocalMonster(lMonster);
        foundedLocalMonster=lMonster;
    }

    private void loadLocalMonster(LocalbasedMonster lMonster) {
        LLACoordinate location = new LLACoordinate(lMonster.getLocation()
                .getLatitude(), lMonster.getLocation().getLongitude(), 0, 0);
        gLocalMonster.setTransparency(0);
        gLocalMonster.setTranslationLLA(location);
        gLocalMonster.setVisible(true);
        mRadar.setVisible(true);
    }

    private void unloadLocalMonster() {
        gLocalMonster.setVisible(false);
        mRadar.setVisible(false);
    }

    private void loadMarkerMonster(MarkerMonster mMonster) {
        //add layout

        gMarkerMonster.setTransparency(0);
        gMarkerMonster.setTranslation(new Vector3d(0, -20f, -200f));
        gMarkerMonster.setVisible(true);

    }

    private void unLoadMarkerMonster() {
        gMarkerMonster.setVisible(false);
    }

    private void NotifyEvent(final int eventNo,final LocalbasedMonster t1,final MarkerMonster t2) {
        mVibration.vibrate(300);
        if (eventNo == 0) {
            Toast.makeText(getApplicationContext(), "Gathering", Toast.LENGTH_SHORT).show();
            setCollectingState(true);
        }
        if (eventNo == 1) {
            Toast.makeText(getApplicationContext(), "Location monster", Toast.LENGTH_SHORT).show();
            FoundLocalMonster(t1);
        }
        if (eventNo == 2){
            Toast.makeText(getApplicationContext(), "BOSS!!", Toast.LENGTH_SHORT).show();
            FoundMarkerMonster(t2);
        }
        /*dialogState = true;
        String s[]={"Found something around here!","Found monster around here","Found BOSS monster!!"};
        String s1[]={"Want to get it?","You wanna fight?","Want to talk with him?"};
        int n[]={R.drawable.treasure_icon,R.drawable.monster2,R.drawable.monster_marker_icon};
        new AlertDialog.Builder(this)
                .setTitle(s[eventNo])
                .setIcon(n[eventNo])
                .setMessage(s1[eventNo])
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                if (eventNo == 0)
                                    setCollectingState(true);
                                if (eventNo == 1) {
                                    FoundLocalMonster(t1);
                                }
                                if (eventNo == 2){
                                    FoundMarkerMonster(t2);
                                 }
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                metaioSDK.setTrackingConfiguration("DUMMY",
                                        false);
                                dialogState = false;
                            }
                        }).show();*/
    }

    private boolean checkLocationDistance(Location monsterLocation, double maxDistance) {
        if (monsterLocation.distanceTo(cLocation) <= maxDistance) {
            return true;
        } else
            return false;
    }

    private boolean CheckPhoneHeading(double acceptableAngle, double initialAngle) {
        double difOfAngle = phone_heading - initialAngle;
        if (difOfAngle > 180) difOfAngle -= 360;
        if (difOfAngle < -180) difOfAngle += 360;
        if (Math.abs(difOfAngle) <= acceptableAngle)
            return true;
        else
            return false;
    }

    private boolean CheckLocationArea(float pointLat, float pointLng, float height, float width, Location target) {
        if ((target.getLatitude() >= pointLat && target.getLatitude() <= height + pointLat) &&
                (target.getLongitude() >= pointLng && target.getLongitude() <= width + pointLng))
            return true;
        else
            return false;
    }

    private void FoundMapItem() {
        metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
       // setVisibilityCollectItem(true);
        //gameState = 2;
    }

    public void setVisibilityCollectItem(boolean parameter) {
        View motherView = mList.get(Constants.OVERLAY_LAYOUT);
        if(parameter)
            motherView.findViewById(R.id.backToNormal).setVisibility(View.VISIBLE);
        else
            motherView.findViewById(R.id.backToNormal).setVisibility(View.GONE);
        for (IGeometry t : CollectingModelPack)
            t.setVisible(parameter);
    }

    public void setVisibilityLocationBasedMonster(boolean parameter) {
        View motherView = mList.get(Constants.OVERLAY_LAYOUT);
        gLocalMonster.setVisible(parameter);
    }


    public void setGeneralState(boolean parameter){
        if(!parameter) {
            unLoadMarkerMonster();
            setVisibilityLocationBasedMonster(false);
            setVisibilityCollectItem(false);
            gameState = 0;
            dialogState = false;
            metaioSDK.setTrackingConfiguration("DUMMY", false);
        }else{
            hitPoint=100;
            gameState=1;
            dialogState=true;
        }

    }

    public void setCollectingState(boolean parameter){
        setGeneralState(parameter);
        collectItemState = parameter;
        setVisibilityCollectItem(parameter);
    }

    public void setMarkerMonsterState(boolean parameter){
        setGeneralState(parameter);
        markerBasedMonsterState=parameter;
        if(!parameter)
            unloadLocalMonster();
    }

    public void setLocaltioBasedMonsterState(boolean parameter){
        setGeneralState(parameter);
        locationBasedMonsterState=parameter;
        //setVisibilityLocationBasedMonster(parameter);
        if(!parameter)
            unloadLocalMonster();
    }

    public void BackToNormal(View v) {
        setCollectingState(false);
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
        ResourceManager temp = new ResourceManager(getApplicationContext(), metaioSDK, mRadar);
        temp.LoadARcontent();
        gLocalMonster = GlobalResource.getmLocationModel();
        gMarkerMonster = GlobalResource.getmMarkerModel();
        gStoreObject = GlobalResource.getmStoreModel();
        mRadar = GlobalResource.getmRadar();
        CollectingModelPack = GlobalResource.getCollectingModelPack();
    }



    private void tryDrawing(SurfaceHolder holder) {
        Log.i("www", "Trying to draw...");

        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            Log.e("www", "Cannot draw onto the canvas as it's null");
        } else {
            drawMyStuff(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawMyStuff(final Canvas canvas) {
        Random random = new Random();
        Log.i("www", "Drawing...");
        canvas.drawRGB(255, 128, 128);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_DOWN){
            touchEffectView.setGlowX(event.getX());
            touchEffectView.setGlowY(event.getY());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchEffectView.setVisibility(View.GONE);
            //gMarkerMonster.setTransparency(0);
        }
        return super.onTouch(v, event);
    }

    @Override
    protected void onGeometryTouched(IGeometry geometry) {
        if (geometry.getFadeInTime() == 501) {
            geometry.setVisible(false);
            setCollectingState(false);
            Toast.makeText(getApplicationContext(), "Got 1 item!", Toast.LENGTH_SHORT).show();
        } else {
            touchEffectView.setVisibility(View.VISIBLE);
            hitPoint-=20;
            //gMarkerMonster.startAnimation("create");
            if (hitPoint==0) {
                if (geometry.equals(gLocalMonster))
                    setLocaltioBasedMonsterState(false);
                if (geometry.equals(gMarkerMonster))
                    setMarkerMonsterState(false);
                Toast.makeText(getApplicationContext(), "You had slain the enemy", Toast.LENGTH_SHORT).show();
            }
            /*
            geometry.setTransparency(geometry.getTransparency() + 0.2f);
            if (geometry.getTransparency() == 1) {
                if(geometry.equals(gLocalMonster))
                    setLocaltioBasedMonsterState(false);
                if(geometry.equals(gMarkerMonster))
                    setMarkerMonsterState(false);
                Toast.makeText(getApplicationContext(), "You had slain the enemy", Toast.LENGTH_SHORT).show();
            }
            */
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
                map = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.map)).getMap();
                map.getUiSettings().setZoomControlsEnabled(false);
                LatLng mapCenter = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 18f));
                map.setMyLocationEnabled(true);
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        sLocation = "2";
                    }
                });
                AddAllMarker();
                SDK_ready = 1;
                TextView t = (TextView) mList.get(Constants.MAP_LAYOUT).findViewById(
                        R.id.bearing_text);
                t.setVisibility(View.GONE);
                Button b = (Button) mList.get(Constants.MAP_LAYOUT).findViewById(R.id.switch_location);
                b.setVisibility(View.VISIBLE);
                resumeMapButton = (Button) mList.get(Constants.MAP_LAYOUT).findViewById(R.id.toggleZoom);
                tBearing = (TextView) findViewById(R.id.bearing_text);
                tBearing.setVisibility(View.VISIBLE);
            }
            cLocation = new Location("");
            cLocation.setLatitude(Lat);
            cLocation.setLongitude(Lng);
            if (dlocation.size() != 0 && index != -1) {
                cLocation.setLatitude(dlocation.get(index).getLatitude());
                cLocation.setLongitude(dlocation.get(index).getLongitude());

            }
            //bearing
            GeomagneticField field = new GeomagneticField((float) Lat,
                    (float) Lng, (float) Alt, System.currentTimeMillis());
            mDeclination = field.getDeclination();

            //check map object location
            if (SDK_ready == 1) {
                if (gameState == 1) {
                    if (collectItemState)
                        FoundMapItem();
                    if(locationBasedMonsterState){
                        int distance=(int)foundedLocalMonster.getLocation().distanceTo(cLocation);
                        if(distance>foundedLocalMonster.getDistance()){
                            setLocaltioBasedMonsterState(false);
                        }
                    }
                    if(markerBasedMonsterState){
                        int distance=(int)foundedMarkerMonster.getLocation().distanceTo(cLocation);
                        if(distance>foundedMarkerMonster.getDistance()){
                            setMarkerMonsterState(false);
                        }
                    }

                } else {
                for (MarkerMonster t : markerMonster) {
                    if (checkLocationDistance(t.getLocation(),t.getDistance())&&CheckPhoneHeading(t.getRadius(),t.getInitialAngle())) {
                        if (!dialogState)
                            NotifyEvent(2,null,t);
                    }
                }
                    for (LocalbasedMonster t : localbaseMonster) {
                        if (checkLocationDistance(t.getLocation(),t.getDistance())) {
                            if (!dialogState)
                                NotifyEvent(1,t,null);
                        }
                    }
                    if (CheckLocationArea(18.795526f, 98.953083f, 0.000846f, 0.000206f, cLocation)) {
                    //if (CheckLocationArea(18.795726f, 98.952469f, 0.000886f, 0.00015f, cLocation)) {
                        if (!dialogState)
                            NotifyEvent(0,null,null);
                    }
                }
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
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.overlay_camera_layout, null);
                        mList.add(mGUIView);
                        mGUIView.setVisibility(View.GONE);
                        addContentView(mGUIView, new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT));
                        mGUIView = View.inflate(MainActivity.this,
                                R.layout.main, null);
                        mList.add(mGUIView);
                        mGUIView.setVisibility(View.GONE);
                        touchEffectView=(CustomView)mList.get(Constants.OVERLAY_LAYOUT).findViewById(R.id.touch_effect);
                    }
                }
            });

        }

        @Override
        public void onTrackingEvent(TrackingValuesVector trackingValues) {
            found = 0;
            int x=0;
            for (int i = 0; i < trackingValues.size(); i++) {
                final TrackingValues v = trackingValues.get(i);
                found = v.getCoordinateSystemID();
                Log.i("kak",
                        "Tracking state for COS " + v.getCoordinateSystemID()
                                + " is " + v.getState() + found);
                if (v.getState().compareTo(ETRACKING_STATE.ETS_INITIALIZED) == 0)
                    startCount = true;
                if (v.getState().compareTo(ETRACKING_STATE.ETS_FOUND) == 0) {
                    if(gameState<=2&&gameState>0) {
                        mVibration.vibrate(100);
                        gameState+=2;
                    }
                    if(markerBasedMonsterState)
                        gMarkerMonster.startAnimation("create");
                    startCount = false;
                    timeCount = 0;
                }
            }
        }

        @Override
        public void onInstantTrackingEvent(boolean success, String file) {
            if (success) {
                MetaioDebug
                        .log("MetaioSDKCallbackHandler.onInstantTrackingEvent: "
                                + file);
                metaioSDK.setTrackingConfiguration(file);
                setVisibilityCollectItem(true);
            } else {
                MetaioDebug.log(Log.ERROR,
                        "Failed to create instant tracking configuration!");
            }
        }
    }
}
