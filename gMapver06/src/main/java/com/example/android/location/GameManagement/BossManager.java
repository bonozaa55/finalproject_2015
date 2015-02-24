package com.example.android.location.GameManagement;

import android.os.Handler;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectGroup;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.metaio.sdk.jni.IGeometry;

import java.util.Map;

/**
 * Created by Adisorn on 2/15/2015.
 */
public class BossManager {
    public static final int STATE_UNDERLING_L_ATK = 1;
    public static final int STATE_UNDERLING_R_ATK = 2;
    public static final int STATE_BOSS_ATK = 3;
    public static final int STATE_SHIELD = 0;

    ObjectGroup objectGroup;
    ObjectDetail under_R, under_L, boss;
    int BOSS_STATE;
    int count = 1;
    boolean isBossAttack=true,isBossHitting=false, isUnderlingDead = true;

    public BossManager() {
        initResource();
    }

    public int onElephantBossTouch(String key, GameGenerator gameGenerator, int atkDmg) {
        String geometryID = objectGroup.getObjectDetailList().get(key).getKey();
        int retDmg = atkDmg;
        switch (BOSS_STATE) {
            case STATE_SHIELD:
                retDmg = 0;
                gameGenerator.playerGetHit(50);
                break;
            case STATE_UNDERLING_L_ATK:
                if (ObjectID.ELEPHANT_UNDERLING_R.equals(geometryID)) {
                    stopAnimation();
                    changeBossState(STATE_SHIELD);
                }
                break;
            case STATE_UNDERLING_R_ATK:
                if (ObjectID.ELEPHANT_UNDERLING_L.equals(geometryID)) {
                    stopAnimation();
                    changeBossState(STATE_SHIELD);
                }
                break;
        }
        return retDmg;
    }

    public void stopAnimation() {
        for (Map.Entry<String, ObjectDetail> t : objectGroup.getObjectDetailList().entrySet())
            t.getValue().getModel().startAnimation(1,2);
    }
    public void setPickingEnabled(){
        for (Map.Entry<String, ObjectDetail> t : objectGroup.getObjectDetailList().entrySet())
            t.getValue().getModel().setPickingEnabled(false);
    }

    public void changeBossState(int STATE) {
        if(!isBossHitting) {
            count = 1;
            stopAnimation();
            BOSS_STATE = STATE;
            boolean check1=STATE==STATE_UNDERLING_L_ATK&&under_L.getRemainingHP()<=0;
            boolean check2=STATE==STATE_UNDERLING_R_ATK&&under_R.getRemainingHP()<=0;
            if(check1)
                BOSS_STATE=STATE_UNDERLING_R_ATK;
            else if(check2)
                BOSS_STATE=STATE_UNDERLING_L_ATK;
            else if(under_L.getRemainingHP()<=0&&under_R.getRemainingHP()<=0&&STATE!=STATE_BOSS_ATK)
                BOSS_STATE=STATE_BOSS_ATK;

            switch (BOSS_STATE) {
                case STATE_UNDERLING_L_ATK:
                    under_L.getModel().startAnimation("attack");
                    break;
                case STATE_UNDERLING_R_ATK:
                    under_R.getModel().startAnimation("attack");
                    break;
                case STATE_BOSS_ATK:
                    isBossHitting=true;
                    boss.getModel().startAnimation("attack");
                    break;
                case STATE_SHIELD:
                    under_L.getModel().startAnimation("loop", true);
                    under_R.getModel().startAnimation("loop", true);
                    break;
            }
        }
    }

    public void checkOnAnimationEnd(String animationName, IGeometry geometry, GameGenerator gameGenerator) {
        if (animationName.equals("attack")) {
            geometry.startAnimation("attack_back");
            int dmg = 500;
            if (BOSS_STATE == STATE_UNDERLING_L_ATK || BOSS_STATE == STATE_UNDERLING_R_ATK)
                dmg = 150;
            gameGenerator.playerGetHit(dmg);
        }
        if (animationName.equals("attack_back")) {
            if (BOSS_STATE != STATE_BOSS_ATK) {
                if (count < 2)
                    geometry.startAnimation("attack");
                else
                    geometry.stopAnimation();
                count++;
            } else {
                isBossHitting=false;
                geometry.stopAnimation();
            }
        }


    }

    public void initResource() {
        objectGroup = GameGenerator.getObjectGroup();
        BOSS_STATE = STATE_SHIELD;
        Map<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_BOSS).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t1 : temp.entrySet()) {
            if (t1.getValue().getKey().equals(ObjectID.ELEPHANT_UNDERLING_L))
                under_L = t1.getValue();
            if (t1.getValue().getKey().equals(ObjectID.ELEPHANT_UNDERLING_R))
                under_R = t1.getValue();
            if (t1.getValue().getKey().equals(ObjectID.ELEPHANT))
                boss = t1.getValue();
            t1.getValue().getModel().setScale(0.2f);
            t1.getValue().getModel().startAnimation("loop", true);
        }
    }

    void checkHPsystem(final GameGenerator gameGenerator) {
        int bossRemainPercent = boss.getRemainingHP() * 100 / boss.getMaxHP();
        int underL_RemainPercent = under_L.getRemainingHP() * 100 / under_L.getMaxHP();
        int underR_RemainPercent = under_R.getRemainingHP() * 100 / under_R.getMaxHP();
        if (bossRemainPercent <= 50 && isBossAttack) {
            changeBossState(STATE_BOSS_ATK);
            isBossAttack = false;
        }
        if ((underL_RemainPercent <= 0 || underR_RemainPercent <= 0) && isUnderlingDead) {
            changeBossState(STATE_BOSS_ATK);
            isUnderlingDead = false;
        }
        if (bossRemainPercent <= 0) {
            MainActivity.makeToast("Congrats you had finish a hard boss");
            setPickingEnabled();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameGenerator.resetState(GlobalResource.STATE_MARKER);
                }
            }, 2000);
        }

    }
}
