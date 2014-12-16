package lib.locate.KNN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import lib.locate.KNN.RssiMeanFilter;
import lib.locate.KNN.WifiReferencePointProxy;
import lib.locate.KNN.WifiReferencePointVO;
import map.navi.component.NavigationIndoorPosition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.util.Log;

public class WifiFingerPrintReceiver extends BroadcastReceiver {

	private NavigationIndoorPosition indoorPosition;
	
	private int mFunctioName;
	private String TAG = "WifiFingerPrintReceiver";
	
	private final int FUNCTION_SITESURVEY = 0;
	private final int FUNCTION_LOCATION	= 1;
	private final int FUNCTION_TESTING = 2;

	private ArrayList<Integer>[] rssiGroup;
	private ArrayList<String> mApName = new ArrayList<String>();
	private int[] mApScanRssi;
	private double[] mApScanSD;
	
	private int currentScanCount = 0;
	private int maxScanCount = 0;
	private int testingModeScanCount = 0;
	
	private float maxDistance3M = 120;
	private float maxDistance2M = 80;
	private int maxTestCount = 100;
	
	private ArrayList<HashMap> scanDataList;
	//private ArrayList<HashMap> onReceiveData;
	private int[] onReceiveData;
	private ArrayList<float[]> aryPointXY = new ArrayList<float[]>();
	
	private int outOfDistanceCount2M = 0; 
	private int outOfDistanceCount3M = 0; 
	
	private boolean bIsPointScanningDone = false;

