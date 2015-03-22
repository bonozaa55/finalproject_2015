package com.example.android.location.GameManagement;

import android.content.Context;
import android.os.Handler;

import com.example.android.location.Activity.MainActivity;
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
    public static final int STATE_GET_CRAFT_MISSION_2=7;
    public static final int STATE_GET_NEW_ITEM_DEF=8;
    public static final int STATE_LOSE_TO_BOSS = 5;
    public static final int STATE_WIN_BOSS=6;
    public static final int STATE_COMPLETE = 4;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.showSimpleDialog("ผู้เฒ่า", "ยินดีต้อนรับสู่โลกใหม่ สหายข้า! ถ้าเจ้าต้องการรับรู้ถึงความเป็นมาของโลกนี้ละก็จงเดินทาง" +
                        "มาหาข้า ที่โรงนา IE เถิด");
                GlobalResource.setMISSION_STATE(Mission_ONE.STATE_GET_MISSION);
            }
        }, 3500);

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
        CraftMission mCraftMission2=GlobalResource.getCraftMissionList().get(1);
        switch (Mission_STATE) {
            case STATE_GET_MISSION:
                MainActivity.showSimpleDialog("ผู้เฒ่า", "\tโปรดช่วยเราด้วย ตอนนี้มีปีศาจไส้อั่วเมาแล้วอาละวาด " +
                        "คอยระรานคนที่เดินผ่านไปมา สถานที่นั้นก็คือประตูทางเข้าของเมืองนี้ด้านที่ติดกับหอนับเวลา");
                //player run to boss ,dev need to edit boss location
                break;
            case STATE_LOSE_TO_BOSS:
                if (Player.getHp() <= 0) {
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "\tสหายยข้า! ดูเหมือนเจ้าจะเจ็บหนักนะเจ้ารีบไปรักษาตัวเถอะ ที่ป้ายของอาคาร" +
                            "ผลิตไฟฟ้าด้านทิศเหนือ มียารักษาอยู่จงไปที่นั้นเถิด");
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

                    Player.getPlayerEquipment().remove(ItemsID.ATK_HAND);
                    Player.getPlayerEquipment().put(ItemsID.ATK_SLINK_SHOT, new PlayerItem(ItemsID.ATK_SLINK_SHOT, 1));
                    Player.setAtkDmg(ItemDATA.getItemList().get(ItemsID.ATK_SLINK_SHOT).getAtkDMG());
                    GlobalResource.setMISSION_STATE(STATE_GET_NEW_ITEM);
                    Player.removeMaterialRequired(mCraftMission.getMaterialRequireList());

                    MainActivity.makeToast("คุณได้รับหนังสติํกทรงพลัง 1 ea");
                }else {
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "ไปหาสิ่งของมาให้ครบก่อน ถ้าถึงจะสร้างให้เจ้า ว่ะฮ่าๆๆๆ");
                }
                break;
            case STATE_GET_NEW_ITEM:
                if (mCraftMission2.getMissionStatus() != 2) {
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "โอ๊ะดูเหมือนข้าจะลืมให้บางอย่างกับเจ้านะ นี้ใบรายการสำหรับสร้างเสื้อจอมยุทธ" +
                            "สามารถช่วยผลการโจมตีทุกชนิด ว่ะฮ่าๆๆๆ ของดีมันไม่ได้มาง่ายๆหรอกนะข้าขอเตือนไว้ก่อน ");
                    mCraftMission2.setMissionStatus(2);

                    ArrayList<CraftMission> temp= MyCraftMissionManager.getMyCraftMissionList();
                    temp.add(mCraftMission2);
                    MyCraftMissionManager.setMyCraftMissionList(temp);
                    MainActivity.makeToast("คุณได้รับ ใบรายการสร้างเสื้อจอมยุทธ 1 ea!");
                    GlobalResource.setMISSION_STATE(STATE_GET_CRAFT_MISSION_2);
                }
                break;
            case STATE_GET_CRAFT_MISSION_2:
                if(mCraftMission2.getMissionStatus()==3){
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "เจ้านี้สุดยอดจริงๆ หาของมาได้ครบถ้วน เอ้า! รับไปตามสัญญาเสื้อจอมยุทธของเจ้า" +
                            "ใช้งานระวังหน่อยละ ถ้าขาดข้าฆ่าเจ้าแน่ มันทำยากนะรู้ไหมมม");
                    Player.getPlayerEquipment().remove(ItemsID.DEF_OLD_SHIRT);
                    Player.getPlayerEquipment().put(ItemsID.DEF_NICE_SHIRT, new PlayerItem(ItemsID.DEF_NICE_SHIRT, 1));
                    Player.setDefDmg(ItemDATA.getItemList().get(ItemsID.DEF_NICE_SHIRT).getDefDMG());

                    Player.removeMaterialRequired(mCraftMission2.getMaterialRequireList());
                    GlobalResource.setMISSION_STATE(STATE_GET_NEW_ITEM_DEF);
                    MainActivity.makeToast("คุณได้รับเสื้อจอทยุทธ 1 ea");
                }else {
                    MainActivity.showSimpleDialog("ผู้เฒ่า", "ไปหาสิ่งของมาให้ครบก่อน เจ้าถึงจะได้รับมันไป ว่ะฮ่าๆๆๆ");
                }
                break;
            case STATE_GET_NEW_ITEM_DEF:
                MainActivity.showSimpleDialog("ผู้เฒ่า", "ข้าไม่มีอะไรจะให้เจ้าแล้วละ ที่เหลือขึ้นอยู่กับความถึกของเจ้าแล้วล่ะ ปีศาจไส้อั่วนี้โหดใช่ย่อย" +
                        " นะว่าไหม");

                break;
            case STATE_WIN_BOSS:
                MainActivity.showSimpleDialog("ผู้เฒ่า","ยอดเยี่ยมม ยอดเยี่ยมจริงๆ แบบนี้สิไม่ผิดหวังที่ข้าฝากความหวังไว้กับเจ้า" +
                        "เอ้าา รับไปรางวัลสำหรับเจ้าาา!");
                MainActivity.makeToastItem(ItemsID.GOLD,10000);
                GlobalResource.setMISSION_STATE(STATE_COMPLETE);
                break;
            case STATE_COMPLETE:
                MainActivity.showSimpleDialog("ผู้เฒ่า","นี้ข้าหมดตัวแล้วเนี้ย เจ้าจะมาเอาอะไรกับข้าอีกอยากได้เงินก็ไปหากับพวก" +
                        "มอนเตอร์ริมถนนนู้นน");
                break;
        }
    }

    public static void setVisibilityModel(boolean parameter) {
        HashMap<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.THE_OLD_MAN).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t : temp.entrySet()) {
            IGeometry tt = t.getValue().getModel();
            tt.setPickingEnabled(parameter);
            tt.setVisible(parameter);
        }
    }
}
