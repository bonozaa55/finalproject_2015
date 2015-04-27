package com.example.android.location.Interface;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.location.R;
import com.example.android.location.Util.Constants;

/**
 * Created by Adisorn on 4/6/2015.
 */
public class SecondFragment extends Fragment {
    static final String ARG_PAGE = "page";


    public static SecondFragment create(int typePage) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, typePage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_choose_layout, container, false);
        final int typePage = getArguments().getInt(ARG_PAGE, -1);
        rootView.findViewById(R.id.newgame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityFragmentManager.startActivity(typePage, Constants.ACTION_RESET);
            }
        });
        rootView.findViewById(R.id.loadgame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityFragmentManager.startActivity(typePage, Constants.ACTION_NORMAL);
            }
        });
        TextView t = (TextView) rootView.findViewById(R.id.txt_mode);
        t.setText("Marker Mode");
        if (typePage == StartActivityFragmentManager.LOCATION_INTENT)
            t.setText("Location Mode");

        return rootView;
    }

}
