package com.doubleservice.bigcitynavigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lib.locate.KNN.RssiMeanFilter;
import lib.locate.KNN.WifiReferencePointProxy;
import lib.locate.KNN.WifiReferencePointVO;
import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.IBeaconLocatePlan;
import map.navi.Data.NaviNode;
import map.navi.Data.NaviPlan;

import com.doubleservice.bigcitynavigation.R.drawable;
import com.radiusnetworks.ibeacon.IBeacon;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class WiFiService extends Service {

	private String TAG = "WiFIService";
	private ArrayList<String> push_beacon = new ArrayList<String>();
	
	//****//KNN Receiver variable=====================================================================
	//public String currentFloor="";
	private ArrayList<HashMap> scanDataList;
	private ArrayList<Integer>[] rssiGroup;  
	public ArrayList<String> mApName = new ArrayList<String>();
	public int[] mApScanRssi;
	//private ArrayList<String> myBeaconName = new ArrayList<String>();
	//private String[] myBeaconName = new String[] { "0_1", "1_0", "2_0", "1_1" };
	private int maxScanCount = 10;
	private int mScanCount = 0;
	private static int FUNCTION_SITESURVEY = 0;
	private static int FUNCTION_LOCATION = 1;
	private int mFunctioName;
	private ArrayList<HashMap> list;	
	private String[] bigCityWiFiArray = {"54:d0:ed:a1:5a:70","54:d0:ed:a1:5c:10","54:d0:ed:a1:57:00","54:d0:ed:a1:5c:50","54:d0:ed:a1:5c:78","54:d0:ed:a1:54:b8",
			"54:d0:ed:a1:58:b8","54:d0:ed:a1:55:d8","54:d0:ed:a1:54:a0","54:d0:ed:a1:54:08","54:d0:ed:a1:56:58","54:d0:ed:a1:5d:08",
			"54:d0:ed:a1:5d:40","54:d0:ed:a1:5d:58","54:d0:ed:a1:53:68","54:d0:ed:a1:5b:30"};//new String[16]; 
	private String[] sogoWiFiArray ={"54:d0:ed:a1:5a:90","54:d0:ed:a1:56:70","54:d0:ed:a1:57:10","54:d0:ed:a1:56:30","54:d0:ed:a1:55:78",
			"54:d0:ed:a1:56:c0","54:d0:ed:a1:4f:d8","54:d0:ed:a1:5d:18","54:d0:ed:a1:50:10","54:d0:ed:a1:53:88"};// new String[10];
	
	//public IBeaconLocatePlan detectArea ;
	//public int[] mApScanRssi;
	public  ArrayList<String> scanList;
	
	//****//==================================================================================
	WifiReferencePointProxy proxy;
	private String currentFloor ="";
	public WifiManager wiFiManager ;
	private int changeFloorTimeLimit = 1;
	private int changeTime = 0;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "onCreate BEGIN");
		 initialLocateList();
		//proxy = new WifiReferencePointProxy(ApplicationController.getInstance());
		//proxy.initDB();
		variableReset();
		wiFiManager = (WifiManager) ApplicationController.getInstance().getSystemService(ApplicationController.getInstance().WIFI_SERVICE);
		this.registerReceiver(mBroadcastReceiver, new IntentFilter( new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)));
				//"onIBeaconServiceConnect"));
		wiFiManager.startScan();
	}

	private void initialLocateList() {
			
			
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
		this.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
		//if (knnWifiReceiver != null)
		
		Log.i(TAG, "onDestroy BEGIN");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand BEGIN");
		
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
	
	private void checkCurrentMap(List<ScanResult> wifiAPs) {
		int maxRssi = -999;
		String tempFloorName ="";
		for(ScanResult result:wifiAPs) {
			int rssi = result.level;
			String bssid = result.BSSID.toString();
			if(rssi >= maxRssi) {
				
				for(int i = 0; i < bigCityWiFiArray.length; i++) {
					if(bigCityWiFiArray[i].equals(bssid)) {
						tempFloorName = "BigCity";
						maxRssi = rssi;
						/*
						if(!ApplicationController.getInstance().floor.equals("BigCity")) { 
							maxRssi = rssi;
							ApplicationController.getInstance().floor = "BigCity";
						}
						*/
					}
				}
				
				for(int j = 0; j < sogoWiFiArray.length; j++) {
					if(sogoWiFiArray[j].equals(bssid)) {
						tempFloorName = "SOGO";
						maxRssi = rssi;
						/*
						if(!ApplicationController.getInstance().floor.equals("SOGO")) {
							maxRssi = rssi;
							ApplicationController.getInstance().floor = "SOGO";
						}
						*/
					}
				}
			}
		}
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
			mScanCount++;
			List<ScanResult> results = wiFiManager.getScanResults();
			
			checkCurrentMap(results);
			
			for (ScanResult result : results) {
				String point = result.BSSID.toString();
				Log.i(TAG, "MAC of the AP= " +point);
				
				if (result.level < -20) {
					try {
						int idx = mApName.indexOf(point);
						//onReceiveData[idx] = result.level;
						rssiGroup[idx].add(result.level);
						Log.i(TAG, "MAC of the AP =" +point);
						
					} catch (Exception e) {
					}
				}
			}
			
			if(mFunctioName == FUNCTION_LOCATION) {
				maxScanCount = 3;
				if(mScanCount >= maxScanCount) {
					calculateMeanRssi();
					clearBeaconRssiArray();
					//setCurrentLocationRssi(mApScanRssi, scanDataList);
					mScanCount =0;
					
				}
				wiFiManager.startScan();
			} 					
			//checkCurrentFloorAndChangeScanListAndDataBase();				
		}

	};


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
			
			if(mFunctioName == FUNCTION_LOCATION) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				String apMac = "AP_" + mApName.get(apCount).toString().replace(":", "_");
				map.put("AP_BSSID", apMac);
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

	public void setCurrentLocationRssi(int rssi[], final ArrayList<HashMap> scanDataList) {
		mApScanRssi = rssi;
		scanList = mApName;
		
		this.scanDataList = scanDataList;
		scanAnalyze();
		
	}
	
	private void scanAnalyze() {
		Log.i(TAG, "setCurrentLocationRssi BEGIN");
		String s = "";
		for(int index = 0;index<WifiReferencePointVO.aryApList.size();index++) {
			s+="\n"+WifiReferencePointVO.aryApList.get(index);
		}
		//Toast.makeText(ApplicationController.getInstance(), "floor:"+ApplicationController.getInstance().getFloor()+",\ncurrent DB = "+WifiReferencePointVO.DBName, Toast.LENGTH_SHORT).show();
		
		WifiReferencePointProxy proxy = new WifiReferencePointProxy(ApplicationController.getInstance());
		//proxy.initDB();
		
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		list = proxy.queryReferencePointDis(scanDataList);
		knnSortingDistanceStrongToWeak(list);
		Log.i(TAG, "list size = "+list.size());
		Log.i(TAG, WifiReferencePointVO.DBName);
		if(list.size()>1) {
			float newPositionX = (
					Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_X)) +
					Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_X)) + 
					Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_X)) 
					) / 3;
			float newPositionY = (
					Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_Y)) + 
					Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_Y)) + 
					Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_Y))
					) / 3;
		
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
	
}
