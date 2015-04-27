package com.example.android.location.GameManagement;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.android.location.Activity.LocationActivity;
import com.example.android.location.Interface.MyCraftMissionManager;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDATA;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Mission.CraftMission;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Resource.Player.PlayerItem;
import com.example.android.location.Util.Constants;
import com.google.android.gms.games.internal.GamesLog;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.tools.io.AssetsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 2/24/2015.
 */
public class Mission_ONE {
    public static final int STATE_GET_MISSION = 1;
    public static final int STATE_GO_TO_BOSS = -1;
    public static final int STATE_GO_TO_HEAL = -2;
    public static final int STATE_GET_CRAFT_MISSION = 3;
    public static final int STATE_GO_TO_AREA1 = -3;
    public static final int STATE_GO_TO_AREA2_AND_FISHING = -4;
    public static final int STATE_GET_CRAFT_MISSION_2 = 7;
    public static final int STATE_GET_NEW_ITEM_DEF = 8;
    public static final int STATE_LOSE_TO_BOSS = 2;
    public static final int STATE_WIN_BOSS = 10;
    public static final int STATE_COMPLETE = 11;
    static GameGenerator gameGenerator;
    static IMetaioSDKAndroid metaioSDKAndroid;
    static Context context;
    InstructionManager mInstructionManager;
    View instructionView;

    public Mission_ONE(GameGenerator gameGenerator, IMetaioSDKAndroid metaioSDKAndroid, Context context
            , InstructionManager mInstructionManager) {
        Mission_ONE.gameGenerator = gameGenerator;
        Mission_ONE.metaioSDKAndroid = metaioSDKAndroid;
        Mission_ONE.context = context;
        this.mInstructionManager = mInstructionManager;
    }

    public Mission_ONE() {
    }

