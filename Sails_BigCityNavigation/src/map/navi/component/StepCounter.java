package map.navi.component;

import java.util.ArrayList;

import com.doubleservice.bigcitynavigation.R;


import lib.locate.algorithm.Math.component.LinearLine;
import lib.locate.algorithm.Math.component.Point;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class StepCounter {
	
	private Activity activity;
	private AlertDialog dmAlert;
	private String TAG = "StepCounter";
	private boolean showDialog = false;
	//private float preY = 0,preZ = 0;
	private float preY = 0,preZ = 0;
	private boolean initial = true;
	private float highThreshold =0f,lowThreshold=0f;
	private float lowX,lowY,lowZ;
	private float y_highThreshold = 1.2f; 
	private float y_lowThreshold = -1.2f;
	private float z_highThreshold = 1.2f;
	private float z_lowThreshold = -1.4f;
	
	public float[] value ;
	private boolean isOver = false;
	private boolean countAccelationSquareRoot = false;
	private boolean isThreadOpen = false;
	private float accelationSquareRoot ;
	private int noShakeCounter = 0;
	private float preAccelationSquareRoot =0.98f;
	private float[] gravity = new float[3],linear_acceleration = new float[3];
	
	
	
	public StepCounter(Activity activity) {//Activity activiy) {
		this.activity = activity;
		value = new float[3];
	}
	
	private void showTakeRightDirection(String message) {
		Builder builder = new AlertDialog.Builder(this.activity);
		dmAlert  = builder.create();
		dmAlert.setTitle("hotzmsg_title");
		dmAlert.setMessage(message);
		dmAlert.setCancelable(false);
		dmAlert.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// isDMShow = true;
				dmAlert.dismiss();
				showDialog = false;
			}
		});
		if(!showDialog) {
			dmAlert.show();
			showDialog = true;
		}
	}
	private long time = System.currentTimeMillis();
	private float pre_Roll =0f;
	
	
	/*
	private Handler mShakeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if(deltaZ<DELTAZ_LIMIT) {
				noShakeCounter++;
				if(noShakeCounter>2) {
					isOver = false;
					noShakeCounter = 0;	
				}
			}
			
			if(isOver) {
				mShakeHandler.postDelayed(mThreadShake, 100);
			}
			else {
				isThreadOpen = false;
			}
		}
	};
	
	private Runnable mThreadShake = new Runnable() {
		@Override
		public void run() {
			mShakeHandler.sendEmptyMessage(0);			
		}		
	};
	
	
	private boolean mInitialized = false;
	private float mLastX ,mLastY,mLastZ;
	private final float NOISE = 1f;
	private final float DELTAZ_LIMIT = 3f;
	private double deltaX,deltaY,deltaZ;
	public boolean detectStep(float[] mGravity) {
		float x = mGravity[0];
        float y = mGravity[1];
        float z = mGravity[2];
        //float[] linear_acceleration = new float[3];
        
        //gravity[0] = 0;
        //gravity[1] = 0;
        //gravity[2] = 0;
        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

        linear_acceleration[0] = x - gravity[0];
        linear_acceleration[1] = y - gravity[1];
        linear_acceleration[2] = z - gravity[2];
        //Log.i(TAG, "deltaZ = "+linear_acceleration[0]+", deltaX = "+linear_acceleration[1]+", deltaY = "+linear_acceleration[2]);
        //Log.i(TAG, "linear Z = "+linear_acceleration[2]);
		
		if (!mInitialized) {
		 // sensor is used for the first time, initialize the last read values
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
		} 
		else {
			deltaX = Math.abs(mLastX - x);
			deltaY = Math.abs(mLastY - y);
			deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE)
				deltaX = (float) 0.0;
			if (deltaY < NOISE)
				deltaY = (float) 0.0;
			if (deltaZ < NOISE)
				deltaZ = (float) 0.0;	 
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			
			if (deltaX > deltaY) { // Horizontal shake
				return false;
			} 
			else if (deltaY > deltaX) {// Vertical shake
				return false;
			} 
			else if ((deltaZ > deltaX) && (deltaZ > deltaY)) {// Z shake
				//Log.i(TAG, "deltaZ = "+deltaZ+", deltaX = "+deltaX+", deltaY = "+deltaY);
				//Log.i(TAG, "linear X = "+linear_acceleration[0]+", linear Y = "+linear_acceleration[1]+", linear Z = "+linear_acceleration[2]);
				
				//if(deltaZ>this.DELTAZ_LIMIT){// ||deltaZ<NOISE) {
					//if(deltaZ>this.DELTAZ_LIMIT) {
						//if(!isThreadOpen) {
							//isOver = true;
			        		//isThreadOpen = true;
			        		//mShakeHandler.postDelayed(mThreadShake, 100);
			        	//}
					//}
					//return false;
				//}
				//else {//if(!isOver){
					return true;
				//}
			} 
			else { // no shake detected
			 return false;
			}
		}
		return false;
	}
	*/
	
	private Runnable mThreadtouchPanel = new Runnable() {
		@Override
		public void run() {
			handler.sendEmptyMessage(0);			
		}		
	};
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			isInDetectPreiod = false;
			isDetectComplete = true;
		}
	};
	
	private ArrayList<Float> passedAccelation = new ArrayList<Float>();
	private int passedSize = 6;
	private float average = 0;
	private float HeightLimit =100, HeightestLimit =200,LowLimit=98;
	private boolean preHeight = false;
	private float preHeightCounter = 0,preHeightCounterLimit = 5;
	private float passedHeight = 0;
	private boolean isShake = false;
	private float dynamicHightLimit = 0;
	private ArrayList<Float> passedHightLimit = new ArrayList<Float>();
	private float maxAverage =0;
	private boolean isInDetectPreiod = false;
	private boolean isDetectComplete = false;
	public boolean detectStep2(float[] mGravity) {
		
		float x = mGravity[0];
        float y = mGravity[1];
        float z = mGravity[2];
     
        float accelation = (x * x+ y * y + z * z);
           
        if(passedAccelation.size()<passedSize) {
        	passedAccelation.add(accelation);
        	
        }
        else {
        	for(int index=0;index<passedAccelation.size();index++) {
        		average += passedAccelation.get(index);
        	}
        	average /= passedAccelation.size();
        	//Log.i(TAG, "average = "+average);
        	/*
        	if(average >(HeightLimit) && !isInDetectPreiod){//&& average<HeightestLimit) {
    			//if(preHeight) {
    				//preHeightCounter++;
    			//}
        		Log.i(TAG, "average = "+average);
        		isInDetectPreiod = true;
        		maxAverage = average;
        		handler.postDelayed(mThreadtouchPanel, 250);
        		//passedHeight = average;
        	}
        	else if(isInDetectPreiod) {
        		if(average>maxAverage) {
        			maxAverage = average;
        		}
        	}
        	
        	if(isDetectComplete) {
        		isDetectComplete = false;
        		
        		if(maxAverage<HeightestLimit) {
        			return true;
        		}
        	}
    		average = 0;
    		passedAccelation.clear();
    		return false;
        	*/
        	
        	if(preHeight && average<LowLimit) {
        		//Log.i(TAG, "passedHeight = "+passedHeight+", average = "+average+", dynamicHightLimit = "+dynamicHightLimit);
        		passedHightLimit.add(Math.abs(passedHeight-HeightLimit));
        		
        		if(passedHightLimit.size()>100) {
        			dynamicHightLimit = 0;
        			for(int index=0;index<passedHightLimit.size();index++) {
        				dynamicHightLimit += passedHightLimit.get(index);
        			}
        			dynamicHightLimit /= passedHightLimit.size();
        			passedHightLimit.clear();
        		}
        		
        		average = 0;
            	passedAccelation.clear();
            	preHeight = false;
            	if(preHeightCounter>preHeightCounterLimit) {
            		preHeightCounter = 0;
            		return false;
            	}
            	else
        		 return true;
        	}
        	else {
        		if(average >(HeightLimit) && average<HeightestLimit) {
        			if(preHeight) {
        				preHeightCounter++;
        			}
        			preHeight = true;
            		passedHeight = average;
            	}
        		
        		average = 0;
        		passedAccelation.clear();
        		return false;
        	}
        	
        	
        	
        	
        	//if()
        }
        return false;
	}
	
	
	public boolean detectStep(float[] mGravity) {
		
        float[] value = this.filter(mGravity);
        float y_G = value[1],z_G = value[2];
       
        if(!(((int)y_G)>0||((int)y_G)<0) ) {//&& !(((int)z_G)>0||((int)z_G)<0)) { 
        	 preY = y_G;
         	preZ = z_G;
        		//return false;
        }
        else if(!(preY>0 && y_G<0)){//!(preZ>0 && z_G<0)&& ) {
        	 preY = y_G;
         	preZ = z_G;
        	//return false;
    	}
        preY = y_G;
    	preZ = z_G;
        
      
		
		float x = mGravity[0];
        float y = mGravity[1];
        float z = mGravity[2];
        //this.filter(mGravity);
        
        
       // long seconds = System.currentTimeMillis()-time;
       // time = System.currentTimeMillis();
       // float roll_dis = pre_Roll -z;
       // pre_Roll = z;
    //	float mOrientation_x = mOrientation[0];//mGravity[0];
     //   float mOrientation_y = mOrientation[1];//mGravity[1];
      //  float mOrientation_z = mOrientation[2];//mGravity[2];
            //Log.i(TAG, "seconds = " +seconds+", pre roll"+pre_Roll+", roll"+mOrientation[2]);
            //if(seconds<5000 && ) {
            	
            //}
           
        
        accelationSquareRoot = (x * x+ y * y + z * z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);//(x * x + y * y + z * z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        countAccelationSquareRoot = true;
        if(accelationSquareRoot>2f&& z>20) {
        	//this.showTakeRightDirection("isOVER!!!\n"+value+"\nx = "+mGravity[0]+"\ny = "+mGravity[1]+"\nz = "+mGravity[2]);
    		isOver = true;
    	}
        
       // Log.i("StepCounter","x = "+value[0]+",y = "+value[1]+",z = "+value[2]);
        //float YZ = ( y * y + z * z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        if(accelationSquareRoot>1.1f && accelationSquareRoot<2f && z<20 &&z>=1){//&&y>2.6 ) {
        	
        	if(!isOver) {
        		//if()
        		//this.showTakeRightDirection(""+accelationSquareRoot+"\nx = "+mGravity[0]+"\ny = "+mGravity[1]+"\nz = "+mGravity[2]+"" +
        			//	"\nx = "+value[0]+"\ny = "+value[1]+"\nz = "+value[2]+
        				//"\nx = "+linear_acceleration[0]+"\ny = "+linear_acceleration[1]+"\nz = "+linear_acceleration[2]);
        		gravity[0] = 0;
        		gravity[1] = 0;
        		gravity[2] = 0;
        		//this.showTakeRightDirection(""+accelationSquareRoot+"\nx = "+mGravity[0]+"\ny = "+mGravity[1]+"\nz = "+mGravity[2]+ "\nseconds = " +seconds+"\nrollDis = "+roll_dis);
        		
        		return true;
        	}
        	else
        		return false;
        }	
       // else {
        	//this.showTakeRightDirection("No Step\n"+accelationSquareRoot);
        //}
        if(isOver) {
        	if(!isThreadOpen) {
        		isThreadOpen = true;
        		mShakeHandler.postDelayed(mThreadShake, 100);
        	}
        }
    	 preY = y_G;
      	preZ = z_G;
      	
        
		return false;
	}
	
	//private float[] gravity = new float[3],linear_acceleration = new float[3];
	
	private float[] filter(float[] mGravity) {
		float x = mGravity[0];
        float y = mGravity[1];
        float z = mGravity[2];
        //float[] linear_acceleration = new float[3];
        
        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

        linear_acceleration[0] = x - gravity[0];
        linear_acceleration[1] = y - gravity[1];
        linear_acceleration[2] = z - gravity[2];
        
        
        float[] value = new float[3];
        value[0] = gravity[0];
        value[1] = gravity[1];
        value[2] = gravity[2];
        this.value = value;
		return value;
	}
	
	/*
	private float[] filter(float[] mGravity) {
		
		float x = mGravity[0];
        float y = mGravity[1];
        float z = mGravity[2];
        float FILTERING_VALAUE = 0.1f;
        lowX = x * FILTERING_VALAUE + lowX * (1.0f - FILTERING_VALAUE);
        lowY = y * FILTERING_VALAUE + lowY * (1.0f - FILTERING_VALAUE);
        lowZ = z * FILTERING_VALAUE + lowZ * (1.0f - FILTERING_VALAUE);

        //High-pass filter
        float highX = x - lowX;
        float highY = y - lowY;
        float highZ = z - lowZ;
        
        float[] value = new float[3];
        value[0] = highX;
        value[1] = highY;
        value[2] = highZ;
        this.value = value;
		return value;
	}
	*/
		private Handler mShakeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if(countAccelationSquareRoot) {
				if(accelationSquareRoot<0.99) {
					noShakeCounter++;
					if(noShakeCounter>1) {
						isOver = false;
						noShakeCounter = 0;
						
					}
				}
			}
			countAccelationSquareRoot = false;
			if(isOver) {
				mShakeHandler.postDelayed(mThreadShake, 100);
			}
			else {
				isThreadOpen = false;
			}
		}
	};
	
	private Runnable mThreadShake = new Runnable() {
		@Override
		public void run() {
			mShakeHandler.sendEmptyMessage(0);			
		}		
	};
	

	
}
