package com.doubleservice.findchild;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.doubleservice.DataVO.ChildDataVO;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.IBeaconDetailVO;
import com.doubleservice.bigcitynavigation.FreeHomepage;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.R.drawable;
import com.doubleservice.proxy.PreferenceProxy;
import com.radiusnetworks.ibeacon.IBeacon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ChildDetectService extends Service {

	private String TAG = "ChildDetectService";
	private int alarmDis = 5;
	private double childDistance = 0;
	private boolean isBeaconSetup = false;
	private int overDistanceCount = 0;
	private String mBeaconNumber;
	private ChildDataVO vo = new ChildDataVO();
	private boolean bIsSoundOpen = false;
	private long lastNotificationTime;
	private SimpleDateFormat sdf;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand BEGIN");
		
		sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date currentDt = new Date();
		lastNotificationTime = currentDt.getTime();

		receiveBeaconData();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy(){
		removeReceiver();
		super.onDestroy();
	}

	private void getUserSetting() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getUserSetting BEGIN");

		PreferenceProxy prefProxy = new PreferenceProxy(this);

		vo = prefProxy.getChildBeaconData();
		bIsSoundOpen = vo.bIsSound;
		mBeaconNumber = vo.mBeaconNumebr;
		Log.i(TAG, "child beacon number = " + mBeaconNumber);
		alarmDis = vo.mWarningDistance;
		Log.i(TAG, "warning distance = " + alarmDis);
		isBeaconSetup = vo.bIsBeaconSetting;
	}

	private void receiveBeaconData() {
		// TODO Auto-generated method stub
		IntentFilter mFilter = new IntentFilter("onIBeaconServiceConnect");
		registerReceiver(mBroadcastReceiver, new IntentFilter(
				"onIBeaconServiceConnect"));
	}

	private void removeReceiver() {
		// TODO Auto-generated method stub
		if (mBroadcastReceiver != null)
			unregisterReceiver(mBroadcastReceiver);
	}

	private HashMap<String, IBeacon> ibeacons  = new HashMap<String,IBeacon>();
	private int count = 0;
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			getUserSetting();
			
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
							if (overDistanceCount >= 6 ) {	
								launchAlert();
							}
						} else {
							overDistanceCount = 0;
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
		Log.i(TAG, "launchAlert BEGIN, is detect page = " +GlobalDataVO.isDetectPage);

		//removeReceiver();

		// generate Notification
		overDistanceCount = 0;
		
		Date currentDt = new Date();
		long currentTime = currentDt.getTime();
		long timeDiff = (long)((currentTime - lastNotificationTime)/1000);///60;
		if(timeDiff >=5) {
			if (!GlobalDataVO.isDetectPage) {
				lastNotificationTime = currentTime;
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this).setContentTitle(getResources().getString(R.string.dialog_title_child_gone))
						.setSmallIcon(drawable.ic_launcher)
						.setContentText(getResources().getString(R.string.dialog_msg_child_gone));

				mBuilder.setTicker("BigCity message");
				mBuilder.setAutoCancel(true);
				if(bIsSoundOpen) mBuilder.setDefaults(Notification.DEFAULT_ALL);
					//mBuilder.setSound(Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6"));
				else {
					long[] vibrate = {0,100,200,300};
					mBuilder.setVibrate(vibrate);
				}
				

				Intent resultIntent = new Intent(this, FreeHomepage.class);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
						0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				mBuilder.setContentIntent(resultPendingIntent);

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(0, mBuilder.build());
			}
		}

	}

	protected double calDistance(int rssi) {
		// TODO Auto-generated method stub

		int baseRssi = -72;
		double alphaValue = 1.4;

		if (rssi > baseRssi)
			rssi = baseRssi;

		double d = Math.pow(10, (0 - (rssi - baseRssi)) / (10 * alphaValue));

		return d;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