    public static void setVisibilityModel(boolean parameter) {
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.THE_OLD_MAN).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setPickingEnabled(parameter);
            tt.setVisible(parameter);
        }
    }

    public void startMission() {
        instructionView=GlobalResource.getListOfViews().get(Constants.HELP_LAYOUT);
        mInstructionManager.showMarker(GlobalResource.getMISSION_STATE());
        mInstructionManager.setSTATE(GlobalResource.getMISSION_STATE());

        if (GlobalResource.getMISSION_STATE() == STATE_GET_MISSION) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "ยินดีต้อนรับสู้โลกของข้า ว่ะฮ่าๆ " +
                            "รีบๆมาหาข้าา ข้ามีเรื่องอยากขอร้องเจ้าหน่อย",1);
                    showInstruction(GlobalResource.STATE_MISSION);
                    mInstructionManager.showMarker(STATE_GET_MISSION);
                    GlobalResource.setMISSION_STATE(Mission_ONE.STATE_GET_MISSION);

                }
            }, 3500);
        }
        CraftMission mCraftMission = GlobalResource.getCraftMissionList().get(0);
        CraftMission mCraftMission2 = GlobalResource.getCraftMissionList().get(1);
        if(GlobalResource.getMISSION_STATE()>=STATE_LOSE_TO_BOSS)
            mInstructionManager.showMarker(STATE_GO_TO_HEAL);
        if (GlobalResource.getMISSION_STATE() >= STATE_GET_CRAFT_MISSION) {
            mCraftMission.setMissionStatus(2);
            ArrayList<CraftMission> temp = MyCraftMissionManager.getMyCraftMissionList();
            temp.add(mCraftMission);
            mInstructionManager.showMarker(STATE_GO_TO_AREA1);
            MyCraftMissionManager.setMyCraftMissionList(temp);
        }

        if (GlobalResource.getMISSION_STATE() >= STATE_GET_CRAFT_MISSION_2) {
            mCraftMission2.setMissionStatus(2);
            mInstructionManager.showMarker(STATE_GO_TO_AREA2_AND_FISHING);
            ArrayList<CraftMission> temp = MyCraftMissionManager.getMyCraftMissionList();
            temp.add(mCraftMission2);
            MyCraftMissionManager.setMyCraftMissionList(temp);
        }
        if(GlobalResource.getMISSION_STATE()==STATE_GET_NEW_ITEM_DEF){
            mInstructionManager.showMarker(STATE_GO_TO_BOSS);
        }if(GlobalResource.getMISSION_STATE()==STATE_GET_NEW_ITEM_DEF){

        }


        //resetStateToMission();
    }

    public void bossFear() {
        com.example.android.location.Activity.LocationActivity.showSimpleDialog("ปีศาจไส้อั่ว", "ข้ากลัวแล้วว ข้าจะไม่ระรานใครอีกแล้ว",0);
    }

    public void resetStateToMission() {
        String filePath = AssetsManager.getAssetPath(context,
                "TrackingConfig/Assets/MarkerConfig_Mission.xml");
        metaioSDKAndroid.setTrackingConfiguration(filePath);
        setVisibilityModel(true);
    }

    void showInstruction(int STATE){
        mInstructionManager.setSTATE(STATE);
        mInstructionManager.highLightArea();
        mInstructionManager.changeInstructionView(STATE);
        instructionView.setVisibility(View.VISIBLE);

    }

    public void checkMissionState() {
        int Mission_STATE = GlobalResource.getMISSION_STATE();
        CraftMission mCraftMission = GlobalResource.getCraftMissionList().get(0);
        final CraftMission mCraftMission2 = GlobalResource.getCraftMissionList().get(1);
        switch (Mission_STATE) {
            case STATE_GET_MISSION:
                LocationActivity.showSimpleDialog("ผู้เฒ่า", "\tโปรดช่วยเราด้วย ตอนนี้มีปีศาจไส้อั่วเมาแล้วอาละวาด " +
                        "คอยระรานคนที่เดินผ่านไปมาา กำจัดมันให้เราที",1);

                showInstruction(GlobalResource.STATE_MARKER);
                mInstructionManager.showMarker(STATE_GO_TO_BOSS);
                break;
            case STATE_LOSE_TO_BOSS:
                if (Player.getHp() <= 0) {
                    com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "\tสหายยข้า! ดูเหมือนเจ้าจะเจ็บหนักนะเจ้ารีบไปรักษาตัวเถอะ",1);
                    showInstruction(GlobalResource.STATE_HEALING);
                    mInstructionManager.showMarker(STATE_GO_TO_HEAL);
                } else {
                    if (mCraftMission.getMissionStatus() != 2) {
                        com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "\tสหายยข้า! ไม่ต้องพูดอะไรข้าเข้าใจแล้วจงรับสิ่งนี้ไป ใบรายการสำหรับสร้าง" +
                                "หนังสติ๊ก เอาไว้ไปต่อกรกับเจ้าปีศาจไส้อั่ว",1);

                        showInstruction(GlobalResource.STATE_LOCATIONBASED);
                        mInstructionManager.showMarker(STATE_GO_TO_AREA1);
                        mCraftMission.setMissionStatus(2);
                        ArrayList<CraftMission> temp = MyCraftMissionManager.getMyCraftMissionList();
                        temp.add(mCraftMission);
                        MyCraftMissionManager.setMyCraftMissionList(temp);
                        com.example.android.location.Activity.LocationActivity.makeToast("คุณได้รับ ใบรายการสร้างหนังสติ๊ก 1 ea!", Toast.LENGTH_LONG);
                        GlobalResource.setMISSION_STATE(STATE_GET_CRAFT_MISSION);
                    }
                }
                break;
            case STATE_GET_CRAFT_MISSION:
                if (mCraftMission.getMissionStatus() == 3) {
                    com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "ว้าวว เจ้าได้สิ่งของมาครบแล้วข้าจะสร้างหนังสติ๊กให้เจ้าเอง มาๆ ส่งของทั้งหมดมา" +
                            "ฮ่าๆๆ ข้าบอกว่าทั้งหมดไงง",1);
                    showInstruction(GlobalResource.STATE_AREA2);
                    mInstructionManager.showMarker(STATE_GO_TO_AREA2_AND_FISHING);
                    Player.getPlayerEquipment().remove(ItemsID.ATK_HAND);
                    Player.getPlayerEquipment().put(ItemsID.ATK_SLINK_SHOT, new PlayerItem(ItemsID.ATK_SLINK_SHOT, 1));
                    Player.setAtkDmg(ItemDATA.getItemList().get(ItemsID.ATK_SLINK_SHOT).getAtkDMG());
                    GlobalResource.setMISSION_STATE(STATE_GET_CRAFT_MISSION_2);
                    Player.removeMaterialRequired(mCraftMission.getMaterialRequireList());
                    com.example.android.location.Activity.LocationActivity.getPlayerDB().updatePlayerEquipment(ItemsID.ATK_SLINK_SHOT + "", ItemsID.DEF_OLD_SHIRT + "");
                    com.example.android.location.Activity.LocationActivity.playSound("get_item.mp3", false);
                    com.example.android.location.Activity.LocationActivity.makeToast("คุณได้รับหนังสติํกทรงพลัง 1 ea", Toast.LENGTH_LONG);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCraftMission2.setMissionStatus(2);
                                ArrayList<CraftMission> temp = MyCraftMissionManager.getMyCraftMissionList();
                                temp.add(mCraftMission2);
                                MyCraftMissionManager.setMyCraftMissionList(temp);
                                com.example.android.location.Activity.LocationActivity.makeToast("คุณได้รับ ใบรายการสร้างเสื้อจอมยุทธ 1 ea!"
                                        , Toast.LENGTH_LONG);
                            }
                        }, 1000);


                    } else {
                        com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "ไปหาสิ่งของมาให้ครบก่อน ถ้าถึงจะสร้างให้เจ้า ว่ะฮ่าๆๆๆ",1);
                    }

                break;
            case STATE_GET_CRAFT_MISSION_2:
                if (mCraftMission2.getMissionStatus() == 3) {
                    com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "เจ้านี้สุดยอดจริงๆ หาของมาได้ครบถ้วน เอ้า! รับไปตามสัญญาเสื้อจอมยุทธของเจ้า" +
                            "ใช้งานระวังหน่อยละ ถ้าขาดข้าฆ่าเจ้าแน่ มันทำยากนะรู้ไหมมม",1);
                    Player.getPlayerEquipment().remove(ItemsID.DEF_OLD_SHIRT);
                    Player.getPlayerEquipment().put(ItemsID.DEF_NICE_SHIRT, new PlayerItem(ItemsID.DEF_NICE_SHIRT, 1));
                    Player.setDefDmg(ItemDATA.getItemList().get(ItemsID.DEF_NICE_SHIRT).getDefDMG());
                    com.example.android.location.Activity.LocationActivity.playSound("get_item.mp3", false);
                    Player.removeMaterialRequired(mCraftMission2.getMaterialRequireList());
                    GlobalResource.setMISSION_STATE(STATE_GET_NEW_ITEM_DEF);
                    com.example.android.location.Activity.LocationActivity.getPlayerDB().updatePlayerEquipment(ItemsID.ATK_SLINK_SHOT + "", ItemsID.DEF_NICE_SHIRT + "");
                    LocationActivity.makeToast("คุณได้รับเสื้อจอทยุทธ 1 ea", Toast.LENGTH_LONG);
                    mInstructionManager.setSTATE(GlobalResource.STATE_MARKER);
                    mInstructionManager.highLightArea();
                } else {
                    com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "ไปหาสิ่งของมาให้ครบก่อน เจ้าถึงจะได้รับมันไป ว่ะฮ่าๆๆๆ",1);
                }
                break;
            case STATE_GET_NEW_ITEM_DEF:
                com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "ข้าไม่มีอะไรจะให้เจ้าแล้วละ ที่เหลือขึ้นอยู่กับความถึกของเจ้าแล้วล่ะ ปีศาจไส้อั่วนี้โหดใช่ย่อย" +
                        " นะว่าไหม",1);

                break;
            case STATE_WIN_BOSS:
                com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "ยอดเยี่ยมม ยอดเยี่ยมจริงๆ แบบนี้สิไม่ผิดหวังที่ข้าฝากความหวังไว้กับเจ้า" +
                        "เอ้าา รับไปรางวัลสำหรับเจ้าาา!",1);
                GameGenerator.setPlayerItem(ItemsID.GOLD, 10000, true);
                GlobalResource.setMISSION_STATE(STATE_COMPLETE);
                break;
            case STATE_COMPLETE:
                com.example.android.location.Activity.LocationActivity.showSimpleDialog("ผู้เฒ่า", "นี้ข้าหมดตัวแล้วเนี้ย เจ้าจะมาเอาอะไรกับข้าอีกอยากได้เงินก็ไปหากับพวก" +
                        "มอนเตอร์ริมถนนนู้นน",1);
                break;
        }
        com.example.android.location.Activity.LocationActivity.getPlayerDB().updatePlayerData();
    }
}
