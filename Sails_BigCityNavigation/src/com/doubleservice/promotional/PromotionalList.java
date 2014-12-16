package com.doubleservice.promotional;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.doubleservice.DataVO.BrandDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.brand.BrandDBProxy;
import com.doubleservice.brand.BrandListAdapter;

public class PromotionalList extends Activity{

	private int mPosition = 0;
	private String TAG = "PromotionalList";
	private BrandDBProxy proxy;
	
	private int mCount;
	private String[] brandAreas;
	private String[] brandContents;
	private String[] brandIDs;
	private String[] brandImgs;
	private String[] brandTitles;
	private String[] posX;
	private String[] posY;
	private String[] shopList;
	
	private ArrayList<HashMap> dataList;
	private ListView mListview;
	private BrandListAdapter adapter;
	private int mFloor = 0; 
	private ArrayList<BrandDataVO> aryBrandVO = new ArrayList<BrandDataVO>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.brand_list_layout);
		
		Bundle params = getIntent().getExtras();
		if(params != null) {
			mFloor = params.getInt(RequestCodeVO.CMD_BRAND_FLOOR);
			Log.i(TAG, "open list =" +mFloor);
		}
		
		proxy = new BrandDBProxy(this);
		setComponent();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		proxy.closeDB();
	}
	
	private void setComponent() {
		// TODO Auto-generated method stub
		initListData();
		dataList = getData();
		
		mListview = (ListView) findViewById(R.id.listView1);
		mListview.setOnScrollListener(mScrollListener);
		adapter = new BrandListAdapter(this, dataList, brandImgs);
		mListview.setAdapter(adapter);
		mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				openPage((String)dataList.get(arg2).get("id"));
			}
			
		});
	}
	
	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				adapter.setFlagBusy(true);
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				adapter.setFlagBusy(false);
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				adapter.setFlagBusy(false);
				break;
			default:
				break;
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};
	
	public void initListData() {
		
		
		//aryBrandVO = proxy.queryDataByFloor(mFloor+1);
		
		proxy.queryPromotionalData(mFloor+1);
		
		mCount = proxy.getBrandCount();
		brandAreas = new String[mCount];
		brandContents = new String[mCount];
		brandIDs = new String[mCount];
		brandImgs = new String[mCount];
		brandTitles = new String[mCount];
		posX = new String[mCount];
		posY = new String[mCount];
		shopList = new String[mCount]; 
		
		if(mCount > 0 ) {
			brandAreas = proxy.getBrandAreas();
			brandContents = proxy.getBrandContent();
			brandIDs = proxy.getBrandID();
			brandImgs = proxy.getBrandImg();
			brandTitles = proxy.getBrandTitles();
			posX = proxy.getBrandPosX();
			posY = proxy.getBrandPosY();
			shopList = proxy.getShopList();
		}
		
	}
	
	private ArrayList<HashMap> getData() {
		// TODO Auto-generated method stub
		
		@SuppressWarnings("rawtypes")
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		HashMap<String, Object> map = new HashMap<String, Object>();

		for (int i = 0; i < mCount; i++) {
			map = new HashMap<String, Object>();
			
		
			
			map.put("id", brandIDs[i]);
			map.put("title", brandTitles[i]);
			map.put("content", brandContents[i]);
			map.put("img", brandImgs[i]);
			
			//map.put("dis", mDistance[i]);
			list.add(map);
		}
		
		return list;
		
	}
	
	protected void openPage(String id) {
		// TODO Auto-generated method stub
		Log.i(TAG, "openPage item = " +id);
		
		Intent intent = new Intent();
		intent.putExtra(RequestCodeVO.CMD_REQ_PROMOTIONAL, id);
		intent.setClass(this, FreePromotionalPage.class);
		//intent.setClass(this, PromotionalContent.class);
		this.startActivity(intent);
	}

}
