package map.navi.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.radiusnetworks.ibeacon.IBeacon;


import lib.locate.KNN.IBeaconScanReceiver;
import lib.locate.KNN.WifiFingerPrintReceiver;
import lib.locate.KNN.WifiReferencePointProxy;
import lib.locate.KNN.WifiReferencePointVO;
import map.navi.Data.IBeaconLocateData;
import map.navi.Data.IBeaconLocatePlan;
import map.navi.Data.NaviNode;
import map.navi.Data.NaviPlan;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NavigationIndoorPosition{

	public Navigation activity;
	private String TAG = "NavigationIndoorPosition";
	private Collection<IBeacon> positionIbeacons ;
	private NaviPlan plan;
	public WifiManager wiFiManager ;
	public WifiFingerPrintReceiver knnWifiReceiver;
	public lib.locate.KNN.IBeaconScanReceiver knnBeaconReceiver;
	public int[] mApScanRssi;
	public  ArrayList<String> scanList;
	
	//public int positionX = 0,positionY = 0;
	
	private Handler wifiScanBackgroundHandler = new Handler(),floorScanHandler = new Handler();
	private int backgroundScanTime = 1000; 
	public boolean isScanning = false;
	public ArrayList<NaviNode> passedKnnPosition = new ArrayList<NaviNode>();
	public int passedPositionLimitTimes = 1;
	private ProgressDialog pd;
	private int relocateSaveTimes = 1;
	public boolean isRelocate = false;
	private boolean startScanning = false;
	public boolean isWiFi = false;
	public boolean hasNewLocate = true;
	//public IBeaconLocatePlan detectArea ;
	//private WifiReferencePointProxy proxy;// = new WifiReferencePointProxy(activity);
	//public String currentFloor="";//,scanedFloor="";
	public ArrayList<HashMap> scanDataList;
	public boolean isAnalyze = false;
	public boolean beaconStop = false; 
	private long preTime = 0;
	private boolean isLocateFault = false;
	//public WifiReferencePointProxy proxy;
	public NavigationIndoorPosition(Navigation activity,NaviPlan plan) {
		this.activity = activity;
		positionIbeacons = new ArrayList<IBeacon>();
		this.plan = plan;
		//detectArea = new IBeaconLocatePlan();
		if(isWiFi)
			wiFiManager = (WifiManager) this.activity.getSystemService(this.activity.WIFI_SERVICE);
		
		//currentFloor = detectArea.currentArea;
		//scanedFloor = detectArea.currentArea;
		//proxy = new WifiReferencePointProxy(activity);
		//proxy.initDB();
		
		//else
			//ApplicationController.getInstance().onIBeaconServiceStart();
		//wifiScanBackgroundHandler.postDelayed(wifiScanBackgroundRunner, backgroundScanTime);
		//startScanning = false;
		//this.activity.registerReceiver(knnWifiReceiver, new IntentFilter(
				//WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	
	public void changeMap(NaviPlan plan) {
		this.plan = plan;
		
	}
	
	private boolean relocateSuccess = false;
	private boolean isLocateNow = false;
	public void beaconRelocateToPosition() {
		// TODO Auto-generated method stub
		if(!isLocateNow) {
			isLocateNow = true;
			pd = ProgressDialog.show(this.activity, "", "Locate Position , please wait ...");//this.getResources().getString(R.string.progress_load));		
			//this.locateOnce = false;
			//indoorPosition.reLocatePosition();
			isRelocate = true;
			relocateSuccess = false;
			//this.passedKnnPosition.clear();
			preTime = System.currentTimeMillis();
			Log.i(TAG, "set PreTimes = " +(preTime));
			beaconHandlerRelocatePosition.postDelayed(beaconRelocateToPosition, 100);
		}
	}
		
	
	private Runnable beaconRelocateToPosition = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Log.i(TAG,"countLocatePosition(relocateSaveTimes) = "+countLocatePosition(relocateSaveTimes) );
			if(ApplicationController.knnPos_X!=0 && ApplicationController.knnPos_Y!=0){
				relocateSuccess = true;
				if(passedKnnPosition.size()>passedPositionLimitTimes) {
					passedKnnPosition.remove(0);
				}		
				Log.i(TAG,"App X = "+ApplicationController.knnPos_X+",App y = "+ApplicationController.knnPos_Y);
				NaviNode newPosition = new NaviNode("",ApplicationController.knnPos_X,ApplicationController.knnPos_Y);//this.newPositionX,this.newPositionY);
				
				passedKnnPosition.add(newPosition);
			}
			
			if(relocateSuccess == true) {
				relocateSuccess = false;
				isLocateFault = false;
				beaconHandlerRelocatePosition.sendEmptyMessage(0);
			}
			else {
				//Log.i(TAG, "Times = " +(System.currentTimeMillis()-preTime));
				if(!((System.currentTimeMillis()-preTime)>15000)) {
					beaconHandlerRelocatePosition.postDelayed(this, 100);
				}
				else {
					Toast.makeText(ApplicationController.getInstance(),activity.getResources().getString(R.string.toast_str_locate_out_of_time), Toast.LENGTH_LONG).show();
					isLocateFault = true;
					beaconHandlerRelocatePosition.sendEmptyMessage(0);
				}
			}
		}
		
	};
	
	private Handler beaconHandlerRelocatePosition = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			isLocateNow = false;
			isRelocate = false;
			
			//Toast.makeText(activity, "you are in :" +ApplicationController.getInstance().getFloor(), Toast.LENGTH_SHORT).show();
			//Log.i(TAG, "CURRENT FLOOR!!!!!!!="+detectArea.currentArea);
			//if(relocateSuccess) {
				//relocateSuccess = false;
			if(!isLocateFault)
				broadcastIndoorPositionState("onRelocateDone");
			else
				isLocateFault = false;
			//}
			//else
				//beaconHandlerRelocatePosition.postDelayed(beaconRelocateToPosition, 100);//ibeaconBackgroundGetPositionHandler.postDelayed(ibeaconBackgroundGetPosition, 100);//ibeaconStartScan();
		}
	};
	
	public void startScan() {
		if(!startScanning) {
			if(isWiFi) {
				if(knnWifiReceiver == null) {
					knnWifiReceiver = new WifiFingerPrintReceiver(this,1);
				}
				startScanning = true;
				wifiScanBackgroundHandler.postDelayed(wifiScanBackgroundRunner, backgroundScanTime);
			}
		}
		beaconStop = false;
		if(!isRunScan)
			ibeaconBackgroundGetPositionHandler.postDelayed(ibeaconBackgroundGetPosition, 300);
	}
	
	private boolean isRunScan = false;
	
	
	private Runnable ibeaconBackgroundGetPosition = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Log.i(TAG,"countLocatePosition(relocateSaveTimes) = "+countLocatePosition(relocateSaveTimes) );
			//if(ApplicationController.knnPos_X!=0 && ApplicationController.knnPos_Y!=0){
			//if(!isRunScan) {
				//if(ApplicationController.knnPos_X!=0 && ApplicationController.knnPos_Y!=0) {
					//isRunScan = true;
				isRunScan = true;
					NaviNode newPosition = new NaviNode("",ApplicationController.knnPos_X,ApplicationController.knnPos_Y);//this.newPositionX,this.newPositionY);
				
					if(passedKnnPosition.size()>passedPositionLimitTimes) {
						passedKnnPosition.remove(0);
					}		
					passedKnnPosition.add(newPosition);
					isRunScan = false;
				//Log.i(TAG, "passedKnnPosition.size = "+passedKnnPosition);
					hasNewLocate = true;
					broadcastIndoorPositionState("onWiFiScanDone");
					//locateSuccess = true;
				//}
				//else
					//locateSuccess = false;
				//}
				
				ibeaconBackgroundGetPositionHandler.sendEmptyMessageDelayed(0, 100);
			
				
			//}
		}
		
	};
	
	private Handler ibeaconBackgroundGetPositionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//Log.i(TAG, "CURRENT FLOOR!!!!!!!="+detectArea.currentArea);
			//Toast.makeText(ApplicationController.getInstance(), "floor:"+ApplicationController.getInstance().getFloor()+",\ncurrent DB = "+WifiReferencePointVO.DBName
			//		+",new X = "+ApplicationController.knnPos_X+", new Y = "+ApplicationController.knnPos_Y, 300).show();
			//isRunScan = false;
			
			//Toast.makeText(ApplicationController.getInstance(), "new X = "+ApplicationController.knnPos_X+", new Y = "+ApplicationController.knnPos_Y, 1000).show();
			
			//if(locateSuccess)
				
			//if(!beaconStop)
				//handlerRelocatePosition.postDelayed(ibeaconBackgroundGetPosition, 1000);
		}
	};
	
	public void stopScan() {
		startScanning = false;
		beaconStop = true; 
	}
	
	public void changeFloorSetting() {
		//knnWifiReceiver = null;
	}
	boolean isRegisted = false; 
	private Runnable wifiScanBackgroundRunner = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isScanning && startScanning){
				if(isWiFi) {
					activity.registerReceiver(knnWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
					wiFiManager.startScan();
				}
				//else {
					//if(!isRegisted) {
					//	activity.registerReceiver(knnBeaconReceiver, new IntentFilter("onIBeaconServiceConnect"));
						//isRegisted = true;
					//}
				//}
				isScanning = true;
			}
			if(startScanning) {
				wifiScanBackgroundHandler.postDelayed(this, backgroundScanTime);
				//Toast.makeText(activity, "is Scanning",backgroundScanTime-1000).show();
			}
		}
	};

	private Runnable scanCurrentFloorBackgroundRunner = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//if(!currentFloor.equals(scanedFloor)) {
				//broadcastIndoorPositionState("onFloorChange");
				//currentFloor = scanedFloor;
			//}
			//if(startScanning) {
			floorScanHandler.postDelayed(this, backgroundScanTime);
				//Toast.makeText(activity, "is Scanning",backgroundScanTime-1000).show();
			//}
		}
	};
	
	
	
	public void setSiteSurveyRssiData(int[] mApScanRssi) {
		this.mApScanRssi = mApScanRssi;
	}
	/*
	public void setCurrentLocation(float newPositionX,float newPositionY,int[] rssi) {
		this.mApScanRssi = rssi;
		this.isScanning = false;
		this.activity.unregisterReceiver(knnWifiReceiver);
		NaviNode newPosition = new NaviNode("",(int)newPositionX,(int)newPositionY);//this.newPositionX,this.newPositionY);
		if(this.passedKnnPosition.size()>this.passedPositionLimitTimes) {
			this.passedKnnPosition.remove(0);
		}
		this.passedKnnPosition.add(newPosition);
		if(!isRelocate) {
			this.broadcastIndoorPositionState("onWiFiScanDone");
		}
	}
	*/
	
	private Runnable scanAnalyze = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG, "setCurrentLocationRssi BEGIN");
			
			if(ApplicationController.getInstance().getFloor().equals("BigCity")) {
				//WifiReferencePointVO.DBName = "/sdcard/"+"bc_bigcity_referencepoint_db.db";
			}
			else {
				//WifiReferencePointVO.DBName = "/sdcard/"+"sogo_bigcity_referencepoint_db.db";
			}
			//WifiReferencePointProxy proxy = new WifiReferencePointProxy(activity);
			//proxy.initDB();
			
			isScanning = false;
			
			if (isWiFi) {
				if((knnWifiReceiver != null))
					activity.unregisterReceiver(knnWifiReceiver);
			}
			else  {
				if((knnBeaconReceiver != null))
				{
					try{
						activity.unregisterReceiver(knnBeaconReceiver);
					}
					catch(Exception e){
						
					}
				}
					
			}
			//detectArea.analyzeCurrentArea(scanList,mApScanRssi);
			//currentFloor = detectArea.currentArea;
			/*
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
				if(passedKnnPosition.size()>passedPositionLimitTimes) {
					passedKnnPosition.remove(0);
				}
				
				passedKnnPosition.add(newPosition);
				
				Log.i(TAG, "passedKnnPosition.size = "+passedKnnPosition);
				hasNewLocate = true;
			}
			else
				hasNewLocate = false;
			*/
			
			
			//scanAnalyzeHandler.sendEmptyMessage(0);
		}
		
	};
	/*
	private Handler scanAnalyzeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//proxy.closeDB();
			if(!isRelocate) {
				broadcastIndoorPositionState("onWiFiScanDone");
			}
			isAnalyze = false;
		}
	};
	*/
	/*
	public void setCurrentLocationRssi(int rssi[], final ArrayList<HashMap> scanDataList) {
		mApScanRssi = rssi;
		scanList = knnBeaconReceiver.mApName;
		
		this.scanDataList = scanDataList;
		
		if(!isAnalyze) {
			isAnalyze = true;
			scanAnalyzeHandler.postDelayed(scanAnalyze, 1000);
		}
		/*
		new Thread() {
			@Override
			public void run() {
				
				Log.i(TAG, "setCurrentLocationRssi BEGIN");
				
				
				isScanning = false;
				
				if (isWiFi) {
					if((knnWifiReceiver != null))
						activity.unregisterReceiver(knnWifiReceiver);
				}
				else  {
					//if((knnBeaconReceiver != null))
						//this.activity.unregisterReceiver(knnBeaconReceiver);
				}
				detectArea.analyzeCurrentArea(scanList,mApScanRssi);
				currentFloor = detectArea.currentArea;
				
				ArrayList<HashMap> list = new ArrayList<HashMap>();
				list = proxy.queryReferencePointDis(scanDataList);
				//knnSortingDistanceStrongToWeak(list);
				/*
				Collections.sort(list, new Comparator<HashMap>() {
					@Override
					public int compare(HashMap lhs, HashMap rhs) {
						// TODO Auto-generated method stub
						return ((Integer) rhs.get(WifiReferencePointVO.DISTANCE) < (Integer) lhs
								.get(WifiReferencePointVO.DISTANCE) ? 1 : -1);
					}
				});
				*/
				/*
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
				*//*
				//float[] newPositionXY = new float[]{newPositionX, newPositionY};
					NaviNode newPosition = new NaviNode("",200,200);//(int)newPositionX,(int)newPositionY);//this.newPositionX,this.newPositionY);
					if(passedKnnPosition.size()>passedPositionLimitTimes) {
						passedKnnPosition.remove(0);
					}
					passedKnnPosition.add(newPosition);
					//hasNewLocate = true;
				//}
				//else
					hasNewLocate = false;
					
				if(!isRelocate) {
					broadcastIndoorPositionState("onWiFiScanDone");
				}
				/*
				
				Message msg = new Message();
				msg.what = MESSAGE_GET_POSITION;
				msg.obj = newPositionXY;

				posHandler.sendMessage(msg);
				*/
		/*
				proxy.closeDB();
			}

		}.start();
		*/
	//}

	/*
	Handler posHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_GET_POSITION:
				float[] positionXY = (float[])msg.obj;
				String strToastRssiMsg = "";
				for (int i = 0; i < scannedRssi.length; i++) {
					strToastRssiMsg = strToastRssiMsg + "RSSI" + i + " = " + scannedRssi[i]
							+ "\n";
				}
				
				Toast.makeText(position_1F.this, strToastRssiMsg, Toast.LENGTH_LONG)
				.show();
				
				float[] arrayPointM = new float[9];
				imageViewHelper.matrixPoint.getValues(arrayPointM);
				float[] pointXYTrans = transPointPixelToTrans(positionXY[0], positionXY[1]);

				arrayPointM[2] = Float.valueOf(pointXYTrans[0] - bitmapPoint.getWidth()
						* arrayPointM[0] / 2);
				arrayPointM[5] = Float.valueOf(pointXYTrans[1]
						- bitmapPoint.getHeight() * arrayPointM[4] / 2);
				imageViewHelper.matrixPoint.reset();
				imageViewHelper.matrixPoint.setValues(arrayPointM);
				imageViewPoint.setImageMatrix(imageViewHelper.matrixPoint);
				break;
			}
		}
	};
	*/
	
	
	
	
	
	
	public boolean countLocatePosition(int counterNumber) {
		//Log.i(TAG, "passedPosition.size() = "+passedPosition.size());
		if(this.passedKnnPosition.size()>=counterNumber) {
			return true;
		}
		return false;
	}
	
	public void setPositionIBeaconCollection(Collection<IBeacon> positionCollection) {
		this.positionIbeacons = positionCollection;
	}
	
	public float[] getNewLocateKNNPosition() {
		return this.getPositionByCounterTimes(this.relocateSaveTimes);
	}
	
	public float[] getPositionByCounterTimes(int counterTimes) {
		float[] xy = new float[2];
		if(counterTimes>this.passedKnnPosition.size()) {
			counterTimes = this.passedKnnPosition.size();
		}
		
		for(int index = this.passedKnnPosition.size()-counterTimes;index<this.passedKnnPosition.size();index++) {
			xy[0] = xy[0]+this.passedKnnPosition.get(index).x;
			xy[1] = xy[1]+this.passedKnnPosition.get(index).y;
		}
		if(counterTimes >0) {
			xy[0] /= counterTimes;
			xy[1] /= counterTimes;
		}	
		if(counterTimes==1) {
			xy[0] = this.passedKnnPosition.get(0).x;
			xy[1] = this.passedKnnPosition.get(0).y;
		}
		return xy;
	}
	
	public void relocateToPosition() {
		// TODO Auto-generated method stub
		pd = ProgressDialog.show(this.activity, "", "Locate Position , please wait ...");//this.getResources().getString(R.string.progress_load));		
		//this.locateOnce = false;
		//indoorPosition.reLocatePosition();
		isRelocate = true;
		//this.passedKnnPosition.clear();
		handlerRelocatePosition.postDelayed(relocateToPosition, 100);
	}
	
	private Runnable relocateToPosition = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Log.i(TAG,"countLocatePosition(relocateSaveTimes) = "+countLocatePosition(relocateSaveTimes) );
			if(countLocatePosition(relocateSaveTimes))
				handlerRelocatePosition.sendEmptyMessage(0);
			else
				handlerRelocatePosition.postDelayed(this, 100);
		}
		
	};
	
	private Handler handlerRelocatePosition = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			isRelocate = false;
			
			//Toast.makeText(activity, "you are in :" +ApplicationController.getInstance().getFloor(), Toast.LENGTH_SHORT).show();
			//Log.i(TAG, "CURRENT FLOOR!!!!!!!="+detectArea.currentArea);
			if(isRunScan)
				broadcastIndoorPositionState("onRelocateDone");
			//else
				//ibeaconStartScan();
		}
	};
	
	public void broadcastIndoorPositionState(String broadcastAction) {
		Intent i = new Intent();
		i.setAction(broadcastAction);
		activity.sendBroadcast(i);
	}
	
	public static void knnSortingDistanceStrongToWeak(ArrayList<HashMap> list) {
		
		/*
		 * 
		 * Collections.sort(list, new Comparator<HashMap>() {
					@Override
					public int compare(HashMap lhs, HashMap rhs) {
						// TODO Auto-generated method stub
					
						return ((Integer) rhs.get(WifiReferencePointVO.DISTANCE) < (Integer) lhs
								.get(WifiReferencePointVO.DISTANCE) ? 1 : -1);
					}
				});
		 */
		
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
