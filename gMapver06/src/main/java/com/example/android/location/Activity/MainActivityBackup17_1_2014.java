package com.example.android.location.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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

public class MainActivityBackup17_1_2014 extends ARViewActivity implements
        OnMarkerClickListener {
    private static TextView tBearing;
    DialogInterface.OnClickListener dialogClickListener;
    HashMap<String, String> markersIDAndType = new HashMap<String, String>();
    SensorManager mSensorManager;
    boolean dialog = true;
    ArrayList<MarkerMonster> markerMonster = new ArrayList<MarkerMonster>();
    ArrayList<LocalbasedMonster> localbaseMonster = new ArrayList<LocalbasedMonster>();
    ArrayList<MapObject> mapCollectableItemList = new ArrayList<MapObject>();
    float phone_heading;
    int count = 0;
    int layoutID = R.layout.overlay_camera_layout;
    int found = 0, SDK_ready = 0, index = -1;
    IGeometry gLocalMonster, gMarkerMonster, gStoreObject;
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
    private boolean startCount = false,collectItemState=false,PhoneHeadingState=false;
    private int gameState=0;
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
            if(PhoneHeadingState) {
                metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
                timeCount = 0;
            }
        }
        // checkDistanceToTarget();
    }

    public void initSensor() {
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
                    if(SDK_ready==1)
                        tBearing.setText(bearing+"");
                    if(gameState<=2&&gameState>0)
                        CheckPhoneHeading(20,90);
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
                        if (z_value >= 8&&!collectItemState) {// map
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
        sLocation = "2";
        LocalbasedMonster tMonster = new LocalbasedMonster(18.795795,
                98.952532, -130f, 30, 20);
        LocalbasedMonster tMonster2 = new LocalbasedMonster(18.796526,
                98.952473, -130f, 30, 20);
        /*
         * LocalbasedMonster tMonster3 = new LocalbasedMonster(18.799983,
		 * 98.9597227, -130f, 30, 20);
		 */
        localbaseMonster.add(tMonster);
        localbaseMonster.add(tMonster2);
        // localbaseMonster.add(tMonster3);
        MarkerMonster t1 = new MarkerMonster(18.796425, 98.953134, -130f, 45,
                20, "ENG");
		/*MarkerMonster t1 = new MarkerMonster(18.799983, 98.9597227, -130f, 45,
                20, "ENG");*/
        markerMonster.add(t1);
        MarkerMonster t2 = new MarkerMonster(18.795768, 98.952795, -130f, 45,
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
        dlocation.add(new LLACoordinate(18.796425, 98.953134, 0, 0));
        dlocation.add(new LLACoordinate(18.90, 98.952996, 0, 0));
        /*dlocation.add(new LLACoordinate(18.796425, 98.953134, 0, 0));
        dlocation.add(new LLACoordinate(18.90, 98.952996, 0, 0));
		dlocation.add(new LLACoordinate(18.795774, 98.952849, 0, 0));
		dlocation.add(new LLACoordinate(18.90, 98.952996, 0, 0));*/
       /* MapObject t=new MapObject(18.799983,98.9597227,30,20,90);
        temp = new Location("");
        temp.setLatitude(18.799983);
        temp.setLongitude(98.9597227);
        mapCollectableItemList.add(t);
        addMarker(temp,R.drawable.armor_icon2,"x");*/

    }

    public void CreateDialog(final Context cont) {
        final Dialog dialog = new Dialog(cont);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);

        Button button1 = (Button) dialog.findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(cont, "Close dialog", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
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

    // marker monster
    private boolean CheckNotifyMarkerMonster(MarkerMonster mMonster) {
        Location temp = new Location(cLocation);
        double distance = temp.distanceTo(mMonster.getLocation());
        double radius = mMonster.getRadius();
        double positive_angle = mMonster.getInitialAngle() + radius;
        double negative_angle = mMonster.getInitialAngle() - radius;
        boolean check;
        check = phone_heading > negative_angle && phone_heading < positive_angle;
        check = true;
        //Log.i("www",distance+"");
        if (check && distance <= mMonster.getDistance())
            return true;
        else
            return false;
    }

    private boolean CheckNotifyLocalMonster(LocalbasedMonster lMonster) {
        Location temp = new Location(cLocation);
        double distance = temp.distanceTo(lMonster.getLocation());
        double radius = lMonster.getRadius();
        double positive_angle = lMonster.getInitialAngle() + radius;
        double negative_angle = lMonster.getInitialAngle() - radius;
        boolean check;
        check = phone_heading > negative_angle && phone_heading < positive_angle;
        check = true;
        if (check && distance <= lMonster.getDistance())
            return true;
        else
            return false;
    }

    private void FoundMarkerMonster(final MarkerMonster mMonster) {
        String filepath = AssetsManager.getAssetPath(
                getApplicationContext(),
                "TutorialDynamicModels/Assets/MarkerConfig_"
                        + mMonster.getImagePath() + ".xml");
        metaioSDK.setTrackingConfiguration(filepath);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        new AlertDialog.Builder(this)
                .setTitle("Found a monster! at " + mMonster.getImagePath())
                .setIcon(R.drawable.monster_marker_icon)
                .setMessage("You wanna fight?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                loadMarkerMonster(mMonster);
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                metaioSDK.setTrackingConfiguration("DUMMY",
                                        false);
                            }
                        }).show();
        dialog = false;
    }

    private void FoundLocalMonster(final LocalbasedMonster lMonster) {
        metaioSDK.setTrackingConfiguration("GPS", false);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        new AlertDialog.Builder(this)
                .setTitle("Found a monster around here!!")
                .setIcon(R.drawable.monster2)
                .setMessage("You wanna fight?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                loadLocalMonster(lMonster);
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                metaioSDK.setTrackingConfiguration("DUMMY",
                                        false);
                            }
                        }).show();
        dialog = false;
    }
    /*
    private void FoundMapItem(final MapObject temp){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        new AlertDialog.Builder(this)
                .setTitle("Found something around here!")
                .setIcon(R.drawable.monster_marker_icon)
                .setMessage("Want to get it?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                collectItemState=true;
                                metaioSDK.startInstantTracking(
                                        "INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
                                loadMapObject();
                                SetVisibilityCollectItemInterface(View.VISIBLE);
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                metaioSDK.setTrackingConfiguration("DUMMY",
                                        false);
                            }
                        }).show();
        dialog = false;
    }
*/


    private void loadLocalMonster(LocalbasedMonster lMonster) {
        LLACoordinate location = new LLACoordinate(lMonster.getLocation()
                .getLatitude(), lMonster.getLocation().getLongitude(), 0, 0);
        gLocalMonster.setTranslationLLA(location);
        gLocalMonster.setVisible(true);
        mRadar.setVisible(true);
    }

    private void unloadMapObject(){
        for(IGeometry t:CollectingModelPack)
            t.setVisible(false);
    }

    private void loadMapObject(){
        for(IGeometry t:CollectingModelPack)
            t.setVisible(true);
    }

    private void unloadLocalMonster() {
        gLocalMonster.setVisible(false);
        mRadar.setVisible(false);
    }

    private void loadMarkerMonster(MarkerMonster mMonster) {
        gMarkerMonster.setTranslation(new Vector3d(0, -20f, -200f));
        gMarkerMonster.setVisible(true);

    }

    private void unLoadMarkerMonster() {
        gMarkerMonster.setVisible(false);
    }

   /* private boolean CheckLocationAndHeading(Location monsterLocation, double acceptableAngle, double initialAngle, double maxDistance) {
        Location temp = new Location(cLocation);
        double distance = temp.distanceTo(monsterLocation);
        double difOfAngle;
        difOfAngle = phone_heading - initialAngle;
        if (difOfAngle > 180) difOfAngle -= 360;
        if (difOfAngle < -180) difOfAngle += 360;
        if (Math.abs(difOfAngle) <= acceptableAngle && distance <= maxDistance)
            return true;
        else
            return false;
    }*/
   private void NotifyEvent(int eventNo){
       Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
       v.vibrate(500);
       new AlertDialog.Builder(this)
               .setTitle("Found something around here!")
               .setIcon(R.drawable.monster_marker_icon)
               .setMessage("Want to get it?")
               .setPositiveButton(android.R.string.yes,
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog,
                                               int which) {
                               gameState=1;
                               collectItemState=true;
                               SetVisibilityCollectItemInterface(View.VISIBLE);
                           }
                       })
               .setNegativeButton(android.R.string.no,
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog,
                                               int which) {
                               metaioSDK.setTrackingConfiguration("DUMMY",
                                       false);
                           }
                       }).show();
       dialog=false;
   }
   private boolean CheckLocationDistance(Location monsterLocation,  double maxDistance) {
       if (monsterLocation.distanceTo(cLocation) <= maxDistance) {
           if(dialog)
                NotifyEvent(0);
           return true;
       }else
           return false;
   }
    private void CheckPhoneHeading(double acceptableAngle, double initialAngle){
        double difOfAngle=phone_heading - initialAngle;
        if (difOfAngle > 180) difOfAngle -= 360;
        if (difOfAngle < -180) difOfAngle += 360;
        if(Math.abs(difOfAngle) <= acceptableAngle){
            PhoneHeadingState=true;
            if(gameState==1)
                FoundMapItem();
        }else
            PhoneHeadingState=false;
        Log.i("www",PhoneHeadingState+"");
    }

    private void FoundMapItem(){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);
        metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
        loadMapObject();
        SetVisibilityCollectItemInterface(View.VISIBLE);
        gameState=2;
        //metaioSDK.setTrackingConfiguration("DUMMY",false);
    }

    public void SetVisibilityCollectItemInterface(int parameter){
        View motherView=mList.get(Constants.OVERLAY_LAYOUT);
        motherView.findViewById(R.id.backToNormal).setVisibility(parameter);
    }

    public void BackToNormal(View v){
        collectItemState=false;
        SetVisibilityCollectItemInterface(View.GONE);
        metaioSDK.setTrackingConfiguration("DUMMY",false);
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
            LatLng cameraLocation = map.getCameraPosition().target;
            Location temp = new Location("temp");
            temp.setLatitude(cameraLocation.latitude);
            temp.setLongitude(cameraLocation.longitude);
            float distance = cLocation.distanceTo(temp);
            if (map.getCameraPosition().zoom == 18f && (distance < 5 || sLocation == "2")) {
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

    private void loadStoreObject() {
        gMarkerMonster.setVisible(false);
        gLocalMonster.setVisible(false);
        gStoreObject.setVisible(true);
    }

    private void unloadStoreObject() {
        gStoreObject.setVisible(false);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (markersIDAndType.get(marker.getId()) == "Store") {
            CreateDialog(this);
            new AlertDialog.Builder(this)
                    .setTitle("Kaioshin's Shop")
                    .setIcon(R.drawable.store_icon)
                    .setMessage("Wanna buy something?")
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    String filepath = AssetsManager
                                            .getAssetPath(
                                                    getApplicationContext(),
                                                    "TutorialDynamicModels/Assets/MarkerConfig_Store.xml");
                                    boolean result = metaioSDK
                                            .setTrackingConfiguration(filepath);
                                    loadStoreObject();
                                }
                            })
                    .setNegativeButton(android.R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // do nothing
                                }
                            }).show();
        }
        return true;
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


    @Override
    protected void onGeometryTouched(IGeometry geometry) {
        if(geometry.getFadeInTime()==501) {
            geometry.setVisible(false);
            collectItemState = false;
            SetVisibilityCollectItemInterface(View.GONE);
            metaioSDK.setTrackingConfiguration("DUMMY", false);
            Toast.makeText(getApplicationContext(), "Got 1 item!", Toast.LENGTH_SHORT).show();
        }else {
            geometry.setTransparency(geometry.getTransparency() - 0.2f);
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
                map.setOnMarkerClickListener(MainActivityBackup17_1_2014.this);
                LatLng mapCenter = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 18f));
                map.setMyLocationEnabled(true);
                AddAllMarker();
                SDK_ready = 1;
                TextView t = (TextView) mList.get(Constants.MAP_LAYOUT).findViewById(
                        R.id.bearing_text);
                t.setVisibility(View.GONE);
                Button b = (Button) mList.get(Constants.MAP_LAYOUT).findViewById(R.id.switch_location);
                b.setVisibility(View.VISIBLE);
                resumeMapButton = (Button) mList.get(Constants.MAP_LAYOUT).findViewById(R.id.toggleZoom);
                tBearing=(TextView)findViewById(R.id.bearing_text);
                tBearing.setVisibility(View.VISIBLE);
            }
            cLocation = new Location("");
            cLocation.setLatitude(Lat);
            cLocation.setLongitude(Lng);
            if (dlocation.size() != 0 && index != -1) {
                cLocation.setLatitude(dlocation.get(index).getLatitude());
                cLocation.setLongitude(dlocation.get(index).getLongitude());

            }
            GeomagneticField field = new GeomagneticField((float) Lat,
                    (float) Lng, (float) Alt, System.currentTimeMillis());
            mDeclination = field.getDeclination();
            if (SDK_ready == 1) {

                int found = 0;
                for (LocalbasedMonster t : localbaseMonster) {
                    if (CheckNotifyLocalMonster(t)) {
                        if (dialog)
                            FoundLocalMonster(t);
                        found = 1;
                    }
                }
                if (found == 0)
                    unloadLocalMonster();
                for (MarkerMonster t : markerMonster) {
                    if (CheckNotifyMarkerMonster(t)) {
                        if (dialog)
                            FoundMarkerMonster(t);
                        found = 1;
                    }
                }
                if(found==0)
                    unLoadMarkerMonster();
                for (MapObject t : mapCollectableItemList) {
                   /* if (CheckLocationAndHeading(t.getLocation(), t.getAcceptableAngle(), t.getInitialAngle()
                            , t.getAcceptableDistance())) {
                        if (dialog)
                            FoundMapItem(t);
                        found=1;
                    }else
                        collectItemState=false;*/
                    if (CheckLocationDistance(t.getLocation(), t.getAcceptableDistance())) {

                        found=1;
                    }
                }
                if (found == 0) {
                    unloadMapObject();
                    dialog = true;
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
                        mGUIView = View.inflate(MainActivityBackup17_1_2014.this,
                                R.layout.map_layout, null);
                        mList.add(mGUIView);
                        mGUIView.setVisibility(View.GONE);
                        addContentView(mGUIView, new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT));
                        mGUIView = View.inflate(MainActivityBackup17_1_2014.this,
                                R.layout.overlay_camera_layout, null);
                        mList.add(mGUIView);
                        mGUIView.setVisibility(View.GONE);
                        addContentView(mGUIView, new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT));
                        mGUIView = View.inflate(MainActivityBackup17_1_2014.this,
                                R.layout.main, null);
                        mList.add(mGUIView);
                        mGUIView.setVisibility(View.GONE);
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
                Log.i("kak",
                        "Tracking state for COS " + v.getCoordinateSystemID()
                                + " is " + v.getState() + found);
                if (v.getState().compareTo(ETRACKING_STATE.ETS_INITIALIZED) == 0)
                    startCount = true;
                if (v.getState().compareTo(ETRACKING_STATE.ETS_FOUND) == 0) {
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
            } else {
                MetaioDebug.log(Log.ERROR,
                        "Failed to create instant tracking configuration!");
            }
        }
    }
}
