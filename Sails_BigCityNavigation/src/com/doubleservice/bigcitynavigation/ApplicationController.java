package com.doubleservice.bigcitynavigation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import lib.locate.KNN.WifiReferencePointVO;
import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.BitmapLruCache;
import map.navi.Data.LocationData;
import map.navi.Data.NaviPlan;
import map.navi.component.NavigationIndoorPosition;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.doubleservice.DataVO.NavDataVO;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class ApplicationController extends Application implements
		IBeaconConsumer {

	private static ApplicationController mInstance;
	private HashMap<String, IBeacon> beacons;
	private IBeaconManager iBeaconManager;
	private String TAG = "ApplicationController";
	private RequestQueue mRequestQueue;
	public static BitmapLruCache cache;
	private static Context mContext;
	public static ArrayList<LocationData> locationsData;
	public static NavDataVO navData; 
	private String[] bigCityBeaconArray = new String[39]; 
	private String[] sogoBeaconArray = new String[33];
	public String floor;
	public String mLocationApDetail = "";
	
	public static float knnPos_X,knnPos_Y;
	public static String knnPos_XAndYBelongFloor;
	public static ArrayList<DevicePositionData> deviceLocationsData;
	public static boolean isNeedToLocateToAPPoint ;
	public static float nearAPX,nearAPY;
	public static float testPos_X,testPos_Y;
	public static boolean isToSailTechMode ;
	public void onCreate() {
		super.onCreate();
		/*
		for(int i = 0; i<bigCityBeaconArray.length; i++) {
			bigCityBeaconArray[i] = "0_"+Integer.toString(i+1);
		}
		
		for(int j = 40; j < 73; j++) {
			sogoBeaconArray[j-40] = "0_"+Integer.toString(j);
		}
		*/
		// initialize the singleton

		floor = "";
		knnPos_X = 0;
		knnPos_Y = 0;
		mInstance = this;
		mContext = this;
		isNeedToLocateToAPPoint = false;
		isToSailTechMode = false;
		nearAPX = 0;
		nearAPY = 0;
		cache = new BitmapLruCache();
		navData = new NavDataVO();
		
		locationsData = new ArrayList<LocationData>();
		deviceLocationsData = new ArrayList<DevicePositionData>();
		pListRunner();
		parseLocation();
		parseDeviceData();
	}
	
	public String[] getDevicePosition(String name) {
		String[] devicePos = new String[]{"0", "0"};
		
		for (int i = 0; i < deviceLocationsData.size(); i++) {
			DevicePositionData data = deviceLocationsData.get(i);
			if(data.getDeviceName().equals(name)) {
				devicePos[0] = data.getDevicePosX();
				devicePos[1] = data.getDevicePosY();
			}
			
		}
		
		return devicePos;
	}

	private void parseDeviceData() {
		// TODO Auto-generated method stub
		try {
			InputStream s = getApplicationContext().getAssets().open(
					"beacon_position.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(s));
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("device");
			for (int i = 0; i < nodeList.getLength(); i++) {
				DevicePositionData data = new DevicePositionData();
				Node node = nodeList.item(i);
				Element fstElmnt = (Element) node;
				
				data.deviceName = getDeviceLocationXMLStringByElementAndTagName(fstElmnt, "name");
				data.devicePosX = getDeviceLocationXMLStringByElementAndTagName(fstElmnt, "posx");
				data.devicePosY = getDeviceLocationXMLStringByElementAndTagName(fstElmnt, "posy");
				deviceLocationsData.add(data);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getDeviceLocationXMLStringByElementAndTagName(Element fstElmnt,
			String TagName) {

		NodeList list = fstElmnt.getElementsByTagName(TagName);
		Element element = (Element) list.item(0);
		list = element.getChildNodes();
		String s = ((Node) list.item(0)).getNodeValue();

		return s;
	}
	public void setFloor(String floor) {
		if(!floor.equals("")) {
			this.floor = floor;
		}
		else
			this.floor = this.floor;
	}
	public static String getStringByResId(int resId) {
		return mContext.getString(resId);
	}
	
	public static float convertPixelsToDp(float px){
		Resources resources = mContext.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	} 
	
	public static float convertDpToPixel(float dp){
	    Resources resources = mContext.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi/160f);
	    return px;
	}
	
	public NavDataVO getLocationDataByNumber(String number) {
		NavDataVO vo = new NavDataVO();
		for (int i = 0; i < locationsData.size(); i++) {
			LocationData mData = locationsData.get(i);
			if (mData.getLocationNumber().equals(number)) {
				vo.ItemName = mData.getLocationTitle();
				vo.Number = mData.getLocationNumber();
				vo.Area = mData.getLocationArea();
				vo.Floor = mData.getLocationFloor();
				vo.PositionX = mData.getLocationPosX();
				vo.PositionY = mData.getLocationPosY();

				return vo;
			}
		}

		return null;
	}

	private void pListRunner() {
		// TODO Auto-generated method stub
		
		//pd = ProgressDialog.show(this, "", "Loading Map , Please Wait ...");
		new Thread() {
			@Override
			public void run() {
				NaviPlan.loadPList2("beacon.plist",mContext);//.testLoadPList(currentMapIndex);
				handler.sendEmptyMessage(0);
			}

		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//pd.dismiss();
			//indoorPosition = new NavigationIndoorPosition(Navigation.this,plan);
			
			//isLocate = true;
			//indoorPosition.relocateToPosition();
			//backgroundLocateHandler.postDelayed(backgroundLocateRunner, backgroundLocateTime);
		}
	};
	
	
	private void parseLocation() {

		try {
			InputStream s = getApplicationContext().getAssets().open(
					"locations.xml");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(s));
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("location");

			/*
			 * <title>���L���]</title> <number>4001</number> <area>BIG
			 * CITY</area> <floor>F4</floor> <coorX>90</coorX>
			 * <coorY>218</coorY>
			 */
			for (int i = 0; i < nodeList.getLength(); i++) {
				LocationData data = new LocationData();
				Node node = nodeList.item(i);
				Element fstElmnt = (Element) node;

				data.locationTitle = getXMLStringByElementAndTagName(fstElmnt,
						"title");
				data.locationNumber = getXMLStringByElementAndTagName(fstElmnt,
						"number");
				data.locationArea = getXMLStringByElementAndTagName(fstElmnt,
						"area");
				data.locationFloor = getXMLStringByElementAndTagName(fstElmnt,
						"floor");
				data.locationPosX = getXMLStringByElementAndTagName(fstElmnt,
						"coorX");
				data.locationPosY = getXMLStringByElementAndTagName(fstElmnt,
						"coorY");
				locationsData.add(data);
				// Log.i(TAG, data.dataLog());
			}

		} catch (Exception e) {

		}
	}

	private String getXMLStringByElementAndTagName(Element fstElmnt,
			String TagName) {

		NodeList list = fstElmnt.getElementsByTagName(TagName);
		Element element = (Element) list.item(0);
		list = element.getChildNodes();
		String s = ((Node) list.item(0)).getNodeValue();
		if(TagName.equals("coorX")||TagName.equals("coorY")) {
			int coor = Math.round((Float.parseFloat(s)));
			s = String.valueOf(coor);
		}
		
		return s;
	}

	public static synchronized ApplicationController getInstance() {
		return mInstance;
	}

	public String getUuid() {
		String android_id = Secure.getString(getBaseContext()
				.getContentResolver(), Secure.ANDROID_ID);

		return android_id;
	}

	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

		VolleyLog.d("Adding request to queue: %s", req.getUrl());

		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag if tag is empty
		req.setTag(TAG);

		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public void onIBeaconServiceStart() {
		Log.i("TAG", "onIBeaconService BEGIN");
		if (iBeaconManager == null) {
			this.iBeaconManager = IBeaconManager
					.getInstanceForApplication(this);
			this.iBeaconManager.bind(this);
		}
	}

	public void onIBeaconServiceStop() {
		if (iBeaconManager != null) {
			this.iBeaconManager.unBind(this);
		}
	}

	@Override
	public void onIBeaconServiceConnect() {
		iBeaconManager.setRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons,
					Region region) {
				Iterator localIterator = null;
				int maxRssi = -999;
				if (iBeacons.size() > 0) {
					// Log.i("TAG",
					// "The first iBeacon I see is about "+iBeacons.iterator().next().getAccuracy()+" meters away.");
					beacons = new HashMap<String, IBeacon>();
					localIterator = iBeacons.iterator();

					while (true) {

						if (!localIterator.hasNext()) {
							break;
						}

						IBeacon localIBeacon = (IBeacon) localIterator.next();
						String str = Integer.toString(localIBeacon.getMajor())
								+ "_"
								+ Integer.toString(localIBeacon.getMinor());

						int rssi = localIBeacon.getRssi();
						/*
						if(rssi >= maxRssi) {
							maxRssi = rssi;
							for(int i = 0; i < bigCityBeaconArray.length; i++) {
								if(bigCityBeaconArray[i].equals(str)) {
									if(!floor.equals("BigCity")) { 
										floor = "BigCity";
										
										//NaviPlan.loadPList2("bigcity_beacon.plist",mContext);
										//WifiReferencePointVO.DBName = "/sdcard/"+"bc_bigcity_referencepoint_db.db";
									}
										//
									
								}
							}
							
							for(int j = 0; j < sogoBeaconArray.length; j++) {
								if(sogoBeaconArray[j].equals(str)) {
									
									if(!floor.equals("SOGO")) {
										floor = "SOGO";
										//NaviPlan.loadPList2("sogo_beacon.plist",mContext);//
										//WifiReferencePointVO.DBName = "/sdcard/"+"sogo_bigcity_referencepoint_db.db";
									}
								}
							}
						}
*/

						if (beacons.containsKey(str))
							continue;
						beacons.put(str, localIBeacon);

						// setIBeaconDetails();

					}
					
					//Log.i(TAG, "Floor = "+floor);
					
					Intent intent = new Intent();
					intent.setAction("onIBeaconServiceConnect");
					intent.putExtra("IBEACON_HASHMAP", beacons);
					sendBroadcast(intent);
				}

			}
		});

		try {
			iBeaconManager.startRangingBeaconsInRegion(new Region(
					"myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {
		}
	}

	public String getFloor() {
		// TODO Auto-generated method stub
		return floor;
	}

}
