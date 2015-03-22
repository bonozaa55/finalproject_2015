package com.example.android.location.Interface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.location.GameManagement.GameGenerator;
import com.example.android.location.R;
import com.example.android.location.Resource.GlobalResource;
import com.example.android.location.Resource.Item.ItemDATA;
import com.example.android.location.Resource.Item.ItemDetail;
import com.example.android.location.Resource.Item.ItemsID;
import com.example.android.location.Resource.Player.Player;
import com.example.android.location.Resource.Player.PlayerItem;
import com.example.android.location.Util.Constants;

import java.util.ArrayList;

/**
 * Created by Adisorn on 3/21/2015.
 */
public class MyItemGridAdapter extends BaseAdapter {
    ArrayList<PlayerItem> arrayList;
    private Context context;

    public MyItemGridAdapter(Context c) {
        // TODO Auto-generated method stub
        context = c;
        updatePlayerItem();
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return arrayList.size();
    }

    public PlayerItem getItem(int position) {
        // TODO Auto-generated method stub
        return arrayList.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.overlay_myitem_grid_item, null);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.myitem_img);
        TextView txt = (TextView) convertView.findViewById(R.id.myitem_name);
        ItemDetail t = ItemDATA.getItemList().get(arrayList.get(position).getId());

        if (t != null) {
            img.setImageResource(t.getIconResource());
            int itemQuantity=Player.getPlayerItems().get(t.getId()).getQuantity();
            txt.setText(t.getName()+"("+itemQuantity+")");
        }

        return convertView;
    }


    public void updatePlayerItem() {
        arrayList = new ArrayList<PlayerItem>(Player.getPlayerItems().values());
        arrayList.remove(Player.getPlayerItems().get(ItemsID.GOLD));
        TextView t= (TextView) GlobalResource.getListOfViews().get(Constants.MY_ITEMS_LAYOUT).findViewById(R.id.my_item_gold);
        t.setText(GameGenerator.getPlayerItemQuantity(ItemsID.GOLD)+"");
        notifyDataSetChanged();
    }
}
