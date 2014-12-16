package com.doubleservice.findfriends;

import java.util.ArrayList;
import java.util.HashMap;

import com.doubleservice.bigcitynavigation.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater myInflater;
	ArrayList<HashMap> list = null;

	public FriendListAdapter(Context context, ArrayList<HashMap> data) {
		this.mContext = (Activity)context;
		myInflater = LayoutInflater.from(context);
		this.list = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = myInflater.inflate(R.layout.friend_item, null);

			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.friendName);
			holder.phone = (TextView)convertView.findViewById(R.id.friendPhone);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText((String) list.get(position).get("id"));
		holder.phone.setText((String) list.get(position).get("phone"));
		
		return convertView;
	}
	
	public final static class ViewHolder {
		public TextView name;
		public TextView phone;
	}
}
