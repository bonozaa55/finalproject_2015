package com.example.android.location.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.easyandroidanimations.library.BounceAnimation;
import com.example.android.location.Interface.StartActivityFragmentManager;
import com.example.android.location.R;
import com.example.android.location.Util.ImmersiveModeFragment;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Adisorn on 4/6/2015.
 */
public class StartActivity extends FragmentActivity {
    public static final String FRAGTAG = "ImmersiveModeFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOther();
        setContentView(R.layout.start_view_pager);
        StartActivityFragmentManager t=new StartActivityFragmentManager(findViewById(R.id.start_view_pager_layout)
                ,getSupportFragmentManager(),this);

    }

    void initOther(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ImmersiveModeFragment immersiveModeFragment = new ImmersiveModeFragment();
            transaction.add(immersiveModeFragment, FRAGTAG);
            transaction.commit();
        }
    }


}
