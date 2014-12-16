package com.doubleservice.bigcitynavigation;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.R.drawable;
import com.doubleservice.parking.ParkDescription;
import com.doubleservice.proxy.PreferenceProxy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PushMessageService extends Service {

	private String TAG = "PushMessageService";
	private int intentId;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "onStart BEGIN, ID = " + startId);

		try {
			Bundle params = intent.getExtras();
			intentId = 0;
			if (params != null) {
				intentId = params.getInt(RequestCodeVO.CMD_ALARM_NOTIFICATION);
				Log.i(TAG, "intentId = " + intentId);
				switch (intentId) {
				case GlobalDataVO.NOTIFICATION_PARKING_ALARM_ID: {
					showParkingNotification();
					break;
				}
				case GlobalDataVO.NOTIFICATION_RESERVATION_ID: {
					String msg = getResources().getString(
							R.string.notification_msg_dr);
					showNotification(msg);
					break;
				}
				case GlobalDataVO.NOTIFICATION_PARKING_SHORT_ALERT: {
					String msg = getResources().getString(
							R.string.notification_msg_park_15min);
					showNotification(msg);
					break;
				}
				}
				
			}
			Log.i(TAG, "intentId = " + intentId);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showNotification(String msg) {
		// TODO Auto-generated method stub
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setContentTitle(
						getResources().getString(R.string.setting_warn))
				.setSmallIcon(drawable.ic_launcher).setContentText(msg);

		mBuilder.setTicker("BigCity message");
		mBuilder.setAutoCancel(true);
		mBuilder.setDefaults(Notification.DEFAULT_ALL);

		Intent resultIntent = new Intent(this, FreeHomepage.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
		
		if(intentId == GlobalDataVO.NOTIFICATION_PARKING_SHORT_ALERT) {
			PreferenceProxy proxy = new PreferenceProxy(this);
			proxy.clearParkingData();
		}
	}

	private void showParkingNotification() {
		// TODO Auto-generated method stub
		Log.i(TAG, "showParkingNotification BEGIN");

		ParkingVO vo = new ParkingVO();
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		vo = prefProxy.getIsParking();

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setContentTitle(
						getResources().getString(
								R.string.dialog_title_parking_warning))
				.setSmallIcon(drawable.ic_launcher)
				.setContentText(
						getResources().getString(
								R.string.dialog_msg_parking_ontime));

		mBuilder.setTicker("BigCity message");
		mBuilder.setAutoCancel(true);
		if (vo.soundOpen) {
			mBuilder.setDefaults(Notification.DEFAULT_ALL);
		} else {
			long[] vibrate = { 0, 100, 200, 300 };
			mBuilder.setVibrate(vibrate);
		}
		/*
		 * mBuilder.setSound(Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI
		 * , "6")); long[] vibrate = {0,100,200,300};
		 * mBuilder.setVibrate(vibrate);
		 */

		Intent resultIntent = new Intent(this, FreeHomepage.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
