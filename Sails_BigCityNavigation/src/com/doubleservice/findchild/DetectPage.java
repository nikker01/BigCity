package com.doubleservice.findchild;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.doubleservice.DataVO.ChildDataVO;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.proxy.PreferenceProxy;
import com.radiusnetworks.ibeacon.IBeacon;

public class DetectPage extends BaseDrawerActivity {

	private String TAG = "DetectPage";
	private String mBeaconNumber;

	private ChildDataVO vo = new ChildDataVO();
	private double childDistance = 0;
	private int alarmDis = 5;
	private int overDistanceCount = 0;
	private boolean bIsDialogOpen = false;

	private AnimationDrawable frameAnim;

	private TextView mDeviceName;
	private TextView mDetectRegion;
	private TextView dis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(R.layout.child_detect_page,
				null, false);
		frameLayout.addView(activityView);

		mDeviceName = (TextView) findViewById(R.id.txtDeviceName);
		mDetectRegion = (TextView) findViewById(R.id.txtDetectRegion);
		//dis = (TextView)findViewById(R.id.txtDis);

		//ApplicationController.getInstance().onIBeaconServiceStart();
		getActionBar().setTitle(
				getResources().getString(R.string.menu_find_child));

		getUserSetting();
		startUpRegister();

		frameAnim = new AnimationDrawable();
		frameAnim.addFrame(
				getResources().getDrawable(R.drawable.child_detect_f1), 500);
		frameAnim.addFrame(
				getResources().getDrawable(R.drawable.child_detect_f2), 500);
		frameAnim.addFrame(
				getResources().getDrawable(R.drawable.child_detect_f3), 500);
		frameAnim.addFrame(
				getResources().getDrawable(R.drawable.child_detect_f4), 500);
		frameAnim.setOneShot(false);

		ImageView img = (ImageView) findViewById(R.id.imageView3);
		img.setBackground(frameAnim);
		// img.setBackgroundDrawable(frameAnim);
		//frameAnim.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GlobalDataVO.isDetectPage = true;
		stopDetectService();
		frameAnim.start();
		this.startUpRegister();
	}

	@Override
	protected void onPause() {
		super.onPause();
		GlobalDataVO.isDetectPage = false;
		if(mBroadcastReceiver != null ) {
			try {
				this.unregisterReceiver(mBroadcastReceiver);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
		frameAnim.stop();
		startUpDetectService();
	}

	private void startUpRegister() {
		// TODO Auto-generated method stub
		IntentFilter mFilter = new IntentFilter("onIBeaconServiceConnect");
		registerReceiver(mBroadcastReceiver, new IntentFilter(
				"onIBeaconServiceConnect"));
	}

	private HashMap<String, IBeacon> ibeacons  = new HashMap<String,IBeacon>();
	private int count = 0;
	
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "mBroadcastReceiver BEGIN");
			HashMap<String, IBeacon> beacons = (HashMap<String, IBeacon>) intent
					.getSerializableExtra("IBEACON_HASHMAP");
			
			
			if(count<3 && !beacons.containsKey(mBeaconNumber)) {
				count++;
			}
			else{
			if(!beacons.containsKey(mBeaconNumber)) {
				launchAlert();//alertNoSuchDevice();
			}
			
			Iterator localIterator = beacons.entrySet().iterator();

			try {
				while (localIterator.hasNext()) {

					if (!localIterator.hasNext()) {
						return;
					}

					IBeacon localIBeacon = (IBeacon) ((Map.Entry) localIterator
							.next()).getValue();

					String point = localIBeacon.getMajor() + "_"
							+ localIBeacon.getMinor();

					if (point.equals(mBeaconNumber)) {

						//childDistance = localIBeacon.getAccuracy();
						childDistance = calDistance(localIBeacon.getRssi());

						// Log.i(TAG, "Beacon dis = " + childDistance +
						// ", overDistanceCount = " +overDistanceCount);

						if (childDistance > alarmDis) {
							overDistanceCount++;
						} else {
							overDistanceCount = 0;
						}

						if (overDistanceCount >= 6) {
							launchAlert();
						}

					}
				}

				Log.i(TAG, "addIBeconsDetails END");
			} catch (NullPointerException e) {
				Log.i(TAG, "addIBeconsDetails With Null Data");
			}
			count =0;
			}
		}
	};

	protected void launchAlert() {
		// TODO Auto-generated method stub
		Log.i(TAG, "launchAlert BEGIN");

		overDistanceCount = 0;

		if (!bIsDialogOpen) {
			bIsDialogOpen = true;
			final MediaPlayer mp = MediaPlayer.create(this, R.raw.doorbell);
			mp.start();
			Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(getResources().getString(
					R.string.dialog_title_child_gone));
			alertDialog.setMessage(getResources().getString(
					R.string.dialog_msg_child_gone));
			alertDialog.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							bIsDialogOpen = false;
						}
					});

			alertDialog.setCancelable(false);
			alertDialog.show();
		}

	}

	protected void alertNoSuchDevice() {
		// TODO Auto-generated method stub
		Log.i(TAG, "alertNoSuchDevice");
		
		if (!bIsDialogOpen) {
			bIsDialogOpen = true;
			Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(getResources().getString(
					R.string.dialog_title_child_gone));
			alertDialog.setMessage(getResources().getString(
					R.string.dialig_msg_cant_detect_device));
			alertDialog.setPositiveButton(
					getResources().getString(R.string.dialog_btn_confirm),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							bIsDialogOpen = false;
						}
					});
			alertDialog.setNegativeButton(
					getResources().getString(R.string.dialog_btn_gosetting),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							goToSetting();
						}
					});

			alertDialog.setCancelable(false);
			alertDialog.show();
		}
	}

	protected void goToSetting() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(this, ChildBeaconSetup.class);
		this.startActivity(intent);
		this.finish();
	}

	protected double calDistance(int rssi) {
		// TODO Auto-generated method stub

		int baseRssi = -69;
		double alphaValue = 1.4;

		if (rssi > baseRssi)
			rssi = baseRssi;

		double d = Math.pow(10, (0 - (rssi - baseRssi)) / (10 * alphaValue));
		
		return d;
	}
	
	private void stopDetectService() {
		Intent intent = new Intent(this, ChildDetectService.class);
		stopService(intent);
	}

	private void startUpDetectService() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, ChildDetectService.class);
		startService(intent);
	}

	private void getUserSetting() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getUserSetting BEGIN");

		PreferenceProxy prefProxy = new PreferenceProxy(this);

		vo = prefProxy.getChildBeaconData();
		mBeaconNumber = vo.mBeaconNumebr;
		Log.i(TAG, "child beacon number = " + mBeaconNumber);
		alarmDis = vo.mWarningDistance;
		Log.i(TAG, "warning distance = " + alarmDis);

		mDeviceName.setText(mBeaconNumber);
		mDetectRegion.setText(getResources().getString(
				R.string.layout_find_child_txt1)
				+ Integer.toString(alarmDis) + "m~10m");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action: {
			Intent intent = new Intent();
			intent.setClass(DetectPage.this, ChildBeaconSetup.class);
			startActivity(intent);
			DetectPage.this.finish();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action if it is present.
		getMenuInflater().inflate(R.menu.menu_find_child, menu);
		return true;
	}
}
