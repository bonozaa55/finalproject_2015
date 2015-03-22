package com.example.android.location.Interface;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.location.R;
import com.example.android.location.Resource.Mission.CraftMission;
import com.example.android.location.Resource.Item.ItemDetail;
import com.example.android.location.Resource.Item.ItemDATA;
import com.example.android.location.Resource.Mission.MaterialRequired;

import java.util.ArrayList;

/**
 * Created by Adisorn on 2/7/2015.
 */
public class CraftMissionAdapter implements ExpandableListAdapter{
    private Context _context;
    private ArrayList<CraftMission> craftMissionList;

    public CraftMissionAdapter(Context context,
                               ArrayList<CraftMission> craftMissionList) {
        this._context = context;
        this.craftMissionList = craftMissionList;
    }

    @Override
    public CraftMission getChild(int groupPosition, int childPosititon) {
        return this.craftMissionList.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final CraftMission mCraftMission=getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.craft_list_item, null);
        }
        View button=convertView.findViewById(R.id.craft_detail_get_recipe);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CraftMission> temp= MyCraftMissionManager.getMyCraftMissionList();
                if(mCraftMission.getMissionStatus()!=2) {
                    mCraftMission.setMissionStatus(2);
                    temp.add(mCraftMission);
                    MyCraftMissionManager.setMyCraftMissionList(temp);
                    Toast.makeText(_context,"Complete!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(_context,"You have got this mission already!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ImageView img=(ImageView) convertView.findViewById(R.id.craft_detail_img);
        img.setImageResource(mCraftMission.getImgResource());
        TextView propertyText=(TextView)convertView.findViewById(R.id.craft_detail_property);
        propertyText.setText(mCraftMission.getProperty());

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.craft_detail_title);

        if(mCraftMission.getMissionStatus()==0) {
            LinearLayout requiredLayout = (LinearLayout) convertView.findViewById(R.id.craft_detail_list_require);
            View temp=requiredLayout.getChildAt(0);
            requiredLayout.removeAllViews();
            requiredLayout.addView(temp);
            ArrayList<MaterialRequired> itemsRequired = mCraftMission.getMaterialRequireList();
            for (MaterialRequired t : itemsRequired) {
                ItemDetail item= ItemDATA.getItemList().get(t.getItemID());
                TextView tv = new TextView(_context);
                tv.setText("\t\t"+item.getName() + "(" + t.getQuantity() + ")");
                requiredLayout.addView(tv);
            }
            mCraftMission.setMissionStatus(1);
        }

        txtListChild.setText(mCraftMission.getName());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public CraftMission getGroup(int groupPosition) {
        return this.craftMissionList.get(groupPosition);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return this.craftMissionList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).getName();
        if (convertView == null) {
            LayoutInflater inflaInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflaInflater.inflate(R.layout.craft_list_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l2) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
