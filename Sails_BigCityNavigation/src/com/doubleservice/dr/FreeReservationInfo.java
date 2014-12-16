package com.doubleservice.dr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.AlarmReceiverUtil;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.memorycontroller.ImageLoader;
import com.doubleservice.proxy.PreferenceProxy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class FreeReservationInfo extends Activity {

	private String TAG = "FreeReservationInfo";
	private int mPosition = 0;
	private String mLocationCode;
	private ImageLoader mImageLoader;
	private String[] imgUrl = {
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve01.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve02.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve03.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve04.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve05.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve06.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve07.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve08.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve09.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve10.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve11.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve12.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve13.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve14.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/reserve15.png" };

	private NavDataVO navDataVO = new NavDataVO();

	private ReservationInfoLayout mpLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageLoader = new ImageLoader(this);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(
				getResources().getString(R.string.layout_title_reservation));

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mPosition = params.getInt(RequestCodeVO.CMD_REQ_RESERVATION);
			Log.i(TAG, "mPosition = " + mPosition);
			switch (mPosition) {
			case 1:
				mLocationCode = "31";
				break;
			case 2:
				mLocationCode = "9";
				break;
			case 3:
				mLocationCode = "7";
				break;
			case 4:
				mLocationCode = "12";
				break;
			case 5:
				mLocationCode = "8";
				break;
			}

			if (mLocationCode != null) {
				navDataVO = ApplicationController.getInstance()
						.getLocationDataByNumber(mLocationCode);
				navDataVO.MethodID = GlobalDataVO.NAV_METHOD_DINNING_ROOM;
			}

		}

		// getReservation();
		setComponent();
		setAlarmNotification();
	}
	
	private void setAlarmNotification() {
		// TODO Auto-generated method stub
		Log.i(TAG, "setAlarmNotification BEGIN");
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, +5);
		
		Intent intent = new Intent(this, AlarmReceiverUtil.class);
		intent.putExtra("REQ_CODE", GlobalDataVO.NOTIFICATION_RESERVATION_ID);
	    PendingIntent sender = PendingIntent.getBroadcast(this, 
	    		GlobalDataVO.NOTIFICATION_RESERVATION_ID, intent, 0);
	    AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	private void setComponent() {
		// TODO Auto-generated method stub
		mpLayout = new ReservationInfoLayout(this);
		setContentView(mpLayout);
		
		Calendar c = Calendar.getInstance();
		Date tdt = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 19:30");
		String info = sdf.format(tdt);
		
		String time;
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		String reservationTime = prefProxy.getReservationTime();
		if(reservationTime.equals("-1")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm");
			time = df.format(tdt);
		} else {
			time = reservationTime;
		}
		
		mpLayout.txtReservationInfo.setText(info);
		mpLayout.txtReservationInfo.setTextSize(24.0f);
		mpLayout.txtReservationInfo.setTextColor(0xffa10303);
		mpLayout.txtReservationTime.setText(time);
		mpLayout.txtReservationTime.setTextSize(18.0f);
		mpLayout.txtReservationTime.setTextColor(0xff9f9f9f);

		mImageLoader.DisplayImage(imgUrl[mPosition - 1], mpLayout.topView,
				false);
		mpLayout.btnNavigation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mLocationCode != null) {
					navDataVO.MethodID	=	GlobalDataVO.NAV_METHOD_DINNING_ROOM;
					ApplicationController.navData = navDataVO;
					Intent intent = new Intent();
					intent.putExtra(RequestCodeVO.CMD_NAV, navDataVO);
					ApplicationController.navData = navDataVO;
					intent.setClass(FreeReservationInfo.this, Navigation.class);
					startActivity(intent);
					GlobalDataVO.CURRENT_DRAWER_ITEM = 999;
				} else {
					Toast.makeText(
							FreeReservationInfo.this,
							getResources().getString(R.string.toast_str_goto7f),
							Toast.LENGTH_LONG).show();
				}

			}
		});

		mpLayout.btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder MyAlertDialog = new AlertDialog.Builder(
						FreeReservationInfo.this);
				MyAlertDialog.setTitle(getResources().getString(
						R.string.dialog_title_reservation_cancel));
				MyAlertDialog.setMessage(getResources().getString(
						R.string.dialog_msg_reservation_cancel));

				MyAlertDialog.setPositiveButton(
						getResources().getString(R.string.dialog_btn_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								clearMyReservation();
							}
						});
				MyAlertDialog.setNegativeButton(
						getResources().getString(R.string.dialog_btn_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});

				MyAlertDialog.setCancelable(false);
				MyAlertDialog.show();

			}
		});
		
		switch(mPosition){
		case 5:
		case 7:
		case 15:
			mpLayout.imgBottom.setBackgroundResource(R.drawable.bg_btm_nowaiting);
			break;
		case 2:
		case 9:
		case 13:
			mpLayout.imgBottom.setBackgroundResource(R.drawable.bg_btm_45min);
			break;
		case 3:
		case 10:
		case 14:
			mpLayout.imgBottom.setBackgroundResource(R.drawable.bg_btm_1h30);
			break;
		case 4:
		case 6:
		case 11:
			mpLayout.imgBottom.setBackgroundResource(R.drawable.bg_btm_1h50);
			break;
		case 1:
		case 8:
		case 12:
			mpLayout.imgBottom.setBackgroundResource(R.drawable.bg_btm_2h30);
			break;
		}

	}

	protected void clearMyReservation() {
		// TODO Auto-generated method stub
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		prefProxy.clearDinningRoomReservation();

		Intent intent = new Intent();
		intent.setClass(this, DinningRoomList.class);
		this.startActivity(intent);
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			Intent intent = new Intent();
			intent.setClass(this, DinningRoomList.class);
			this.startActivity(intent);
			this.finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}
