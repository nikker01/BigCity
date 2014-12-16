package com.doubleservice.findchild;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doubleservice.DataVO.ChildDataVO;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.dr.FreeDinningRoomPage;
import com.doubleservice.proxy.PreferenceProxy;

public class ChildBeaconSetup extends Activity {

	private String TAG = "ChildBeaconSetup";
	
	private TextView mWarningDistance;
	
	private String beacon[] = {"130","133","138","140","145", "147", "149", "148"};
	private String m[] = { "5m", "10m", "15m" };
	private ImageView mSoundBox;
	private ImageView mWarnBox;
	private boolean bIsSoundOpen = true;
	private boolean bIsSoundEnable = true;
	private boolean bIsWarnEnable = true;
	private ChildDataVO mChildVO = new ChildDataVO();
	private int mDis = 5;
	
	private boolean isWarning = true;
	private boolean isBeaconSetup = false;
	private String mDeviceNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.beacon_setup_layout);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.menu_find_child));
		
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreference();
		setupView();
	}
	
	private void getPreference() {
		// TODO Auto-generated method stub
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		ChildDataVO vo =  prefProxy.getChildBeaconData();
		isBeaconSetup = vo.bIsBeaconSetting;
		if(isBeaconSetup) 
		{
			mDis = vo.mWarningDistance;
			mDeviceNumber = vo.mBeaconNumebr;
			isWarning = vo.bIsWarnning;
		}
		Log.i(TAG, "isBeaconSetup = "+isBeaconSetup);
	}

	private void setupView() {
		// TODO Auto-generated method stub
		final EditText editMajor = (EditText) findViewById(R.id.editBeaconMajor);
		if(isBeaconSetup) {
			String[] temp = mDeviceNumber.split("_");
			editMajor.setText(temp[1]);
		}
			
		mWarnBox = (ImageView)findViewById(R.id.imgIsWarning);
		mWarnBox.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("onCheckedChanged", "is Checked = " +bIsWarnEnable);
				if(bIsWarnEnable) {
					bIsWarnEnable = false;
					mWarnBox.setImageDrawable(getResources().getDrawable(R.drawable.btn_no));
				} else{
					bIsWarnEnable = true;
					mWarnBox.setImageDrawable(getResources().getDrawable(R.drawable.btn_yes));
				}
				setWarning(bIsWarnEnable);
			}});
		if(isWarning) {
			bIsWarnEnable = true;
			mWarnBox.setImageDrawable(getResources().getDrawable(R.drawable.btn_yes));
		} else {
			bIsWarnEnable = false;
			mWarnBox.setImageDrawable(getResources().getDrawable(R.drawable.btn_no));
		}
		
		mWarningDistance = (TextView)findViewById(R.id.txtWarningDis);
		mWarningDistance.setText(mDis+"m~10m");
		mSoundBox = (ImageView)findViewById(R.id.imgIsWarningSound);
		
		ImageView btnConfirm = (ImageView) findViewById(R.id.img_btnComplete);
		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mChildBeaconSetting = ""; // = "0_15";
				String userInputNumber = editMajor.getText().toString();
				boolean isRightNumber = checkBeaconNumber(userInputNumber);
				if(isRightNumber) {
					setChildBeaconPreference("2_" + userInputNumber);
					//setChildBeaconPreference(mChildBeaconSetting);
				} else if(!isRightNumber){
					Toast.makeText(ChildBeaconSetup.this, 
							getResources().getString(R.string.toast_str_wrong_beacon_number), 
							Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		mSoundBox.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("onCheckedChanged", "is Checked = " +bIsSoundEnable);
				if(bIsSoundEnable) {
					bIsSoundEnable = false;
					mSoundBox.setImageDrawable(getResources().getDrawable(R.drawable.btn_no));
				} else {
					bIsSoundEnable = true;
					mSoundBox.setImageDrawable(getResources().getDrawable(R.drawable.btn_yes));
				}
				bIsSoundOpen = bIsSoundEnable;
			}});

		/*
		mWarningDistance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new AlertDialog.Builder(ChildBeaconSetup.this)
						.setTitle("Setting")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setSingleChoiceItems(m, 0,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										setWarningDistance(which);
										dialog.dismiss();
									}
								}).setCancelable(false).show();
			}
		});
		*/
		
		ImageView btnDelete = (ImageView)findViewById(R.id.img_btnDelete);
		if(!isBeaconSetup) btnDelete.setVisibility(View.GONE);
		btnDelete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChildBeaconSetup.this, ChildDetectService.class);
				stopService(intent);
				clearBeacon();
			}});
	}
	
	public void clearBeacon() {
		// TODO Auto-generated method stub
		Log.i(TAG, "clearBeacon BEGIN");
		openDeleteDialog();
		
		/*
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		ChildDataVO vo = new ChildDataVO();
		vo = prefProxy.getChildBeaconData();
		Log.i(TAG, "clearBeacon bIsBeaconSetting ="+ vo.bIsBeaconSetting + "");
		
		if(vo.bIsBeaconSetting) {
			openDeleteDialog();
		} 
		*/
	}

	private void openDeleteDialog() {
		// TODO Auto-generated method stub
		String alertTitle = getResources().getString(R.string.setting_warn);
		String alertMsg = getResources().getString(R.string.dialog_msg_delete_beacon);
		String btnConfirm = getResources().getString(
				R.string.dialog_btn_confirm);
		String btnCancel = getResources().getString(R.string.dialog_btn_cancel);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(alertTitle);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(btnConfirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						PreferenceProxy prefProxy = new PreferenceProxy(ChildBeaconSetup.this);
						prefProxy.clearChildTracking();
						Toast.makeText(ChildBeaconSetup.this, 
								getResources().getString(R.string.toast_str_delete_beacon), 
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
						intent.setClass(ChildBeaconSetup.this, FindChildDescription.class);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(btnCancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private boolean checkBeaconNumber(String number) {
		for(int i = 0; i < beacon.length; i++) {
			if(number.equals(beacon[i])) {
				return true;
			}
		}
		return false;
	}

	private void setWarningDistance(int which) {
		// TODO Auto-generated method stub
		if(which == 0) {
			mDis = 5;
			mWarningDistance.setText(m[0]);
		} else if(which == 1) {
			mDis = 10;
			mWarningDistance.setText(m[1]);
		} else if(which == 2) {
			mDis = 15;
			mWarningDistance.setText(m[2]);
		}
		
	}

	private void setChildBeaconPreference(String mChildBeaconSetting) {
		// TODO Auto-generated method stub
		Log.i(TAG, "setChildBeaconPreference beacon munber = "
				+ mChildBeaconSetting + "is warning = " +isWarning + " Warning dis = " +mDis + 
				 " sound open = " +bIsSoundOpen);

		mChildVO.mBeaconNumebr = mChildBeaconSetting;
		mChildVO.mWarningDistance = mDis;
		mChildVO.bIsSound = bIsSoundOpen;
		mChildVO.bIsWarnning = isWarning;
		mChildVO.bIsBeaconSetting = true;
		
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		prefProxy.setChildBeaconData(mChildVO);
		prefProxy.setChildIsTracking();
		

		Toast.makeText(this,
				"setting success, Beacon number = " + mChildBeaconSetting,
				Toast.LENGTH_SHORT).show();
		

		goToChildDetectPage();
	}

	private void goToChildDetectPage() {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.setClass(this, DetectPage.class);
		startActivity(i);
	}
	
	private void setWarning(boolean isWarning) {
		Log.i(TAG, "setWarning "+isWarning);
		
		/*
		if(!isWarning) {
			//isWarning = false;
			mSoundBox.setEnabled(false);
			mSoundBox.setImageAlpha(40);
			mWarningDistance.setEnabled(false);

		} else {
			//isWarning = true;
			mSoundBox.setEnabled(true);
			mSoundBox.setImageAlpha(255);
			mWarningDistance.setEnabled(true);

		}
		*/
		
		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}

		return true;
	}
}
