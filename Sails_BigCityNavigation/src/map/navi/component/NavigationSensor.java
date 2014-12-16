package map.navi.component;


import java.util.ArrayList;
import java.util.HashMap;

import lib.locate.KNN.WifiReferencePointVO;
import map.navi.Data.NaviNode;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NavigationSensor implements SensorEventListener {

	private Activity activity;
	private String TAG = "NavigationSensor";
	private SensorManager sensorMgr;
	private float[] mGravity; 
	private final float IS_MOVE_LIMIT = 1.4f;
	private final float IS_MOVE_VALUE = 1.2f;
	private final float IS_STAY = 1f;
	public float azimuth =0;
	private float lowX,lowY,lowZ;
	public boolean isMove = false;
	public float[] mOrientation = new float[3];
	private StepCounter stepCounter ;
	private long preTime;
	public NavigationSensor(Activity activity) {
		this.activity = activity;
		
		sensorMgr = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
		sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
		sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
		
		mGravity = new float[3];
		mGravity[0] = 0;
		mGravity[1] = 0;
		mGravity[2] = 0;
		//mHandlerTime.postDelayed(timerRun,3000);
		String strSensor  = "Name: " + sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getName()
		        + "\nVersion: " + String.valueOf(sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getVersion())
		        + "\nVendor: " + sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getVendor()
		        + "\nType: " + String.valueOf(sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getType())
		        + "\nMax: " + String.valueOf(sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getMaximumRange())
		        + "\nResolution: " + String.valueOf(sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getResolution())
		        + "\nPower: " + String.valueOf(sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getPower())
		        + "\nClass: " + sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getClass().toString();
		//Log.v("sensor Info", strSensor);
		 stepCounter = new StepCounter(activity);
		 preTime = System.currentTimeMillis();
	}
	
	private Runnable stepRunner = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(0);
		}
	};
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			Intent accelerometerIntent = new Intent();
            accelerometerIntent.setAction("onNavigationSensorMove");
            accelerometerIntent.putExtra("RIGHT_PHONE_TAKE", phoneIntHandISRightDirection());            
            accelerometerIntent.putExtra("SENSOR_MOVE", stepCounter.detectStep(mGravity));
		}
	};
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		switch (event.sensor.getType()) {
        	case Sensor.TYPE_ACCELEROMETER:
        		mGravity = event.values.clone();   
        		//new Thread() {
        			//@Override
        			//public void run() {
        				
               		 float x = mGravity[0];
                        float y = mGravity[1];
                        float z = mGravity[2];
                       
                        float accelationSquareRoot = (x * x+ y * y + z * z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);//(x * x + y * y + z * z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
                       // Log.i(TAG, "========================================");
                        
                        //Log.i(TAG, "Gravity x = "+x+",y = "+y+",z = "+z);
                        //Log.i(TAG, "accelationSquareRoot = "+accelationSquareRoot);
                        //Log.i(TAG, "========================================");
                        
                        Intent accelerometerIntent = new Intent();
                        accelerometerIntent.setAction("onNavigationSensorMove");
                        accelerometerIntent.putExtra("RIGHT_PHONE_TAKE", phoneIntHandISRightDirection());
                        accelerometerIntent.putExtra("GRAVITY_VALUE", accelationSquareRoot);
                        accelerometerIntent.putExtra("GRAVITY_ARRAY",stepCounter.value);//value);// mGravity);
                        
                        //stepCounter.detectStep2(mGravity);
                        
                        accelerometerIntent.putExtra("SENSOR_MOVE", stepCounter.detectStep(mGravity));//detectStep(mGravity,mOrientation));
                        /*if(accelationSquareRoot >= IS_MOVE_VALUE && accelationSquareRoot<=IS_MOVE_LIMIT) {
                       	 isMove = true;
                       	 accelerometerIntent.putExtra("SENSOR_MOVE", true);
                		 }
                        else if(accelationSquareRoot<=IS_STAY){
                      	  	isMove = false;
                      	  	accelerometerIntent.putExtra("SENSOR_MOVE", false);
                        }
                        else {
                       	 accelerometerIntent.putExtra("SENSOR_MOVE", isMove);
                       	// accelerometerIntent.putExtra("MOVE_CHANGE", false);
                        }
                        */
                   	 activity.sendBroadcast(accelerometerIntent);
        			//}

        		//}.start();
        		
        	break;
        	case Sensor.TYPE_ORIENTATION:
        		//pitch = event.values[1];
        		azimuth = event.values[0];
        		this.mOrientation = event.values.clone();
        		//Log.i(TAG, "mOrientation x = "+mOrientation[0]+",y = "+mOrientation[1]+", z = "+mOrientation[2]);
        		//Log.i(TAG,"pitch = "+ event.values[1]);
        		//if((System.currentTimeMillis()-preTime)>500) {
        			Intent i = new Intent();
        			i.setAction("onNavigationSensorRotate");
        			i.putExtra("SENSOR_ROTATE", azimuth);
        			activity.sendBroadcast(i);
        			preTime = System.currentTimeMillis();
        		//}
        		
        	//txTop.setText("嚙賣�嚙踝蕭�蕭嚙賢音嚙賢�謇�" + event.values[0]);// + ", Y - "+ event.values[1] + ", Z - " + event.values[2]);
        	break;
    	}
	}
	
	private long time = System.currentTimeMillis();
	private float pre_Roll =0f;
	
	public boolean phoneIntHandISRightDirection() {
		long seconds = System.currentTimeMillis()-time;
		float x = mOrientation[0];//mGravity[0];
        float y = mOrientation[1];//mGravity[1];
        float z = mOrientation[2];//mGravity[2];
        //Log.i(TAG, "seconds = " +seconds+", pre roll"+pre_Roll+", roll"+mOrientation[2]);
        //if(seconds<5000 && ) {
        	
        //}
        time = System.currentTimeMillis();
        pre_Roll = z;
        if(y<-5&&y>-75 && z>-20&&z<20) {
        	//Log.i(TAG, "x = "+x+", y = "+y+",z = "+z);
            
        	return true;
        }
        else
        	return false;
		//if(y>-3 && z>1 && x>-6&& x<6) {
			//return true;
		//}
		//else
        
			//return false;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	//private NavigationView naviView;
	
	/*
    private final Runnable timerRun = new Runnable() {
	    public void run() {
	      mHandlerTime.postDelayed(this,100);
	      float x = mGravity[0];
          float y = mGravity[1];
          float z = mGravity[2];
          float accelationSquareRoot = (x * x + y * y + z * z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
          if(accelationSquareRoot >= IS_MOVE_VALUE) {
        	  isMove = true;
        	  Intent i = new Intent();
        	  i.setAction("onNavigationSensorMove");
        	  i.putExtra("SENSOR_MOVE", true);
        	  activity.sendBroadcast(i);
  		  }
          else {
        	  isMove = false;
          }
	   }
	};
	*/
}
