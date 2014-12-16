package com.doubleservice.bigcitynavigation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import lib.locate.KNN.RssiMeanFilter;
import lib.locate.KNN.WifiReferencePointProxy;
import lib.locate.KNN.WifiReferencePointVO;
import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.IBeaconLocatePlan;
import map.navi.Data.NaviNode;
import map.navi.Data.NaviPlan;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectPostRequest;
import com.doubleservice.DataVO.PushMsgVO;
import com.doubleservice.bigcitynavigation.R.drawable;
import com.doubleservice.bigcitynavigation.pushmsg.MsgPushInterface;
import com.doubleservice.proxy.MsgListProxy;
import com.doubleservice.proxy.RtlsApiProxy;
import com.radiusnetworks.ibeacon.IBeacon;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class IbeaconService extends Service implements MsgPushInterface{

	private String TAG = "IbeaconService";
	private ArrayList<String> push_beacon = new ArrayList<String>();
	//private String[] major = {"1", "1", "1", "1", "1", "1", "2" };
	//private String[] minor = {"1", "2", "3", "4", "5", "6", "147" };
	private String[] major = {"1", "1", "1", "1", "1", "1",  "0" };
	private String[] minor = {"4", "5", "6", "1", "2", "3", "40" };
	private String[] pushName = new String[6];
	//private boolean alertOnce = false;
	private boolean[] alertOnce;
	private Long pretime;
	public HashMap<String, IBeacon> beacons;

	//****//KNN Receiver variable=====================================================================
	//public String currentFloor="";
	private ArrayList<HashMap> scanDataList;
	private ArrayList<Integer>[] rssiGroup;  
	public ArrayList<String> mApName = new ArrayList<String>();
	public int[] mApScanRssi;
	//private ArrayList<String> myBeaconName = new ArrayList<String>();
	//private String[] myBeaconName = new String[] { "0_1", "1_0", "2_0", "1_1" };
	private int[] iBeaconRssi; // = new int[] { 0, 0, 0, 0 };
	private int maxScanCount = 10;
	private int mScanCount = 0;
	private static int FUNCTION_SITESURVEY = 0;
	private static int FUNCTION_LOCATION = 1;
	private int mFunctioName;
	private boolean bIsPointScanningDone = false;
	private long preTime ;
	//private ArrayList<HashMap> list;	
	private String[] bigCityBeaconArray = new String[39]; 
	private String[] sogoBeaconArray = new String[33];
	
	//public IBeaconLocatePlan detectArea ;
	//public int[] mApScanRssi;
	public  ArrayList<String> scanList;
	
	//****//==================================================================================
	WifiReferencePointProxy proxy;
	private String currentFloor = "";
	private int changeFloorTimeLimit = 3;
	private int changeTime = 0;
	private boolean isTesting = false;
	private TestWifiService testData;
	private ArrayList<HashMap> aryBeaconMsg = new ArrayList<HashMap>();
	private boolean isMsgDialogOpen = false;
	private boolean isNeedToOpenFile = false;
	public static SailsTechActivity sailsTechActivity ;
	private MsgListProxy msgListProxy;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreate BEGIN");
		 initialLocateList();
		//proxy = new WifiReferencePointProxy(ApplicationController.getInstance());
		//proxy.initDB();
		 msgListProxy = new MsgListProxy(this,60);
		pushName[0] = this.getResources().getString(R.string.shop1);
		pushName[1] = this.getResources().getString(R.string.shop2);
		pushName[2] = this.getResources().getString(R.string.shop3);
		pushName[3] = this.getResources().getString(R.string.shop4);
		pushName[4] = this.getResources().getString(R.string.shop5);
		pushName[5] = this.getResources().getString(R.string.shop6);
		for (int index = 0; index < major.length; index++) {
			push_beacon.add(major[index] + "_" + minor[index]);
		}
		alertOnce = new boolean[major.length];
		//====================================================
		//detectArea = new IBeaconLocatePlan();
		variableReset();
		
		//======================================================
		this.pretime = System.currentTimeMillis();
		
		if(!isTesting) {
		}
		else {			
			this.testHandler.postDelayed(mThreadTest, 1000);
		}
	}

	private void initialLocateList() {
		for(int i = 0; i<bigCityBeaconArray.length; i++) {
			bigCityBeaconArray[i] = "0_"+Integer.toString(i+1);
		}
		
		for(int j = 40; j < 73; j++) {
			sogoBeaconArray[j-40] = "0_"+Integer.toString(j);
		}
	}
	
	
	private void variableReset() {
		mApName = WifiReferencePointVO.aryApList;
		rssiGroup = (ArrayList<Integer>[]) new ArrayList[mApName.size()]; 
		mApScanRssi = new int[mApName.size()];
		
		for (int i = 0; i < WifiReferencePointVO.aryApList.size(); i++) {
			rssiGroup[i] = new ArrayList<Integer>();
		}
		
		mFunctioName = FUNCTION_LOCATION;
		
		//list = new ArrayList<HashMap>();
		scanDataList = new ArrayList<HashMap>();
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(!isTesting) {
			this.unregisterReceiver(mBroadcastReceiver);
		}
		Log.i(TAG, "onDestroy BEGIN");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		
		super.onStart(intent, startId);
		if(!isTesting) {
			IntentFilter mFilter = new IntentFilter("onIBeaconServiceConnect");
			registerReceiver(mBroadcastReceiver, new IntentFilter(
					"onIBeaconServiceConnect"));
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		/*Log.i(TAG, "onStartCommand BEGIN");
		if(!isTesting) {
			IntentFilter mFilter = new IntentFilter("onIBeaconServiceConnect");
			registerReceiver(mBroadcastReceiver, new IntentFilter(
					"onIBeaconServiceConnect"));
		}
		*/
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void checkCurrentFloorAndChangeScanListAndDataBase() {
		if(ApplicationController.getInstance().getFloor().equals("BigCity") && currentFloor!="BigCity") {
			currentFloor="BigCity";
			WifiReferencePointVO.DBName = "/sdcard/bc_bigcity_referencepoint_db.db";
			NaviPlan.loadPList2("bigcity_beacon.plist",ApplicationController.getInstance());
			proxy = new WifiReferencePointProxy(ApplicationController.getInstance());
			variableReset();
		}
		else if(ApplicationController.getInstance().getFloor().equals("SOGO") && currentFloor!="SOGO"){
			currentFloor = "SOGO";
			WifiReferencePointVO.DBName = "/sdcard/sogo_bigcity_referencepoint_db.db";
			NaviPlan.loadPList2("sogo_beacon.plist",ApplicationController.getInstance());//	
			proxy = new WifiReferencePointProxy(ApplicationController.getInstance());
			variableReset();
		}
	}
	
	private String getFloorByMAC(String mac) {
		String floor = "";
		for(int i = 0; i < bigCityBeaconArray.length; i++) {
			if(bigCityBeaconArray[i].equals(mac)) {
				floor = "BigCity";
			}
		}
		
		for(int j = 0; j < sogoBeaconArray.length; j++) {
			if(sogoBeaconArray[j].equals(mac)) {
				floor = "SOGO";	
			}
		}
		
		return floor;
	}
	
	private void checkCurrentMap(ArrayList<IBeacon> ibeacons) {
		int maxRssi = -999;
		String tempFloorName = ApplicationController.getInstance().floor;
		String StrongRssiFloor = "";
		if(ibeacons.size()>1) {
			int size = 0;
			if(ibeacons.size()>2)
				size = 2;
			else 
				size = ibeacons.size();
			boolean isInSameFloor = true;
			IBeacon firstBeacon = ibeacons.get(0);
			String majorAndMinor = firstBeacon.getMajor() + "_"+ firstBeacon.getMinor();
			String floor = this.getFloorByMAC(majorAndMinor);
	
			for(int index = 1;index <size;index++) {
				IBeacon beacon =  ibeacons.get(index);
				majorAndMinor =  beacon.getMajor() + "_"+ beacon.getMinor();
				if(!floor.equals(this.getFloorByMAC(majorAndMinor))) {
					isInSameFloor = false;
				}
			}
			
			if(isInSameFloor) {
				tempFloorName = floor;
			}
			
			if(tempFloorName.equals("")) {
				tempFloorName = floor;
			}
		}
		/*
		for(IBeacon beacon:ibeacons) {
			int rssi = beacon.getRssi();
			String majorAndMinor = beacon.getMajor() + "_"+ beacon.getMinor();
			if(rssi >= maxRssi) {
				
				for(int i = 0; i < bigCityBeaconArray.length; i++) {
					if(bigCityBeaconArray[i].equals(majorAndMinor)) {
						tempFloorName = "BigCity";
						maxRssi = rssi;
						
					}
				}
				
				for(int j = 0; j < sogoBeaconArray.length; j++) {
					if(sogoBeaconArray[j].equals(majorAndMinor)) {
						tempFloorName = "SOGO";
						maxRssi = rssi;
						
					}
				}
			}
		}
		*/
		if(!tempFloorName.equals(ApplicationController.getInstance().floor)) {
			changeTime++;
			if(changeTime>=changeFloorTimeLimit) { 
				ApplicationController.getInstance().floor = tempFloorName;
				changeTime = 0;
			}
		}
		//String floor = ApplicationController.getInstance().floor;
		checkCurrentFloorAndChangeScanListAndDataBase();
		
	}
	
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Log.i(TAG, "mBroadcastReceiver BEGIN");
			//HashMap<String, IBeacon> beacons
			beacons	 = (HashMap<String, IBeacon>) intent.getSerializableExtra("IBEACON_HASHMAP");
			Iterator localIterator = beacons.entrySet().iterator();
			
			ArrayList<IBeacon> ibeacons = new ArrayList<IBeacon>();
			//ibeacons.addAll(beacons.values());
			for(IBeacon ibeacon:beacons.values()) {
				if(ibeacon.getMajor()==0)
				ibeacons.add(ibeacon);
			}
			MathProxy.iBeaconSortingRssiStrongToWeak(ibeacons);
			checkCurrentMap(ibeacons);
			
			mScanCount++;
			
			try {
				while (localIterator.hasNext()) {

					if (!localIterator.hasNext()) {
						return;
					}

					IBeacon localIBeacon = (IBeacon) ((Map.Entry) localIterator
							.next()).getValue();
					String point = localIBeacon.getMajor() + "_"
							+ localIBeacon.getMinor();
					//Log.i(TAG, "localIBeacon :"+localIBeacon.getMajor()+"_"+localIBeacon.getMinor());
					
					
					try {
						int idx = mApName.indexOf(point);
						if (localIBeacon.getRssi() < -40)
							rssiGroup[idx].add(localIBeacon.getRssi());
					
					} catch(Exception e) {
						e.printStackTrace();
					}
					
				}
				
				//checkCurrentFloorAndChangeScanListAndDataBase();
				
				//MathProxy.iBeaconSortingRssiStrongToWeak(ibeacons);

				for (IBeacon ibeacon : ibeacons) {
					String key = ibeacon.getMajor() + "_" + ibeacon.getMinor();

					for (int index = 0; index < push_beacon.size(); index++) {// String
																				// push:push_beacon)
																				// {
						if (push_beacon.get(index).equals(key)
								&& ibeacon.getRssi() > -80) {

							if (!alertOnce[index]) {
								Log.i(TAG, key + ", Rssi: " + ibeacon.getRssi()
										+ ",compare " + push_beacon.get(index));
								long times = (System.currentTimeMillis() - pretime);
								// Log.i(TAG, "times: "+times);
								if (times > 3000) {
									if(index > 6) {
										alertOnce[6] = true;
										alertOnce[7] = true;
										
									} else {
										alertOnce[index] = true;
									}
									
									//pushAlert(index);
									//pushAlert(pushName[index]);
								}
							}
							break;
						}
					}

				}
				//Log.i(TAG, "addIBeconsDetails END");
				
			
				
				
				
			} catch (NullPointerException e) {
				Log.i(TAG, "addIBeconsDetails With Null Data");
			}
			
//========================================================================
			
			if((mFunctioName == FUNCTION_LOCATION)) {
				maxScanCount = 3;
				if(mScanCount >= maxScanCount) {
					ApplicationController.getInstance().mLocationApDetail = "";
					calculateMeanRssi();
					clearBeaconRssiArray();
					//Log.i(TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
					setCurrentLocationRssi(mApScanRssi, scanDataList);
					mScanCount = 0;
					//mActivity.setCurrentFingerPrint(iBeaconRssi);
					if(ApplicationController.isToSailTechMode) {
						updateLocateToServer(ApplicationController.knnPos_X ,ApplicationController.knnPos_Y);
					}
				}
			}			
//========================================================================
			
			
		}

	};

	private void transLocationToServer() {
		
	}
	

	
	private void clearRssiArray() {
		// TODO Auto-generated method stub
		for (int apCount = 0; apCount < mApName.size(); apCount++) {
			rssiGroup[apCount].clear();
		}
	}

	private void finishScanning() {
		// TODO Auto-generated method stub
		Log.i(TAG, "finishScanning BEGIN");
		calculateMeanRssi();
		//calculateBeaconMeanRssi();
		clearBeaconRssiArray();
		//mActivity.setBeaconData(iBeaconRssi);
		//mActivity.siteSurveyMoving();
		
	}

	private void calculateMeanRssi() {
		// TODO Auto-generated method stub
		for (int apCount = 0; apCount < mApName.size(); apCount++) {
			RssiMeanFilter meanFilter = new RssiMeanFilter(rssiGroup[apCount]);
			mApScanRssi[apCount] = meanFilter.getMean();

			//Log.i(TAG, "calculateBeaconMeanRssi " + mApName.get(apCount)
				//	+ " Avg RSSI = " + mApScanRssi[apCount]);
			int rssi;
			if(mApScanRssi[apCount] == 0) {
				rssi = -999;
			} else rssi = mApScanRssi[apCount];
			
			String ap = mApName.get(apCount).replace("_", ":");

			//Log.i(TAG, "calculateBeaconMeanRssi " + mApName.get(apCount)
				//	+ " Avg RSSI = " + mApScanRssi[apCount]);
			
			ApplicationController.getInstance().mLocationApDetail = 
					ApplicationController.getInstance().mLocationApDetail+ap +","+rssi+";";
			
			if(mFunctioName == FUNCTION_LOCATION) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				String apMac = "AP_" + mApName.get(apCount).toString().replace(":", "_");
				map.put("AP_BSSID", apMac);
				//if(mApScanRssi[apCount]<-85) {
					//mApScanRssi[apCount] = -999;
				//}
				map.put("AP_RSSI", mApScanRssi[apCount]);
				//map.put("IBeacon_RSSI", value)
				scanDataList.add(map);
			}
		}
	}
	private void clearBeaconRssiArray() {
			// TODO Auto-generated method stub
		for (int apCount = 0; apCount < mApName.size(); apCount++) {
			rssiGroup[apCount].clear();
		}
	}

	public void pushAlert(int index) {
		Log.i(TAG, "in Alert " + index);
		/*
		String[] pushMsg = {
				getResources().getString(R.string.shop4),
				getResources().getString(R.string.shop5),
				getResources().getString(R.string.shop6)
		};
		
		if(index < 3) {
	
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.notification_title_ad));
			builder.setMessage(getResources().getString(
					R.string.dialog_msg_beacon_service)
					+ pushMsg[index]);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				
					pretime = System.currentTimeMillis();
				}
			});
			AlertDialog alert = builder.create();
			alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

			alert.show();
			
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setContentTitle(getResources().getString(R.string.notification_title_ad))
					.setSmallIcon(drawable.ic_launcher)
					.setContentText(getResources().getString(R.string.notification_msg_ad));

			mBuilder.setTicker("BigCity message");
			mBuilder.setAutoCancel(true);
			mBuilder.setDefaults(Notification.DEFAULT_ALL);
			
			Intent resultIntent = new Intent(this, AdActivity.class);
			resultIntent.putExtra("AdNumber", index);
			PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
					0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, mBuilder.build());
		}
*/
	}

	protected void launchAlert() {
		// TODO Auto-generated method stub
		Log.i(TAG, "launchAlert BEGIN");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("warn");
		builder.setMessage("near the beacon");
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				//alertOnce = false;
			}
		});
		AlertDialog alert = builder.create();
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alert.show();
	}

	public void setCurrentLocationRssi(int rssi[], final ArrayList<HashMap> scanDataList) {
		mApScanRssi = rssi;
		scanList = mApName;
		
		this.scanDataList = scanDataList;
		scanAnalyze();
		
	}
	
	private void scanAnalyze() {
		Log.i(TAG, "setCurrentLocationRssi BEGIN");

		//detectArea.analyzeCurrentArea(scanList,mApScanRssi);
		//currentFloor = detectArea.currentArea;
		String s = "";
		for(int index = 0;index<WifiReferencePointVO.aryApList.size();index++) {
			s+=", "+WifiReferencePointVO.aryApList.get(index);
		}
		//Toast.makeText(ApplicationController.getInstance(), "floor:"+ApplicationController.getInstance().getFloor()+",\ncurrent DB = "+WifiReferencePointVO.DBName+"\n"+s, Toast.LENGTH_SHORT).show();
		
		WifiReferencePointProxy proxy = new WifiReferencePointProxy(ApplicationController.getInstance());
		//proxy.initDB();
		float newPositionX = 0;
		float newPositionY = 0;
		
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		ArrayList<HashMap> bearestApDataList = new ArrayList<HashMap>();
		for(HashMap<String, Object> map:scanDataList) {
			HashMap<String, Object> newMap = new HashMap<String, Object>();
			
			//String apMac = "AP_" + mApName.get(apCount).toString().replace(":", "_");
			newMap.put("AP_BSSID", map.get("AP_BSSID"));
			Integer rssi = (Integer)map.get("AP_RSSI");
			if(rssi==0) {
				rssi = -999;
			}
			newMap.put("AP_RSSI", rssi);
			//map.put("IBeacon_RSSI", value)
			bearestApDataList.add(newMap);
		}
		
		int mRssiDiff = proxy.queryIsNearestApExist(bearestApDataList);
		if(mRssiDiff > 7) {
			String deviceName = proxy.getNearestDevice();
			String[] position = new String[]{"0", "0"};
			position = ApplicationController.getInstance().getDevicePosition(deviceName);
			newPositionX = Float.parseFloat(position[0]);
			newPositionY = Float.parseFloat(position[1]);
			ApplicationController.isNeedToLocateToAPPoint = true;
			ApplicationController.nearAPX = newPositionX;
			ApplicationController.nearAPY = newPositionY;
			//Toast.makeText(ApplicationController.getInstance(), deviceName , 300).show();
			NaviNode newPosition = new NaviNode("",(int)newPositionX,(int)newPositionY);//this.newPositionX,this.newPositionY);
			ApplicationController.knnPos_X = newPositionX;
			ApplicationController.knnPos_Y = newPositionY;
			ApplicationController.knnPos_XAndYBelongFloor = ApplicationController.getInstance().getFloor();
			Log.i(TAG, "new X = "+ApplicationController.knnPos_X+", new Y = "+ApplicationController.knnPos_Y);
		} 
		else {
			list = proxy.queryReferencePointDis(scanDataList);
			knnSortingDistanceStrongToWeak(list);
			Log.i(TAG, "list size = "+list.size());
			Log.i(TAG, WifiReferencePointVO.DBName);
			if(list.size()>1) {
				boolean isSecond = false,isThird =false;
				newPositionX = Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_X));
				newPositionY = Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_Y));
				
				float secondPositionX = Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_X));
				float secondPositionY = Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_Y));
				
				float thirdPositionX = Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_X));
				float thirdPositionY = Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_Y));
				
				float dis1And2 = MathProxy.getDistance(newPositionX, newPositionY, secondPositionX, secondPositionY);
				float dis1And3 = MathProxy.getDistance(newPositionX, newPositionY, thirdPositionX, thirdPositionY);
				
				if(dis1And2<(20*5)) {
					newPositionX += secondPositionX;
					newPositionY += secondPositionY;
					isSecond = true;
				}
				if(dis1And3<(20*5)) {
					newPositionX += thirdPositionX;
					newPositionY += thirdPositionY;
					isThird = true;
				}
				if(isSecond && isThird) {
					newPositionX /= 3;
					newPositionY /= 3;
				}
				else if(isSecond || isThird) {
					newPositionX /= 2;
					newPositionY /= 2;
				}
					/*
				newPositionX = (
						Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_X))); +
						Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_X)) + 
						Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_X)) 
						) / 3;*/
					/*
				newPositionY = (
						Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_Y))); + 
						Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_Y)) + 
						Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_Y))
						) / 3;*/
			}
		
		
		//float[] newPositionXY = new float[]{newPositionX, newPositionY};
			NaviNode newPosition = new NaviNode("",(int)newPositionX,(int)newPositionY);//this.newPositionX,this.newPositionY);
			ApplicationController.knnPos_X = newPositionX;
			ApplicationController.knnPos_Y = newPositionY;
			ApplicationController.knnPos_XAndYBelongFloor = ApplicationController.getInstance().getFloor();
			Log.i(TAG, "new X = "+ApplicationController.knnPos_X+", new Y = "+ApplicationController.knnPos_Y);
			//Toast.makeText(ApplicationController.getInstance(), "new X = "+ApplicationController.knnPos_X+", new Y = "+ApplicationController.knnPos_Y, Toast.LENGTH_SHORT).show();
		}
		scanDataList = new ArrayList<HashMap>();
		
		
	}
	
	public static void knnSortingDistanceStrongToWeak(ArrayList<HashMap> list) {
	HashMap temp;
	 if (list.size()>1) // check if the number of orders is larger than 1
        {
            for (int index=0; index<list.size(); index++) // bubble sort outer loop
            {
                for (int i=0; i < list.size()-index-1; i++) 
                {
                    if (((Integer)list.get(i).get(WifiReferencePointVO.DISTANCE))>((Integer)list.get(i+1).get(WifiReferencePointVO.DISTANCE)) )
                    {
                        temp = list.get(i);
                        list.set(i,list.get(i+1) );
                        list.set(i+1, temp);
                    }
                }
            }
        }
	
	}
	
	private void updateLocateToServer(float x,float y) {
		//if(changeMapDone) {
			String android_id = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
			//Toast.makeText(this, android_id, Toast.LENGTH_SHORT).show();
			String floor = ApplicationController.getInstance().floor;//plan.currentMapName;
			//RtlsApiProxy proxy = new RtlsApiProxy(this);
			String location = ApplicationController.getInstance().mLocationApDetail;
			JsonObjectPostRequest jsonObjectRequest = updNavigation(android_id, 
					Float.toString(x), Float.toString(y), floor, location);
			jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
					DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			Log.i(TAG, "ID = "+android_id);
			ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
			this.pushMessage(x, y);
		//}
	}

	private boolean checkPushMessageDeviceSameWithLocateMap(String[] deviceName){
		if(deviceName.length<2){
			return false;
		}
		String Major =  deviceName[0];
		String Minor = deviceName[1];
		
		if(Major.equals("0")) {
			int minor = Integer.parseInt(Minor);
			
			if(minor<=39 && ApplicationController.getInstance().floor.equals("BigCity")) {
				return true;
			}else
			if(minor>=40 && minor<=72 && ApplicationController.getInstance().floor.equals("SOGO")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void pushMessage(float x, float y) {
		// TODO Auto-generated method stub
		String[] msgSTRAndURL = this.msgListProxy.queryMsg(Float.toString(x), Float.toString(y));
		String mPushMsg = msgSTRAndURL[0];
		String url = msgSTRAndURL[1];  
		String[] deviceName = msgSTRAndURL[2].split(":");
		boolean isSameMap = checkPushMessageDeviceSameWithLocateMap(deviceName);
		if (!mPushMsg.equals("")&&!url.equals("") && isSameMap) {
			
			boolean inCache = isBeaconInCache(mPushMsg);
			String mFlag = "flag";//apMsg[1];
			String mTimeLimit = "10";//apMsg[2];
			String mFileExt = "FileExt";//apMsg[3];
			String mFileName = "FileName";//apMsg[4];
			//String mFileUrl = apMsg[5].replace(";", "");
			//String url[] = apMsg[5].split(";");
			String mFileUrl = url;//[0];

			final PushMsgVO vo = new PushMsgVO();
			vo.pushMsg = mPushMsg;
			vo.flag = mFlag;
			vo.timeLimit = mTimeLimit;
			vo.fileExt = mFileExt;
			vo.fileName = mFileName;
			vo.url = mFileUrl;

			Log.i(TAG, "updNavigation res mPushMsg ="
					+ mPushMsg + " mFlag =" + mFlag
					+ " mTimeLimit = " + mTimeLimit
					+ " mFileExt = " + mFileExt + " mFileName="
					+ mFileName + " mFileUrl = " + mFileUrl);

			if(!isMsgDialogOpen) {
				
				Builder MyAlertDialog = new AlertDialog.Builder(sailsTechActivity);
				MyAlertDialog.setTitle(getResources().getString(
						R.string.dialog_title_get_push_msg));
				if(!mFileName.equals("") && !mFileUrl.equals("")) {
					isNeedToOpenFile = true;
					MyAlertDialog.setMessage(vo.pushMsg+ "\n"
							+ getResources().getString(R.string.dialog_msg_get_file));
				} else if(mFileName.equals("") || mFileUrl.equals("")){
					MyAlertDialog.setMessage(vo.pushMsg);
					isNeedToOpenFile = false;
				}
				
				MyAlertDialog.setPositiveButton(getResources()
						.getString(R.string.dialog_btn_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int which) {
								isMsgDialogOpen = false;
								if(isNeedToOpenFile) {
									isNeedToOpenFile = false;
									goToPushMsgFileActivity(vo);
								}
							}
						});
				if(isNeedToOpenFile) {
					MyAlertDialog.setNegativeButton((getResources()
						.getString(R.string.dialog_btn_cancel)),
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int which) {
								isMsgDialogOpen = false;
								if(isNeedToOpenFile) {
									isNeedToOpenFile = false;
							
								}													
							}
						});
				}

				MyAlertDialog.setCancelable(false);
				
				if(!inCache) {
					Log.i(TAG, "Add msg to cache");
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date currentDt = new Date();
					String dts=sdf.format(currentDt);

					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("msg", mPushMsg);
					map.put("push_time", dts);
					aryBeaconMsg.add(map);
					isMsgDialogOpen = true;
					
					MyAlertDialog.show();
					
				} 
			}
		}
	}
