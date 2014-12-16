package lib.locate.Ibeacon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class ApplicationController extends Application implements IBeaconConsumer{

	private static ApplicationController mInstance;
	private HashMap<String, IBeacon> beacons;
	private IBeaconManager iBeaconManager;
	private String TAG = "ApplicationController";
	private RequestQueue mRequestQueue;
	
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		mInstance = this;
	}

	public static synchronized ApplicationController getInstance() {
		return mInstance;
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
		if(iBeaconManager == null) {
			this.iBeaconManager = IBeaconManager.getInstanceForApplication(this);
			this.iBeaconManager.bind(this);
		}
	}
	
	public void onIBeaconServiceStop() {
		if(iBeaconManager != null) {
			this.iBeaconManager.unBind(this);
		}
	}
	
	@Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setRangeNotifier(new RangeNotifier() {
        @Override 
        public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
        	Iterator localIterator = null;
        	if (iBeacons.size() > 0) {
                //Log.i("TAG", "The first iBeacon I see is about "+iBeacons.iterator().next().getAccuracy()+" meters away.");       
                beacons = new HashMap<String, IBeacon>();
				localIterator = iBeacons.iterator();
				while (true) {
					
					if( !localIterator.hasNext()) {
						break ;
					}
					
					IBeacon localIBeacon = (IBeacon) localIterator.next();
					String str = Integer.toString(localIBeacon.getMajor())+ "_"
							+ Integer.toString(localIBeacon.getMinor());
					if (beacons.containsKey(str))
						continue;
					beacons.put(str, localIBeacon);
					
					//setIBeaconDetails();
					

				}
				Intent i = new Intent();
				i.setAction("onIBeaconServiceConnect");
				i.putExtra("IBEACON_HASHMAP", beacons);
				sendBroadcast(i);
            }
        }
        });

        try {
            iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

	
    
}
