package com.doubleservice.parking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.proxy.PreferenceProxy;

public class ParkDescription extends BaseDrawerActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(
				R.layout.parking_des_page, null, false);
		frameLayout.addView(activityView);

		getActionBar().setTitle(getResources().getString(R.string.menu_parking));
		setupView();
	}

	private void setupView() {
		// TODO Auto-generated method stub
		ImageView startToUse = (ImageView)findViewById(R.id.btnStart);
		startToUse.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				ParkingVO vo = new ParkingVO();
				PreferenceProxy prefProxy = new PreferenceProxy(
						getBaseContext());
				vo = prefProxy.getIsParking();

				if (vo.isParking) {
					intent = new Intent();
					intent.setClass(ParkDescription.this, UserParkingInfo.class);
				} else {
					intent = new Intent();
					intent.setClass(ParkDescription.this, ParkWarningSetting.class);
				}
				
				ParkDescription.this.startActivity(intent);
				ParkDescription.this.finish();
			}});
	}
}
