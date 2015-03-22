package com.example.android.location.GameManagement;

import android.widget.ProgressBar;

import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Util.Constants;

/**
 * Created by Adisorn on 2/22/2015.
 */
public class HealManager {

    ProgressBar healValue;


    public HealManager() {
        initResource();
    }

    public double calculateShakeValue(float[] values, double mAccelCurrent) {
        double mAccelLast, mAccel = 0;
        double x = values[0];
        double y = values[1];
        double z = values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = Math.sqrt((x * x + y * y + z * z));
        double delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        mAccel *= 0.1;
        mAccel = Math.abs(Math.round(mAccel));
        healValue.setProgress((int) (healValue.getProgress()+mAccel));
        return mAccelCurrent;
    }
    void initResource(){
        healValue = (ProgressBar) GlobalResource.getListOfViews().get(Constants.HEAL_LAYOUT).findViewById(R.id.healing_progress);
    }
}
