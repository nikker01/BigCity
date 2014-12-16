package com.doubleservice.bigcitynavigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lib.locate.KNN.WifiReferencePointProxy;
import lib.locate.KNN.WifiReferencePointVO;
import map.navi.Data.NaviPlan;

import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.Log;

public class TestWifiService {
	/*
	HashMap<String, Object> map = new HashMap<String, Object>();
	String apMac = "AP_" + mApName.get(apCount).toString().replace(":", "_");
	map.put("AP_BSSID", apMac);
	map.put("AP_RSSI", mApScanRssi[apCount]);
	//map.put("IBeacon_RSSI", value)
	scanDataList.add(map);*/
	private String[] sogoWiFiArray ={"54:d0:ed:a1:5a:90","54:d0:ed:a1:56:70","54:d0:ed:a1:57:10","54:d0:ed:a1:56:30","54:d0:ed:a1:55:78",
			"54:d0:ed:a1:56:c0","54:d0:ed:a1:4f:d8","54:d0:ed:a1:5d:18","54:d0:ed:a1:50:10","54:d0:ed:a1:53:88"};// new String[10];
	private int[] sogoWiFiRssiArray = {0,0,0,0,0,0,0,-60,-60,-70};
	
	private int[] sogoWiFiRssiArrayNearBigCity = {-59,-76,-64,-82,0,-76,0,0,0,0};
	
	private String[] bigCityWiFiArray = {"54:d0:ed:a1:5a:70","54:d0:ed:a1:5c:10","54:d0:ed:a1:57:00","54:d0:ed:a1:5c:50","54:d0:ed:a1:5c:78",
										"54:d0:ed:a1:54:b8",//"54:d0:ed:a1:58:b8",
										"54:d0:ed:a1:55:d8","54:d0:ed:a1:54:a0","54:d0:ed:a1:54:08",
										"54:d0:ed:a1:56:58","54:d0:ed:a1:5d:08","54:d0:ed:a1:5d:40","54:d0:ed:a1:5d:58","54:d0:ed:a1:53:68",
										"54:d0:ed:a1:5b:30"};//new String[16]; 
	private int[] bigCityWiFiRssiArray = {  0,-70,-65,  0,	0,
										  -75,	0,	0,	0,	0,
											0,	0,	0,	0,	0,
											0};

	private int[] bigCityWiFiRssiArray2= {-74,-52,-64,-59,-58,-58,0,0,-70,-79,0,0,0,0,0,0};
	
	private int[] bigCityWiFiRssiArray3= {0,-81,-69,-57,0,-76,-49,0,-73,-78,-87,0,0,0,0};
	
	public ArrayList<HashMap> dbnList;
	public ArrayList<TestSiteSurveyData> dbDateList = new ArrayList<TestSiteSurveyData> ();
	public List<TestIBeaconScanResult> list = new ArrayList<TestIBeaconScanResult>();
	
	public TestWifiService(int dataIndex) {
		dataIndex = 1;
		if(dataIndex ==0) {
			for(int index = 0;index<sogoWiFiArray.length;index++) {
				TestIBeaconScanResult result = new TestIBeaconScanResult("AP",sogoWiFiArray[index],sogoWiFiRssiArray[index]);
				list.add(result);
			}
		}
		else if(dataIndex ==2) {
			for(int index = 0;index<sogoWiFiArray.length;index++) {
				TestIBeaconScanResult result = new TestIBeaconScanResult("AP",sogoWiFiArray[index],sogoWiFiRssiArrayNearBigCity[index]);
				list.add(result);
			}
		}
		else if(dataIndex ==1) {
			for(int index = 0;index<bigCityWiFiArray.length;index++) {
				TestIBeaconScanResult result = new TestIBeaconScanResult("AP",bigCityWiFiArray[index],bigCityWiFiRssiArray3[index]);
				list.add(result);
			}
		}
		
	}

	public TestWifiService() {
		WifiReferencePointVO.DBName = "/sdcard/bc_bigcity_referencepoint_db.db";
		NaviPlan.loadPList2("bigcity_beacon.plist",ApplicationController.getInstance());
		//WifiReferencePointVO.DBName = "/sdcard/sogo_bigcity_referencepoint_db.db";
		//NaviPlan.loadPList2("sogo_beacon.plist",ApplicationController.getInstance());//	
		
		WifiReferencePointProxy proxy = new WifiReferencePointProxy(ApplicationController.getInstance());
		
		dbnList = proxy.queryReferencePoints();
		for(HashMap map :dbnList) {
			int size = (Integer)map.get("ColumnSize");
			float positionX = Float.parseFloat((String)map.get(WifiReferencePointVO.POSITION_X));
			float positionY = Float.parseFloat((String)map.get(WifiReferencePointVO.POSITION_Y));
			ArrayList<TestIBeaconScanResult> result = new ArrayList<TestIBeaconScanResult> ();
			
			for(int index = 4;index<size;index++) {
				String[] MajorAndMinor = transMACInDBToRealMAC((String)map.get("Name"+index)).split(":");
				
				int rssi = Integer.parseInt((String)map.get("Rssi"+index));//+randomRssi();	
				result.add(new TestIBeaconScanResult(MajorAndMinor[0],MajorAndMinor[1],rssi));
			}
			TestSiteSurveyData data = new TestSiteSurveyData(positionX,positionY,result);
			dbDateList.add(data);
		}
		// TODO Auto-generated constructor stub
	}
	
	private int randomRssi() {
		int rssi =0;
		int random = (int)(Math.random()*10000)%15;
		int NagtiveOrPositive = (int)(Math.random()*10000)%2;
		if(NagtiveOrPositive == 0) {
			rssi = random;
		}
		else 
			rssi = -1*random;
		return rssi;
	}
	
	public String transMACInDBToRealMAC(String apMac) {
		String[] mac = apMac.split("_");
		String rMac ="";
		for(int index=1;index<mac.length;index++) {
			rMac+= mac[index];
			if(index<2) {
				rMac+=":";
			}
		}
		return rMac;
	}
}
