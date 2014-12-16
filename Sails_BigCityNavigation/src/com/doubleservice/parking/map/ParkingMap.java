package com.doubleservice.parking.map;

import java.util.HashMap;

import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.NaviPlan;
import map.navi.component.NavigationCalculator;
import map.navi.component.NavigationView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.radiusnetworks.ibeacon.IBeacon;

public class ParkingMap  extends BaseDrawerActivity {

	private NavigationView naviView;
	private float[] positionX = { 1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1058,1053,1048,1043,1038,1033,1028,1023,1018,1013,1008,1004,999,994,989,984,979,974,969,964,958,944,939,934,929,924,919,914,908,908,908,908,908,908,908,908,908,908,908,908,908};
	private float[] positionY = { 411,415,419,423,427,432,437,441,445,449,453,457,461,465,469,473,477,481,485,489,493,498,503,508,513,518,523,528,533,538,543,548,552,552,552,552,552,552,552,552,552,552,552,552,552,556,561,564,567,570,572,575,578,584,588,590,592,594,596,598,600,605,609,614,619,624,629,634,639,644,649,654,659,665};
	//path 1 index = 0~32
	//path 2 index = 33 ~ 43
	//path 3 index = 45 ~ 60
	//path 4 index = 61 ~73
	private NaviPlan plan;
	private NavigationCalculator navigationCalculator;
	
	private Handler mThreadHandler;
	private HandlerThread mThread;
	private int positionIndex = 0;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		//setContentView(R.layout.position1f);
		

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(R.layout.position1f, null, false);
		frameLayout.addView(activityView);
		
		naviView = (NavigationView) findViewById(R.id.navigationView);
		plan = new NaviPlan(this);
		navigationCalculator = new NavigationCalculator(this.plan);
		naviView.setNavigationCalculator(navigationCalculator);
		this.plan.setPlan(2);
		this.setTitle(plan.mapNames[2]);
		naviView.setNavigationPic(plan.mapIDs[2], R.drawable.point3,R.drawable.sail_arrow,R.drawable.pin,R.drawable.icon_navi_end,R.drawable.icon_navi_start,this.plan.currentMapPixelperMeter,this.plan.currentStandard_azimuth,this.plan.currentRoadDeviation);		
		
		mThreadHandler = new Handler();
        mThreadHandler.post(runnable);
        naviView.locatePoint.setVisibility(this.naviView.locatePoint.VISIBLE);
	}
	
	private void showToast() {
		Toast.makeText(ParkingMap.this, this.getResources().getString(R.string.dialog_msg_arrived_parking), 1000).show();
	}
	
	private Runnable runnable = new Runnable(){

        public void run(){
        	//path 1 index = 0~32
        	//path 2 index = 33 ~ 43
        	//path 3 index = 44 ~ 60
        	//path 4 index = 61 ~73
        	naviView.setLocatePointXY(positionX[positionIndex], positionY[positionIndex]);
        	if(positionIndex<=32) {
        		naviView.locatePoint.setImage(R.drawable.point2_to_bottom);
        	}
        	else if(positionIndex<=43) {
        		naviView.locatePoint.setImage(R.drawable.point2_to_left);
        	}
        	else if(positionIndex<=60) {
        		naviView.locatePoint.setImage(R.drawable.point2_to_bottom_left);
        	}
        	else if(positionIndex<=73) {
        		naviView.locatePoint.setImage(R.drawable.point2_to_bottom);
        	}
        	naviView.pointCenter();
        	positionIndex++;
        	if(positionIndex>=positionX.length){
        		positionIndex =0;
        		mThreadHandler.postDelayed(runnable, 3000);
        		showToast();
        	}
        	else
        		mThreadHandler.postDelayed(runnable, 40);
        }

    };
	
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	mThreadHandler.removeCallbacks(runnable);
    }
    
}
