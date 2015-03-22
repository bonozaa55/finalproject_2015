package com.example.android.location.GameManagement;

import android.os.CountDownTimer;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.Resource.Object.ObjectID;
import com.metaio.sdk.jni.IGeometry;

import java.util.ArrayList;

/**
 * Created by Adisorn on 3/21/2015.
 */
public class MistManager {
    IGeometry models[];
    GameGenerator mGameGenerator;
    CountDownTimer countDownTimer;
    ArrayList<String> modelIDlist=new ArrayList<String>();



    public void generateAttacker(){
        stopTimer();
        initResource();
        if(models==null)
            mGameGenerator=MainActivity.getmGameGeneretor();
        countDownTimer= new CountDownTimer(15200,1000) {
            int time=0;

            @Override
            public void onTick(long millisUntilFinished) {
                time++;

                if(time%3==0)
                    mGameGenerator.playerGetHit(20,false);
                if(time%5==0){
                    int random=mGameGenerator.randInt(0,modelIDlist.size()-1);
                    IGeometry t=GameGenerator.getObjectGroup().getObjectDetailList().get(modelIDlist.get(random)).getModel();
                    t.stopAnimation();
                    t.startAnimation("pri_attack");
                }
            }
            @Override
            public void onFinish() {
                generateAttacker();
            }
        }.start();
    }

    public void stopTimer(){
        if(countDownTimer!=null)
            countDownTimer.cancel();
    }

    public void onMistModelAnimationEnd(String animationName,IGeometry model){
        if(animationName.equals("pri_attack")){
            mGameGenerator.playerGetHit(100,true);
            model.startAnimation("pri_attack_back");
        }else if(animationName.equals("pri_attack_back")){
            model.startAnimation("pri_loop",true);
        }
    }


    void initResource(){
        for (int i=0;i<3;i++)
            modelIDlist.add(ObjectID.GROUP_PRISONER+"_"+ObjectID.PRISONER+"_"+i);
    }
}
