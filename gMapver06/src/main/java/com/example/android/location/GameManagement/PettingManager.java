package com.example.android.location.GameManagement;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.location.Activity.MainActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Object.ObjectDATA;
import com.example.android.location.Resource.Object.ObjectDetail;
import com.example.android.location.Resource.Object.ObjectID;
import com.example.android.location.Resource.Object.ObjectLoader;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Util.Constants;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Vector3d;

import java.util.Map;

/**
 * Created by Adisorn on 3/15/2015.
 */
public class PettingManager {
    static String petID = "a";
    static int index = 0, count = 0;
    IGeometry iGeometry;
    int[] divide = {20, 10, 5};
    ProgressBar petProgress;
    ObjectDetailManager mObjectDetailManager;
    boolean isEvolve = false, isEating = false, isHit = false;

    public PettingManager(ObjectDetailManager mObjectDetailManager) {
        this.mObjectDetailManager = mObjectDetailManager;
        initResource();
    }

    public static String getPetID() {
        return petID + index;
    }

    public void resumeAnimation() {
        iGeometry.startAnimation("loop", true);
    }

    void checkMultipleTouch(int hits) {
        count++;
        if (count >= hits) {
            iGeometry.stopAnimation();
            iGeometry.setTranslation(new Vector3d(0, 0, -555));
            Player.setIsGetPet(true);
            delayPickingEnable(2000);
            MainActivity.makeToast("Congrats!", Toast.LENGTH_LONG);
        } else
            MainActivity.makeToast("จับข้าอีกสิ~ ขออีก " + (hits - count) + " ที", Toast.LENGTH_SHORT);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                count = 0;
                MainActivity.makeToast("เจ้าช้าไปนะ เค๊ยกๆๆ", Toast.LENGTH_SHORT);
                isHit = false;
                delayPickingEnable(2000);

            }
        }, 2000);
    }

    void delayPickingEnable(int time) {
        iGeometry.setPickingEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iGeometry.setPickingEnabled(true);
            }
        }, time);
    }

    public void checkGeometryPetTouch(final IGeometry petGeometry) {
        if (!Player.isIsGetPet()) {
            if (!isHit) {
                isHit = true;
                petGeometry.pauseAnimation();
                MainActivity.showSimpleDialog("โดฟรามิง", "อยากได้ข้าเป็นสัตว์เลี้ยงหรอ ไม่ง่ายหรอกนะ");
            } else {
                checkMultipleTouch(10);
            }
        } else {
            if (!isEvolve && !isEating) {
                petProgress.setVisibility(View.VISIBLE);
                int playerFish = GameGenerator.getPlayerItemQuantity(ItemsID.FISH) - 1;
                if (playerFish >= 0) {
                    GameGenerator.setPlayerItem(ItemsID.FISH, -1, false);
                    ObjectDetail objectDetail = GameGenerator.getObjectGroup().getObjectDetailList().get(petGeometry.getName());
                    final ObjectDATA objectDATA = ObjectDATA.getObjectDATAHashMap().get(objectDetail.getKey());
                    float dif = objectDATA.getMaxSize() - objectDATA.getSize();
                    float scale = petGeometry.getScale().getX() + (divide[index] * dif / 100f);
                    int progress = (int) ((scale - objectDATA.getSize()) * 100 / (dif));
                    petProgress.setProgress(progress);
                    petGeometry.setScale(scale);
                    if (scale >= objectDATA.getMaxSize()) {
                        MainActivity.makeToast("Giving ITEM!", Toast.LENGTH_LONG);
                        //petGeometry.setVisible(false);
                        isEvolve = true;
                        startGiveItemAnimation(petGeometry, objectDATA);
                    }
                    isEating = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isEating = false;
                        }
                    }, 1000);
                }
            }
        }

    }

    void startGiveItemAnimation(final IGeometry geometry, final ObjectDATA objectDATA) {

        new CountDownTimer(8200, 200) {
            int count = 0;
            float scaleStep = 0.02f;

            @Override
            public void onTick(long millisUntilFinished) {
                count++;
                if (count > 0 && count <= 5)
                    geometry.setScale(geometry.getScale().getX() + scaleStep);
                if (count > 5 && count <= 10)
                    geometry.setScale(geometry.getScale().getX() - scaleStep);
                if (count > 10 && count <= 30) {
                    if (count % 4 <= 1)
                        geometry.setVisible(false);
                    else
                        geometry.setVisible(true);
                }
                if (count > 30 && count <= 40) {
                    if (count % 2 == 1)
                        geometry.setVisible(false);
                    else
                        geometry.setVisible(true);
                }
            }

            @Override
            public void onFinish() {
                isEvolve = false;
                GameGenerator.setPlayerItem(ItemsID.EGG, 1, true);
                geometry.setScale(objectDATA.getSize());
                geometry.setVisible(true);
                petProgress.setProgress(0);
                petProgress.setVisibility(View.GONE);
            }
        }.start();
    }

    void initResource() {
        View petLayout = GlobalResource.getListOfViews().get(Constants.PET_LAYOUT);
        petProgress = (ProgressBar) petLayout.findViewById(R.id.pet_progress);
        Map<String, ObjectDetail> temp = ObjectLoader.getObjectGroupList().get(ObjectID.GROUP_PET).getObjectDetailList();
        for (Map.Entry<String, ObjectDetail> t1 : temp.entrySet())
            if (t1.getValue().getKey().equals(ObjectID.PET_V1))
                iGeometry = t1.getValue().getModel();
    }

}