	public WifiFingerPrintReceiver(NavigationIndoorPosition activity, int functionName) {
		this.indoorPosition = activity;
		this.mFunctioName = functionName;

		mApName = WifiReferencePointVO.aryApList;
		rssiGroup = (ArrayList<Integer>[]) new ArrayList[mApName.size()];
		mApScanRssi = new int[mApName.size()];
		mApScanSD = new double[mApName.size()];

		for (int i = 0; i < WifiReferencePointVO.aryApList.size(); i++) {
			rssiGroup[i] = new ArrayList<Integer>();
		}
		
		mFunctioName = functionName;
		scanDataList = new ArrayList<HashMap>();
		//onReceiveData = new ArrayList<HashMap>();
		onReceiveData = new int[WifiReferencePointVO.aryApList.size()];
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onReceive BEGIN");
		currentScanCount++;

		List<ScanResult> results = indoorPosition.wiFiManager.getScanResults();
		for (ScanResult result : results) {
			//HashMap<String, Integer> map = new HashMap<String, Integer>();
			
			String point = result.BSSID.toString();
			//Log.i(TAG, "MAC of the AP= " +point);
			
			if (result.level < -20) {
				try {
					int idx = mApName.indexOf(point);
					onReceiveData[idx] = result.level;
					rssiGroup[idx].add(result.level);
					Log.i(TAG, "MAC of the AP =" +point);
					//map.put(point, result.level);
					//onReceiveData.add(map);
				} catch (Exception e) {
				}
			}
		}
		
		if(mFunctioName == FUNCTION_SITESURVEY) {
			maxScanCount = 5;
			if(currentScanCount >= maxScanCount){
				bIsPointScanningDone = true;
				calculateMeanRssi();
				//calculateStandarDeviation();
				clearRssiArray();
				//indoorPosition.setSiteSurveyRssiData(mApScanRssi, bIsPointScanningDone);
				//act.setSiteSurveyRssiData(mApScanRssi, mApScanSD);
			} else {
				bIsPointScanningDone = false;
				//indoorPosition.setSiteSurveyRssiData(onReceiveData, bIsPointScanningDone);
				indoorPosition.wiFiManager.startScan();
			}
			
		} else if(mFunctioName == FUNCTION_LOCATION) {
			maxScanCount = 2;
			if(currentScanCount >= maxScanCount){
				calculateMeanRssi();
				clearRssiArray();
				//queryNearestDistance(mApScanRssi);
				//indoorPosition.setCurrentLocationRssi(mApScanRssi, scanDataList);
			} else {
				indoorPosition.wiFiManager.startScan();
			}
		} else if(mFunctioName == FUNCTION_TESTING) {
			maxScanCount = 2;
	
			if(currentScanCount >= maxScanCount) {
				calculateMeanRssi();
				clearRssiArray();
				//setTestingData(mApScanRssi);
			} else {
				indoorPosition.wiFiManager.startScan();
			}
		}
	}
/*
	private void setTestingData(int rssi[]) {
		// TODO Auto-generated method stub
		Log.i(TAG, "setTestingData BEGIN");
		testingModeScanCount++;
		currentScanCount = 0;
		
		TestingModeProxy tmProxy = new TestingModeProxy(act);
		tmProxy.initDB();
		
		WifiReferencePointProxy proxy = new WifiReferencePointProxy(act);
		proxy.initDB();
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		list = proxy.queryReferencePointDis(scanDataList);
		
		Collections.sort(list, new Comparator<HashMap>() {
			@Override
			public int compare(HashMap lhs, HashMap rhs) {
				// TODO Auto-generated method stub
				
				return (Integer) rhs.get(WifiReferencePointVO.DISTANCE) == (Integer) lhs
						.get(WifiReferencePointVO.DISTANCE) ? 0 :
							((Integer) rhs.get(WifiReferencePointVO.DISTANCE) < (Integer) lhs
									.get(WifiReferencePointVO.DISTANCE) ? 1 : -1);
			}
		});
		
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
		
		float dis = (float) Math.sqrt((Math.pow((newPositionX-WifiReferencePointVO.lastPosX), 2) +
						(Math.pow((newPositionY-WifiReferencePointVO.lastPosY), 2)) ));
		
		Log.i(TAG, "Distance between last and current point = " +dis);
		
		TestingModeVO tmVO = new TestingModeVO();
		tmVO.mPosX = Float.toString(newPositionX);
		tmVO.mPosY = Float.toString(newPositionY);
		tmVO.mDis = Float.toString(dis);
		if(dis > maxDistance2M) {
			tmVO.mPosJump2M = "Y";
		} else tmVO.mPosJump2M = "N";
		
		if(dis > maxDistance3M) {
			tmVO.mPosJump3M = "Y";
		} else tmVO.mPosJump3M = "N";
		
		tmProxy.setTestingData(tmVO);
		
		if(testingModeScanCount >= maxTestCount) {
			tmProxy.closeDbAndCursor();
			getTestingModeRes();
			act.finishTestingMode(outOfDistanceCount2M, outOfDistanceCount3M);
		} else {
			act.wiFiManager.startScan();
		}
	}

	private void getTestingModeRes() {
		// TODO Auto-generated method stub
		TestingModeProxy tmProxy = new TestingModeProxy(act);
		tmProxy.initDB();
		outOfDistanceCount2M = tmProxy.queryOutOfDistance("2M");
		outOfDistanceCount3M = tmProxy.queryOutOfDistance("3M");
	}
*/
	/*
	private void queryNearestDistance(int rssi[]) {
		Log.i(TAG, "queryNearestDistance BEGIN");
		
		WifiReferencePointProxy proxy = new WifiReferencePointProxy(act);
		proxy.initDB();
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		//list = proxy.queryReferencePointDis(mApScanRssi);
		list = proxy.queryReferencePointDis(scanDataList);
		//list = proxy.getRegularCoodinate();
		
		Collections.sort(list, new Comparator<HashMap>() {
			@Override
			public int compare(HashMap lhs, HashMap rhs) {
				// TODO Auto-generated method stub
				
				return (Integer) rhs.get(WifiReferencePointVO.DISTANCE) == (Integer) lhs
						.get(WifiReferencePointVO.DISTANCE) ? 0 :
							((Integer) rhs.get(WifiReferencePointVO.DISTANCE) < (Integer) lhs
									.get(WifiReferencePointVO.DISTANCE) ? 1 : -1);
				
				//return (Integer) rhs.get(WifiReferencePointVO.DISTANCE) < (Integer) lhs
					//	.get(WifiReferencePointVO.DISTANCE) ? 1 : -1;
			}
		});
*/		
		/*
		int knnSample = 3;
		double mPosibility = -1;
		int knnNo = 0;
		for(int i = 0; i < knnSample; i++) {
			BayesClassifier bayes = new BayesClassifier(list.get(i), scanDataList);
			double res = bayes.getLocationPosibility();
			if(res > mPosibility) {
				mPosibility = res;
				knnNo = i;
			}
		}
		
		Log.i(TAG, "The most suitable knn is no" +knnNo + ", Pos = "+mPosibility);
		*/
		//using Bayes algorithm to get positionX, Y
		/*
		float newPositionX = Float.parseFloat((String) list.get(knnNo).get(WifiReferencePointVO.POSITION_X));
		float newPositionY = Float.parseFloat((String) list.get(knnNo).get(WifiReferencePointVO.POSITION_Y));
		*/
		
