package com.example.android.location.GameManagement;

import android.content.Context;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.ItemsID;
import com.example.android.location.Resource.LocationBasedObject;
import com.example.android.location.Resource.MapObject;
import com.example.android.location.Resource.MarkerObject;
import com.example.android.location.Resource.ObjectDetail;
import com.example.android.location.Resource.ObjectID;
import com.example.android.location.Resource.ObjectLoader;
import com.example.android.location.Resource.Player;
import com.example.android.location.Resource.PlayerItem;
import com.example.android.location.Util.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.Rotation;
import com.metaio.tools.io.AssetsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Adisorn on 1/30/2015.
 */
public class GameGenerator {

    public static Location foundedObjectLocation;
    public static double foundedObjectDistance;
    public Vibrator mVibration;
    HashMap<String, String> mapMarkerNameList = new HashMap<String, String>();
    GoogleMap mMap;
    Context context;
    IMetaioSDKAndroid metaioSDK;
    MapObjectManager mMapObjectManager;
    LocationObjectManager mLocationObjectManager;
    MarkerObjectManager mMarkerObjectManager;
    private ArrayList<IGeometry> locationModelList, markerModelList, mapObjectModelList;
    private ArrayList<LocationBasedObject> locationGeometryList = new ArrayList<LocationBasedObject>();
    private ArrayList<MarkerObject> markerGeometryList = new ArrayList<MarkerObject>();
    private HashMap<String,ObjectDetail> objectDetailHashMap;
    private CountDownTimer mCountDownTimer;
    private ProgressBar playerHpBar;
    private View getHitView;
    boolean isHitting=false;

    public boolean isHitting() {
        return isHitting;
    }

    public GameGenerator(Context context, IMetaioSDKAndroid metaioSDK, MapObjectManager mMapObjectManager
            , MarkerObjectManager mMarkerObjectManager, LocationObjectManager mLocationObjectManager, GoogleMap map) {
        this.context = context;
        this.metaioSDK = metaioSDK;
        this.mMapObjectManager = mMapObjectManager;
        this.mLocationObjectManager = mLocationObjectManager;
        this.mMap = map;
        this.mMarkerObjectManager = mMarkerObjectManager;
        initResource();
    }

    public static void setFoundedObjectDistance(double foundedObjectDistance) {
        GameGenerator.foundedObjectDistance = foundedObjectDistance;
    }

    public static void setFoundedObjectLocation(Location foundedObjectLocation) {
        GameGenerator.foundedObjectLocation = foundedObjectLocation;
    }

