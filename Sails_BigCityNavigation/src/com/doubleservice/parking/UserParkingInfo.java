package com.doubleservice.parking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.parking.map.ParkingMap;
import com.doubleservice.proxy.PreferenceProxy;

public class UserParkingInfo extends BaseDrawerActivity{
	
	private String TAG = "UserParkingInfo";
	
	private int mUserSettingHour;
	private int mUserSettingMin;
	private TextView time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(
				R.layout.park_info, null, false);
		frameLayout.addView(activityView);

		getActionBar().setTitle(getResources().getString(R.string.menu_parking));
		
		setupView();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getPreference();
	}

	private void getPreference() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getPreference BEGIN");
		ParkingVO vo = new ParkingVO();
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		vo = prefProxy.getIsParking();
		if(vo.isParking)
		{
			time.setText(vo.mTime);
		}
		
	}

	private void setupView() {
		// TODO Auto-generated method stub
		ImageView btn = (ImageView)findViewById(R.id.btnNavigation);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToNavigation();
			}});
		time = (TextView)findViewById(R.id.txtSettingTime);
	}
	
	protected void goToNavigation() {
		// TODO Auto-generated method stub
		Log.i(TAG, "goToNavigation");
		
		String alertTitle = getResources().getString(R.string.setting_warn);
		String alertMsg = getResources().getString(R.string.dialog_msg_gotomap);
		String btnConfirm = getResources().getString(R.string.dialog_btn_confirm);
		String btnCancel = getResources().getString(R.string.dialog_btn_cancel);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(alertTitle);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(btnConfirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						NavDataVO vo = new NavDataVO();
						vo.MethodID = GlobalDataVO.NAV_METHOD_PARKING;
						ApplicationController.navData = vo;
						Intent intent = new Intent();
						intent.putExtra(RequestCodeVO.CMD_NAV, vo);
						intent.setClass(UserParkingInfo.this, ParkingMap.class);//Navigation.class);
						ApplicationController.navData = vo;
						startActivity(intent);
						GlobalDataVO.CURRENT_DRAWER_ITEM = 999;
					}
				});
		builder.setNegativeButton(btnCancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void deleteParkingInfo() {
		// TODO Auto-generated method stub
		Log.i(TAG, "deleteParkingInfo BEGIN");
		
		Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(getResources().getString(R.string.dialog_title_park_delete));
		alertDialog.setMessage(getResources().getString(R.string.dialog_msg_park_delete));
		alertDialog.setPositiveButton(getResources().getString(R.string.dialog_btn_remove),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//PreferenceProxy prefProxy = new PreferenceProxy(UserParkingInfo.this);
						//prefProxy.clearParkingData();
						//deleteComplete();
					}
				});
		alertDialog.setNegativeButton(getResources().getString(R.string.dialog_btn_cancel), 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		alertDialog.setCancelable(false);
		alertDialog.show();
		
		
	}

	protected void settingPage() {
		// TODO Auto-generated method stub
		Log.i(TAG, "settingPage BEGIN");
		
		Intent intent = new Intent();
		intent.putExtra(RequestCodeVO.CMD_REQ_ISPARKING, true);
		intent.setClass(this, ParkWarningSetting.class);
		this.startActivity(intent);
	}

	protected void deleteComplete() {
		PreferenceProxy prefProxy = new PreferenceProxy(UserParkingInfo.this);
		prefProxy.clearParkingData();
		Toast.makeText(this, getResources().getString(R.string.toast_str_park_delete), 
				Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent();
		intent.setClass(this, ParkDescription.class);
		this.startActivity(intent);
		this.finish();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action if it is present.
		getMenuInflater().inflate(R.menu.park_setting_bar, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action:
			settingPage();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