		//using knn algorithm to get positionX, Y
/*		float newPositionX = (
				Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_X)) +
				Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_X)) + 
				Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_X)) 
				) / 3;
		float newPositionY = (
				Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_Y)) + 
				Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_Y)) + 
				Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_Y))
				) / 3;
		
		WifiReferencePointVO.lastPosX = newPositionX;
		WifiReferencePointVO.lastPosY = newPositionY;
		
		double x0_x1 =  Math.pow(
		Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_X)) - 
		Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_X)), 2);
		double y0_y1 =Math.pow(
		Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_Y)) - 
		Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_Y)), 2);
		
		double x1_x2 = Math.pow(
		Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_X)) - 
		Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_X)), 2);
		double y1_y2 = Math.pow(
		Float.parseFloat((String) list.get(1).get(WifiReferencePointVO.POSITION_Y)) - 
		Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_Y)), 2);
		
		double x2_x0 = Math.pow(
		Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_X)) - 
		Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_X)), 2);
		double y2_y0 = Math.pow(
		Float.parseFloat((String) list.get(2).get(WifiReferencePointVO.POSITION_Y)) - 
		Float.parseFloat((String) list.get(0).get(WifiReferencePointVO.POSITION_Y)), 2);
		
		Log.i(TAG, "x0_x1 = "+x0_x1+" y0_y1 = "+y0_y1+" x1_x2 = "+x1_x2+" y1_y2 = "+y1_y2
				+" x2_x0 = "+x2_x0+" y2_y0 =" +y2_y0);
		
		double e1 = Math.sqrt(x0_x1 + y0_y1);
		double e2 = Math.sqrt(x1_x2 + y1_y2);
		double e3 = Math.sqrt(x2_x0 + y2_y0);
		
		String edges = "The Edge E1 = "+e1 + " E2 = "+e2 + " E3 = " +e3; 
		
		String res = "The 1st Nearest posX = " + newPositionX + " posY = "
				+ newPositionY;
		
		String mOnLine =  (String) list.get(0).get(WifiReferencePointVO.PATH_NUMBER);
		
		Log.i(TAG, "" +res);
		Log.i(TAG, "on Line: " +mOnLine);
		
		indoorPosition.setCurrentLocation(newPositionX, newPositionY, rssi);
		
	}
*/
	private void clearRssiArray() {
		// TODO Auto-generated method stub
		for (int apCount = 0; apCount < mApName.size(); apCount++) {
			rssiGroup[apCount].clear();
		}
	}
/*	
	private void calculateStandarDeviation() {
		// TODO Auto-generated method stub
		for (int apCount = 0; apCount < mApName.size(); apCount++) {
			RssiStandardDeviation filter = new RssiStandardDeviation(rssiGroup[apCount]);
			mApScanSD[apCount] = filter.getStandardDeviation();
			
			Log.i(TAG, "calculateStandarDeviation " + mApName.get(apCount)
					+ " RSSI SD = " + mApScanSD[apCount]);
		}
	}
*/
	
	private void calculateMeanRssi() {
		// TODO Auto-generated method stub
		
		String mLocationApDetail = "";
		
		for (int apCount = 0; apCount < mApName.size(); apCount++) {

			RssiMeanFilter meanFilter = new RssiMeanFilter(rssiGroup[apCount]);
			mApScanRssi[apCount] = meanFilter.getMean();
			/*
			mLocationApDetail = mLocationApDetail + 
					mApName.get(apCount) +","+mApScanRssi[apCount]+";";
*/
			Log.i(TAG, "calculateBeaconMeanRssi " + mApName.get(apCount)
					+ " Avg RSSI = " + mApScanRssi[apCount]);
			//Log.i(TAG, "mLocationApDetail = " +mLocationApDetail);
			
			if(mFunctioName == FUNCTION_LOCATION || mFunctioName == FUNCTION_TESTING) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				String apMac = "AP_" + mApName.get(apCount).toString().replace(":", "_");
				map.put("AP_BSSID", apMac);
				map.put("AP_RSSI", mApScanRssi[apCount]);
				//map.put("IBeacon_RSSI", value)
				scanDataList.add(map);
			}
		}
	}
	
}
