package com.doubleservice.bigcitynavigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import lib.locate.KNN.IBeaconScanReceiver;
import lib.locate.KNN.WifiReferencePointProxy;
import lib.locate.KNN.WifiReferencePointVO;
import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.LocationData;
import map.navi.Data.NaviNode;
import map.navi.Data.NaviPlan;
import map.navi.Data.POI;
import map.navi.ViewComponent.NavigationPopupView;
import map.navi.component.NavigationAPIProxy;
import map.navi.component.NavigationCalculator;
import map.navi.component.NavigationIndoorPosition;
import map.navi.component.NavigationSensor;
import map.navi.component.NavigationView;
import map.navi.component.PushMessageManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectPostRequest;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.PushMsgVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.bigcitynavigation.pushmsg.MsgPushInterface;
import com.doubleservice.proxy.MsgListProxy;
import com.doubleservice.proxy.RtlsApiProxy;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Navigation extends BaseDrawerActivity implements MsgPushInterface {

	private String TAG = "Navigation";
	private Button btnGetPosition;
	private NavigationView naviView ;
	private NavigationAPIProxy navigationProxy;
	private NavigationIndoorPosition indoorPosition;
	private NavigationSensor sensor;
	private NavigationCalculator navigationCalculator;
	private PushMessageManager pushMessageManager;
	private String currentFloor;
	private TextView txTop ;
	private NavigationPopupView popupWindow;
	private ProgressDialog pd;
	private ProgressDialog changeMapDialog;
	private ProgressDialog apiRequestDialog;
	public static final int REQUSET_CODE_ROUTESETTING =100;
	
	
	public NaviPlan plan;
	private int currentMapIndex ;
	private BroadcastReceiver mReceiver;
	private ArrayList<IBeacon> scannedIbeacons = new ArrayList<IBeacon>();
	private ArrayList<IBeacon> scannedPushMessageIBeacons = new ArrayList<IBeacon>();
	private ArrayList<IBeacon> scannedPositionIBeacons = new ArrayList<IBeacon>();
	private boolean isMoved = false;	
	private boolean isLocate = false;
	private boolean showDialog = false;
	private boolean isTouchedPanel = false;
	private boolean detectTouchPanelThreadOpen = false;
	private int startPositionIndex = 0,endPositionIndex = 0;
	
	private int countKNNLocateOutOfLimit =0;
	private int KNNLocateOutOfLimitTimes = 3;
	private int theNumberofPassedKNNLocatePosition = 1;
	
	private float theLimetMeterBetweenKNNAndLocatePoint = 150000;
	private float theLimitMeterBetweenKNNAndINS = 500000;
	private boolean isLocated = false,isNeedRoute = false;
	private float pre_azimuth=0,current_azimuth = 0f; 
	private boolean isThreadRotate = false;
	  

	public boolean isNavigating = false,isNextTarget = false;
	private int currentNavigationRouteTargetIndex = 1;
	private NaviNode currentTargetNode ;
	private boolean isNormalMode = false;
	private boolean isNavigatingButToKnn = false;
	private boolean isShowArrivedInformation = false;
	//private static int STATE_PARKING =0,STATE_TO_ITEM=1;
	//private int NAVI_STATE;
	private boolean isNeedGoToPark = false;
	private boolean isNeedTOFoodorShop = false;
	private int currentMode =0;
	private boolean isFromonActivityResult = false;
	private boolean isInitial = true;
	private boolean isBeaoncMode = true;
	private int changeMapTime = 3;
	private int mapDifferentTime = 0;
	private boolean detectChangeMap = false;
	private boolean targetNotInSameMap = false;
	private boolean isParkingFirstOpen = false;
	private NavDataVO currentNavData; 
	private boolean bIsHotzoneDialogOpen = false;
	//private boolean isChangedToParking = false;
	
	private boolean isMsgDialogOpen = false;
	private boolean isNeedToOpenFile = false;
	private boolean isChangingMap = false;
	private boolean everChangingMap = false; 
	private String changingName = "";
	private boolean isStopSearchFriend = true;
	private boolean parkingFirstTime = true;
	private boolean isTopushMessage = false;
	private ArrayList<HashMap> aryBeaconMsg = new ArrayList<HashMap>();
	private boolean manInMove = false;
	public boolean isHandChangeMap = false;
	private boolean changeMapDone = true;
	
	//WifiReferencePointProxy proxy;
	private boolean isTestingSitesurveyData = false;
	private MsgListProxy msgListProxy;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		//setContentView(R.layout.position1f);
		

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(R.layout.position1f, null, false);
		frameLayout.addView(activityView);
		
		//btnGetPosition = (Button) findViewById(R.id.btnGetPosition);	
		//btnGetPosition.setOnClickListener(btnGetPositionListener);
		plan = new NaviPlan(this);
		naviView = (NavigationView) findViewById(R.id.navigationView);
		sensor = new NavigationSensor(this);
		navigationCalculator = new NavigationCalculator(this.plan);
		naviView.setNavigationCalculator(navigationCalculator);
		pushMessageManager = new PushMessageManager(this);
		txTop = (TextView)findViewById(R.id.top_textview);
		popupWindow = new NavigationPopupView(this);
		indoorPosition = new NavigationIndoorPosition(Navigation.this,plan);
		msgListProxy = new MsgListProxy(this,100);
		registerBroadcastReceiver();
		
		this.currentMapIndex = 0;
		this.setCurrentIndoorPositionMapByIndex(currentMapIndex);
		isInitial = false;
	}
	
	
	
	private void updateLocateToServer(float[] xy) {
		this.updateLocateToServer(xy[0], xy[1]);
		/*
		String android_id = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);
		Log.i(TAG, "ID = "+android_id);
		//Toast.makeText(this, android_id, Toast.LENGTH_SHORT).show();
		String floor = plan.currentMapName;
		RtlsApiProxy proxy = new RtlsApiProxy(this);
		String location = ApplicationController.getInstance().mLocationApDetail;
		JsonObjectPostRequest jsonObjectRequest = proxy.updNavigation(android_id,
				Float.toString(xy[0]), Float.toString(xy[1]), floor, location);
		ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);*/
	}
	
	private void updateLocateToServer(float x,float y) {
		//if(changeMapDone) {
			String android_id = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
			//Toast.makeText(this, android_id, Toast.LENGTH_SHORT).show();
			String floor = plan.currentMapName;
			RtlsApiProxy proxy = new RtlsApiProxy(this);
			String location = ApplicationController.getInstance().mLocationApDetail;
			JsonObjectPostRequest jsonObjectRequest = proxy.updNavigation(android_id, 
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
								
				Builder MyAlertDialog = new AlertDialog.Builder(this);
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

							Log.i(TAG, "updNavigation res mPushMsg ="
									+ mPushMsg + " mFlag =" + mFlag
									+ " mTimeLimit = " + mTimeLimit
									+ " mFileExt = " + mFileExt + " mFileName="
									+ mFileName + " mFileUrl = " + mFileUrl);

							if(!isMsgDialogOpen) {
								isMsgDialogOpen = true;
								
								Builder MyAlertDialog = new AlertDialog.Builder(this);
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
									
									RtlsApiProxy proxy = new RtlsApiProxy(this);
									JsonObjectPostRequest jsonObjectRequest =
											proxy.updateStatus(dataContent.getString("ap_message"));
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
            this.startActivity(intent);  
		}
		else {
		Intent intent = new Intent();
		intent.putExtra("ap_msg", vo);
		intent.setClass(this, PushMsgFileActivity.class);
		isTopushMessage = true;
		this.startActivity(intent);
		}
	}
	
	@Override
	public void onPause() {
		this.unregisterReceiver(mReceiver);
		mReceiver = null;
		//this.popupWindow.onPanelTouchAction();
		super.onPause();
	}
	
	private Runnable mThreadMapChange = new Runnable() {
		@Override
		public void run() {
			mapChangeHandler.sendEmptyMessage(0);			
		}		
	}; 
	
	private Handler mapChangeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			changeMapDialog.dismiss();
			detectChangeMap = false;
			isChangingMap = false;
			//naviView.locatePoint.setVisibility(naviView.locatePoint.VISIBLE);
			//if(currentMode != popupWindow.viewParking ) {
				//naviView.locatePoint.setVisibility(naviView.locatePoint.VISIBLE);
			//}
			//else if(plan.currentMapName.equals(ApplicationController.getInstance().getFloor())) {
				//naviView.locatePoint.setVisibility(naviView.locatePoint.VISIBLE);
			//}
			
			if(plan.currentMapName.equals(ApplicationController.getInstance().getFloor()) ) {
				
				//changeMapDone = true;
				float[] knnXY = indoorPosition.getPositionByCounterTimes(theNumberofPassedKNNLocatePosition);
				naviView.setLocatePointXYToLine(knnXY[0], knnXY[1]);
				//naviView.locatePoint.setVisibility(naviView.VISIBLE);
				//naviView.pointCenter();
				if(currentMode == popupWindow.viewParking && !plan.mapNames[2].equals(plan.currentMapName)) {
					if(parkingFirstTime) {
						parkingFirstTime = false;
						POI endPOINode = plan.currentParkingElevator;
						currentEndPOINode = endPOINode;
						naviView.navToTargetFromCurrentPosition(currentEndPOINode.x,currentEndPOINode.y);
						float[] locateXY = naviView.getLocatePointPixelOnMap(); 
				
						naviView.setItemToLocation(naviView.currentNaivStart, locateXY[0], locateXY[1]);
						naviView.setItemToLocation(naviView.targetpoint, currentEndPOINode.x, currentEndPOINode.y);
        		
						naviView.targetpoint.setVisibility(naviView.targetpoint.VISIBLE);
						naviView.currentNaivStart.setVisibility(naviView.currentNaivStart.VISIBLE);
						naviView.route.setVisibility(naviView.route.VISIBLE);
						naviView.locatePoint.setVisibility(naviView.locatePoint.VISIBLE);
						isNavigating = true;
					}
				}
			
			}
			else {
				//naviView.locatePoint.setVisibility(naviView.locatePoint.INVISIBLE);
				everChangingMap = false;
				if(currentMode != popupWindow.viewParking )
				changeMapByMapName(ApplicationController.getInstance().getFloor());
			}
		}
	};
	
	public void changeMapByMapName(String name) {
		int index = this.plan.getMapIndexByName(name);
		if(index !=-1) {
			if(!isChangingMap) {
				isChangingMap = true;
				if(currentMode != popupWindow.viewFriend && currentMode != popupWindow.viewFoodAndShop) {
					//this.naviView.locatePoint.setVisibility(this.naviView.locatePoint.INVISIBLE);
					changeMapDialog = ProgressDialog.show(this, "", getResources().getString(R.string.progress_dialog_msg_change_map));
				}
				setCurrentIndoorPositionMapByIndex(index);
				startPositionIndex = 0;
				endPositionIndex = 0;
				this.isNavigating = false;
				this.popupWindow.changePopView(this.currentMode);
				if(currentMode != popupWindow.viewFriend && currentMode != popupWindow.viewFoodAndShop) {
					//changeMapDone = false;
					mapChangeHandler.postDelayed(mThreadMapChange, 5000);
				}
				else {
					isChangingMap = false;
				}
			}
			//else {
				//everChangingMap = true;
				//changingName = name;
			//}
		}	
		else {
			//Toast.makeText(Navigation.this, this.getResources().getString(R.string.toast_str_detect_not_in_floor), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	public void onResume() {
		if(mReceiver==null)
			registerBroadcastReceiver();
		super.onResume();
		
		
		boolean hasSpecialRequest = false;
		
		//Bundle bundle = this.getIntent().getExtras();
		
		
		 //if(bundle!=null) {
		if(!isFromonActivityResult && !isTopushMessage)
			isNavigating = false;
			 NavDataVO data = ApplicationController.navData ;//(NavDataVO)bundle.getSerializable(RequestCodeVO.CMD_NAV);
			 currentNavData = data;
			 //Log.i(TAG, "id: "+data.MethodID+",name :"+data.ItemName+",area: "+data.Area+", x: "+data.PositionX+",y: "+data.PositionY);
			 if(data!=null) {
				 hasSpecialRequest = true;
				 Log.i(TAG, "id: "+data.MethodID+",name :"+data.ItemName+",area: "+data.Area+", x: "+data.PositionX+",y: "+data.PositionY);
				 int methodId = data.MethodID;
				 this.naviView.route.setVisibility(this.naviView.route.INVISIBLE);
			 		this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.INVISIBLE);
			 		this.naviView.currentNaivStart.setVisibility(this.naviView.currentNaivStart.INVISIBLE);
			 		this.naviView.friend.setVisibility(this.naviView.friend.INVISIBLE);
				 switch(methodId) {
				 	case GlobalDataVO.NAV_METHOD_FIND_FRIEND:
				 		String mapName = data.Area;
				 		currentMode = this.popupWindow.viewFriend;
				 		this.changeMapByMapName(mapName);
				 		POI friend = new POI(data.Number,(Float.parseFloat(data.PositionX))*2,(Float.parseFloat(data.PositionY))*2,data.ItemName);
						currentEndPOINode = friend;
						this.naviView.setItemToLocation(this.naviView.friend, friend.x, friend.y);
						this.naviView.friend.setVisibility(this.naviView.friend.VISIBLE);
				 		this.popupWindow.setFreeAndShopTaegetName(friend.POIName);
				 		this.popupWindow.changePopView(this.popupWindow.viewFriend);
				 		isStopSearchFriend = false;
				 		friendChangeHandler.postDelayed(mThreadFriendChange, 30000);
				 	break;
				 	case GlobalDataVO.NAV_METHOD_PROMOTIONAL:
				 		String promoMapName = data.Area;
				 		currentMode = this.popupWindow.viewFoodAndShop;
				 		this.changeMapByMapName(promoMapName);
				 		POI promoPOI = new POI(data.Number,Integer.parseInt(data.PositionX),Integer.parseInt(data.PositionY),data.ItemName);
						currentEndPOINode = promoPOI;
						this.naviView.setItemToLocation(this.naviView.friend, promoPOI.x, promoPOI.y);
						this.naviView.friend.setVisibility(this.naviView.friend.VISIBLE);
				 		this.popupWindow.setFreeAndShopTaegetName(promoPOI.POIName);
				 		this.popupWindow.changePopView(this.popupWindow.viewFoodAndShop);
				 	break;
				 	case GlobalDataVO.NAV_METHOD_BRAND:
				 		String brandMapName = data.Area;
				 		currentMode = this.popupWindow.viewFoodAndShop;
				 		this.changeMapByMapName(brandMapName);
				 		POI brandPOI = new POI(data.Number,Integer.parseInt(data.PositionX),Integer.parseInt(data.PositionY),data.ItemName);
						currentEndPOINode = brandPOI;
						this.naviView.setItemToLocation(this.naviView.friend, brandPOI.x, brandPOI.y);
						this.naviView.friend.setVisibility(this.naviView.friend.VISIBLE);
				 		this.popupWindow.setFreeAndShopTaegetName(brandPOI.POIName);
				 		this.popupWindow.changePopView(this.popupWindow.viewFoodAndShop);
				 	break;
				 	case GlobalDataVO.NAV_METHOD_DINNING_ROOM:
				 		String dinningMapName = data.Area;
				 		currentMode = this.popupWindow.viewFoodAndShop;
				 		this.changeMapByMapName(dinningMapName);
				 		POI dinningPOI = new POI(data.Number,Integer.parseInt(data.PositionX),Integer.parseInt(data.PositionY),data.ItemName);
						currentEndPOINode = dinningPOI;
						this.naviView.setItemToLocation(this.naviView.friend, dinningPOI.x, dinningPOI.y);
						this.naviView.friend.setVisibility(this.naviView.friend.VISIBLE);
				 		this.popupWindow.setFreeAndShopTaegetName(dinningPOI.POIName);
				 		this.popupWindow.changePopView(this.popupWindow.viewFoodAndShop);
				 	break;
				 	case GlobalDataVO.NAV_METHOD_PARKING:
				 		String PARKING = this.plan.mapNames[2];
				 		this.changeMapByMapName(PARKING);
				 		currentMode = this.popupWindow.viewParking;
				 		POI parkPOINode = this.plan.currentParkingElevator;
				 		currentEndPOINode = parkPOINode;
						this.naviView.setItemToLocation(this.naviView.friend, parkPOINode.x, parkPOINode.y);
						this.popupWindow.setFreeAndShopTaegetName(this.plan.mapNames[2]+this.getResources().getString(R.string.map_parking_area_a));
						this.popupWindow.changePopView(this.popupWindow.viewParking);
						this.naviView.friend.setVisibility(this.naviView.friend.INVISIBLE);
						this.naviView.locatePoint.setVisibility(this.naviView.locatePoint.INVISIBLE);
						this.isParkingFirstOpen = true;
				 		//this.startLocate();
				 	break;
				 } 
			 }
			 else {
				 hasSpecialRequest = false;
				//Toast.makeText(Navigation.this,this.getResources().getString(
					//			R.string.toast_str_no_navitarget_data), Toast.LENGTH_LONG).show();
			 }
			 
		 
		 if(!hasSpecialRequest && !isTopushMessage) {
			 currentMode = this.popupWindow.viewNavigation;
		 	if(!isFromonActivityResult) {
		 		this.naviView.route.setVisibility(this.naviView.route.INVISIBLE);
		 		this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.INVISIBLE);
		 		this.naviView.currentNaivStart.setVisibility(this.naviView.currentNaivStart.INVISIBLE);
		 		this.naviView.friend.setVisibility(this.naviView.friend.INVISIBLE);
		 		this.popupWindow.changePopView(this.popupWindow.viewNavigation);
		 	}
		 	else {
				 isFromonActivityResult = false;
				 this.popupWindow.onPanelTouchAction();
			}
		 }
		 
		 //this.popupWindow.onPanelTouchAction(); 
		 /*
			public int MethodID; // �末摨瘀��������氬�
			public String ItemName = ""; //撠瑹��陷摮�
			public String PositionX;
			public String PositionY;
			public String Number;
			public String Area;
			public String Floor;
			*/
		 if(currentMode != this.popupWindow.viewFriend) {
			 isStopSearchFriend = true;
		 }
		 if(!isHandChangeMap) {//!isTopushMessage &&) {
			 if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
					/*
					
					*/
					Toast.makeText(this,
							getResources().getString(R.string.toast_str_ble_not_work),
							Toast.LENGTH_LONG).show();
					String mTitle = getResources().getString(R.string.dialog_title_bluetooth_inavailable);
					String mContent = getResources().getString(R.string.dialog_msg_blutooth_inavailable);
					String mBtnConfirm = getResources().getString(R.string.dialog_btn_confirm);
					String mBtnCancel = getResources().getString(R.string.dialog_btn_cancel);
					BaseAlertMsg.pushGeneralAlert(this, mTitle, mContent, mBtnConfirm, mBtnCancel, new IGenericAlert(){
						@Override
						public void PositiveMethod(DialogInterface dialog, int id) {
							// TODO Auto-generated method stub
							Intent i = new Intent("/");
							ComponentName cm = new ComponentName("com.android.settings","com.android.settings.bluetooth.BluetoothSettings");
							i.setComponent(cm);
							i.setAction("android.intent.action.VIEW");
							startActivityForResult(i , 0);
						}

						@Override
						public void NegativeMethod(DialogInterface dialog, int id) {
							// TODO Auto-generated method stub
							//this.finish();
						}});
					
				}
				else {
			 this.startLocate();
				}
		 }
		
		 if(isTopushMessage) {
			 isTopushMessage = false;
		 }
		
		
	}
	
	public void naviToFoodAndShop() {
		//POI endPOINode = this.plan.currentParkingElevator;//new POI(data.Number,Integer.parseInt(data.PositionX),Integer.parseInt(data.PositionY),data.ItemName);
		//currentEndPOINode = endPOINode;
		/*if(isLocated) {
			
			naviView.navToTargetFromCurrentPosition(currentEndPOINode.x,currentEndPOINode.y);
			this.naviView.setItemToLocation(this.naviView.targetpoint,currentEndPOINode.x,currentEndPOINode.y);
			this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.VISIBLE);
			this.naviView.friend.setVisibility(this.naviView.friend.INVISIBLE);
			this.naviView.currentNaivStart.setVisibility(naviView.currentNaivStart.VISIBLE);
			
			float[] locateXY = naviView.getLocatePointPixelOnMap(); 
			this.naviView.setItemToLocation(naviView.currentNaivStart, locateXY[0], locateXY[1]);
		}
		*/
		//else {			
			isNeedTOFoodorShop = true;
			startLocate();
		//}
	}


		
	private void naviToTheFuckingItem(NavDataVO data) {
		//POI  poi
		/*
		if(startPositionIndex>0) {
			s += this.plan.POICollection.get(startPositionIndex-1).POIName+"\n";
			POI startPOINode = plan.POICollection.get(startPositionIndex-1);
			naviView.navToTargetFromStartPOI(startPOINode.x, startPOINode.y, endPOINode.x, endPOINode.y);
			this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.VISIBLE);
		}
		else {
			s += "Current Position\n";
			if(isLocated) {
				naviView.navToTargetFromCurrentPosition(endPOINode.x,endPOINode.y);
				this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.VISIBLE);
			}
			else {
				currentEndPOINode = endPOINode;
				isNeedRoute = true;
				startLocate();
			}
		}
		
		this.naviView.setItemToLocation(this.naviView.targetpoint, endPOINode.x, endPOINode.y);
		
		this.isNextTarget = false;
		this.currentNavigationRouteTargetIndex = 1;
		if(!isNormalMode){
			this.naviView.changeLocatePointPicture(R.drawable.point2);
			this.rotateLocatePointToDirectTarget();
			this.isNavigating = true;
		}
		s += "End: "+this.plan.POICollection.get(endPositionIndex).POIName;
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		this.popupWindow.changeToNavigationMode(this.plan.POICollection.get(endPositionIndex).POIName);
	}*/
	}
	/*
	private void pListRunner() {
		// TODO Auto-generated method stub
		
		pd = ProgressDialog.show(this, "", "Loading Map , Please Wait ...");
		new Thread() {
			@Override
			public void run() {
				plan.loadPList2(currentMapIndex);//.testLoadPList(currentMapIndex);
				handler.sendEmptyMessage(0);
			}

		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			indoorPosition = new NavigationIndoorPosition(Navigation.this,plan);
			
			//isLocate = true;
			//indoorPosition.relocateToPosition();
			//backgroundLocateHandler.postDelayed(backgroundLocateRunner, backgroundLocateTime);
		}
	};
*/
	public void startLocate() {
		//indoorPosition.startScan();
		if(currentMode != popupWindow.viewFriend) {
		isLocate = true;
		//if(isBeaoncMode)
		indoorPosition.beaconRelocateToPosition();
		//else
			//indoorPosition.relocateToPosition();
		isTouchedPanel = true;//isTouchPanel();
		}
	}

	private float[] getNextNodeXYOnPath() {
		
		//NaviNode node = (NaviNode)this.naviView.currentNavigationRoute.get(currentNavigationRouteIndex);
		currentTargetNode = (NaviNode)this.naviView.currentNavigationRoute.get(currentNavigationRouteTargetIndex);
		
		float[] nodeXY = {currentTargetNode.x,currentTargetNode.y};
		return nodeXY;
	}
	
	private float setNearlyPixel(float pixel,float nearlyPixel1,float nearlyPixel2) {
		float dis1 = Math.abs(pixel-nearlyPixel1);
		float dis2 = Math.abs(pixel-nearlyPixel2);
		if(dis1<=dis2)
			return dis1;
		else
			return dis2;
	}
	
	private int findCurrentLocatePointInWhichRouteRegion() {
		
		if(isFirstTimeToGo) {
			NaviNode startNode = (NaviNode)this.naviView.currentNavigationRoute.get(0);
			naviView.setLocatePointXYToLine(startNode.x, startNode.y);
			isFirstTimeToGo = false;
		}
		
		float[] currentLocateXY = naviView.getLocatePointPixelOnMap();
		currentLocateXY[0] = (float) Math.round(currentLocateXY[0]);
		currentLocateXY[1] = (float) Math.round(currentLocateXY[1]);
		NaviNode searchedTargetNode;
		NaviNode preNode;
		
		for(int index = 1;index<naviView.currentNavigationRoute.size();index++) {
			preNode = (NaviNode)this.naviView.currentNavigationRoute.get(index-1);
			searchedTargetNode = (NaviNode)this.naviView.currentNavigationRoute.get(index);
			float[] currentNodeXY = {searchedTargetNode.x,searchedTargetNode.y};
			float[] preNodeXY = {preNode.x,preNode.y};
			
			if(MathProxy.getDistance(currentLocateXY, preNodeXY)<this.plan.currentMapPixelperMeter) {
				currentLocateXY[0]=preNodeXY[0];
				currentLocateXY[1]=preNodeXY[1];
			}
			if(MathProxy.getDistance(currentLocateXY, searchedTargetNode)<this.plan.currentMapPixelperMeter) {
				currentLocateXY[0]= currentNodeXY[0];
				currentLocateXY[1]= currentNodeXY[1];
			}
			/*if(Math.abs(currentLocateXY[0]-preNodeXY[0])<this.plan.currentMapPixelperMeter) {
				currentLocateXY[0]=preNodeXY[0];
			}
			if(Math.abs(currentLocateXY[1]-preNodeXY[1])<this.plan.currentMapPixelperMeter) {
				currentLocateXY[1]=preNodeXY[1];
			}
			
			if(Math.abs(currentLocateXY[0]-currentNodeXY[0])<this.plan.currentMapPixelperMeter) {
				currentLocateXY[0]=currentNodeXY[0];
			}
			if(Math.abs(currentLocateXY[1]-currentNodeXY[1])<this.plan.currentMapPixelperMeter) {
				currentLocateXY[1]=currentNodeXY[1];
			}
			*/
			if(MathProxy.xyIsBetweenStartAndEnd(currentLocateXY,currentNodeXY,preNodeXY)){
				isNavigatingButToKnn = false;
				if(index!=naviView.currentNavigationRoute.size()-1) {
					isNextTarget = false;
				}
				return index;
			}
		}
		
		isNavigatingButToKnn = true;
		return currentNavigationRouteTargetIndex;
	}
	
	private void checkLocatePointNextNode() {		
		if(!isNavigatingButToKnn && currentNavigationRouteTargetIndex<naviView.currentNavigationRoute.size()) {
			float[] currentLocateXY = naviView.getLocatePointPixelOnMap();
			currentLocateXY[0] = (float) Math.round(currentLocateXY[0]);
			currentLocateXY[1] = (float) Math.round(currentLocateXY[1]);
			NaviNode currentNode = (NaviNode)this.naviView.currentNavigationRoute.get(currentNavigationRouteTargetIndex);
			NaviNode preNode = (NaviNode)this.naviView.currentNavigationRoute.get(currentNavigationRouteTargetIndex-1);
			float[] currentNodeXY = {currentNode.x,currentNode.y};
			float[] preNodeXY = {preNode.x,preNode.y};
			//if(Math.abs(currentLocateXY[0]-currentNodeXY[0])<this.plan.currentMapPixelperMeter) {
				//currentLocateXY[0]=currentNodeXY[0];
			//}
			//if(Math.abs(currentLocateXY[1]-currentNodeXY[1])<this.plan.currentMapPixelperMeter) {
				//currentLocateXY[1]=currentNodeXY[1];
			//}
			//Log.i(TAG, "currentLocateX = "+currentLocateXY[0]+", currentLocateY = "+currentLocateXY[1]+
			//			"\ncurrentNodeX = "+currentNodeXY[0]+", currentNodeY = "+currentNodeXY[1]+
			//			"\npreNodeX = "+preNodeXY[0]+", preNodeY = "+preNodeXY[1]);
			//float deviation = 10f;
			//Log.i(TAG, "index = "+currentNavigationRouteIndex+", size = "+naviView.currentNavigationRoute.size());
			if(currentNavigationRouteTargetIndex<naviView.currentNavigationRoute.size()-1) {
				if(!MathProxy.xyIsBetweenStartAndEndNoEqu(currentLocateXY, preNodeXY, currentNodeXY)) {
					NaviNode NextNode = (NaviNode)this.naviView.currentNavigationRoute.get(currentNavigationRouteTargetIndex+1);
					float[] nextNodeXY = {NextNode.x,NextNode.y};
					if(MathProxy.xyIsBetweenStartAndEnd(currentLocateXY,currentNodeXY,nextNodeXY)) {
						currentNavigationRouteTargetIndex++;
					}
					
					NaviNode finalNode = (NaviNode)this.naviView.currentNavigationRoute.get(naviView.currentNavigationRoute.size()-1);
					float[] finalNodeXY = {finalNode.x,finalNode.y};
					
					if((MathProxy.getDistance(currentNodeXY, finalNodeXY)/plan.currentMapPixelperMeter)<=1.2) {
						isNextTarget = true;
					}
				}
			}
			else if(!isNavigatingButToKnn){
				isNextTarget = true;
			}
		}
	}
	
	//private 
	
	private Handler touchPanelHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			currentNavigationRouteTargetIndex = findCurrentLocatePointInWhichRouteRegion();
			if(!isNormalMode && isNavigating && !isNavigatingButToKnn) {
				checkLocatePointNextNode();
				float[] currentLocateXY = naviView.getLocatePointPixelOnMap();
				float[] routeTargetXY = getNextNodeXYOnPath();
				float rotate = getRotate(currentLocateXY,routeTargetXY);
				naviView.rotateItem(0, rotate);
				NaviNode preNode = (NaviNode)naviView.currentNavigationRoute.get(currentNavigationRouteTargetIndex-1);
				NaviNode currentNode = (NaviNode)naviView.currentNavigationRoute.get(currentNavigationRouteTargetIndex);
				//Toast.makeText(Navigation.this,"preNode x = "+preNode.x+",y = "+preNode.y+ "\ncurrentNode x = "+currentNode.x+",y = "+currentNode.y,Toast.LENGTH_SHORT ).show();
				
				float[] moveXY = naviView.getNewPositionOnLineByRotateDirection(preNode,currentNode,rotate);
				//Toast.makeText(Navigation.this,"preNode x = "+preNode.x+",y = "+preNode.y+ "\ncurrentNode x = "+currentNode.x+",y = "+currentNode.y
					//							+"\nmoveXY x = "+moveXY[0]+",y = "+moveXY[1],Toast.LENGTH_SHORT ).show();
				
				//Log.i(TAG,"moveXY X = "+moveXY[0]+",Y = "+moveXY[1]);
				/*if(isNextTarget) {
					NaviNode NextNode = (NaviNode)naviView.currentNavigationRoute.get(currentNavigationRouteTargetIndex+1);
					
					moveXY[0] = NextNode.x;
					moveXY[1] = NextNode.y;
				}
				*/
				float[] knnXY = indoorPosition.getPositionByCounterTimes(theNumberofPassedKNNLocatePosition);
				float distance = navigationCalculator.calculateDistanceOnLine(plan,moveXY[0],moveXY[1],knnXY[0], knnXY[1]);
				//txTop.setText("current Dis = "+distance+"m");
				if(distance<theLimitMeterBetweenKNNAndINS) {
					if(isLocated){ 
						naviView.setLocatePointXYToLine(moveXY[0], moveXY[1]);//.pointMovingWithoutPath();
						
						updateLocateToServer(moveXY[0], moveXY[1]);
						//naviView.pointCenter();
						rotateLocatePointToDirectTarget();
						float[] currentLocate = naviView.getLocatePointPixelOnMap();
						NaviNode finalNode = (NaviNode)naviView.currentNavigationRoute.get(naviView.currentNavigationRoute.size()-1);
						NaviNode prefinalNode = (NaviNode)naviView.currentNavigationRoute.get(naviView.currentNavigationRoute.size()-2);
						float disEnd = (MathProxy.getDistance(prefinalNode, finalNode)/plan.currentMapPixelperMeter);
						if((MathProxy.getDistance(currentLocate, finalNode)/plan.currentMapPixelperMeter)<=(disEnd+3)) {
							if(!isShowArrivedInformation) {
								isShowArrivedInformation = true;
								showArrivedMessage(currentEndPOINode.POIName);
							}
						}
						isMoved = true;
					}
				}
				if(isNextTarget) {
					float[] currentLocate = naviView.getLocatePointPixelOnMap();
					NaviNode finalNode = (NaviNode)naviView.currentNavigationRoute.get(naviView.currentNavigationRoute.size()-1);
					NaviNode prefinalNode = (NaviNode)naviView.currentNavigationRoute.get(naviView.currentNavigationRoute.size()-2);
					float disEnd = (MathProxy.getDistance(prefinalNode, finalNode)/plan.currentMapPixelperMeter);
					if((MathProxy.getDistance(currentLocate, finalNode)/plan.currentMapPixelperMeter)<=(disEnd+1)) {
						if(!isShowArrivedInformation) {
							isShowArrivedInformation = true;
							showArrivedMessage(currentEndPOINode.POIName);
						}
					}
					
				}
			}
			else if(isNormalMode){
				//Log.i(TAG, "isMove");
				float[] moveXY = naviView.getCurrentMoveOnLineCoordinate();
				float[] knnXY = indoorPosition.getPositionByCounterTimes(theNumberofPassedKNNLocatePosition);
				
				float distance = navigationCalculator.calculateDistanceOnLine(plan,moveXY[0],moveXY[1],knnXY[0], knnXY[1]);
				
				if(distance<theLimitMeterBetweenKNNAndINS) {
					//if(!naviView.isTouch) {
					//Log.i(TAG, "isLessthan" );
					if(isLocated){ 
						//Log.i(TAG, "islocated");
						naviView.setLocatePointXY(moveXY[0], moveXY[1]);//.pointMovingWithoutPath();
						updateLocateToServer(moveXY[0], moveXY[1]);
						isMoved = true;
					}
					//}
				}
			}
			
			/*
			//if(!isTouchedPanel) {
				float[] moveXY = naviView.getCurrentMoveOnLineCoordinate();
				float[] knnXY = indoorPosition.getPositionByCounterTimes(theNumberofPassedKNNLocatePosition);
				
				float distance = navigationCalculator.calculateDistanceOnLine(plan,moveXY[0],moveXY[1],knnXY[0], knnXY[1]);
				if(distance<theLimitMeterBetweenKNNAndINS) {
					//if(!naviView.isTouch) {
					if(isLocated){ 
						naviView.setLocatePointXY(moveXY[0], moveXY[1]);//.pointMovingWithoutPath();
						updateLocateToServer(moveXY[0], moveXY[1]);
						isMoved = true;
					}
					//}
				}
			//}
			//else if(!naviView.isTouch){
				//isTouchedPanel = false;
			//}			
			* 
			 */
			detectTouchPanelThreadOpen = false;
		}
	};
	
	private void showArrivedMessage(String msg) {
		final AlertDialog.Builder singlechoicedialog = new AlertDialog.Builder(this);
		singlechoicedialog.setMessage(this.getResources().getString(R.string.arrive_target)+"-"+msg);
	
		singlechoicedialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				closeNavigation();
				popupWindow.closeNavigationMode();
		       	isShowArrivedInformation = false;
		       	float[] xy = naviView.getLocatePointPixelOnMap();
		       	naviView.setLocatePointXYToLine(xy[0], xy[1]);
	    	}
		});
  
		AlertDialog alert_dialog = singlechoicedialog.create();
		alert_dialog.show();
	}
	
	private Runnable mThreadtouchPanel = new Runnable() {
		@Override
		public void run() {
			touchPanelHandler.sendEmptyMessage(0);			
		}		
	};
	

	private Runnable testrotateThread = new Runnable() {
		@Override
		public void run() {
			testRotateHandler.sendEmptyMessage(0);			
		}		
	};

	private boolean isTest = false;
	private Handler testRotateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(!isTest) {
				isTest = true;
				testRotate();
			}
			//naviView.rotateItem(standard_azimuth,current_azimuth);
			//txTop.setText("\ncurrent azi: "+current_azimuth+"\n map_north azi: "+standard_azimuth);
			//testRotateHandler.postDelayed(r, delayMillis)
			//testrotateThread;
			testRotateHandler.postDelayed(testrotateThread, 4000);
		}
	};
	
	
	private void testRotate(){
		//float[] currentPoint = naviView.getLocatePointPixelOnMap();
		
		naviView.targetpoint.setVisibility(naviView.targetpoint.VISIBLE);
		float x = (float)((Math.random()*1000000)%650);
		float y = (float)((Math.random()*1000000)%418);
		naviView.setLocatePointXY(x,y);
		float[] currentPoint = naviView.getLocatePointPixelOnMap();
		
		naviView.setItemToLocation(naviView.targetpoint, 337, 180);
		float[] target = {337,180};
		float rotate = MathProxy.getRotateFormStartToEnd(currentPoint, target);

		naviView.rotateItem(0,rotate);
		txTop.setText("rotate: "+rotate);
		isTest = false;
	}
	
	private void rotateLocatePointToDirectTarget() {
		float[] locateXY = this.naviView.getLocatePointPixelOnMap();
		float[] targetXY = this.naviView.getItemPixelOnMap(this.naviView.targetpoint);
		float rotate = MathProxy.getRotateFormStartToEnd(locateXY, targetXY);
		this.naviView.rotateItem(0,rotate);
	}
	
	private float getRotate(float start[],float end[]) {
		return MathProxy.getRotateFormStartToEnd(start, end);
	}
	
	private void registerBroadcastReceiver() {
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("onIBeaconServiceConnect");
		mFilter.addAction("onNavigationSensorMove");
		mFilter.addAction("onNavigationSensorRotate");
		mFilter.addAction("onWiFiScanDone");
		mFilter.addAction("onRelocateDone");
		mFilter.addAction("onNavigationViewTouch");
		mFilter.addAction("onNavigationViewTouchOnePoint");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub	
				if(intent.getAction().equals("onNavigationSensorMove")) {
					boolean isRightTake =  (Boolean) intent.getSerializableExtra("RIGHT_PHONE_TAKE");
					if(!manInMove) 
						manInMove = (Boolean) intent.getSerializableExtra("SENSOR_MOVE");
					//naviView.setLocatePointXY(450, 365);
					//Log.i(TAG, "isMove");
					if(!isRightTake && !showDialog){
						//Toast.makeText(Navigation.this, "Please take your phone in right diretion...", Toast.LENGTH_LONG).show();
						showDialog = true;
					}
					else if(isRightTake && !isLocate) {
						showDialog = false;
						boolean isMove =  (Boolean) intent.getSerializableExtra("SENSOR_MOVE");
						
							if(!isMoved && isMove){
								if(!detectTouchPanelThreadOpen) {
									//Log.i(TAG, "isMove!!!!!!!!!!!!!!");
									touchPanelHandler.postDelayed(mThreadtouchPanel, 300);
									detectTouchPanelThreadOpen = true;
								}	
							}
							else if(isMoved && !isMove) {
								isMoved = false;
							}
						
						
						/*
						//float value = (Float) intent.getSerializableExtra("GRAVITY_VALUE");
						//float[]  valueArray = (float[]) intent.getSerializableExtra("GRAVITY_ARRAY");
						boolean isMove =  (Boolean) intent.getSerializableExtra("SENSOR_MOVE");
						if(!isMoved && isMove ){
							Log.i(TAG, "isMove = "+isMove);
							
							if(!detectTouchPanelThreadOpen) {
								touchPanelHandler.postDelayed(mThreadtouchPanel, 100);
								detectTouchPanelThreadOpen = true;
							}							
						}
						else  if(isMoved && !isMove) {
								isMoved = false;
						}
						*/
					}
				}
				else if(intent.getAction().equals("onNavigationViewTouch")) {
					boolean isTouch =  (Boolean) intent.getSerializableExtra("TOUCH");
					if(isTouch) {
						isTouchedPanel = true;
					}
				}
				else if(intent.getAction().equals("onNavigationViewTouchOnePoint")) {
					popupWindow.onPanelTouchAction();
					//updateLocateToServer(naviView.getLocatePointPixelOnMap());
				}
				else if(intent.getAction().equals("onNavigationSensorRotate")) {
					current_azimuth =  (Float) intent.getSerializableExtra("SENSOR_ROTATE");
					//txTop.setText("\ncurrent azi: "+current_azimuth+"\n map_north azi: "+standard_azimuth);
					if(isNormalMode) {
						current_azimuth =  (Float) intent.getSerializableExtra("SENSOR_ROTATE");
						current_xy = naviView.getLocatePointPixelOnMap();
						//txTop.setText("\ncurrent azi: "+current_azimuth);//+"\n map_north azi: "+standard_azimuth);
						naviView.rotateCompass(current_azimuth);
						if(!isThreadRotate&&isLocated) {
							isThreadRotate = true;
							rotateThread();
						}
					}
					
					
					//WifiReferencePointProxy proxy = new WifiReferencePointProxy(Navigation.this);
					//proxy.initDB();
					//float[] xy = naviView.getLocatePointPixelOnMap();
					//float standard_azimuth = proxy.getAzimuthValue(xy[0], xy[1]);
					//txTop.setText("\ncurrent azi: "+azimuth+"\n map_north azi: "+standard_azimuth);
					//naviView.rotateItem(standard_azimuth,azimuth);
				}
				else if(intent.getAction().equals("onIBeaconServiceConnect")) {
					HashMap<String, IBeacon> beacons = (HashMap<String, IBeacon>) intent.getSerializableExtra("IBEACON_HASHMAP");
					scannedIbeacons.clear();
					scannedIbeacons.addAll(beacons.values());
					//seperateScannedIBeacons();
					if(currentMode == popupWindow.viewParking) {
						for(IBeacon Ibeacon:scannedIbeacons) {
							
							String ibeaconID = Ibeacon.getMajor()+"_"+Ibeacon.getMinor();
							if(ibeaconID.equals(plan.toParkingPlaceBeacon)&&Ibeacon.getRssi()>-75) {
								//isChangedToParking = true;
								if(!plan.currentMapName.equals(plan.mapNames[2])) {
									setCurrentIndoorPositionMapByIndex(2);
									startPositionIndex = 0;
									endPositionIndex = 0;
									popupWindow.closeNavigationMode();
								}
							}
						}
					}
				}
				else if(intent.getAction().equals("onWiFiScanDone")) {	
					boolean mapANdPointDifferent = false;
					if(!isLocate && !isHandChangeMap) {	
						if(indoorPosition.hasNewLocate) {
							if(indoorPosition.countLocatePosition(theNumberofPassedKNNLocatePosition)) {
								String s1 = plan.currentMapName;
								String s2 = ApplicationController.getInstance().getFloor();
								Log.i(TAG, "CurrentMApNAme = "+s2);
								if(!plan.currentMapName.equals(ApplicationController.getInstance().getFloor()) ) {
									mapANdPointDifferent = true;
									mapDifferentTime++;
									if(mapDifferentTime>changeMapTime) {
										mapDifferentTime = 0;
										if(currentMode == popupWindow.viewNavigation)
											detectChangeMap = true;
										else 
											naviView.locatePoint.setVisibility(naviView.locatePoint.INVISIBLE);
									}
								}
								else {
									mapDifferentTime = 0;
								}
									
								
								if(!detectChangeMap && !mapANdPointDifferent) {
									naviView.locatePoint.setVisibility(naviView.locatePoint.VISIBLE);
									//Toast.makeText(Navigation.this, "update Locate...", 100).show();
									
									float[] currentLocateXY = naviView.getLocatePointPixelOnMap();
									float[] knnXY = indoorPosition.getPositionByCounterTimes(theNumberofPassedKNNLocatePosition);
									
									float[] newLocateXY = new float[2];
									float distance = navigationCalculator.calculateDistanceOnLine(plan, currentLocateXY[0], currentLocateXY[1], knnXY[0], knnXY[1]);
									if(distance>theLimetMeterBetweenKNNAndLocatePoint && distance<30) {
											countKNNLocateOutOfLimit++;
									}	
									if(countKNNLocateOutOfLimit>KNNLocateOutOfLimitTimes) {
										newLocateXY = knnXY;
										countKNNLocateOutOfLimit = 0;
									}
									else {
										newLocateXY = currentLocateXY;
									}	
									if(!isNormalMode && isNavigating) {	
										float[] nearlyXY = navigationCalculator.findProjectionWithExistRoute(naviView.currentNavigationRoute, newLocateXY[0], newLocateXY[1]);
										float dist = (MathProxy.getDistance(newLocateXY, nearlyXY))/plan.currentMapPixelperMeter;
										if(dist<=5) {
											newLocateXY = nearlyXY;
										}
									}
									if(!isNavigating) {
										float[] knnInLineXY = navigationCalculator.findProjectionToLineInCurrentGraph(knnXY[0], knnXY[1]); 
										float dist = navigationCalculator.calculateDistanceOnLine(plan, newLocateXY[0], newLocateXY[1], knnInLineXY[0], knnInLineXY[1]);//(MathProxy.getDistance(newLocateXY, knnInLineXY))/plan.currentMapPixelperMeter;
										if(dist>=4 && dist<10) {
											newLocateXY = knnXY;
										}
									}
									//naviView.friend.setVisibility(naviView.friend.VISIBLE);
									//naviView.setItemToLocation(naviView.friend,knnXY[0], knnXY[1]);
									if(isLocated) {
										if(!isNormalMode) {
											if(plan.currentMapName.equals(ApplicationController.getInstance().getFloor())) {
												
												if(manInMove) {
													//Toast.makeText(Navigation.this,"manInMove = "+manInMove, 200).show();
													if(ApplicationController.isNeedToLocateToAPPoint) {
														naviView.setLocatePointXYToLine(ApplicationController.nearAPX,ApplicationController.nearAPY);
														ApplicationController.isNeedToLocateToAPPoint = false;
													}
													else
														naviView.setLocatePointXYToLine(newLocateXY[0], newLocateXY[1]);
													manInMove = false;
													if(currentMode == popupWindow.viewFriend) {
														naviView.locatePoint.setVisibility(naviView.locatePoint.INVISIBLE);
													}
												}
											}
										}
										else {											
												naviView.setLocatePointXY(newLocateXY[0], newLocateXY[1]);
										}
										if(plan.currentMapName.equals(ApplicationController.getInstance().getFloor())){
											updateLocateToServer(naviView.getLocatePointPixelOnMap());//newLocateXY[0], newLocateXY[1]);
										}
									}
									//indoorPosition.startScan();
									if(isTestingSitesurveyData) {
										naviView.friend.setVisibility(naviView.friend.VISIBLE);
										naviView.setItemToLocation(naviView.friend,ApplicationController.testPos_X,ApplicationController.testPos_Y);
										naviView.setLocatePointXYToLine(knnXY[0], knnXY[1]);
										updateLocateToServer(naviView.getLocatePointPixelOnMap());
									}
								}
								else {
									if(currentMode != popupWindow.viewFriend && currentMode != popupWindow.viewFoodAndShop)
										changeMapByMapName(ApplicationController.getInstance().getFloor());
									else
										detectChangeMap = false;
								} 
							}
							
						}		
						indoorPosition.startScan();
						
						
					}
					
				}
				else if(intent.getAction().equals("onRelocateDone")) {
					//if(isLocate) {
						//if(currentMode == popupWindow.viewParking) {
						if(!plan.currentMapName.equals(ApplicationController.getInstance().getFloor())||isParkingFirstOpen) {
							if(isParkingFirstOpen) {
								isParkingFirstOpen = false;
							}
							if((currentMode == popupWindow.viewNavigation)||(currentMode == popupWindow.viewParking)) {
								detectChangeMap = true;
								if(!plan.currentMapName.equals(ApplicationController.getInstance().getFloor())) {
									String sMap = ApplicationController.getInstance().getFloor();
									changeMapByMapName(ApplicationController.getInstance().getFloor());//.detectArea.currentArea);
								}
							}
						}
						
						if((currentMode == popupWindow.viewFoodAndShop)) {
							int targetMapIndex = plan.getMapIndexByName(currentNavData.Area);
							if(targetMapIndex!=-1) {
								//String floor = ApplicationController.getInstance().getFloor();
								if(!(ApplicationController.getInstance().getFloor().equals(plan.mapNames[targetMapIndex]))) {
									targetNotInSameMap = true;
									Toast.makeText(Navigation.this,getResources().getString(R.string.toast_str_please_go_to)+" "+plan.mapNames[targetMapIndex]+" "+getResources().getString(R.string.toast_str_will_arrive)+currentNavData.ItemName, Toast.LENGTH_LONG).show();
								}
							}							
						}
						else {
							targetNotInSameMap = false;
						}
							//}
						
						float[] xy = new float [2];
						//if(currentMode != popupWindow.viewParking)
						xy = indoorPosition.getNewLocateKNNPosition(); 
						if(!ApplicationController.knnPos_XAndYBelongFloor.equals(ApplicationController.getInstance().getFloor()))
						{
							naviView.locatePoint.setVisibility(naviView.locatePoint.INVISIBLE);
						}
						else {
							naviView.locatePoint.setVisibility(naviView.locatePoint.VISIBLE);
						}
						//else {
							//	xy[0] = ApplicationController.knnPos_X;
							//	xy[1] = ApplicationController.knnPos_Y;
						//}
						
						if(!isNormalMode) {
							if(ApplicationController.isNeedToLocateToAPPoint) {
								naviView.setLocatePointXYToLine(ApplicationController.nearAPX,ApplicationController.nearAPY);
								ApplicationController.isNeedToLocateToAPPoint = false;
							}
							else
								naviView.setLocatePointXYToLine(xy[0], xy[1]);
						}
						else {
							naviView.setLocatePointXY(xy[0], xy[1]);
						}
						
						
						if(plan.currentMapName.equals(ApplicationController.getInstance().getFloor())) {
							updateLocateToServer(naviView.getLocatePointPixelOnMap());//newLocateXY[0], newLocateXY[1]);
						}
						else
							naviView.locatePoint.setVisibility(naviView.locatePoint.INVISIBLE);
						
						//naviView.setItemToLocation(naviView.friend,xy[0], xy[1]);
						
						isLocated = true;
						if(isNeedRoute) {
							naviView.navToTargetFromCurrentPosition(currentEndPOINode.x,currentEndPOINode.y);
							naviView.targetpoint.setVisibility(naviView.targetpoint.VISIBLE);
							naviView.currentNaivStart.setVisibility(naviView.currentNaivStart.VISIBLE);
							float[] locateXY = naviView.getLocatePointPixelOnMap(); 
							naviView.setItemToLocation(naviView.currentNaivStart, locateXY[0], locateXY[1]);
							isNeedRoute = false;
						}
						if(currentMode == popupWindow.viewParking) {
							//naviView.locatePoint.setVisibility(naviView.locatePoint.INVISIBLE);
							
							/*
							POI endPOINode = plan.currentParkingElevator;
							currentEndPOINode = endPOINode;
							naviView.navToTargetFromCurrentPosition(currentEndPOINode.x,currentEndPOINode.y);
							naviView.targetpoint.setVisibility(naviView.targetpoint.VISIBLE);
							naviView.currentNaivStart.setVisibility(naviView.currentNaivStart.VISIBLE);
							naviView.route.setVisibility(naviView.route.VISIBLE);
							isNavigating = true;
							*/
						}
						if(isNeedTOFoodorShop) {
							if(!targetNotInSameMap) {
								isNeedTOFoodorShop = false;
								naviView.navToTargetFromCurrentPosition(currentEndPOINode.x,currentEndPOINode.y);
								naviView.targetpoint.setVisibility(naviView.targetpoint.VISIBLE);
								naviView.friend.setVisibility(naviView.friend.INVISIBLE);
								naviView.currentNaivStart.setVisibility(naviView.currentNaivStart.VISIBLE);
								//float[] locateXY = naviView.getLocatePointPixelOnMap(); 
								//naviView.setItemToLocation(naviView.currentNaivStart, locateXY[0], locateXY[1]);
								isNavigating = true;
							}
							else {
								naviView.route.setVisibility(naviView.route.INVISIBLE);
						 		naviView.targetpoint.setVisibility(naviView.targetpoint.INVISIBLE);
						 		naviView.currentNaivStart.setVisibility(naviView.currentNaivStart.INVISIBLE);
							}
						}
						if(isNavigating) {
							
			        		isNextTarget = false;
			        		currentNavigationRouteTargetIndex = 1;
							naviView.navToTargetFromCurrentPosition(currentEndPOINode.x,currentEndPOINode.y);
							float[] locateXY = naviView.getLocatePointPixelOnMap(); 
							naviView.setItemToLocation(naviView.currentNaivStart, locateXY[0], locateXY[1]);
							naviView.setItemToLocation(naviView.targetpoint, currentEndPOINode.x, currentEndPOINode.y);
			        		
							rotateLocatePointToDirectTarget();
						}
						if(targetNotInSameMap) {
							targetNotInSameMap = false;
						}
						else {	
							indoorPosition.startScan();
						}
					//}
						
						if(plan.currentMapName.equals(ApplicationController.getInstance().getFloor()))
							naviView.pointCenter();
						isLocate = false;
				}
			}
		};
		this.registerReceiver(mReceiver, mFilter);
	}
	float[] current_xy =new float[2];// = naviView.getLocatePointPixelOnMap();
	float standard_azimuth =0;
	
	private void rotateThread() {
		
		new Thread() {
			@Override
			public void run() {
				//WifiReferencePointProxy proxy = new WifiReferencePointProxy(Navigation.this);
				//proxy.initDB();
				standard_azimuth = plan.currentStandard_azimuth;//proxy.getAzimuthValue(current_xy[0], current_xy[1]);
				isThreadRotate = false;
				if(!once){
				rotateHandler.sendEmptyMessage(0);
				//once = true;
				}
			}
		}.start();
	}
	private boolean once = false;
	private Handler rotateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//testRotateHandler.postDelayed(testrotateThread, 4000);
			//naviView.rotateItem(standard_azimuth,current_azimuth);
			naviView.rotateItem(plan.currentStandard_azimuth,current_azimuth);
			//txTop.setText("\ncurrent azi: "+current_azimuth+"\n map_north azi: "+standard_azimuth);
			
		}
	};
	
	private void seperateScannedIBeacons() {
		this.scannedPushMessageIBeacons.clear();
		this.scannedPositionIBeacons.clear();
		for(IBeacon Ibeacon:scannedIbeacons) {
			
			String ibeaconID = Ibeacon.getMajor()+"_"+Ibeacon.getMinor();
			Log.i(TAG, "BEACON "+ibeaconID);
			if(this.plan.MessageCollection.containsKey(ibeaconID)) {
				this.scannedPushMessageIBeacons.add(Ibeacon);
			}
			else if(this.plan.PositionCollection.containsKey(ibeaconID)) {
				this.scannedPositionIBeacons.add(Ibeacon);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( currentMode == this.popupWindow.viewNavigation) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_navigation, menu);
			return super.onCreateOptionsMenu(menu);
		}
		return super.onCreateOptionsMenu(null);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_selectfloor:
	        	isTouchedPanel = true;
	        	changeFloorDialog();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private int floorSelection;
	private void changeFloorDialog() {
		floorSelection = -1;
		final AlertDialog.Builder singlechoicedialog = new AlertDialog.Builder(this);
		singlechoicedialog.setTitle("Map");
		singlechoicedialog.setSingleChoiceItems(this.plan.mapNames, -1,new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int item) {
			 floorSelection = item;
			 currentMapIndex = item;
		 }
		});
		
		singlechoicedialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(floorSelection!=-1) {
					isHandChangeMap = true;
					isTouchedPanel = true;
					indoorPosition.stopScan();
					setCurrentIndoorPositionMapByIndex(floorSelection);
					startPositionIndex = 0;
					endPositionIndex = 0;
					popupWindow.closeNavigationMode();
					
					
				}
				//Toast.makeText(Navigation.this, "select!  "+floorSelection, Toast.LENGTH_SHORT).show();
			}
		});
	    	        
		singlechoicedialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				isTouchedPanel = true;
	    	        	}
	    });  
		AlertDialog alert_dialog = singlechoicedialog.create();
		alert_dialog.show();
	}
	
	private void setCurrentIndoorPositionMapByIndex(int index) {
		this.plan.setPlan(index);
		this.pre_azimuth = plan.currentStandard_azimuth;
		this.naviView.currentMapName = plan.mapNames[index];
		//this.pushMessageManager.setPushMessageCollection(this.plan.MessageCollection);
		indoorPosition.changeMap(this.plan);
    	//firstStart = true;
       	this.setTitle(plan.mapNames[index]);
    	initMapById(plan.mapIDs[index]);
    	if(detectChangeMap && !isHandChangeMap) {
    		//detectChangeMap = false;
    		
    		this.startLocate();
    		//indoorPosition.beaconRelocateToPosition();
    	}
    	//if(isLocate)//currentMode == popupWindow.viewParking)
    		//indoorPosition.beaconRelocateToPosition();//.startScan();
    	//this.pListRunner();		
	}

	private void initMapById(int MapId) {
		naviView.route.invalidate();
		naviView.route.setImageDrawable(null);
		//Bitmap bitmap = BitmapFactory.decodeResource(getResources(), MapId);
		if(isNormalMode)
			naviView.setNavigationPic(MapId, R.drawable.point2,R.drawable.sail_arrow,R.drawable.pin,R.drawable.icon_navi_end,R.drawable.icon_navi_start,this.plan.currentMapPixelperMeter,this.plan.currentStandard_azimuth,this.plan.currentRoadDeviation);		
		else {
			//if(isInitial) {
				naviView.setNavigationPic(MapId, R.drawable.point3,R.drawable.sail_arrow,R.drawable.pin,R.drawable.icon_navi_end,R.drawable.icon_navi_start,this.plan.currentMapPixelperMeter,this.plan.currentStandard_azimuth,this.plan.currentRoadDeviation);		
			//	isInitial = false;
			//}
			//else
				//naviView.changePic(MapId, R.drawable.point3,R.drawable.arrow,R.drawable.pin,R.drawable.icon_navi_end,R.drawable.icon_navi_start,this.plan.currentMapPixelperMeter,this.plan.currentStandard_azimuth,this.plan.currentRoadDeviation);
		}
	}
	
	public void closeNavigation() {
		this.naviView.friend.setVisibility(this.naviView.friend.INVISIBLE);
		
		this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.INVISIBLE);
		this.naviView.currentNaivStart.setVisibility(this.naviView.currentNaivStart.INVISIBLE);
		this.naviView.route.setVisibility(this.naviView.route.INVISIBLE);
		if(!isNormalMode) {
			//this.naviView.changeLocatePointPicture(R.drawable.point3);
			this.isNavigating = false;
			this.isNextTarget = false;
    		this.currentNavigationRouteTargetIndex = 1;
    		
		}
		if(this.currentMode == this.popupWindow.viewFoodAndShop) {
			this.naviView.friend.setVisibility(this.naviView.friend.VISIBLE);
		}
	}
	public void openNavigation() {
		//this.naviView.friend.setVisibility(this.naviView.friend.VISIBLE);
		//this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.VISIBLE);
		this.naviView.route.setVisibility(this.naviView.route.VISIBLE);
	}
	
	private POI currentEndPOINode ;
	private boolean isFirstTimeToGo = false;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		isFromonActivityResult  = true;
        switch(requestCode){
        case REQUSET_CODE_ROUTESETTING :
        	if(resultCode!=0) {
        		
        		startPositionIndex = data.getExtras().getInt("start");
        		endPositionIndex = data.getExtras().getInt("end");
        		POI endPOINode = plan.POICollection.get(endPositionIndex);
        		currentEndPOINode = endPOINode;
        		String s = "Start: ";
        		boolean notNavigating = false;
        		if(startPositionIndex>0) {
        			s += this.plan.POICollection.get(startPositionIndex-1).POIName+"\n";
        			POI startPOINode = plan.POICollection.get(startPositionIndex-1);
        			naviView.navToTargetFromStartPOI(startPOINode.x, startPOINode.y, endPOINode.x, endPOINode.y);
        			this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.VISIBLE);
        			this.naviView.currentNaivStart.setVisibility(this.naviView.currentNaivStart.VISIBLE);
        			this.naviView.setItemToLocation(this.naviView.currentNaivStart, startPOINode.x, startPOINode.y);
        			this.naviView.setItemToLocation(this.naviView.targetpoint, endPOINode.x, endPOINode.y);
        			
        			notNavigating = true;
        		}
        		else {
        			s += "Current Position\n";
        			if(isLocated) {
        				naviView.navToTargetFromCurrentPosition(endPOINode.x,endPOINode.y);
        				this.naviView.targetpoint.setVisibility(this.naviView.targetpoint.VISIBLE);
            			this.naviView.currentNaivStart.setVisibility(this.naviView.currentNaivStart.VISIBLE);

						indoorPosition.beaconRelocateToPosition();
        			}
        			else {
        				currentEndPOINode = endPOINode;
        				isNeedRoute = true;
        				if(!isHandChangeMap)
        					startLocate();
        			}
        			notNavigating = false;
        		}
        		
        		//this.naviView.setItemToLocation(this.naviView.targetpoint, endPOINode.x, endPOINode.y);
        		
        		
        		this.isNextTarget = false;
        		this.currentNavigationRouteTargetIndex = 1;
        		if(!isNormalMode){
        			//this.naviView.changeLocatePointPicture(R.drawable.point2);
        			isFirstTimeToGo = true;
        			this.rotateLocatePointToDirectTarget();
        			if(!notNavigating)
        				this.isNavigating = true;
        		}
        		s += "End: "+this.plan.POICollection.get(endPositionIndex).POIName;
        		//Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        		this.popupWindow.changeToNavigationMode(this.plan.POICollection.get(endPositionIndex).POIName);
        	}
        	
        break;
        }
	}
	
	private Runnable mThreadFriendChange = new Runnable() {
		@Override
		public void run() {
			friendChangeHandler.sendEmptyMessage(0);			
		}		
	}; 
	
	private Handler friendChangeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(!isStopSearchFriend) {
				getFriendLocation();
			}
		}
	};
	
	private void getFriendLocation() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getFriendLocation BEGIN");
		//apiRequestDialog = ProgressDialog.show(this, "", getResources().getString(R.string.progress_dialog_msg_get_friend_location));
		AES aes = new AES();
		
		String strApiUrl =  "http://bigcity-rtls.doubleservice.com/api/bigcity/";//"http://218.211.88.196/api/BigCity/";
		String uuid = ApplicationController.getInstance().getUuid();
		Log.i(TAG, "getFriendLocation by phone: " +ApplicationController.navData.FriendTel);
		String mPhone = ApplicationController.navData.FriendTel;
		try {
			mPhone = aes.encrypt_AES(mPhone);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "getFriendLocation");
		mMap.put("UUID", uuid);
		mMap.put("friendPhone", mPhone);
		Log.i(TAG, "send data = " +mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(strApiUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("getFriendLocation", "onResponse response =" +response.toString() 
								+ "forgroundActivity = "+Navigation.this);
						//if(forgroundActivity.equals(forgroundActivities[0])) {
							getFriendLocation(true, response);
						//} else if(forgroundActivity.equals(forgroundActivities[1])) {
							//mNavActivity.getFriendLocation(true, response);
						//}
						
					}}, new Response.ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("getFriendLocation", "onErrorResponse error = " +error.getMessage());
							//mFriendListActivity.getFriendLocation(false, null);
							//if(forgroundActivity.equals(forgroundActivities[0])) {
								getFriendLocation(false, null);
							//} else if(forgroundActivity.equals(forgroundActivities[1])) {
								//mNavActivity.getFriendLocation(true, response);
							//}
						}},
				mMap );
		
		ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
		
	}
	public void getFriendLocation(boolean isDone, JSONObject response) {
		AES aes = new AES();
		//apiRequestDialog.dismiss();
		if (isDone) {
			try {
				JSONObject obj = new JSONObject(response.getString("data"));
				if (obj != null) {

					// JSONObject dataContent = obj.getJSONObject(i);
					String strFlag = "";
					String phone = "";
					String posX = "";
					String posY = "";
					String floor = "";

					try {

						strFlag = obj.getString("flag");
						//phone = obj.getString(aes.decrypt_AES("phone"));
						phone = aes.decrypt_AES_ParsingSomeChar(obj.getString("phone"));
						posX = obj.getString("pointX");
						posY = obj.getString("pointY");
						floor = obj.getString("floor");

						if (strFlag.equals("y")) {
							Log.i(TAG, "getFriendLocation flag = " + strFlag
									+ " phone = " + phone + "posX = " + posX
									+ "posY = " + posY);
							updateFriendLocation(Float.parseFloat(posX),Float.parseFloat(posY),floor);
							
						} else if (strFlag.equals("n")) {
							//getFriendLocationFail();
						}

					} catch (Exception e) {
						e.printStackTrace();
						//getFriendLocationFail();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				//getFriendLocationFail();
			}
		} else {
			//getFriendLocationFail();
		}
	}
	
	private void updateFriendLocation(float x,float y,String floor) {
		if(!plan.currentMapName.equals(floor)) {
			changeMapByMapName(floor);
		}
		this.naviView.setItemToLocation(this.naviView.friend, x*2, y*2);
		this.naviView.friend.setVisibility(this.naviView.friend.VISIBLE);
		//friendChangeHandler.postDelayed(mThreadFriendChange, 30000);
	}




	
	/*
	public Runnable scanAnalyze = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG, "setCurrentLocationRssi BEGIN");
			
			
			indoorPosition.isScanning = false;
			
			if (indoorPosition.isWiFi) {
				if((indoorPosition.knnWifiReceiver != null))
					Navigation.this.unregisterReceiver(indoorPosition.knnWifiReceiver);
			}
			else  {
				if((indoorPosition.knnBeaconReceiver != null))
				{
					try{
						Navigation.this.unregisterReceiver(indoorPosition.knnBeaconReceiver);
					}
					catch(Exception e){
						
					}
				}
					
			}
			indoorPosition.detectArea.analyzeCurrentArea(indoorPosition.scanList,indoorPosition.mApScanRssi);
			currentFloor = indoorPosition.detectArea.currentArea;
			
			ArrayList<HashMap> list = new ArrayList<HashMap>();
			Log.i(TAG, "setCurrentLocationRssi BREAK1");
			list = proxy.getDistance(indoorPosition.scanDataList);//.queryReferencePointDis(indoorPosition.scanDataList);
			Log.i(TAG, "setCurrentLocationRssi BREAK99");
			indoorPosition.knnSortingDistanceStrongToWeak(list);
			
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
			
				NaviNode newPosition = new NaviNode("",(int)newPositionX,(int)newPositionY);//200,200);//(int)newPositionX,(int)newPositionY);//this.newPositionX,this.newPositionY);
				if(indoorPosition.passedKnnPosition.size()>indoorPosition.passedPositionLimitTimes) {
					indoorPosition.passedKnnPosition.remove(0);
				}
				
				indoorPosition.passedKnnPosition.add(newPosition);
				//Log.i(TAG, "passedKnnPosition.size = "+indoorPosition.passedKnnPosition);
				indoorPosition.hasNewLocate = true;
			}
			else
				indoorPosition.hasNewLocate = false;
		
			scanAnalyzeHandler.sendEmptyMessage(0);
		}
		
	};
	
	public Handler scanAnalyzeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//proxy.closeDB();
			if(!indoorPosition.isRelocate) {
				indoorPosition.broadcastIndoorPositionState("onWiFiScanDone");
			}
			indoorPosition.isAnalyze = false;
		}
	};
	
/*
	private Handler mMoveOnceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			moveOnce = false;
		}
	};
	
	private Runnable mThreadmoveOnce = new Runnable() {
		@Override
		public void run() {
			mMoveOnceHandler.sendEmptyMessage(0);			
		}		
	};
	*/
	
	/*
	public void initActionBar(){
		//getAllMapsName();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, this.plan.mapNames);
 
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setDisplayShowTitleEnabled(false);
 
        ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
 
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            	//File f = new File("/sdcard/Double_Service/" + "Map_Image_"+ strSpinnerContent[itemPosition] + ".jpg");
            	
            	//mAPList = siteSurveyAPIProxy.getAPList();
            	isTouchedPanel = true;
            	//isTouchPanel();
            	plan.setPlan(itemPosition);
            	
            	//currentFloor = strSpinnerContent[itemPosition]; 	
            	naviView.currentMapName = plan.mapNames[itemPosition];// allMaps[itemPosition];
            	pushMessageManager.setPushMessageCollection(plan.MessageCollection);
            	firstStart = true;
            	
            	initMapById(plan.mapIDs[itemPosition]);
            	//Uri uri =  Uri.fromFile(f);
            	/*
            	if( uri != null ) {
            		initImageview(uri);
 	            }
 	            else {
 	            	Toast.makeText(Navigation.this, getResources().getString(R.string.string_no_map), Toast.LENGTH_LONG).show();
 	            }
            	*/
            	//initSpinner();
            	/*
                return false;
            }
        };
        getActionBar().setListNavigationCallbacks(adapter, navigationListener);
	}
	*/
	
	
	/*
	private void getAllMapsName(){
    	
    	//strSpinnerContent = new String[navigatanioProxy.mMapImageList.size()];
    	allMaps = new String[this.plan.mapIDs.length];
    	for(int i=0;i<allMaps.length;i++){//navigatanioProxy.mMapImageList.size();i++){
    		//String strTmp = navigatanioProxy.mMapImageList.get(i).split("###")[0];
    		allMaps[i] = this.plan.mapNames[i];//strTmp;
    	}
    }
	*/
	
	/*
	OnClickListener btnGetPositionListener  = new OnClickListener(){
		@Override  
        public void onClick(View v){
			switch(v.getId()){
			case R.id.btnGetPosition:
				startLocate();
				//isLocate = true;
				//indoorPosition.relocateToPosition();
				//isTouchedPanel = true;//isTouchPanel();
				break;
			}
		}
	};
	*/
	
	/*
	public void initSpinner(){
		  Log.v("Spinner","init Spinner");
		  Spinner spinner = (Spinner) findViewById(R.id.spinnerNav);
		   //嚙賣�嚙賢�鞈∴蕭���耍rrayAdapter�都嚙賡３嚙踝蕭嚙賢砥嚙賢�鞈�蕭嚙賢�嚙踝蕭豯株�釭撗唾��寡嚙踐��鳴蕭�
		  String[] spinnerTx = new String[plan.POICollection.size()];
		  for(int index=0;index<spinnerTx.length;index++) {
			  POI poiNode = plan.POICollection.get(index);
			  spinnerTx[index] = poiNode.POIName;
		  }  
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spinnerTx);
	        //嚙賣�嚙賢�鞈∴蕭嚙踐筑�剖�鞈芸眾雓�哨蕭�剁蕭嚙踐���
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        //嚙賣�嚙賢�頦嚙賣２嚙踝蕭�伐蕭頩��剛�曈渲嚙賜飭謑啣�頦嚙踐筑��
	        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
	            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
	               Log.v("Spinner","isStart: "+firstStart+", index: "+position);
	               if(!firstStart) {
	            	   POI poiNode = plan.POICollection.get(position);
	            	   naviView.navToTarget(poiNode.x,poiNode.y);
	            	   isTouchedPanel = true;//isTouchPanel();
	            	   //naviView.navToTarget(Integer.parseInt(poiNode.getID()));
	               }
	               else
	            	   firstStart = false;
	            }
	            public void onNothingSelected(AdapterView arg0) {
	               
	            }
	        });
	       
	  }
	*/
	//private Handler backgroundLocateHandler = new Handler();
	//private int backgroundLocateTime = 10000; 
	
	/*	
	private Runnable backgroundLocateRunner = new Runnable() {
		@Override
		public void run() {
			if(!isLocate) {		
				if(indoorPosition.countLocatePosition(passedCountNumber)) {
					float[] currentLocateXY = naviView.getLocatePointPixelOnMap();
					float[] knnXY = indoorPosition.getPositionByCounterTimes(passedCountNumber);
					float[] newLocateXY = new float[2];
					float distance = navigationCalculator.calculateDistanceOnLine(plan, currentLocateXY[0], currentLocateXY[1], knnXY[0], knnXY[1]);
					
					if(distance>locateDistanceMeterLimit) {
						countKNNLocateOutOfLimit++;
					}
					if(countKNNLocateOutOfLimit>KNNLocateOutOfLimit) {
						newLocateXY = knnXY;
						countKNNLocateOutOfLimit = 0;
					}
					else {
						newLocateXY = currentLocateXY;
					}
					naviView.setItemToLocation(naviView.friend,knnXY[0], knnXY[1]);
					naviView.setLocatePointXY(newLocateXY[0], newLocateXY[1]);	
				}				
			}			
			backgroundLocateHandler.postDelayed(this, backgroundLocateTime);
		}
		
	};
	*/
	
	/*
	private void initImageviewByUri(Uri uri){
		
		Bitmap bitmap; 
		try {
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			Log.v("imageURI",uri.getPath());
			naviView.setNavigationPic(bitmap , R.drawable.point2,R.drawable.arrow,R.drawable.friendpoint,this.plan.currentMapPixelperMeter,this.plan.currentStandard_azimuth);
			txTop =(TextView) findViewById(R.id.top_textview);
			naviView.route.invalidate();
			naviView.route.setImageDrawable(null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
		
	private void testLocatePoint() {
		Log.i(TAG, "===============================");
		
		//float[] currentLocateXY = this.naviView.getLocatePointPixelOnMap();
		//Log.i(TAG, "currentLocateXY x = "+currentLocateXY[0]+",y = "+currentLocateXY[1]);
		//this.naviView.setItemToLocation(this.naviView.friend, currentLocateXY[0], currentLocateXY[1]);
		//this.naviView.friend.LogMatrix();
		//this.naviView.setLocatePointXY(currentLocateXY[0], currentLocateXY[1]);//.setItemToLocation(naviView.locatePoint,locateNewXY[0], locateNewXY[1]);//
		//this.naviView.locatePoint.LogMatrix();
		//float[] currentLocateXY2 = this.naviView.getLocatePointPixelOnMap();
		
		//Log.i(TAG, "currentLocateXY2 x = "+currentLocateXY2[0]+",y = "+currentLocateXY2[1]);
	
		
		float randomLimit = 3*this.plan.currentMapPixelperMeter;
		float[] testXY = new float[2];
		float[] currentLocateXY = this.naviView.getLocatePointPixelOnMap();
		float mapHeight = naviView.map.imageHeightOrigin;
		float mapWidth = naviView.map.imageWidthOrigin;
		int xDirection,yDirection;
		
		if(((Math.random()*100000)%2)<1)
			xDirection = 1;
		else
			xDirection = -1;
		if(((Math.random()*100000)%2)<1)
			yDirection = 1;
		else
			yDirection = -1;
		//Log.i(TAG, "map height = "+mapHeight+",width = "+mapWidth);
		
		testXY[0] = currentLocateXY[0]+((float) ((Math.random()*10000)%randomLimit)*xDirection);
		testXY[1] = currentLocateXY[1]+((float) ((Math.random()*10000)%randomLimit)*yDirection);
		Log.i(TAG, "===============================");
		//Log.i(TAG, "current X = "+currentLocateXY[0]+", Y = "+currentLocateXY[1]);
		//Log.i(TAG, "X direction = "+xDirection+", Y = "+yDirection);
		//Log.i(TAG, "test X = "+testXY[0]+", Y = "+testXY[1]);
		
		if(testXY[0]<0)
			testXY[0] = 0;
		if(testXY[0]>mapWidth) {
			testXY[0] = mapWidth;
		}
		if(testXY[1]<0)
			testXY[1] = 0;
		if(testXY[1]>mapHeight) {
			testXY[1] = mapHeight;
		}
		float[] locateNewXY = this.navigationCalculator.calculateLocateToNewCoordinate(this.plan,currentLocateXY[0], currentLocateXY[1],
				testXY[0],testXY[1],5,10);
		this.naviView.setLocatePointXY(locateNewXY[0], locateNewXY[1]);//.setItemToLocation(naviView.locatePoint,locateNewXY[0], locateNewXY[1]);//
		this.naviView.setItemToLocation(naviView.friend, testXY[0], testXY[1]);
		Log.i(TAG, "currentLocateXY x = "+currentLocateXY[0]+",y = "+currentLocateXY[1]);
		Log.i(TAG, "locateNewXY x = "+locateNewXY[0]+",y = "+locateNewXY[1]);
			 
	}
	private AlertDialog dmAlert;
	
	private void showTakeRightDirection() {
		Builder builder = new AlertDialog.Builder(this);
		dmAlert  = builder.create();
		dmAlert.setTitle(this.getResources().getString(R.string.hotzmsg_title));
		dmAlert.setMessage("Please take your phone in right diretion...");
		dmAlert.setCancelable(false);
		dmAlert.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// isDMShow = true;
				dmAlert.dismiss();
				showDialog = false;
			}
		});
		dmAlert.show();
	}
	*/
	
}

