package com.doubleservice.findchild;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.radiusnetworks.ibeacon.IBeaconManager;

public class FindChildDescription extends BaseDrawerActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(
				R.layout.child_detect_des_page, null, false);
		frameLayout.addView(activityView);

		getActionBar().setTitle(getResources().getString(R.string.menu_find_child));
		setupView();
	}

	private void setupView() {
		// TODO Auto-generated method stub
		ImageView btnStart = (ImageView)findViewById(R.id.imageView3);
		btnStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (IBeaconManager.getInstanceForApplication(FindChildDescription.this).checkAvailability()) {
					Intent i = new Intent();
					i.setClass(FindChildDescription.this, ChildBeaconSetup.class);
					startActivity(i);
				} else {
					showNoBle();
				}
				
			}});
	}

	protected void showNoBle() {
		// TODO Auto-generated method stub
		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setTitle(getResources().getString(R.string.setting_warn));
		MyAlertDialog.setMessage(getResources().getString(
				R.string.dialog_msg_ble_not_work));

		MyAlertDialog.setPositiveButton(
				getResources().getString(R.string.layout_btn_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		MyAlertDialog.setCancelable(false);
		MyAlertDialog.show();
	}
}