    public static Location getRandomLocation(Location mLocation, int radius) {
        Random random = new Random();
        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;
        double x0 = mLocation.getLongitude();
        double y0 = mLocation.getLatitude();
        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);
        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);
        Location temp=new Location("");
        temp.setLongitude(new_x + x0);
        temp.setLatitude(y + y0);
        return temp;
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public void gameChecking(Location currentLocation) {
        int GAME_STATE = GlobalResource.getGAME_STATE();
        if (GAME_STATE != GlobalResource.STATE_IDLE) {
            if (GAME_STATE != 1) {
                int distance = (int) foundedObjectLocation.distanceTo(currentLocation);
                if (distance > foundedObjectDistance) {
                    /*if (GAME_STATE == GlobalResource.STATE_LOCATIONBASED)
                        mLocationObjectManager.setLocationBasedMonsterState(false, null);*/
                    if (GAME_STATE == GlobalResource.STATE_MARKER)
                        mMarkerObjectManager.setMarkerMonsterState(false, null);
                }
            }

        } else {
            for (Map.Entry<String,ObjectDetail> t : objectDetailHashMap.entrySet()) {
                if(t.getKey().equals(ObjectID.BOSS)) {
                    ObjectDetail temp=t.getValue();
                    double phoneHeading = MainActivity.getPhoneHeading();
                    boolean check = CheckPhoneHeading(t.getValue(), phoneHeading);
                    if (checkLocationDistance(temp.getLocation(), currentLocation, temp.getAcceptableDistance())&& check) {
                        notifyEvent(temp);
                    }
                }
            }
            double temp = Math.random();
            double possibility=0.5;
            if (CheckLocationArea(18.795526f, 98.953083f, 0.000846f, 0.000206f, currentLocation) && temp <=possibility) {
                if (temp+possibility/2 <= possibility/2) {
                    MapObject t = createRandomMapObject();
                    notifyEvent(t);
                } else {
                    ArrayList<ObjectDetail> t=new ArrayList<ObjectDetail>();
                    t.add(createRandomLocationObject(currentLocation, 10));
                    t.add(createRandomLocationObject(currentLocation, 10));
                    t.add(createRandomLocationObject(currentLocation, 10));
                    notifyEvent(t);
                }

            }
        }
    }

    private void notifyEvent(ArrayList<ObjectDetail> t) {
        mVibration.vibrate(300);
        Toast.makeText(context.getApplicationContext(), "Location monster", Toast.LENGTH_SHORT).show();
        foundLocalObject(t);
        startCountdownTimer(20000);
    }

    private void notifyEvent(ObjectDetail t) {
        mVibration.vibrate(300);
        Toast.makeText(context.getApplicationContext(), "BOSS!!", Toast.LENGTH_SHORT).show();
        FoundMarkerMonster(t);
        startCountdownTimer(20000);
    }

    private void notifyEvent(MapObject t) {
        mVibration.vibrate(300);
        Toast.makeText(context.getApplicationContext(), "Gathering", Toast.LENGTH_SHORT).show();
        foundMapObject(t);
        startCountdownTimer(20000);
    }

    public void initResource() {
        playerHpBar= (ProgressBar) GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT)
                .findViewById(R.id.overlay_player_hp);
        playerHpBar.setMax(Player.getHp());
        playerHpBar.setProgress(Player.getHp());
        getHitView=GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT).findViewById(R.id.overlay_get_hit);
        mVibration = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        locationModelList = GlobalResource.getLocationBasedObjectModelList();
        markerModelList = GlobalResource.getMarkerObjectModelList();
        mapObjectModelList = GlobalResource.getMapObjectModelList();
        objectDetailHashMap=ObjectLoader.getObjectList();
        //createLocationObject();

        createMarkerObject();
    }

    private void FoundMarkerMonster(ObjectDetail mMonster) {
        String filePath = AssetsManager.getAssetPath(context.getApplicationContext(),
                "TutorialDynamicModels/Assets/MarkerConfig_" + mMonster.getMarkerPath() + ".xml");
        metaioSDK.setTrackingConfiguration(filePath);
        mMarkerObjectManager.setMarkerMonsterState(true, mMonster);
    }

    private void foundMapObject(MapObject object) {
        mMapObjectManager.setCollectingState(true, object);
        metaioSDK.startInstantTracking("INSTANT_2D_GRAVITY_SLAM_EXTRAPOLATED", "", false);
    }

    private void foundLocalObject(ArrayList<ObjectDetail> lMonster) {
        mLocationObjectManager.setLocationBasedMonsterState(true, lMonster);
        metaioSDK.setTrackingConfiguration("GPS", false);

    }

    private boolean checkLocationDistance(Location objectLocation, Location currentLocation, double maxDistance) {
        if (objectLocation.distanceTo(currentLocation) <= maxDistance) {
            return true;
        } else
            return false;
    }

    private boolean CheckPhoneHeading(ObjectDetail bearingObject, double phone_heading) {
        double difOfAngle = phone_heading - bearingObject.getInitialAngle();
        if (difOfAngle > 180) difOfAngle -= 360;
        if (difOfAngle < -180) difOfAngle += 360;
        if (Math.abs(difOfAngle) <= bearingObject.getAcceptableAngle())
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

    private void createMarkerObject() {/*
        MarkerObject t1 = new MarkerObject(18.796425, 98.953134, 130f, 45, 15, "ENG", markerModelList.get(0));
        markerGeometryList.add(t1);
        for (MarkerObject t : markerGeometryList)
            addMarker(t.getLocation(), R.drawable.monster_marker_icon, "Monster");*/
    }

    private void createLocationObject() {
        Location temp = new Location("");
        temp.setLatitude(18.796535);
        temp.setLongitude(98.952877);
        LocationBasedObject tMonster = new LocationBasedObject(temp, -130f, 30, 15, locationModelList.get(0));
        locationGeometryList.add(tMonster);
        for (LocationBasedObject t : locationGeometryList)
            addMarker(t.getLocation(), R.drawable.monster2, "Monster");
    }

    private MapObject createRandomMapObject() {
        IGeometry model = mapObjectModelList.get(randInt(0, mapObjectModelList.size() - 1));
        MapObject temp = new MapObject(model);
        return temp;
    }

    private ObjectDetail createRandomLocationObject(Location currentLocation, int range) {

        Location newLocation = getRandomLocation(currentLocation, range);
        String key=randInt(1,ObjectLoader.getObjectList().size()-1)+"";
        ObjectDetail objectDetail= objectDetailHashMap.get(key);
        LLACoordinate newLLA = new LLACoordinate(newLocation.getLatitude(), newLocation.getLongitude(), 0, 0);
        double bearingDegree = 180-newLocation.bearingTo(currentLocation);
        objectDetail.getModel().setRotation(new Rotation(0,0,(float)(bearingDegree*Math.PI/180.0)));
        objectDetail.getModel().setTranslationLLA(newLLA);
        objectDetail.setLocation(newLocation);
        return objectDetail;
    }

    public void resetState(int STATE){
        mCountDownTimer.cancel();
        for(Map.Entry<String,ObjectDetail> t:objectDetailHashMap.entrySet())
            t.getValue().setRemainingHP(t.getValue().getMaxHP());

        if(STATE==GlobalResource.STATE_LOCATIONBASED){
            mLocationObjectManager.setLocationBasedMonsterState(false, null);
        }
        if(STATE==GlobalResource.STATE_MARKER)
        {
            mMarkerObjectManager.setMarkerMonsterState(false,null);
        }
        if(STATE==GlobalResource.STATE_GATHERING){
            mMapObjectManager.setCollectingState(false, null);
        }
    }

    public void addMarker(Location location, int icon, String type) {
        MarkerOptions temp = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .anchor(0.0f, 0.0f)
                .flat(true)
                .position(new LatLng(location.getLatitude(), location.getLongitude()));
        Marker x = mMap.addMarker(temp);
        mapMarkerNameList.put(x.getId(), type);
    }



    public void startCountdownTimer(int countDownTime) {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        mCountDownTimer = new CountDownTimer(countDownTime, countDownTime / 4) {
            public void onTick(long millisUntilFinished) {
                    if(GlobalResource.getGAME_STATE()==GlobalResource.STATE_MARKER) {
                        isHitting = true;
                        ObjectLoader.getObjectList().get(ObjectID.BOSS).getModel().setAnimationSpeed(100);
                        ObjectLoader.getObjectList().get(ObjectID.BOSS).getModel().startAnimation("loop");
                    }
            }
            public void onFinish() {
                    resetState(GlobalResource.getGAME_STATE());
            }
        }.start();
    }

    public void playerGetHit(){
        int playerHp=Player.getHp() - 200;
        Player.setHp(playerHp);
        playerHpBar.setProgress(playerHp);
        getHitView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getHitView.setVisibility(View.GONE);
            }
        }, 100);
    }

    public void checkLocationGeometryTouch(String key,int GAME_STATE,IRadar mRadar
            ,HashMap<Integer,PlayerItem> playerItemHashMap){

        ObjectDetail objectDetail=objectDetailHashMap.get(key);
        int atkDmg=Player.getAtkDmg();
        View overlayLayout=GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT);
        ProgressBar progressBar= (ProgressBar) overlayLayout.findViewById(R.id.overlay_object_hp);
        int objectRemainHP=objectDetail.getRemainingHP()-atkDmg;
        objectDetail.setRemainingHP(objectRemainHP);
        progressBar.setMax(objectDetail.getMaxHP());
        progressBar.setProgress(objectRemainHP);
        float alpha = (objectDetail.getMaxHP() - objectRemainHP) / (float)objectDetail.getMaxHP();
        objectDetail.getModel().setTransparency(alpha);
        if(GAME_STATE==GlobalResource.STATE_MARKER) {
            objectDetail.getModel().stopAnimation();
            isHitting = false;
        }
        if(objectRemainHP<=0){
            int playerGold=MainActivity.getPlayerItemQuantity(playerItemHashMap,ItemsID.GOLD);
            playerItemHashMap.put(ItemsID.GOLD,new PlayerItem(ItemsID.GOLD,playerGold+objectDetail.getGoldDrop()));
            MainActivity.makeToast("Receive " + objectDetail.getGoldDrop() + " gold!");
            objectDetail.setRemainingHP(objectDetail.getMaxHP());
            objectDetail.getModel().setPickingEnabled(false);
            if(!mRadar.remove(objectDetail.getModel())||GAME_STATE==GlobalResource.STATE_MARKER)
                resetState(GAME_STATE);
        }
    }
}
