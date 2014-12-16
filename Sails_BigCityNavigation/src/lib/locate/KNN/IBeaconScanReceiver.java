package lib.locate.KNN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lib.locate.KNN.RssiMeanFilter;
import lib.locate.KNN.WifiReferencePointProxy;
import lib.locate.KNN.WifiReferencePointVO;
import map.navi.component.NavigationIndoorPosition;

import com.doubleservice.bigcitynavigation.ApplicationController;
import com.radiusnetworks.ibeacon.IBeacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IBeaconScanReceiver extends BroadcastReceiver{

	private String TAG = "IBeaconScanReceiver";
	private ArrayList<HashMap> scanDataList;
	private ArrayList<Integer>[] rssiGroup;  //= (ArrayList<Integer>[]) new ArrayList[4];
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
	private ArrayList<HashMap> list;
	
	 NavigationIndoorPosition indoorPosition;//position_1F mActivity;
	
	public IBeaconScanReceiver(NavigationIndoorPosition act, int functionName) {
		this.indoorPosition = act;
		
		mApName = WifiReferencePointVO.aryApList;
		rssiGroup = (ArrayList<Integer>[]) new ArrayList[mApName.size()]; 
		mApScanRssi = new int[mApName.size()];
		
		for (int i = 0; i < WifiReferencePointVO.aryApList.size(); i++) {
			rssiGroup[i] = new ArrayList<Integer>();
		}
		
		mFunctioName = functionName;
		
		list = new ArrayList<HashMap>();
		scanDataList = new ArrayList<HashMap>();
		//preTime = System.currentTimeMillis();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "time = "+(System.currentTimeMillis()-preTime));
		
		mScanCount++;
		Log.i(TAG, "onReceive BEGIN");
		Log.i(TAG, "Beacon Recieveer recieve");
		
		
				HashMap<String, IBeacon> beacons = (HashMap<String, IBeacon>) intent
						.getSerializableExtra("IBEACON_HASHMAP");
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

				
				Log.i("addIBeconsDetails",
						"UUID = " + localIBeacon.getProximityUuid()
								+ "  Major = "
								+ Integer.toString(localIBeacon.getMajor())
								+ "  Minor = "
								+ Integer.toString(localIBeacon.getMinor())
								+ "  point  = " + point + "  RSSI = "
								+ Integer.toString(localIBeacon.getRssi()));

	
					try {
					
						int idx = mApName.indexOf(point);
						if (localIBeacon.getRssi() < -40)
							rssiGroup[idx].add(localIBeacon.getRssi());
					
					} catch(Exception e) {
						e.printStackTrace();
					}
				}

				Log.i(TAG, "addIBeconsDetails END");
			} catch (NullPointerException e) {
				Log.i(TAG, "addIBeconsDetails With Null Data");
			}
		
			if(mFunctioName == FUNCTION_SITESURVEY) {
				maxScanCount = 5;
				if(mScanCount >= maxScanCount) {
					bIsPointScanningDone = true;
					calculateMeanRssi();
					clearRssiArray();
				//mActivity.setSiteSurveyRssiData(mApScanRssi, bIsPointScanningDone);
				//finishScanning();
				} else {
					bIsPointScanningDone = false;
				}
			
			} else if(mFunctioName == FUNCTION_LOCATION) {
				maxScanCount = 3;
				if(mScanCount >= maxScanCount) {
					calculateMeanRssi();
					clearBeaconRssiArray();
					//Log.i(TAG, "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
					//indoorPosition.setCurrentLocationRssi(mApScanRssi, scanDataList);
					mScanCount =0;
					//mActivity.setCurrentFingerPrint(iBeaconRssi);
				}
			}
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

			if(mApScanRssi[apCount] == 0) {
				mApScanRssi[apCount] = -999;
			}
			String ap = mApName.get(apCount).replace("_", ":");

			Log.i(TAG, "calculateBeaconMeanRssi " + mApName.get(apCount)
					+ " Avg RSSI = " + mApScanRssi[apCount]);
			
			ApplicationController.getInstance().mLocationApDetail = ApplicationController.getInstance().mLocationApDetail  + 
					ap +","+mApScanRssi[apCount]+";";
			Log.i(TAG, "mLocationApDetail = " +ApplicationController.getInstance().mLocationApDetail );
			
			if(mFunctioName == FUNCTION_LOCATION) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				String apMac = "AP_" + mApName.get(apCount).toString().replace(":", "_");
				map.put("AP_BSSID", apMac);
				map.put("AP_RSSI", mApScanRssi[apCount]);
				//map.put("IBeacon_RSSI", value)
				scanDataList.add(map);
			}
		}
		
		
		/*
		for (int apCount = 0; apCount < myBeaconName.size(); apCount++) {

			RssiMeanFilter meanFilter = new RssiMeanFilter(rssiGroup[apCount]);
			iBeaconRssi[apCount] = meanFilter.getMean();

			Log.i(TAG, "calculateBeaconMeanRssi " + myBeaconName.get(apCount)
					+ " Avg RSSI = " + iBeaconRssi[apCount]);
			
			if(mFunctioName == FUNCTION_LOCATION) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("IBeacon_Major_Minor", myBeaconName.get(apCount));
				//map.put("IBeacon_RSSI", value)
			}
			
		}
		*/
	}
	
	private void clearBeaconRssiArray() {
		// TODO Auto-generated method stub
		for (int apCount = 0; apCount < mApName.size(); apCount++) {
			rssiGroup[apCount].clear();
		}
	}

}
