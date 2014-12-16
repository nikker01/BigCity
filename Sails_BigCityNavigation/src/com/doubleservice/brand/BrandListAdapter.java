package com.doubleservice.brand;

import java.util.ArrayList;
import java.util.HashMap;

import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.memorycontroller.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BrandListAdapter extends BaseAdapter{

	private Context mContext;
	private int mCount;
	private LayoutInflater myInflater;
	ArrayList<HashMap> list = null;
	private ImageLoader mImageLoader;
	private boolean mBusy = false;
	
	public BrandListAdapter(Context context, ArrayList<HashMap> data, String[] url) {
		this.mContext = context;
		myInflater = LayoutInflater.from(context);
		this.list = data;
		mCount = list.size();
		mImageLoader = new ImageLoader(context);
	}
	
	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}
	
	public ImageLoader getImageLoader(){
		return mImageLoader;
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
		// TODO Auto-generated method stu
		ViewHolder holder = null;
		
		if (convertView == null) {
			convertView = myInflater.inflate(R.layout.brand_list_items, null);

			holder = new ViewHolder();
			
			holder.title = (TextView)convertView.findViewById(R.id.brandTitle);
			holder.content = (TextView)convertView.findViewById(R.id.brandContent);
			holder.img = (ImageView)convertView.findViewById(R.id.list_image);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText((String) list.get(position).get("title"));
		holder.content.setText((String) list.get(position).get("content"));
		holder.img.setImageResource(R.drawable.img_list_default);
		
		String url = "";
		url = (String) list.get(position).get("img");		//urlArrays[position];
		if (!mBusy) {
			mImageLoader.DisplayImage(url, holder.img, false);
		} else {
			mImageLoader.DisplayImage(url, holder.img, true);		
		}
		
		return convertView;
	}
	
	public final static class ViewHolder {
		public TextView title;
		public TextView content;
		public ImageView img;
		
	}
}
