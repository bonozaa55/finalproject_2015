package com.example.android.location.GameManagement;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static CountDownTimer mCountDownTimer;
    public Vibrator mVibration;
    HashMap<String, String> mapMarkerNameList = new HashMap<String, String>();
    GoogleMap mMap;
    Context context;
    IMetaioSDKAndroid metaioSDK;
    ObjectDetailManager mObjectDetailManager;
    boolean isHitting = false, isDelay = false;
    BossManager bossManager;
    FishingManager mFishingManager;
    MistManager mMistManager;

    private HashMap<String, ObjectGroup> objectGroupHashMap;
    private ProgressBar playerHpBar, objectHpBar;
    private View getHitView, mistView, mapGetHitView;
    private TextView getHitDmgView;
    private int meteorCount = 0, locationObjectCount = 0;


    public GameGenerator(Context context, IMetaioSDKAndroid metaioSDK
            , ObjectDetailManager mObjectDetailManager, FishingManager mFishingManager
            , MistManager mMistManager, GoogleMap map) {
        this.context = context;
        this.metaioSDK = metaioSDK;
        this.mMap = map;
        this.mObjectDetailManager = mObjectDetailManager;
        this.mFishingManager = mFishingManager;
        this.mMistManager = mMistManager;
        initResource();
    }

    public static ObjectGroup getObjectGroup() {
        return objectGroup;
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

    public static int getPlayerItemQuantity(int id) {
        PlayerItem t = Player.getPlayerItems().get(id);
        int playerItemQuantity = 0;
        if (t != null)
            playerItemQuantity = t.getQuantity();
        return playerItemQuantity;
    }

    public static void setPlayerItem(int id, int quantity, boolean makeToast) {
        int playerItem = getPlayerItemQuantity(id) + quantity;
        if (playerItem > 0)
            Player.getPlayerItems().put(id, new PlayerItem(id, playerItem));
        else
            Player.getPlayerItems().remove(id);
        if (makeToast)
            MainActivity.makeToastItem(id, quantity);
    }

    public BossManager getBossManager() {
        return bossManager;
    }


    public void gameChecking(Location currentLocation) {
        int GAME_STATE = GlobalResource.getGAME_STATE();
        if (GAME_STATE != GlobalResource.STATE_IDLE) {

            if (GAME_STATE != 1) {
                if (GAME_STATE == GlobalResource.STATE_MARKER) {
                    int distance = (int) foundedObjectLocation.distanceTo(currentLocation);
                    if (distance > foundedObjectDistance)
                        resetState();
                }
            }

        } else if (!isDelay) {
            Mission_ONE x = new Mission_ONE();
            HashMap<String, String> tempHashMap = new HashMap<String, String>();
            int found = 0;
            String[] markerSET;
            String[] onNormalMarkerSET = {ObjectID.GROUP_BOSS, ObjectID.THE_OLD_MAN, ObjectID.GROUP_FISHING
                    , ObjectID.BOTTLE, ObjectID.SHOP_MAN, ObjectID.GROUP_PET};
            String[] onDeadMarkerSET = {ObjectID.THE_OLD_MAN, ObjectID.BOTTLE};
            if (Player.getHp() <= 0) {
                markerSET = onDeadMarkerSET;
                found = 1;
            } else
                markerSET = onNormalMarkerSET;

            for (String stringTemp : markerSET)
                tempHashMap.put(stringTemp, stringTemp);
            for (Map.Entry<String, ObjectGroup> t : objectGroupHashMap.entrySet()) {
                if (tempHashMap.get(t.getKey()) != null) {
                    ObjectGroup temp = t.getValue();
                    double phoneHeading = MainActivity.getPhoneHeading();
                    ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(temp.getMainKey());
                    boolean check = CheckPhoneHeading(objectDATA, phoneHeading);
                    if (checkLocationDistance(objectDATA.getLocation(), currentLocation, objectDATA.getAcceptableDistance())
                            && check) {
                        if (t.getKey().equals(ObjectID.GROUP_BOSS)) {
                            if (GlobalResource.getMISSION_STATE() < Mission_ONE.STATE_WIN_BOSS) {
                                MainActivity.playSound("boss_theme.mp3", true);
                                notifyEvent(temp, GlobalResource.STATE_MARKER, 60000);
                                bossManager = new BossManager();
                            }else
                                notifyEvent(temp, GlobalResource.STATE_MARKER, 10000);
                        }
                        if (t.getKey().equals(ObjectID.THE_OLD_MAN)) {
                            notifyEvent(temp, GlobalResource.STATE_MISSION, 10000);
                        }
                        if (t.getKey().equals(ObjectID.BOTTLE)) {
                            notifyEvent(temp, GlobalResource.STATE_HEALING, 10000);
                        }
                        if (t.getKey().equals(ObjectID.SHOP_MAN)) {
                            notifyEvent(temp, GlobalResource.STATE_SHOPPING, 10000);
                        }
                        if (t.getKey().equals(ObjectID.GROUP_PET)) {
                            MainActivity.playSound("pet_theme.mp3", true);
                            notifyEvent(temp, GlobalResource.STATE_PETTING, 10000);
                        }
                        if (t.getKey().equals(ObjectID.GROUP_FISHING)) {
                            mFishingManager.resetRotateAngle();
                            notifyEvent(temp, GlobalResource.STATE_FISHING, 10000);
                        }
                        found = 1;
                    }
                }
            }

            double temp = 1;
            if (found == 0)
                temp = Math.random();
            double possibility = 0.5;
            if (CheckLocationArea(18.795526, 98.953083, 0.00095, 0.000206, currentLocation) && temp <= possibility) {
                 MainActivity.playSound("area_theme.mp3",true);
                if (temp <= possibility / 3) {
                    ObjectGroup t = createRandomMapObject();
                    notifyEvent(t, GlobalResource.STATE_GATHERING, 20000);
                } else if (temp >= possibility * 2 / 3) {
                    meteorCount = 0;
                    notifyEvent(ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_METEOR)
                            , GlobalResource.STATE_METEOR, 12000);
                } else {
                    locationObjectCount = 0;
                    ObjectGroup t = createRandomLocationObject(currentLocation, 10, 1);
                    notifyEvent(t, GlobalResource.STATE_LOCATIONBASED, 20000);
                }
                found = 1;
            }
            if (CheckLocationArea(18.794280, 98.950866, 0.000463, 0.001099, currentLocation) && found != 1) {
                MainActivity.playSound("mist_theme.mp3", true);
                ObjectGroup t = createRandomLocationObject(currentLocation, 10, 0);
                notifyEvent(t, GlobalResource.STATE_MIST);
                mMistManager.generateAttacker();
                mistView.setVisibility(View.VISIBLE);
                locationObjectCount = 0;
            }
        }
    }


    public void stopTimer() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        mMistManager.stopTimer();
    }

    private void notifyEvent(ObjectGroup t, int GAME_STATE) {
        mVibration.vibrate(300);
        mObjectDetailManager.setGameState(true, t, GAME_STATE);
        objectGroup = t;
    }

    private void notifyEvent(ObjectGroup t, int GAME_STATE, int timeout) {
        mVibration.vibrate(300);
        mObjectDetailManager.setGameState(true, t, GAME_STATE);
        startCountdownTimer(timeout);
        objectGroup = t;
    }

    public void initResource() {
        View overlayLayout = GlobalResource.getListOfViews().get(Constants.OVERLAY_LAYOUT);
        View mapLayout = GlobalResource.getListOfViews().get(Constants.MAP_LAYOUT);
        playerHpBar = (ProgressBar) overlayLayout.findViewById(R.id.overlay_player_hp);
        playerHpBar.setMax(Player.getMaxHP());
        playerHpBar.setProgress(Player.getHp());
        objectHpBar = (ProgressBar) overlayLayout.findViewById(R.id.overlay_object_hp);

        mistView = overlayLayout.findViewById(R.id.overlay_mist);
        getHitView = overlayLayout.findViewById(R.id.overlay_get_hit);
        getHitDmgView = (TextView) overlayLayout.findViewById(R.id.overlay_get_hit_dmg);

        mapGetHitView = mapLayout.findViewById(R.id.map_dead);
        mVibration = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        objectGroupHashMap = ObjectLoader.getObjectGroupList();

        createMarkerObject();


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

    private boolean CheckLocationArea(double pointLat, double pointLng, double height, double width, Location target) {
        Log.i("www1",target.getLatitude()+" "+pointLat+" "+height);
        Log.i("www2",target.getLongitude()+" "+pointLng+" "+width);
        if ((target.getLatitude() >= pointLat && target.getLatitude() <= height + pointLat) &&
                (target.getLongitude() >= pointLng && target.getLongitude() <= width + pointLng))
            return true;
        else
            return false;
    }

    private void createMarkerObject() {

        HashMap<String, ObjectGroup> t1 = ObjectLoader.getObjectGroupList();
        for (Map.Entry<String, ObjectGroup> t : t1.entrySet()) {
            HashMap<String, ObjectDetail> t0 = t.getValue().getObjectDetailList();
            for (Map.Entry<String, ObjectDetail> t2 : t0.entrySet()) {
                if (t2.getValue().getLocation() != null)
                    addMarker(t2.getValue().getLocation(), R.drawable.monster_marker_icon, "Monster");
            }
        }

    }

    private ObjectGroup createRandomMapObject() {
        String key = randInt(14, 15) + "";
        ObjectGroup t1 = ObjectLoader.getObjectGroupList().get(key);
        HashMap<String, ObjectDetail> temp = t1.getObjectDetailList();
        int i = 1;
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            t.getValue().getModel().setTranslation(new Vector3d(randInt(-200, 200), i * 200, 0));
            i++;
        }
        return t1;
    }

    private ObjectGroup createRandomLocationObject(Location currentLocation, int range, int mode) {
        String key;
        if (mode == 1)
            key = randInt(4, 5) + "";
        else
            key = "5";
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

    public void resetState() {
        MainActivity.stopPlaying();
        mistView.setVisibility(View.GONE);
        stopTimer();
        objectHpBar.setVisibility(View.GONE);
        for (Map.Entry<String, ObjectDetail> t : objectGroup.getObjectDetailList().entrySet()) {
            String key = t.getValue().getKey();
            t.getValue().setRemainingHP(ObjectDATA.getObjectDATAHashMap().get(key).getMaxHP());
        }
        mObjectDetailManager.setGameState(false);
        isDelay = true;
        new CountDownTimer(8000, 2000) {

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
                if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_MARKER
                        && GlobalResource.getMISSION_STATE() < Mission_ONE.STATE_WIN_BOSS) {
                    bossManager.changeBossState(count % 3);

                }
                count++;
            }

            public void onFinish() {
                int GAME_STATE = GlobalResource.getGAME_STATE();
                if (GAME_STATE == GlobalResource.STATE_METEOR)
                    MainActivity.makeToast("Meteor Storm is ending!!", Toast.LENGTH_LONG);
                else
                    MainActivity.makeToast("Timeout!!",Toast.LENGTH_LONG);
                resetState();
            }
        }.start();
    }

    void showGetDmg(int dmg) {
        String prefix = "";
        String color = "#B9121B";
        if (dmg >= 0) {
            prefix = "+";
            color = "#58aF27";
        }
        getHitDmgView.setText(prefix + dmg);
        getHitDmgView.setTextColor(Color.parseColor(color));
        getHitDmgView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getHitDmgView.setVisibility(View.GONE);
            }
        }, 200);

    }

    public void playerGetHit(int dmg, boolean isFlash) {

        float percentGetDmg = 1 - (Player.getDefDmg() / 100f);
        int getHitDmg = Math.round(dmg * percentGetDmg);
        showGetDmg(-getHitDmg);
        int playerHp = Player.getHp() - getHitDmg;
        Player.setHp(playerHp);
        playerHpBar.setProgress(playerHp);
        if (isFlash) {
            getHitView.setVisibility(View.VISIBLE);
            mapGetHitView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getHitView.setVisibility(View.GONE);
                    mapGetHitView.setVisibility(View.GONE);
                }
            }, 100);
        }
        if(getHitDmg<80)
        MainActivity.playSoundEffect("get_hit.wav");
        else
        MainActivity.playSoundEffect("get_hit_massive.wav");
        if (playerHp <= 0) {

            if (GlobalResource.getGAME_STATE() == GlobalResource.STATE_MARKER
                    && GlobalResource.getMISSION_STATE() != 0)
                MainActivity.showSimpleDialog("ปีศาจไส้อั่ว", "นี้ผู้เฒ่าส่งเจ้ามาจริงรึเปล่า ฮ่าๆๆๆๆ ทำไมเจ้าอ่อนแอแบบนี้!");
            resetState();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetState();
                    getHitView.setVisibility(View.VISIBLE);
                    GlobalResource.getListOfViews().get(Constants.MAP_LAYOUT).findViewById(R.id.map_dead)
                            .setVisibility(View.VISIBLE);
                    Player.setHp(0);
                    playerHpBar.setProgress(Player.getHp());
                    if (GlobalResource.getMISSION_STATE() == Mission_ONE.STATE_GET_MISSION) {
                        GlobalResource.setMISSION_STATE(Mission_ONE.STATE_LOSE_TO_BOSS);
                        MainActivity.makeToast("Your hp is empty, Please go to visit The old man",Toast.LENGTH_LONG);
                    } else {
                        MainActivity.makeToast("Your hp is empty, Please go to heal station to refill it",Toast.LENGTH_LONG);
                    }
                }
            }, 1000);
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
            MainActivity.makeToast("Meteor Storm is ending!!",Toast.LENGTH_LONG);
            resetState();
        }
    }

    public void checkHealingGeometryTouch(IGeometry geometry, final int GAME_STATE) {
        View healingLayout = GlobalResource.getListOfViews().get(Constants.HEAL_LAYOUT);
        healingLayout.setVisibility(View.VISIBLE);
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
                restorePlayerHP(getHp);
                MainActivity.makeToast("restore " + getHp + " HP",Toast.LENGTH_LONG);
                GlobalResource.getListOfViews().get(Constants.HEAL_LAYOUT).setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (GAME_STATE == GlobalResource.STATE_HEALING)
                            notifyEvent(ObjectLoader.getObjectGroupList().get(ObjectID.BOTTLE)
                                    , GlobalResource.STATE_HEALING, 6000);
                    }
                }, 3000);

            }
        }.start();
    }

    public void restoreHPbyItems() {
        int playerPortions = getPlayerItemQuantity(ItemsID.POTION);
        if (Player.getHp() < Player.getMaxHP() && playerPortions > 0) {
            restorePlayerHP(50);
            Player.getPlayerItems().put(ItemsID.POTION, new PlayerItem(ItemsID.POTION, playerPortions - 1));
            TextView t = (TextView) GlobalResource.getListOfViews()
                    .get(Constants.OVERLAY_LAYOUT).findViewById(R.id.overlay_potions_count);
            t.setText((playerPortions - 1) + "");
        }
    }

    public void restorePlayerHP(int restoreHP) {
        int playerHp = Player.getHp();
        if (playerHp + restoreHP > Player.getMaxHP())
            playerHp = Player.getMaxHP();
        else
            playerHp = playerHp + restoreHP;
        Player.setHp(playerHp);
        playerHpBar.setProgress(playerHp);
        getHitView.setVisibility(View.GONE);
        mapGetHitView.setVisibility(View.GONE);
        showGetDmg(restoreHP);

        MainActivity.playSoundEffect("heal.wav");
        //change to sound
        /*
        getRestore.setVisibility(View.VISIBLE);
        mapRestoreView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getRestore.setVisibility(View.GONE);
                mapRestoreView.setVisibility(View.GONE);
            }
        }, 100);
        */
    }


    public void checkGatheringGeometryTouch(String key, HashMap<Integer, PlayerItem> playerItemHashMap) {
        ObjectDetail objectDetail = objectGroup.getObjectDetailList().get(key);
        String mObjectID = objectDetail.getKey();

        if (mObjectID.equals(ObjectID.STONE))
            setPlayerItem(ItemsID.ORE, 1, true);

        if (mObjectID.equals(ObjectID.GRASS))
            setPlayerItem(ItemsID.GRASS, 1, true);

        objectDetail.getModel().setVisible(false);
        objectDetail.getModel().setPickingEnabled(false);
    }


    public void checkLocationGeometryTouch(String key, int GAME_STATE, IRadar mRadar
            , HashMap<Integer, PlayerItem> playerItemHashMap, GameGenerator gameGenerator) {

        ObjectDetail objectDetail = objectGroup.getObjectDetailList().get(key);
        int atkDmg = Player.getAtkDmg();
        ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(objectDetail.getKey());
        objectHpBar.setVisibility(View.VISIBLE);

        if (GAME_STATE == GlobalResource.STATE_MARKER) {
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
            objectDetail.getModel().setPickingEnabled(false);
            objectDetail.getModel().setVisible(false);
            mRadar.remove(objectDetail.getModel());

            if (GAME_STATE != GlobalResource.STATE_MIST) {
                setPlayerItem(ItemsID.GOLD, objectDATA.getGoldDrop(), true);
            } else
                mMistManager.modelIDlist.remove(key);

            if (GAME_STATE == GlobalResource.STATE_LOCATIONBASED || GAME_STATE == GlobalResource.STATE_MIST)
                locationObjectCount++;

            if (locationObjectCount == 3) {
                resetState();
                if (GAME_STATE == GlobalResource.STATE_MIST)
                    setPlayerItem(ItemsID.MUMMY_PIECE, 1, true);
            }


            if (GAME_STATE == GlobalResource.STATE_METEOR) {
                objectDetail.getModel().setVisible(true);
                checkMeteor(objectDetail.getModel());
            }

        }
    }
}
