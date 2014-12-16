package com.doubleservice.parking;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;
import android.widget.TextView;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.AlarmReceiverUtil;
import com.doubleservice.bigcitynavigation.PushMessageService;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.proxy.PreferenceProxy;

public class ParkWarningSetting extends Activity {

	private String TAG = "ParkWarningSetting";
	
	private TextView mTimeSetting;
	private ImageView mRingSetting;
	private ImageView mBtnPrevious;
	private ImageView mBtnComplete;
	private ImageView mBtnDelete;

	private Date defaultDt;

	private int mDefaultHour;
	private int mDefaultMinute;
	private int mUserSettingHour;
	private int mUserSettingMinute;
	
	private boolean bIsSoundOpen = true;
	private boolean bIsSetting = false;
	private boolean bIsParking = false;

	private int whichTime;
	private String[] mSorting; 
	private int intParkingTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.park_warn_setting);
		
		Bundle params = getIntent().getExtras();
		if(params != null) {
			bIsSetting = params.getBoolean(RequestCodeVO.CMD_REQ_ISPARKING);
			Log.i(TAG, "bIsSetting =" +bIsSetting);
		}
		
		mSorting = new String[] {
				getResources().getString(R.string.layout_parking_time1),
				getResources().getString(R.string.layout_parking_time2),
				getResources().getString(R.string.layout_parking_time3),
				getResources().getString(R.string.layout_parking_time4),
				getResources().getString(R.string.layout_parking_time5),
				getResources().getString(R.string.layout_parking_time6),
				getResources().getString(R.string.layout_parking_time7),
				getResources().getString(R.string.layout_parking_time8),
				getResources().getString(R.string.layout_parking_time9),
				getResources().getString(R.string.layout_parking_time10),
				getResources().getString(R.string.layout_parking_time11),
				getResources().getString(R.string.layout_parking_time12),
				getResources().getString(R.string.layout_parking_time13)
				};

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.menu_parking));
		
		setupView();
		getPreference();
	}

	private void getPreference() {
		// TODO Auto-generated method stub
		ParkingVO vo = new ParkingVO();
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		vo = prefProxy.getIsParking();
		
		bIsParking = vo.isParking;
		if(bIsParking) {
			mTimeSetting.setText(vo.mTime);
			//setTime(vo.mParkingHour, vo.mParkingMinute);
		} else {
			whichTime = 0;
			mTimeSetting.setText(mSorting[whichTime]);
		}
	}

	private void setupView() {
		// TODO Auto-generated method stub
		TextView txtCurrentTime = (TextView)findViewById(R.id.txtCurrentTime);
		Calendar c = Calendar.getInstance();
		Date tdt = c.getTime();
	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String time = sdf.format(tdt);
		txtCurrentTime.setText(time);
		
		mTimeSetting = (TextView) findViewById(R.id.txtTime);
		mTimeSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openSettingDialog();
			}
		});

		mRingSetting = (ImageView) findViewById(R.id.ckBoxRing);
		mRingSetting.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(bIsSoundOpen) {
					bIsSoundOpen = false;
					mRingSetting.setImageResource(R.drawable.btn_no);
				} else {
					bIsSoundOpen = true;
					mRingSetting.setImageResource(R.drawable.btn_yes);
				}
				
			}});
		
		mBtnPrevious = (ImageView) findViewById(R.id.btnBackward);
		mBtnPrevious.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ParkWarningSetting.this, ParkDescription.class);
				ParkWarningSetting.this.startActivity(intent);
				ParkWarningSetting.this.finish();
			}});
		
		mBtnComplete = (ImageView) findViewById(R.id.btnComplete);
		mBtnComplete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				settingComplete();
			}});
		
		mBtnDelete = (ImageView)findViewById(R.id.btnDeleteCar);
		mBtnDelete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(ParkWarningSetting.this);
				alertDialog.setTitle(getResources().getString(R.string.dialog_title_park_delete));
				alertDialog.setMessage(getResources().getString(R.string.dialog_msg_park_delete));
				alertDialog.setPositiveButton(getResources().getString(R.string.dialog_btn_remove),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								PreferenceProxy prefProxy = new PreferenceProxy(ParkWarningSetting.this);
								prefProxy.clearParkingData();
								deleteComplete();
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
			}});
		if(!bIsSetting) {
			mBtnDelete.setVisibility(View.GONE);
		} else {
			mBtnPrevious.setVisibility(View.GONE);
			mBtnComplete.setVisibility(View.GONE);
		}

		//setDefaultTime();
	}
	
	protected void deleteComplete() {
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		prefProxy.clearParkingData();
		Toast.makeText(this, getResources().getString(R.string.toast_str_park_delete), 
				Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent();
		intent.setClass(this, ParkDescription.class);
		this.startActivity(intent);
		this.finish();
		
	}

	protected void openSettingDialog() {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View view = inflater.inflate(R.layout.parking_time_layout, null);
		
		builder.setView(view);
		builder.setTitle(getResources().getString(R.string.dialog_title_parking_warning));
		
		
		final String unitsValues[] = mSorting;
		
		NumberPicker mNumberPicker = (NumberPicker)view.findViewById(R.id.show_num_picker);
        
        mNumberPicker.setMaxValue(12);  
        mNumberPicker.setMinValue(0);  
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener (){
            public void onValueChange(NumberPicker view, int oldValue, int newValue) {
                Log.i(TAG, "mNumberPicker value="+newValue);
                whichTime = newValue;
            }
       });
     
		
		builder.setNegativeButton(R.string.dialog_btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichTime = 0;
					}
				});

		builder.setPositiveButton(R.string.dialog_btn_confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//setTextTime();
						mTimeSetting.setText(mSorting[whichTime]);
					}

				});
		
		final AlertDialog dialog;
		dialog = builder.create();
		dialog.show();
		
	}

	private void settingComplete() {
		// TODO Auto-generated method stub
		Toast.makeText(this, getResources().getString(R.string.toast_str_park_setting),
				Toast.LENGTH_LONG).show();
		
		setParkingPreference();
		setAlarm();
		
		Intent intent = new Intent();
		intent.setClass(this, UserParkingInfo.class);
		this.startActivity(intent);
		this.finish();
	}

	private void setAlarm() {
		// TODO Auto-generated method stub
		Log.i(TAG, "setAlarm BEGIN");
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, +15);
		
		Intent alarmIntent = new Intent(this, AlarmReceiverUtil.class);
		alarmIntent.putExtra("REQ_CODE", GlobalDataVO.NOTIFICATION_PARKING_SHORT_ALERT);
	    PendingIntent sender = PendingIntent.getBroadcast(this, 
	    		GlobalDataVO.NOTIFICATION_PARKING_ALARM_ID, alarmIntent, 0);
	    AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
	}

	private void setParkingPreference() {
		// TODO Auto-generated method stub
		Log.i(TAG, "setParkingPreference BEGIN");
		
		Calendar c = Calendar.getInstance();
		
		switch(whichTime) {
		case 0:
		{
			c.add(Calendar.MINUTE, +30);
		}
			break;
		case 1:
		{
			c.add(Calendar.HOUR_OF_DAY, +1);
		}
			break;
		case 2:
		{
			c.add(Calendar.HOUR_OF_DAY, +2);
		}
			break;
		case 3:
		{
			c.add(Calendar.HOUR_OF_DAY, +3);
		}
			break;
		case 4:
		{
			c.add(Calendar.HOUR_OF_DAY, +4);
		}
			break;
		case 5:
		{
			c.add(Calendar.HOUR_OF_DAY, +5);
		}
		case 6:
		{
			c.add(Calendar.HOUR_OF_DAY, +6);
		}
		case 7:
		{
			c.add(Calendar.HOUR_OF_DAY, +7);
		}
		case 8:
		{
			c.add(Calendar.HOUR_OF_DAY, +8);
		}
		case 9:
		{
			c.add(Calendar.HOUR_OF_DAY, +9);
		}
		case 10:
		{
			c.add(Calendar.HOUR_OF_DAY, +10);
		}
		case 11:
		{
			c.add(Calendar.HOUR_OF_DAY, +11);
		}
		case 12:
		{
			c.add(Calendar.HOUR_OF_DAY, +12);
		}
			break;
		}
		Date tdt = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String time = sdf.format(tdt);
		
		ParkingVO vo = new ParkingVO();
		vo.isParking = true;
		vo.soundOpen = bIsSoundOpen;
		vo.mTime = time;
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		prefProxy.setParking(vo);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			
			Intent intent = new Intent();
			intent.setClass(this, ParkDescription.class);
			this.startActivity(intent);
			this.finish();
			break;
		}

		return true;
	}

	
}