/*
	private void gotMessage(String message) {
		Toast.makeText(this, message, 300).show();
		Builder MyAlertDialog = new AlertDialog.Builder(ApplicationController.getInstance());
		MyAlertDialog.setTitle(getResources().getString(
				R.string.dialog_title_get_push_msg));
		
		MyAlertDialog.setPositiveButton(getResources()
				.getString(R.string.dialog_btn_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int which) {
					}
				});
		if(isNeedToOpenFile) {
			MyAlertDialog.setNegativeButton((getResources()
				.getString(R.string.dialog_btn_cancel)),
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int which) {
						
					}
				});
		}

		MyAlertDialog.setCancelable(false);
		MyAlertDialog.show();
	}
	*/
	public void parseNavigationMsg(JSONObject response, boolean isDone) {
		/*Log.i(TAG, "parseNavigationMsg BEGIN");
		AES aes = new AES();
		
		if (isDone) {
			try {
				JSONArray obj = new JSONArray(response.getString("data"));
				if (obj != null) {
					for (int i = 0; i < obj.length(); i++) {
						JSONObject dataContent = obj.getJSONObject(i);

						if (!dataContent.getString("ap_message").equals("")) {
							Log.i(TAG,
									"updNavigation apMsg = "
											+ dataContent.getString("ap_message"));
							
							String[] apMsg = dataContent
									.getString("ap_message").split(",");
							String mPushMsg = aes
									.decrypt_AES_ParsingSomeChar(apMsg[0]);
							boolean inCache = isBeaconInCache(mPushMsg);
							String mFlag = apMsg[1];
							String mTimeLimit = apMsg[2];
							String mFileExt = apMsg[3];
							String mFileName = apMsg[4];
							//String mFileUrl = apMsg[5].replace(";", "");
							String url[] = apMsg[5].split(";");
							String mFileUrl = url[0];

							final PushMsgVO vo = new PushMsgVO();
							vo.pushMsg = mPushMsg;
							vo.flag = mFlag;
							vo.timeLimit = mTimeLimit;
							vo.fileExt = mFileExt;
							vo.fileName = mFileName;
							vo.url = mFileUrl;
							//gotMessage(vo.pushMsg);
							Log.i(TAG, "updNavigation res mPushMsg ="
									+ mPushMsg + " mFlag =" + mFlag
									+ " mTimeLimit = " + mTimeLimit
									+ " mFileExt = " + mFileExt + " mFileName="
									+ mFileName + " mFileUrl = " + mFileUrl);

							if(!isMsgDialogOpen) {
								isMsgDialogOpen = true;
								
								Builder MyAlertDialog = new AlertDialog.Builder(sailsTechActivity);
								MyAlertDialog.setTitle(getResources().getString(
										R.string.dialog_title_get_push_msg));
								if(!mFileName.equals("") && !mFileUrl.equals("")) {
									isNeedToOpenFile = true;
									MyAlertDialog.setMessage(vo.pushMsg+ "\n"
											+ getResources().getString(R.string.dialog_msg_get_file));
								} else if(mFileName.equals("") || mFileUrl.equals("")){
									MyAlertDialog.setMessage(vo.pushMsg);
									isNeedToOpenFile = false;
								}
								
								MyAlertDialog.setPositiveButton(getResources()
										.getString(R.string.dialog_btn_confirm),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												isMsgDialogOpen = false;
												if(isNeedToOpenFile) {
													isNeedToOpenFile = false;
													goToPushMsgFileActivity(vo);
												}
											}
										});
								if(isNeedToOpenFile) {
									MyAlertDialog.setNegativeButton((getResources()
										.getString(R.string.dialog_btn_cancel)),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												isMsgDialogOpen = false;
												if(isNeedToOpenFile) {
													isNeedToOpenFile = false;
											
												}													
											}
										});
								}

								MyAlertDialog.setCancelable(false);
								
								if(!inCache) {
									Log.i(TAG, "Add msg to cache");
									
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
									Date currentDt = new Date();
									String dts=sdf.format(currentDt);

									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("msg", mPushMsg);
									map.put("push_time", dts);
									aryBeaconMsg.add(map);
									
									MyAlertDialog.show();
									
									//RtlsApiProxy proxy = new RtlsApiProxy(this);
									JsonObjectPostRequest jsonObjectRequest =
											updateStatus(dataContent.getString("ap_message"));
									ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
									
								} 
								
							}
							
						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
	
	
	private boolean isBeaconInCache(String mPushMsg) {
		// TODO Auto-generated method stub
		boolean inCache = false;
		for(int i = 0; i < aryBeaconMsg.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map = aryBeaconMsg.get(i);
			if(mPushMsg.equals((String)map.get("msg"))) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				
				String cacheTime = (String) map.get("push_time");
				Date cacheDt;
				Date currentDt = new Date();
				try {
					cacheDt = sdf.parse(cacheTime);
					long ut1 = cacheDt.getTime();
					long ut2 = currentDt.getTime();
					long min = (ut2-ut1)/1000;
					if( min >= 600) {
						Log.i(TAG, "BeaconInCache over 10min");
						aryBeaconMsg.remove(i);
						inCache = false;
						break;
					} else {
						inCache = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}
		}
		
		return inCache;
	}
	
	private void goToPushMsgFileActivity(PushMsgVO vo) {
		Log.i(TAG, "goToPushMsgFileActivity BEGIN");

		if(vo.url.contains(".mp4")) {
            Intent intent = new Intent(Intent.ACTION_VIEW); 
            intent.setDataAndType(Uri.parse(vo.url), "video/*");
            sailsTechActivity.startActivity(intent);  
		}
		else {
		Intent intent = new Intent();
		intent.putExtra("ap_msg", vo);
		intent.setClass(sailsTechActivity, PushMsgFileActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
		isMsgDialogOpen = false;
		}
	}
	
	public JsonObjectPostRequest updNavigation(String androidID, String x,
			String y, String floor, String location) {
		Log.i(TAG, "updNavigation BEGIN");
		//String apiURL = "http://218.211.88.196/api/updNavigation.php";
		String apiURL = "http://bigcity-rtls.doubleservice.com/api/updNavigation.php";
				
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "updNavigation");
		mMap.put("UUID", androidID);
		mMap.put("Floor", floor);
		mMap.put("pointX", x);//
		mMap.put("pointY", y);
		mMap.put("nickName", "");
		mMap.put("location", location);
		Log.i("JsonObjectPostRequest", "send data = " + mMap.toString());

		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(
				apiURL, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onResponse response ="
								+ response.toString());
						parseNavigationMsg(response, true);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onErrorResponse error = "
								+ error.getMessage());
						parseNavigationMsg(null, true);
					}
				}, mMap);

		return jsonObjectRequest;
	}

	public JsonObjectPostRequest updateStatus(String apMsg) {
		// TODO Auto-generated method stub
		Log.i(TAG, "updateStatus BEGIN");

		//String apiURL = "http://218.211.88.196/api/updMessageLog.php";
		String apiURL = "http://bigcity-rtls.doubleservice.com/api/updMessageLog.php";
				
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "updMessageLog");
		mMap.put("UUID", ApplicationController.getInstance().getUuid());
		mMap.put("ap_message", apMsg);
		Log.i("JsonObjectPostRequest", "send data = " + mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(
				apiURL, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onResponse response ="
								+ response.toString());
						
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onErrorResponse error = "
								+ error.getMessage());
						
					}
				}, mMap);

		return jsonObjectRequest;
	}
	public Runnable mThreadTest = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			testHandler.sendEmptyMessage(0);
		}
		
	};

	private int testindex = 0;
	private int testChangeMapIndex = 0;

	public Handler testHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			mScanCount++;
			List<TestIBeaconScanResult> results ;
			if(testData==null)
				testData = new TestWifiService();
			//queryTest();
			/*
			testData = new TestWifiService(testindex);
			if(testChangeMapIndex<5) {
				testChangeMapIndex++;
			}
			else {
				testChangeMapIndex = 0;
				if(testindex ==0) {
					testindex = 2;
				}
				else if(testindex==1) {
					testindex = 0;
				}
				else if(testindex==2) {
					testindex = 1;
				}
			}
			results  = testData.list;
			*/
			if(testindex>=testData.dbDateList.size()) {
					testindex = 0;
			}
			ApplicationController.testPos_X = testData.dbDateList.get(testindex).positionX;
			ApplicationController.testPos_Y = testData.dbDateList.get(testindex).positionY;
			results = testData.dbDateList.get(testindex).results;
			testindex++;
			
			
			test_checkCurrentMap(results);
			
			for (TestIBeaconScanResult result : results) {
				String point = result.Major+"_"+result.Minor;//.BSSID.toString();
				Log.i(TAG, "MAC of the AP= " +point);
				
				if (result.rssi < -20) {
					try {
						int idx = mApName.indexOf(point);
						//onReceiveData[idx] = result.level;
						rssiGroup[idx].add(result.rssi);
						Log.i(TAG, "MAC of the AP =" +point);
						
					} catch (Exception e) {
					}
				}
			}
			
			if(mFunctioName == FUNCTION_LOCATION) {
				maxScanCount = 1;
				if(mScanCount >= maxScanCount) {
					calculateMeanRssi();
					clearBeaconRssiArray();
					setCurrentLocationRssi(mApScanRssi, scanDataList);
					mScanCount =0;
					if(ApplicationController.isToSailTechMode) {
						//Toast.makeText(ApplicationController.getInstance(), "update!!", 300).show();
						
						updateLocateToServer(ApplicationController.knnPos_X ,ApplicationController.knnPos_Y);
						
					}
				}
				this.postDelayed(mThreadTest, 1000);
			}
		}
	};
	
	private void test_checkCurrentMap(List<TestIBeaconScanResult> wifiAPs) {
		int maxRssi = -999;
		String tempFloorName = ApplicationController.getInstance().getFloor();
		for(TestIBeaconScanResult result:wifiAPs) {
			int rssi = result.rssi;
			String bssid = result.Major+"_"+result.Minor;//.BSSID.toString();
			
			if(rssi >= maxRssi) {
				
				for(int i = 0; i <  bigCityBeaconArray.length; i++) {
					if( bigCityBeaconArray[i].equals(bssid)) {
						tempFloorName = "BigCity";
						maxRssi = rssi;
				
					}
				}
				
				for(int j = 0; j < sogoBeaconArray.length; j++) {
					if(sogoBeaconArray[j].equals(bssid)) {
						tempFloorName = "SOGO";
						maxRssi = rssi;
						
					}
				}
			}
		}
		
		if(!tempFloorName.equals(ApplicationController.getInstance().getFloor())) {//ApplicationController.getInstance().floor) && !tempFloorName.equals("")) {
			changeTime++;
			if(changeTime>=changeFloorTimeLimit) { 
				if(tempFloorName.equals("")) {
					ApplicationController.getInstance().setFloor(tempFloorName);
				}
				ApplicationController.getInstance().setFloor(tempFloorName);
				//ApplicationController.getInstance().floor = tempFloorName;
				changeTime = 0;
			}
		}
		
		//String floor = ApplicationController.getInstance().floor;
		checkCurrentFloorAndChangeScanListAndDataBase();
		
	}
	
}
