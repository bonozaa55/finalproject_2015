package com.example.android.location.Interface;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.location.Activity.MarkerActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.MarkerMode.ImageObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adisorn on 4/7/2015.
 */
public class MarkerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.marker_camera_layout, container, false);
        HashMap<Integer, ImageObject> temp1 = MarkerActivity.getmImageObject();
        for (final Map.Entry<Integer, ImageObject> t : temp1.entrySet()) {
            //re

           // if (t.getKey().intValue()!=R.id.marker3)
               // t.getValue().setIsGetItem(true);

            ImageView temp = (ImageView) rootView.findViewById(t.getKey());
            temp.setBackgroundResource(t.getValue().getBlurImageID());
            temp.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (t.getValue().isGetItem()) {
                temp.setBackgroundResource(t.getValue().getImageID());
                temp.setImageResource(R.drawable.cancel);
            }
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //need blur image
                    MarkerActivity.setTouchImageObject(t.getValue());
                    MarkerActivity.getmZoomViewManager().showZoomView(v, t.getValue().getBlurImageID());
                }
            });


            //re
            //t.getValue().setHitCount(5);
        }
        rootView.findViewById(R.id.mission_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerActivity.checkClearAll();
            }
        });
        //MarkerActivity.startAnimation(rootView);
        return rootView;
    }
}
