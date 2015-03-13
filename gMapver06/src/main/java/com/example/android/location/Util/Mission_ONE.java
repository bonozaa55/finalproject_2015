package com.example.android.location.Util;

import android.content.Context;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.GameManagement.GameGenerator;
import com.example.android.location.Interface.MyCraftMissionManager;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Mission.CraftMission;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Resource.Player.Player;
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
    public static final int STATE_GET_CRAFT_MISSION = 2;
    public static final int STATE_GET_NEW_ITEM = 3;
    public static final int STATE_COMPLETE = 4;
    public static final int STATE_LOSE_TO_BOSS = 5;
    public static final int STATE_WIN_BOSS=6;
    static GameGenerator gameGenerator;
    static IMetaioSDKAndroid metaioSDKAndroid;
    static Context context;

    public Mission_ONE(GameGenerator gameGenerator, IMetaioSDKAndroid metaioSDKAndroid, Context context) {
        Mission_ONE.gameGenerator = gameGenerator;
        Mission_ONE.metaioSDKAndroid = metaioSDKAndroid;
        Mission_ONE.context = context;
    }

    public Mission_ONE() {
    }

    public void startMission() {
        MainActivity.showSimpleDialog("ผู้เฒ่า", "ยินดีต้อนรับสู่โลกใหม่ สหายข้า! เมื่อเจ้าได้เข้าสู่โลกแห่งนี้แล้วจงเดินทางไปพบกับท่านผู้นำทั้ง 13 " +
                "ของเราด้วยเถิด ท่านจงสังเกตุดีๆท่านผู้นำจะยืนอยู่ตรงชั้นล่างสุดของตึกที่สูงที่สุดในโลกแห่งนี้");
        GlobalResource.setMISSION_STATE(Mission_ONE.STATE_GET_MISSION);
        //resetStateToMission();
    }

    public void resetStateToMission() {
        String filePath = AssetsManager.getAssetPath(context,
                "TrackingConfig/Assets/MarkerConfig_Mission.xml");
        metaioSDKAndroid.setTrackingConfiguration(filePath);
        setVisibilityModel(true);
    }

    public void checkMissionState() {
        int Mission_STATE = GlobalResource.getMISSION_STATE();
        CraftMission mCraftMission = GlobalResource.getCraftMissionList().get(0);
        switch (Mission_STATE) {
            case STATE_GET_MISSION:
                MainActivity.showSimpleDialog("ผู้เฒ่า", "\tช่วยเราด้วย ตอนนี้มีปีศาจไส้อั่วเมาแล้วอาละวาด " +
                        "คอยระรานคนที่เดินผ่านไปมา ถ้าข้าจำไม่ผิดละก็อยู่แถวๆทางเข้าคณะของคนเสื้อน้ำเงินนะ ท่านช่วยไปกำจัดมันให้เราหน่อยได้ไหม");
                //player run to boss ,dev need to edit boss location
                break;
            case STATE_LOSE_TO_BOSS:
                if (Player.getHp() <= 0) {
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "\tสหายยข้า! ดูเหมือนเจ้าจะเจ็บหนักนะเจ้ารีบไปรักษาตัวเถอะ ที่บ่อน้ำรูปช้างของเรา" +
                            "สามารถช่วยฟื้นฟูเจ้าได้");
                    gameGenerator.stopTimer();
                    gameGenerator.notifyEvent(ObjectLoader.getObjectGroupList().get(ObjectID.OLD_MAN_KARN)
                            ,GlobalResource.STATE_HEALING);
                } else {
                    if (mCraftMission.getMissionStatus() != 2) {
                        MainActivity.showSimpleDialog("ผู้เฒ่า", "\tสหายยข้า! ไม่ต้องพูดอะไรข้าเข้าใจแล้วจงรับสิ่งนี้ไป ใบรายการสำหรับสร้าง" +
                                "หนังสติ๊ก เอาไว้ไปต่อกรกับเจ้าปีศาจไส้อั่ว อ้อข้าลืมบอกไปอย่างนึงเจ้าสามารถหาสิ่งของได้ที่บริเวณถนนแถวๆโซนจิบชานะ");
                        mCraftMission.setMissionStatus(2);
                        ArrayList<CraftMission> temp= MyCraftMissionManager.getMyCraftMissionList();
                        temp.add(mCraftMission);
                        MyCraftMissionManager.setMyCraftMissionList(temp);
                        MainActivity.makeToast("คุณได้รับ ใบรายการสร้างหนังสติ๊ก 1 ea!");
                        GlobalResource.setMISSION_STATE(STATE_GET_CRAFT_MISSION);
                    }
                }
                break;
            case STATE_GET_CRAFT_MISSION:
                if(mCraftMission.getMissionStatus()==3){
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "ว้าวว เจ้าได้สิ่งของมาครบแล้วข้าจะสร้างหนังสติ๊กให้เจ้าเอง มาๆ ส่งของทั้งหมดมา" +
                            "ฮ่าๆๆ ข้าบอกว่าทั้งหมดไงง");
                    Player.setAtkDmg(50);
                    GlobalResource.setMISSION_STATE(STATE_GET_NEW_ITEM);
                    MainActivity.makeToast("Your Atk BOOSTED!!!");
                }else {
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "ไปหาสิ่งของมาให้ครบก่อน ถ้าถึงจะสร้างให้เจ้า ว่ะฮ่าๆๆๆ");
                }
                break;
            case STATE_GET_NEW_ITEM:
                MainActivity.showSimpleDialog("ผู้เฒ่า", "ข้าได้ช่วยเหลือเจ้าในทุกๆทางแล้วว ที่เหลือก็ขึ้นอยู่กับฝีมือของเจ้าแล้วละสหายข้าา " +
                        "ขอให้เจ้าจงโชคดี ว่ะฮ่าๆๆๆๆ");
                break;
            case STATE_WIN_BOSS:
                MainActivity.showSimpleDialog("ผู้เฒ่า","ยอดเยี่ยมม ยอดเยี่ยมจริงๆ แบบนี้สิไม่ผิดหวังที่ข้าฝากความหวังไว้กับเจ้า" +
                        "เอ้าา รับไปรางวัลสำหรับเจ้าาา!");
                MainActivity.makeToastItem(ItemsID.GOLD,5000);
                GlobalResource.setMISSION_STATE(STATE_COMPLETE);
                break;
            case STATE_COMPLETE:
                MainActivity.showSimpleDialog("ผู้เฒ่า","นี้ข้าหมดตัวแล้วเนี้ย เจ้าจะมาเอาอะไรกับข้าอีกอยากได้เงินก็ไปหากับพวก" +
                        "มอนเตอร์ริมถนนนู้นน");
                break;
        }
    }

    public static void setVisibilityModel(boolean parameter) {
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.OLD_MAN_KARN).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setPickingEnabled(parameter);
            tt.setVisible(parameter);
        }
    }
}
