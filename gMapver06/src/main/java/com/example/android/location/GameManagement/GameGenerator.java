package com.example.android.location.GameManagement;

import android.content.Context;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Object.ObjectDATA;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Resource.Player.PlayerItem;
import com.example.android.location.Util.Constants;
import com.example.android.location.Util.Mission_ONE;
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
import com.metaio.sdk.jni.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Adisorn on 1/30/2015.
 */
public class GameGenerator {

    public static Location foundedObjectLocation;
    public static double foundedObjectDistance;
    public static ObjectGroup objectGroup;
    public Vibrator mVibration;
    HashMap<String, String> mapMarkerNameList = new HashMap<String, String>();
    GoogleMap mMap;
    Context context;
    IMetaioSDKAndroid metaioSDK;
    MapObjectManager mMapObjectManager;
    ObjectDetailManager mObjectDetailManager;
    boolean isHitting = false, isDelay = false;
    BossManager bossManager;
    private HashMap<String, ObjectGroup> objectGroupHashMap;
    private static CountDownTimer mCountDownTimer;
    private ProgressBar playerHpBar, objectHpBar;
    private View getHitView;
    private int meteorNo = 3;
    private int meteorCount = 0;

    public GameGenerator(Context context, IMetaioSDKAndroid metaioSDK, MapObjectManager mMapObjectManager
            , ObjectDetailManager mObjectDetailManager, GoogleMap map) {
        this.context = context;
        this.metaioSDK = metaioSDK;
        this.mMapObjectManager = mMapObjectManager;
        this.mMap = map;
        this.mObjectDetailManager = mObjectDetailManager;
        initResource();
    }

    public static ObjectGroup getObjectGroup() {
        return objectGroup;
    }

    public static void setObjectGroup(ObjectGroup objectGroup) {
        GameGenerator.objectGroup = objectGroup;
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
        Location temp = new Location("");
        temp.setLongitude(new_x + x0);
        temp.setLatitude(y + y0);
        return temp;
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public BossManager getBossManager() {
        return bossManager;
    }

    public boolean isHitting() {
        return isHitting;
    }

    public void gameChecking(Location currentLocation) {
        int GAME_STATE = GlobalResource.getGAME_STATE();
        if (GAME_STATE != GlobalResource.STATE_IDLE) {

            if (GAME_STATE != 1) {
                if (GAME_STATE == GlobalResource.STATE_MARKER) {
                    int distance = (int) foundedObjectLocation.distanceTo(currentLocation);
                    if (distance > foundedObjectDistance)
                        mObjectDetailManager.setGameState(false);
                }
            }

        } else if (!isDelay) {
            Mission_ONE x = new Mission_ONE();
            int found = 0;
            for (Map.Entry<String, ObjectGroup> t : objectGroupHashMap.entrySet()) {
                if (t.getKey().equals(ObjectID.GROUP_BOSS)||t.getKey().equals(ObjectID.OLD_MAN_KARN)
                        ||t.getKey().equals(ObjectID.BOTTLE)) {
                    ObjectGroup temp = t.getValue();
                    double phoneHeading = MainActivity.getPhoneHeading();
                    ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(temp.getMainKey());
                    boolean check = CheckPhoneHeading(objectDATA, phoneHeading);
                    if (checkLocationDistance(objectDATA.getLocation(), currentLocation, objectDATA.getAcceptableDistance())
                            && check) {
                        if (t.getKey().equals(ObjectID.GROUP_BOSS)) {
                            notifyEvent(temp, GlobalResource.STATE_MARKER, 60000);
                            bossManager = new BossManager();
                        }
                        if (t.getKey().equals(ObjectID.OLD_MAN_KARN)) {

                            notifyEvent(temp,GlobalResource.STATE_MISSION,20000);
                        }
                        if (t.getKey().equals(ObjectID.BOTTLE)) {
                            notifyEvent(temp,GlobalResource.STATE_HEALING,20000);
                        }
                        found = 1;
                    }
                }
            }
            double temp = 1;
            if (found == 0)
                temp = Math.random();
            double possibility = 0.5;
            if (CheckLocationArea(18.795526f, 98.953083f, 0.00095f, 0.000206f, currentLocation) && temp <= possibility) {
                temp += 5;
                if (temp <= possibility / 3) {
                    ObjectGroup t = createRandomMapObject();
                    notifyEvent(t,GlobalResource.STATE_GATHERING,20000);
                } else if (temp >= possibility * 2 / 3) {
                    meteorCount = 0;
                    notifyEvent(ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_METEOR)
                            , GlobalResource.STATE_METEOR, 12000);
                } else {
                    ObjectGroup t = createRandomLocationObject(currentLocation, 10);
                    notifyEvent(t, GlobalResource.STATE_LOCATIONBASED, 20000);
                }

            }
        }
    }

