package com.doubleservice.bigcitynavigation;

import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.R.color;
import com.doubleservice.dr.DinningRoomList;
import com.doubleservice.findchild.FindChildDescription;
import com.doubleservice.findfriends.FriendList;
import com.doubleservice.parking.ParkDescription;
import com.doubleservice.parking.UserParkingInfo;
import com.doubleservice.proxy.PreferenceProxy;
import com.james.views.FreeLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HomepageLayout extends FreeLayout {
	public ImageView header;
	public ImageView mFloor;
	public ImageView mFood;
	public ImageView mFindfriend;
	public ImageView mFindchild;
	public ImageView mParking;
	public ImageView mPromotion;
	public ImageView mBrand;
	public ImageView mHappygo;
	
	private Context mContext;

	public HomepageLayout(Context context) {
		super(context);
		
		this.mContext = context;

		this.setFreeLayoutFF();
		this.setPicSize(1080, 1920, TO_WIDTH);
		this.setBackgroundColor(0xfffafafa);
		
		
		header = (ImageView) this.addFreeView(new ImageView(mContext), 1080,
				414);
		header.setBackgroundResource(R.drawable.header);

		mFloor = (ImageView) this.addFreeView(new ImageView(mContext), 27, 407,
				1026, 324);
		mFloor.setBackgroundResource(R.drawable.btn_floor);
		mFloor.setTag(R.string.menu_floor);

		mFood = (ImageView) this.addFreeView(new ImageView(mContext), 27, 736,
				683, 323);
		mFood.setBackgroundResource(R.drawable.btn_food);
		mFood.setTag(R.string.menu_food);

		mFindchild = (ImageView) this.addFreeView(new ImageView(mContext), 715,
				736, 337, 323);
		mFindchild.setBackgroundResource(R.drawable.btn_findchild);
		mFindchild.setTag(R.string.menu_find_child);

		mFindfriend = (ImageView) this.addFreeView(new ImageView(mContext), 27,
				1066, 337, 323);
		mFindfriend.setBackgroundResource(R.drawable.btn_findfriend);
		mFindfriend.setTag(R.string.menu_find_friend);

		mParking = (ImageView) this.addFreeView(new ImageView(mContext), 371,
				1066, 682, 323);
		mParking.setBackgroundResource(R.drawable.btn_parking);
		mParking.setTag(R.string.menu_parking);

		mPromotion = (ImageView) this.addFreeView(new ImageView(mContext), 27,
				1395, 337, 323);
		mPromotion.setBackgroundResource(R.drawable.btn_promotion);
		mPromotion.setTag(R.string.menu_find_promotion);

		mBrand = (ImageView) this.addFreeView(new ImageView(mContext), 372,
				1395, 337, 323);
		mBrand.setBackgroundResource(R.drawable.btn_brand);
		mBrand.setTag(R.string.menu_find_brand);

		mHappygo = (ImageView) this.addFreeView(new ImageView(mContext), 716,
				1395, 337, 323);
		mHappygo.setBackgroundResource(R.drawable.btn_happygo);
		mHappygo.setTag(R.string.menu_happygo);

	}
	
}
