package com.example.android.location.Interface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.location.Resource.Item_Upgradeable;
import com.example.android.location.R;
import com.example.android.location.Resource.User_data;
import com.example.android.location.Util.Constants;

import java.util.ArrayList;

public class AdapterListViewData extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private ArrayList<Item_Upgradeable> list_upgrade_item = new ArrayList<Item_Upgradeable>();

	public AdapterListViewData(Context context,
			ArrayList<Item_Upgradeable> listData) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list_upgrade_item = listData;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		// return position;
		return list_upgrade_item.get(position % list_upgrade_item.size());
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderListAdapter holderListAdapter;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_listview, null);

			holderListAdapter = new HolderListAdapter();

			holderListAdapter.item_name1 = (TextView) convertView
					.findViewById(R.id.item_name2);
			holderListAdapter.upgrade_lv = (TextView) convertView
					.findViewById(R.id.upgrade_lv);
			holderListAdapter.upgrade_cost = (TextView) convertView
					.findViewById(R.id.upgrade_gold_cost);
			holderListAdapter.item_icon1 = (ImageView) convertView
					.findViewById(R.id.icon_item0);
			holderListAdapter.button = (Button) convertView
					.findViewById(R.id.Upgrade_button);
			convertView.setTag(holderListAdapter);
			holderListAdapter.button.setTag(holderListAdapter);
		} else {
			holderListAdapter = (HolderListAdapter) convertView.getTag();
		}
		final Item_Upgradeable t1 = (Item_Upgradeable) getItem(position);
		// Item_Upgradeable t1=list_upgrade_item.get(position);
		holderListAdapter.item_name1.setText(t1.getName() + "");
		holderListAdapter.upgrade_lv.setText(t1.getUpgraded_level() + "");
		holderListAdapter.upgrade_cost.setText(t1.getUpgrade_cost() + "");
		holderListAdapter.item_icon1.setImageResource(t1.getIconPath());

		holderListAdapter.button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				User_data t = Constants.getUser_global();
				HolderListAdapter holderListAdapter = (HolderListAdapter) v
						.getTag();
				int lv_upgrade = t1.getUpgraded_level() + 1;
				int upgrade_cost = (int) (Math.pow(lv_upgrade, 3) * 2 + 123);
				int user_gold = t.getGold();
				if (user_gold >= upgrade_cost) {
					t.setGold(user_gold - upgrade_cost);
					Constants.setUser_global(t);
					t1.setUpgrade_cost(upgrade_cost);
					t1.setUpgraded_level(lv_upgrade);
					holderListAdapter.upgrade_cost.setText(upgrade_cost + "");
					holderListAdapter.upgrade_lv.setText(lv_upgrade + "");
					Constants.getUser_gold().setText(
							(user_gold - upgrade_cost) + "");
				}
			}
		});
		return convertView;
	}
}