    public void stopTimer(){
        if(mCountDownTimer!=null)
            mCountDownTimer.cancel();
    }

    public void notifyEvent(ObjectGroup t,int GAME_STATE) {
        mVibration.vibrate(300);
        Mission_ONE.setVisibilityModel(false);
        FoundObject(t, GAME_STATE);
        objectGroup = t;
    }

    private void notifyEvent(ObjectGroup t, int GAME_STATE, int timeout) {
        mVibration.vibrate(300);
        FoundObject(t, GAME_STATE);
        startCountdownTimer(timeout);
        objectGroup = t;
    }

    public void initResource() {
        View overlayLayout = GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT);
        playerHpBar = (ProgressBar) overlayLayout.findViewById(R.id.overlay_player_hp);
        playerHpBar.setMax(Player.getHp());
        playerHpBar.setProgress(Player.getHp());
        objectHpBar = (ProgressBar) overlayLayout.findViewById(R.id.overlay_object_hp);

        getHitView = overlayLayout.findViewById(R.id.overlay_get_hit);
        mVibration = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        objectGroupHashMap = ObjectLoader.getObjectGroupList();
        //createLocationObject();

        createMarkerObject();
    }

    private void FoundObject(ObjectGroup mModelList, int GAME_STATE) {
        mObjectDetailManager.setGameState(true, mModelList, GAME_STATE);
    }


    private boolean checkLocationDistance(Location objectLocation, Location currentLocation, double maxDistance) {
        if (objectLocation.distanceTo(currentLocation) <= maxDistance) {
            return true;
        } else
            return false;
    }

    private boolean CheckPhoneHeading(ObjectDATA bearingObject, double phone_heading) {
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
        //for (LocationBasedObject t : locationGeometryList)
        addMarker(temp, R.drawable.monster2, "Monster");
    }

    private ObjectGroup createRandomMapObject() {
        String key = randInt(14, 15) + "";
        ObjectGroup t1 = ObjectLoader.getObjectGroupList().get(key);
        HashMap<String, ObjectDetail> temp = t1.getObjectDetailList();
        int i=1;
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            t.getValue().getModel().setTranslation(new Vector3d(randInt(-200,200),i*200,0));
            i++;
        }
        return t1;
    }

    private ObjectGroup createRandomLocationObject(Location currentLocation, int range) {
        String key = randInt(4, 5) + "";
        ObjectGroup t1 = ObjectLoader.getObjectGroupList().get(key);
        HashMap<String, ObjectDetail> temp = t1.getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            Location newLocation = getRandomLocation(currentLocation, range);
            LLACoordinate newLLA = new LLACoordinate(newLocation.getLatitude(), newLocation.getLongitude(), 0, 0);
            double bearingDegree = 180 - newLocation.bearingTo(currentLocation);
            t.getValue().getModel().setRotation(new Rotation(0, 0, (float) (bearingDegree * Math.PI / 180.0)));
            t.getValue().getModel().setTranslationLLA(newLLA);
            t.getValue().setLocation(newLocation);
        }
        return t1;
    }

    public void resetState(int STATE) {
        stopTimer();
        objectHpBar.setVisibility(View.GONE);
            for (Map.Entry<String, ObjectDetail> t : objectGroup.getObjectDetailList().entrySet()) {
                String key = t.getValue().getKey();
                t.getValue().setRemainingHP(ObjectDATA.getObjectDATAHashMap().get(key).getMaxHP());
            }
            mObjectDetailManager.setGameState(false);
        isDelay = true;
        new CountDownTimer(3000, 2000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                isDelay = false;
            }
        }.start();
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
        mCountDownTimer = new CountDownTimer(countDownTime, countDownTime / 12) {
            int count = 0;

            public void onTick(long millisUntilFinished) {
                if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_MARKER) {
                    bossManager.changeBossState(count % 3);
                }
                count++;
            }

            public void onFinish() {
                int GAME_STATE = GlobalResource.getGAME_STATE();
                if (GAME_STATE == GlobalResource.STATE_METEOR)
                    MainActivity.makeToast("Meteor Storm is ending!!");
                else
                    MainActivity.makeToast("Timeout!!");
                resetState(GlobalResource.getGAME_STATE());
            }
        }.start();
    }

    public void playerGetHit(int dmg) {
        int playerHp = Player.getHp() - dmg;
        Player.setHp(playerHp);
        playerHpBar.setProgress(playerHp);
        getHitView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getHitView.setVisibility(View.GONE);
            }
        }, 100);
        if (playerHp <= 0) {
            if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_MARKER) {
                MainActivity.showSimpleDialog("ปีศาจไส้อั่ว", "นี้ผู้เฒ่ากานต์ส่งเจ้ามาจริงรึเปล่า ฮ่าๆๆๆๆ ทำไมเจ้าอ่อนแอแบบนี้!");
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetState(GlobalResource.getGAME_STATE());
                    getHitView.setVisibility(View.VISIBLE);
                    GlobalResource.getListOfViews().get(Constants.MAP_LAYOUT).findViewById(R.id.map_dead)
                            .setVisibility(View.VISIBLE);
                    Player.setHp(0);
                    playerHpBar.setProgress(Player.getHp());
                }
            }, 1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (GlobalResource.getMISSION_STATE() == Mission_ONE.STATE_GET_MISSION) {
                        GlobalResource.setMISSION_STATE(Mission_ONE.STATE_LOSE_TO_BOSS);
                    } else {
                        notifyEvent(ObjectLoader.getObjectGroupList().get(ObjectID.OLD_MAN_KARN)
                                , GlobalResource.STATE_DEAD);
                    }
                }
            }, 2000);
        }

    }

    public void checkMeteor(IGeometry geometry) {
        if (meteorCount < 2) {
            String[] animationList = {"falling", "falling_right", "falling_left"};
            double index = Math.round(Math.random() * 2.0);
            int maxHP = ObjectDATA.getObjectDATAHashMap().get(objectGroup.getMainKey()).getMaxHP();
            objectGroup.getObjectDetailList().entrySet().iterator().next().getValue().setRemainingHP(maxHP);
            geometry.setPickingEnabled(true);
            geometry.startAnimation(animationList[(int) index]);
            meteorCount++;
        } else {
            MainActivity.makeToast("Meteor Storm is ending!!");
            resetState(GlobalResource.STATE_METEOR);
        }
    }

    public void checkHealingGeometryTouch(IGeometry geometry) {
        View healingLayout = GlobalResource.getListOfViews().get(Constants.HEAL_LAYOUT);
        healingLayout.setVisibility(View.VISIBLE);
        getHitView.setVisibility(View.GONE);
        GlobalResource.getListOfViews().get(Constants.MAP_LAYOUT).findViewById(R.id.map_dead)
                .setVisibility(View.GONE);
        GlobalResource.getListOfViews().get(Constants.MAP_LAYOUT).findViewById(R.id.map_dead).setVisibility(View.GONE);
        GlobalResource.setGAME_STATE(GlobalResource.STATE_HEALING);
        final ProgressBar t = (ProgressBar) healingLayout.findViewById(R.id.healing_progress);
        final ProgressBar t2 = (ProgressBar) healingLayout.findViewById(R.id.healing_timer);
        t.setProgress(0);
        geometry.setVisible(false);
        final int countDownTime = 7000;
        new CountDownTimer(countDownTime, countDownTime / 100) {

            public void onTick(long millisUntilFinished) {
                t2.setProgress((int) ((float) millisUntilFinished / countDownTime * 100));
            }

            public void onFinish() {
                int getHp = (int) ((float) t.getProgress() / t.getMax() * 300);
                Player.setHp(Player.getHp() + getHp);
                playerHpBar.setProgress(playerHpBar.getProgress() + getHp);
                GlobalResource.getListOfViews().get(Constants.HEAL_LAYOUT).setVisibility(View.GONE);
                MainActivity.makeToast("restore " + getHp + " HP");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.showHealingDialog();
                    }
                }, 3000);
            }
        }.start();
    }

    public static int getPlayerItemQuantity(HashMap<Integer, PlayerItem> playerItemHashMap, int id) {
        PlayerItem t = playerItemHashMap.get(id);
        int playerItemQuantity = 0;
        if (t != null)
            playerItemQuantity = t.getQuantity();
        return playerItemQuantity;
    }
    public void checkGatheringGeometryTouch(String key, int GAME_STATE, HashMap<Integer, PlayerItem> playerItemHashMap){
        ObjectDetail objectDetail = objectGroup.getObjectDetailList().get(key);
        String mObjectID=objectDetail.getKey();
        if (mObjectID.equals(ObjectID.STONE)) {
            int playerOreQuantity = getPlayerItemQuantity(playerItemHashMap, ItemsID.ORE);
            playerItemHashMap.put(ItemsID.ORE, new PlayerItem(ItemsID.ORE, playerOreQuantity + 1));
            MainActivity.makeToastItem(ItemsID.ORE, 1);
        }
        if (mObjectID.equals(ObjectID.GRASS)) {
            int playerGrassQuantity = getPlayerItemQuantity(playerItemHashMap, ItemsID.GRASS);
            playerItemHashMap.put(ItemsID.GRASS, new PlayerItem(ItemsID.GRASS, playerGrassQuantity + 1));
            MainActivity.makeToastItem(ItemsID.GRASS, 1);
        }
        objectDetail.getModel().setVisible(false);
        objectDetail.getModel().setPickingEnabled(false);
    }


    public void checkLocationGeometryTouch(String key, int GAME_STATE, IRadar mRadar
            , HashMap<Integer, PlayerItem> playerItemHashMap, GameGenerator gameGenerator) {

        ObjectDetail objectDetail = objectGroup.getObjectDetailList().get(key);
        int atkDmg = Player.getAtkDmg();
        View overlayLayout = GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT);

        ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(objectDetail.getKey());
        objectHpBar.setVisibility(View.VISIBLE);
        /*
        float alpha = (objectDetail.getMaxHP() - objectRemainHP) / (float)objectDetail.getMaxHP();
        objectDetail.getModelList().get(0).setTransparency(alpha);
        */
        if (GAME_STATE == GlobalResource.STATE_MARKER) {
            /*
            objectDetail.getModel().stopAnimation();
            isHitting = false;
            */
            atkDmg = bossManager.onElephantBossTouch(key, gameGenerator, atkDmg);
        }
        int objectRemainHP = objectDetail.getRemainingHP() - atkDmg;
        int maxHP = objectDATA.getMaxHP();
        objectDetail.setRemainingHP(objectRemainHP);
        objectHpBar.setMax(maxHP);
        objectHpBar.setProgress(objectRemainHP);
        if (GAME_STATE == GlobalResource.STATE_MARKER)
            bossManager.checkHPsystem(gameGenerator);
        if (objectRemainHP <= 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    objectHpBar.setVisibility(View.GONE);
                }
            });
            int playerGold = getPlayerItemQuantity(playerItemHashMap, ItemsID.GOLD);
            playerItemHashMap.put(ItemsID.GOLD, new PlayerItem(ItemsID.GOLD, playerGold + objectDATA.getGoldDrop()));
            MainActivity.makeToastItem(ItemsID.GOLD, objectDATA.getGoldDrop());
            //objectDetail.setRemainingHP(objectDetail.getMaxHP());
            objectDetail.getModel().setPickingEnabled(false);
            objectDetail.getModel().setVisible(false);
            mRadar.remove(objectDetail.getModel());
            /*if (GAME_STATE == GlobalResource.STATE_MARKER)
                resetState(GAME_STATE);
            */
            if (GAME_STATE == GlobalResource.STATE_METEOR) {
                objectDetail.getModel().setVisible(true);
                checkMeteor(objectDetail.getModel());
            }

        }
    }
}
