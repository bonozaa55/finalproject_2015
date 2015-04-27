package com.example.android.location.Interface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.location.Activity.LocationActivity;
import com.example.android.location.Activity.MarkerActivity;
import com.example.android.location.R;

/**
 * Created by Adisorn on 4/6/2015.
 */
public class StartFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.start_activity_layout, container, false);

            rootView.findViewById(R.id.location_activity_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartActivityFragmentManager.setAdapterPage(StartActivityFragmentManager.LOCATION_INTENT);
                }
            });

            rootView.findViewById(R.id.marker_activity_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartActivityFragmentManager.setAdapterPage(StartActivityFragmentManager.MARKER_INTENT);
                }
            });

            return rootView;
        }
}
