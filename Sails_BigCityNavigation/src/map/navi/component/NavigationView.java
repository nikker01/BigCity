package map.navi.component;

import java.util.ArrayList;
import java.util.LinkedList;

import com.doubleservice.bigcitynavigation.graph.Node;

import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.BitmapLruCache;
import map.navi.Data.NaviNode;
import map.navi.Data.NaviPlan;
import map.navi.Data.POI;
import map.navi.PathAlgorithmn.DijkstraAlgorithm;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class NavigationView extends FrameLayout {
	private static String TAG = "NavigationView";
	
	private Context mContext;
	private Matrix pre_matrix_map = new Matrix(),pre_matrix_locatePoint = new Matrix(),
				   pre_matrix_route = new Matrix(),pre_matrix_friend = new Matrix(),
				   pre_matrix_target = new Matrix(),pre_matrix_navistart = new Matrix();
	private Matrix locatePointMatrixOnLine = new Matrix(),pre_locatePointMatrixOnLine = new Matrix();
	public NaviViewItem map,locatePoint,compass,route,friend,targetpoint,currentNaivStart;
	//private NavigationSensor sensor;
	//private Handler mHandleSensorTime = new Handler();
	private NavigationCalculator naviCaculator;
	
	public float azimuth = 0f;
	public static float standard_azimuth = 0f;  
	private static  float stepDistance = 0f;
	private float stepConstant = 0.6f; 
	private float mapPixelPerMeter = 0f;
	private float currentRoadDeviation = 0f;
	
	private float minScaleR ;
	private float mapLoadScale;
	private PointF preTouch = new PointF();
	private LinkedList currentNavigationPath;
	private int currentTargetID;
	public static PointF mid = new PointF();
	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public static int mode = NONE;
	private  float zoomPointDist = 0f;
	
	private static DisplayMetrics dm;
	private drawLineHelper mDrawLine;  
	//public NaviPlan plan;
	private int currentEdge =0;//-1;
	public String currentMapName ="";
	
	private float lastAzimuth,lastDegree;
	private float compass_LastAzimuth = 0;
	private float compass_LastDegree = 0;
	public boolean isTouch = false;
	private boolean isDrag = false,isZoom = false;
	private boolean fromTouchToNoTouch = false;
	public LinkedList<Node> currentNavigationRoute = new LinkedList<Node>();
	
	
	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		dm = new DisplayMetrics();
		lastAzimuth = this.standard_azimuth;
		lastDegree = 0;
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		initialImageView();
		//sensor = new NavigationSensor(((Activity)context));
		//mHandleSensorTime.postDelayed(getSensorState,100);
	}
		
	public void setNavigationCalculator(NavigationCalculator naviCaculator) {
		this.naviCaculator = naviCaculator;
	}
	
	public void setNavigationPic(int mapID,int locatePointID,int compassID,int friendID,int targetID,int currentNaviStartID,float mapPixelPerMeter,float standard_azimuth,float road_deviation) {
		cleanAllView();
		this.stepDistance = mapPixelPerMeter*stepConstant;
		this.currentRoadDeviation = road_deviation;
		this.mapPixelPerMeter = mapPixelPerMeter;
		this.standard_azimuth = standard_azimuth;
		initialImageView();
		this.map.setImage(mapID);
		this.mapID = mapID;
		this.locatePoint.setImage(locatePointID);
		this.friend.setImage(friendID);
		this.targetpoint.setImage(targetID);
		this.currentNaivStart.setImage(currentNaviStartID);
		this.compass.setImage(compassID);
		minZoom();
		setPicInitial();	
	}
	
	public void changePic(int mapID,int locatePointID,int compassID,int friendID,int targetID,int currentNaviStartID,float mapPixelPerMeter,float standard_azimuth,float road_deviation) {
		//cleanAllView();
		this.stepDistance = mapPixelPerMeter*stepConstant;
		this.currentRoadDeviation = road_deviation;
		this.mapPixelPerMeter = mapPixelPerMeter;
		this.standard_azimuth = standard_azimuth;
		//initialImageView();
		this.map.setImage(mapID);
		this.mapID = mapID;
		this.locatePoint.setImage(locatePointID);
		this.friend.setImage(friendID);
		this.targetpoint.setImage(targetID);
		this.currentNaivStart.setImage(currentNaviStartID);
		this.compass.setImage(compassID);
		setPicInitial();	
		
	}
	
	public void changeLocatePointPicture(int changeID) {
		float[] xy = this.getLocatePointPixelOnMap();
		this.locatePoint.setImage(changeID);
		this.setLocatePointXY(xy[0], xy[1]);
	}

	private void cleanAllView() {
		this.removeAllViews();
		this.compass_LastAzimuth = 0;
		this.compass_LastDegree = 0;
		this.azimuth = 0;
	}
	
	public void rotateCompass(float azimuth) {
		float degree = azimuth;//this.standard_azimuth;
		///*
		if(Math.abs(degree)>180) { 
				degree = 360- degree;
		}
		else
			degree =  0 - degree;
		this.compass.matrix.preRotate(degree-compass_LastDegree,this.compass.imageWidth/2,this.compass.imageHeight/2);
		this.compass.updateImageMatrix();
		
		//rotateAnimation animate = new rotateAnimation(this.compass,degree-compass_LastDegree);
		//this.compass.startAnimation(animate);
		compass_LastDegree += (degree-compass_LastDegree);
		if(Math.abs(compass_LastDegree)>180) {
			if(compass_LastDegree<-180)
				compass_LastDegree+=360;
			else 
				compass_LastDegree-=360;
		}
		compass_LastAzimuth = azimuth;
		//*/
		/*
		this.compass.matrix.preRotate(-1*azimuth,this.compass.imageWidth/2,this.compass.imageHeight/2);
		this.compass.updateImageMatrix();
		*/
	}
	
	public void rotateCurrentPoint() {
		float degree = (azimuth-this.standard_azimuth);
	
	/*		this.setLocatePointMatrixToLine();
			this.locatePoint.matrix.preRotate(degree,this.locatePoint.imageWidth/2,this.locatePoint.imageHeight/2);
			this.locatePoint.updateImageMatrix();
			if(!isTouch) {
				lastDegree = degree;
				lastAzimuth = azimuth;
				fromTouchToNoTouch = false;
			}
		*/
	}
	
	private int mapID ;
	
	private void initMapNode() {
		mDrawLine = new drawLineHelper(this.mContext,this.map.imageWidth,this.map.imageHeight,mapID);
	}
	
	public float[] getMoveStepXY() {
		float[] xy = new float[2];
		xy[0] = (float) Math.cos(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	xy[1] = (float) Math.sin(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	
		return xy;
	}
	
	public float[] getNewPositionOnLineByRotateDirection(NaviNode start,NaviNode end,float rotate) {
		setLocatePointMatrixToLine();
    	float[] pointCurrentXY = this.getLocatePointPixelOnMap();//this.getItemPixelOnMap(locatePoint);
    	
		float moveX = (float) Math.cos(Math.toRadians(90-rotate))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	float moveY = (float) Math.sin(Math.toRadians(90-rotate))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	Log.i(TAG, "moveX = "+moveX+",moveY = "+moveY);
    	float newPointX = pointCurrentXY[0]+moveX;
	   	float newPointY = pointCurrentXY[1]-moveY;
	   	float[] newPointXYtoLine = this.getLinePoint(newPointX, newPointY);//MathProxy.getProjectionPoint(start, end, newPointX, newPointY);//this.getLinePoint(newPointX, newPointY);
	   
			 // NaviNode[] pathStartAndEnd = this.getNearlyPathByXY(this.plan.naviPlanGraph,x, y, false);
			//  pointXY = MathProxy.getProjectionPoint(pathStartAndEnd[0], pathStartAndEnd[1], x, y);
			 // return pointXY;
		
	   	//float[] newPointXYtoLine = MathProxy.getProjectionPoint(start, end, newPointX, newPointY);// this.getLinePoint(newPointX,newPointY);
	   	//Log.i(TAG, "new X = "+newPointX+",new Y = "+newPointY);
	  	//Log.i(TAG, "projectionPoints X = "+projectionPoints.x+",Y = "+projectionPoints.y);
	  		
	   	//float[] newPointXYtoLine = this.getLinePointWithDeviation(pointCurrentXY[0],pointCurrentXY[1],newPointX, newPointY);
	   	//this.setItemToLocation(this.locatePoint,newPointXYtoLine[0], newPointXYtoLine[1]);
		//this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		//isTouch = true;
		//this.rotateCurrentPoint();
		//fromTouchToNoTouch = true;
		//isTouch = false;
	   	return newPointXYtoLine;
	}
	
	
	public float[] getCurrentMoveOnLineCoordinate() {
		//float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);
    	//float moveX = (float) Math.cos(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	//float moveY = (float) Math.sin(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	//float newPointX = pointCurrentXY[0]+moveX;
	   	//float newPointY = pointCurrentXY[1]-moveY;
	   	//float[] newPointXYtoLine = this.getLinePoint(newPointX,newPointY);
	   	
		setLocatePointMatrixToLine();
    	float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);
    	
    	float moveX = (float) Math.cos(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	float moveY = (float) Math.sin(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	//Log.i(TAG, "stepDistance = "+stepDistance+", mapLoadScale = "+mapLoadScale);
    	//Log.i(TAG, "mov X = "+moveX+", Y = "+moveY);
    	float newPointX = pointCurrentXY[0]+moveX;
	   	float newPointY = pointCurrentXY[1]-moveY;
	   	//Log.i(TAG, "==================");
	   	//Log.i(TAG, "pointCurrentXY X =  "+pointCurrentXY[0]+",Y = "+pointCurrentXY[1]);
		
	   	//Log.i(TAG, "newPointXY X =  "+newPointX+",Y = "+newPointY);
		
		float[] newPointXYtoLine = this.getLinePointWithDeviation(pointCurrentXY[0],pointCurrentXY[1],newPointX, newPointY);//this.getLinePoint(newPointX,newPointY);
		//Log.v("Edge algor","edge id: "+((Edge)this.plan.planNaviLine.edges.get(this.currentEdge)).getID());
		//Log.i(TAG, "newPointXYtoLine X =  "+newPointXYtoLine[0]+",Y = "+newPointXYtoLine[1]);
		this.setItemToLocation(this.locatePoint,pointCurrentXY[0], pointCurrentXY[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		//this.locatePoint.LogMatrix();
		isTouch = true;
		this.rotateCurrentPoint();
		fromTouchToNoTouch = true;
		isTouch = false;
	   	
	   	
	   	return newPointXYtoLine;
	}
	
	
	private void pointMovingWithoutPath(){
    	//Log.v("sensor event", "detect moving");
    	setLocatePointMatrixToLine();
    	float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);
    	
    	float moveX = (float) Math.cos(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	float moveY = (float) Math.sin(Math.toRadians(90-(azimuth-standard_azimuth)))*(stepDistance/this.mapLoadScale);//convertPixelsToDp((stepDistance),mContext);
    	//Log.i(TAG, "stepDistance = "+stepDistance+", mapLoadScale = "+mapLoadScale);
    	//Log.i(TAG, "mov X = "+moveX+", Y = "+moveY);
    	float newPointX = pointCurrentXY[0]+moveX;
	   	float newPointY = pointCurrentXY[1]-moveY;
	   	//Log.i(TAG, "==================");
	   	//Log.i(TAG, "pointCurrentXY X =  "+pointCurrentXY[0]+",Y = "+pointCurrentXY[1]);
		
	   	//Log.i(TAG, "newPointXY X =  "+newPointX+",Y = "+newPointY);
		
		float[] newPointXYtoLine = this.getLinePoint(newPointX,newPointY);
		//Log.v("Edge algor","edge id: "+((Edge)this.plan.planNaviLine.edges.get(this.currentEdge)).getID());
		//Log.i(TAG, "newPointXYtoLine X =  "+newPointXYtoLine[0]+",Y = "+newPointXYtoLine[1]);
		this.setItemToLocation(this.locatePoint,newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		//this.locatePoint.LogMatrix();
		isTouch = true;
		this.rotateCurrentPoint();
		fromTouchToNoTouch = true;
		isTouch = false;
	}
	
	private void setLocatePointMatrixToLine() {
		lastAzimuth = this.standard_azimuth;
		lastDegree = 0;
		float[] onLineMatrixValues = new float[9];
    	this.locatePointMatrixOnLine.getValues(onLineMatrixValues);
    	this.locatePoint.matrix.setValues(onLineMatrixValues);// = this.locatePointMatrixOnLine;
    	this.locatePoint.updateImageMatrix();
	}
	
	private void setLocatePointToprojectionPoint() {
		setLocatePointMatrixToLine();
		float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);//.calNewPointPixel();
	 	float[] newPointXYtoLine = this.getLinePoint(pointCurrentXY[0],pointCurrentXY[1]);
		this.setItemToLocation(this.locatePoint,newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		this.rotateCurrentPoint();
	}
	
	public float[] getLocatePointPixelOnMap() {
		//setLocatePointMatrixToLine();
    	float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);
    	isTouch =true;
		this.rotateCurrentPoint();
		fromTouchToNoTouch = true;
		isTouch =false;
		return pointCurrentXY;
	}
	
	public void setLocatePointXY(float X,float Y) {
		setLocatePointMatrixToLine();
		this.setItemToLocation(this.locatePoint,X,Y);//newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);
    	float[] newPointXYtoLine = this.getLinePointWithDeviation(pointCurrentXY[0],pointCurrentXY[1],pointCurrentXY[0],pointCurrentXY[1]);//this.getLinePoint(pointCurrentXY[0],pointCurrentXY[1]);// this.getLinePointWithDeviation(pointCurrentXY[0],pointCurrentXY[1]);//
		this.setItemToLocation(this.locatePoint,newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		isTouch = true;
		this.rotateCurrentPoint();
		fromTouchToNoTouch = true;
		isTouch = false;
	}
	
	public void setLocatePointXYToLine(float X,float Y) {
		setLocatePointMatrixToLine();
		this.setItemToLocation(this.locatePoint,X,Y);//newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);
    	float[] newPointXYtoLine = this.getLinePoint(pointCurrentXY[0],pointCurrentXY[1]);//this.getLinePoint(pointCurrentXY[0],pointCurrentXY[1]);// this.getLinePointWithDeviation(pointCurrentXY[0],pointCurrentXY[1]);//
		this.setItemToLocation(this.locatePoint,newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		isTouch = true;
		this.rotateCurrentPoint();
		fromTouchToNoTouch = true;
		isTouch = false;
	}
	
	public void setLocatePointXYToCurrentNavigationRoute(float X,float Y) {
		setLocatePointMatrixToLine();
		this.setItemToLocation(this.locatePoint,X,Y);//newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		float[] pointCurrentXY = this.getItemPixelOnMap(locatePoint);
    	float[] newPointXYtoLine = this.getLinePoint(pointCurrentXY[0],pointCurrentXY[1]);//navigationCalculator.findProjectionWithExistRoute(currentNavigationRoute, pointCurrentXY[0], pointCurrentXY[1]);//this.getLinePoint(pointCurrentXY[0],pointCurrentXY[1]);//this.getLinePoint(pointCurrentXY[0],pointCurrentXY[1]);// this.getLinePointWithDeviation(pointCurrentXY[0],pointCurrentXY[1]);//
		this.setItemToLocation(this.locatePoint,newPointXYtoLine[0], newPointXYtoLine[1]);
		this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
		isTouch = true;
		this.rotateCurrentPoint();
		fromTouchToNoTouch = true;
		isTouch = false;
	}
	
	public void setItemToLocation(NaviViewItem item,float X,float Y) {
		float[] itemMatrixValue = item.getMatrixValue();
		float[] itemPicCenter = this.calculateItemPicCenter(item);
		float ratio = getMapRatio();
		float xCenter = (itemPicCenter[0]/ratio)/this.mapLoadScale;
		float yCenter = (itemPicCenter[1]/ratio)/this.mapLoadScale;
		float[] XYToTrans = this.convertXYTOTrans(X-xCenter, Y-yCenter); 
		itemMatrixValue[2] = XYToTrans[0];
		itemMatrixValue[5] = XYToTrans[1];
		item.updateImageMatrix(itemMatrixValue);
	}

	public void navToTargetFromCurrentPosition(float targetX,float targetY) {
		float[] locatePointPixel = this.getLocatePointPixelOnMap();
		this.drawPathByStartXYToEndXY(locatePointPixel[0],locatePointPixel[1], targetX, targetY);
	}
	
	public void navToTargetFromStartPOI(float startX,float startY,float targetX,float targetY) {
		this.drawPathByStartXYToEndXY(startX,startY, targetX, targetY);
	}
	

	private float[] calculateItemPicCenter(NaviViewItem item) {
		float[] picCenterXY = new float[2];
		RectF itemRect = new RectF(0,0,item.bitmap.getWidth(),item.bitmap.getHeight());
		item.matrix.mapRect(itemRect); 
		picCenterXY[0] = ((itemRect.right-itemRect.left)/2f);
		picCenterXY[1] = ((itemRect.bottom-itemRect.top)/2f);
		return picCenterXY;
	}
	
	private float getMapRatio() {
		RectF mapRect = new RectF(0,0,map.bitmap.getWidth(),map.bitmap.getHeight());//.getHeight());
		this.map.matrix.mapRect(mapRect);
		float ratio = mapRect.width()/map.bitmap.getWidth(); 
		return ratio;
	}

	
	public float[] getItemPixelOnMap(NaviViewItem item) {
		float[] pixelXY = new float[2];
		float[] itemPicCenter = this.calculateItemPicCenter(item);
		RectF mapRect = new RectF(0,0,map.bitmap.getWidth(),map.bitmap.getHeight());//.getHeight());
		this.map.matrix.mapRect(mapRect);
		RectF itemRect = new RectF(0,0,item.bitmap.getWidth(),item.bitmap.getHeight());
		item.matrix.mapRect(itemRect); 
		//Log.i(TAG, "itemRect.left = "+itemRect.left);
		//Log.i(TAG, "mapRect.left = "+mapRect.left);
		//Log.i(TAG, "itemRect.top = "+itemRect.top);
		//Log.i(TAG, "mapRect.top = "+mapRect.top);
		float ratio = getMapRatio();//mapRect.width()/map.bitmap.getWidth(); 
		pixelXY[0] = ((itemRect.left-mapRect.left+itemPicCenter[0])/ratio)/this.mapLoadScale;
		pixelXY[1] = ((itemRect.top-mapRect.top+itemPicCenter[1])/ratio)/this.mapLoadScale;
		return pixelXY;
	}
	
	private float getItemWidthPixel(NaviViewItem item) {
		RectF itemRect = new RectF(0,0,item.bitmap.getWidth(),item.bitmap.getHeight());
		item.matrix.mapRect(itemRect); 
		return Math.abs(itemRect.left-itemRect.right);
	}
	
	private void drawPathByStartXYToEndXY(float startX,float startY,float endX,float endY) {
		route.invalidate();
		mDrawLine.clean(); 
		currentNavigationRoute.clear();
		LinkedList<Node> path = this.naviCaculator.getShortPathByStartXYAndEndXY(startX, startY, endX, endY);
		ArrayList<Integer> pathX=new ArrayList<Integer>();
		ArrayList<Integer> pathY=new ArrayList<Integer>();
		
		pathX.add((int)((startX)*this.mapLoadScale));
		pathY.add((int)((startY)*this.mapLoadScale));		
		
	   	//float[] startXYtoLine = this.getLinePoint(startX,startY);
	   	//pathX.add((int)((startXYtoLine[0])*this.mapLoadScale));
		//pathY.add((int)((startXYtoLine[1])*this.mapLoadScale));		
		//Log.i(TAG, "Start path x = "+startX+", y = "+startY);
		for(Node node:path) {
			pathX.add((int)(((NaviNode)node).x*this.mapLoadScale));
			pathY.add((int)(((NaviNode)node).y*this.mapLoadScale));
			Log.i(TAG, "path x = "+((NaviNode)node).x+", y = "+((NaviNode)node).y);
		}
			
		
		//float[] endXYtoLine = this.getLinePoint(endX,endY);
	   //	pathX.add((int)((endXYtoLine[0])*this.mapLoadScale));
		//pathY.add((int)((endXYtoLine[1])*this.mapLoadScale));		
		
		
		pathX.add((int)((endX)*this.mapLoadScale));
		pathY.add((int)((endY)*this.mapLoadScale));		
		
		this.route.setImage(mDrawLine.drawPath(pathX, pathY)); 
		this.route.updateImageMatrix();
		currentNavigationRoute.add(new NaviNode(startX+","+startY,(int)startX,(int)startY));
		for(Node node:path) {
			NaviNode navi = (NaviNode)node;
			currentNavigationRoute.add(new NaviNode(navi.x+","+navi.y,(int)navi.x,(int)navi.y));
			//pathX.add((int)(((NaviNode)node).x*this.mapLoadScale));
			//pathY.add((int)(((NaviNode)node).y*this.mapLoadScale));
			//Log.i(TAG, "path x = "+((NaviNode)node).x+", y = "+((NaviNode)node).y);
		}
		//currentNavigationRoute = path;
		currentNavigationRoute.add(new NaviNode(endX+","+endY,(int)endX,(int)endY));
		String s ="";
		for(Node node:currentNavigationRoute) {
			s+="node: "+node.getID()+"\n";
		}
		//Log.i(TAG, s);
		
	}
	
	private void minZoom() {
		//minScaleR = (float) dm.heightPixels / (float) this.map.bitmap.getHeight();//.getImageHeight();
		minScaleR = Math.min(
                (float) dm.widthPixels / (float) map.bitmap.getWidth(),
                (float) dm.heightPixels / (float) map.bitmap.getHeight());
		mapLoadScale = (float)this.map.imageHeight/(float)this.map.imageHeightOrigin;
		this.map.matrix.postScale(minScaleR, minScaleR);
		this.locatePoint.matrix.postScale((minScaleR/2f)*mapLoadScale, ((minScaleR/2f)*mapLoadScale)); 
		this.friend.matrix.postScale((minScaleR/2f)*mapLoadScale,(minScaleR/2f)*mapLoadScale);
		this.currentNaivStart.matrix.postScale((minScaleR)*mapLoadScale,(minScaleR)*mapLoadScale);
		this.targetpoint.matrix.postScale((minScaleR)*mapLoadScale,(minScaleR)*mapLoadScale); 
		this.route.matrix.postScale(minScaleR, minScaleR);
		this.compass.matrix.postScale(minScaleR*2, minScaleR*2);
		//this.locatePoint.matrix.postScale((minScaleR/15f)*mapLoadScale, (minScaleR/15f*mapLoadScale));
		//Log.i(TAG, "imageHeight = "+this.map.imageHeight+", imageHeightOrigin ="+this.map.imageHeightOrigin);
		//Log.i(TAG, "mapLoadScale = "+mapLoadScale);
		//this.locatePoint.LogMatrix();
    }
	 
	private void initialImageView() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.map = new NaviViewItem(this.mContext,params);
		this.locatePoint = new NaviViewItem(this.mContext,params);
		this.friend = new NaviViewItem(this.mContext,params);
		this.targetpoint = new NaviViewItem(this.mContext,params);
		this.currentNaivStart = new NaviViewItem(this.mContext,params);
		this.compass = new NaviViewItem(this.mContext,params);
		this.route = new NaviViewItem(this.mContext,params);
		this.addView(map, params);
		this.addView(route, params);
		this.addView(targetpoint,params);
		this.addView(currentNaivStart,params);
		this.addView(friend, params);
		this.addView(locatePoint, params);
		this.addView(compass, params);		
		//friend.setVisibility(View.INVISIBLE);
		
 	}
	
	 private void setPicInitial() {
		
		this.map.updateImageMatrix();
		this.locatePoint.updateImageMatrix(); 
		this.locatePointMatrixOnLine.setValues(locatePoint.getMatrixValue());
		this.friend.updateImageMatrix();
		this.targetpoint.updateImageMatrix();
		this.currentNaivStart.updateImageMatrix();
		this.route.updateImageMatrix();
		this.compass.updateImageMatrix();
		this.setMapGestureControll();
		this.initMapNode();
		pointMovingWithoutPath();
		this.friend.setVisibility(this.friend.INVISIBLE);
		this.compass.setVisibility(this.compass.INVISIBLE);
		this.targetpoint.setVisibility(this.targetpoint.INVISIBLE);
		this.currentNaivStart.setVisibility(this.currentNaivStart.INVISIBLE);
		this.locatePoint.setVisibility(this.locatePoint.INVISIBLE);
	}
		
	public void rotateItem(float standard_azimuth,float azimuth) {
		this.standard_azimuth = standard_azimuth;
	    this.azimuth = azimuth;
	    rotateCurrentPoint();
	    
	}
	
	private void broadcastOnTouchOnePoint() {
		Intent i = new Intent();
		i.setAction("onNavigationViewTouchOnePoint");
		//i.putExtra("TOUCH",isTouch);
		((Activity)this.mContext).sendBroadcast(i);
	}
	
	private void broadcastOnTouch(boolean isTouch) {
		Intent i = new Intent();
		i.setAction("onNavigationViewTouch");
		i.putExtra("TOUCH",isTouch);
		((Activity)this.mContext).sendBroadcast(i);
	}
	
	
	private boolean isOnePoint = false,isTwoPoint = false,isDraged = false,hadTwoPoint = false;
	
	private void saveCurrentMatrixValue() {
		pre_matrix_map.set(map.matrix);//matrix_map);
    	pre_locatePointMatrixOnLine.set(locatePointMatrixOnLine);
    	pre_matrix_locatePoint.set(locatePointMatrixOnLine);//locatePoint.matrix);//matrix_locatePoint);
    	pre_matrix_route.set(route.matrix);
    	pre_matrix_friend.set(friend.matrix);
    	pre_matrix_target.set(targetpoint.matrix);
    	pre_matrix_navistart.set(currentNaivStart.matrix);
	}
	
	private void setPreMatrixValue() {
		map.matrix.set(pre_matrix_map);
    	locatePoint.matrix.set(pre_matrix_locatePoint);
    	locatePointMatrixOnLine.set(pre_locatePointMatrixOnLine);
    	friend.matrix.set(pre_matrix_friend);
    	route.matrix.set(pre_matrix_route);
    	targetpoint.matrix.set(pre_matrix_target);
    	currentNaivStart.matrix.set(pre_matrix_navistart);
	}

	private void setMapGestureControll() {		
		this.map.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
		        case MotionEvent.ACTION_DOWN: 
		        	//Log.i(TAG, "One_DOWN");
		        	isTouch =true;
		        	saveCurrentMatrixValue();
		        	preTouch.set(event.getX(), event.getY());
		        	isOnePoint = true;
		            broadcastOnTouch(true);
		            break;
		        case MotionEvent.ACTION_POINTER_DOWN: //two point to ZoomIn Or ZoomOut
		        	//Log.i(TAG, "Two_DOWN");
		        	isTouch =true;
		        	zoomPointDist = spacing(event);
		            //if (zoomPointDist > 10f) {
		            	saveCurrentMatrixValue();
			        	midPoint(mid, event);
			        	isTwoPoint = true;
			        	
			        	isZoom = true;
		            //}
		            broadcastOnTouch(true);
		            break;
		        case MotionEvent.ACTION_UP:
		        	//Log.i(TAG, "One_UP");
		        	fromTouchToNoTouch = true;
		        	isTouch =false;
		        	isDraged = false;
		        	isOnePoint = false;
		        	isTwoPoint = false;
		        	broadcastOnTouch(false);
		        	if(!isDrag && !isZoom){
		        		broadcastOnTouchOnePoint();
		        	}
		        	isZoom = false;
		        	isDrag = false;
		        	hadTwoPoint = false;
		        	break;
		        case MotionEvent.ACTION_POINTER_UP:
		        	//Log.i(TAG, "Two_UP");
		        	hadTwoPoint = true;
		        	mode = NONE;
		            isTwoPoint = false;
		            fromTouchToNoTouch = true;
		            isTouch =false;
		           
		            broadcastOnTouch(false);
		            break;
		        case MotionEvent.ACTION_MOVE:
		        	isTouch =true;
		        	if (isTwoPoint &&!hadTwoPoint) {
		        		float newDist = spacing(event);
		               // if (newDist > 10f) {
		                	//Log.i(TAG, "isZoom");
		        		setPreMatrixValue();
		                	float tScale = newDist / zoomPointDist;
		                	
			            	map.matrix.postScale(tScale, tScale, mid.x, mid.y);
			            	locatePoint.matrix.postScale(tScale, tScale, mid.x, mid.y);
			            	locatePointMatrixOnLine.postScale(tScale, tScale, mid.x, mid.y);
			            	friend.matrix.postScale(tScale, tScale, mid.x, mid.y);
			            	targetpoint.matrix.postScale(tScale, tScale, mid.x, mid.y);
			            	currentNaivStart.matrix.postScale(tScale, tScale, mid.x, mid.y);
			            	route.matrix.postScale(tScale, tScale, mid.x, mid.y);
			            	float MapDP = convertPixelsToDp(getItemWidthPixel(map), mContext);
			        		//Log.i(TAG, "MapDP = "+MapDP);
			        		if(MapDP<200) {
			        			setPreMatrixValue();
			        		}	
		                //}   
		                broadcastOnTouch(true);
				           
		            }else if (isOnePoint && !hadTwoPoint) {
		            	setPreMatrixValue();
		            	float dis = getMovingDist(event.getX() - preTouch.x, event.getY() - preTouch.y);
		            	if(isDraged || convertPixelsToDp(dis,mContext)>10f){
		            		//Log.i(TAG, "isDrog");
		            		map.matrix.postTranslate(event.getX() - preTouch.x, event.getY()
		                        - preTouch.y);
		            		locatePoint.matrix.postTranslate(event.getX() - preTouch.x, event.getY()
			                        - preTouch.y);
		            		locatePointMatrixOnLine.postTranslate(event.getX() - preTouch.x, event.getY()
			                        - preTouch.y);
		            		friend.matrix.postTranslate(event.getX() - preTouch.x, event.getY()
			                        - preTouch.y);
		            		targetpoint.matrix.postTranslate(event.getX() - preTouch.x, event.getY()
			                        - preTouch.y);
		            		currentNaivStart.matrix.postTranslate(event.getX() - preTouch.x, event.getY()
			                        - preTouch.y);
		            		route.matrix.postTranslate(event.getX() - preTouch.x, event.getY()
			                        - preTouch.y);
		            		isDrag = true;
		            		isDraged = true;
		            	}
		            	else {
		            		isDrag = false;
		            	}
		            	broadcastOnTouch(true);
				           
		            } 
		            break;
		        }
				map.updateImageMatrix();
				locatePoint.updateImageMatrix(locatePointMatrixOnLine);//.updateImageMatrix();//.setImageMatrix(matrix_locatePoint);
				friend.updateImageMatrix();
				targetpoint.updateImageMatrix();
				route.updateImageMatrix();
				currentNaivStart.updateImageMatrix();
				rotateCurrentPoint();
				return true;
			}		
		});
	}
	
	private static float convertPixelsToDp(float px,Context context){
		Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	} 
	  
	private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
	
	private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
	
	private float getMovingDist(float diffX, float diffY){
		return (float)Math.sqrt(diffX*diffX+diffY*diffY);
	}

	private float[] convertXYTOTrans(float X,float Y) {
		float[] transXY = new float[2];
		RectF mapRect = new RectF(0,0,map.bitmap.getWidth(),map.bitmap.getHeight());//.getHeight());
		this.map.matrix.mapRect(mapRect);
		float ratio = mapRect.width()/map.bitmap.getWidth(); 
		
		transXY[0] = (X*this.mapLoadScale*ratio)+mapRect.left;
		transXY[1] = (Y*this.mapLoadScale*ratio)+mapRect.top;
		return transXY;
	}
	
	private float[] getLinePoint(float px,float py){
		  //float[] pointXY = new float [2];
		  //NaviNode[] pathStartAndEnd = this.naviCaculator.getNearlyPathByXY(,px, py, false);
		  //pointXY = MathProxy.getProjectionPoint(pathStartAndEnd[0], pathStartAndEnd[1], px, py);
		  /*
		  if(tmpLength>40){
			  pointXY[0] = px;
			  pointXY[1] = py;
		  }
		  */
		  //return pointXY;
		return this.naviCaculator.findProjectionToLineInCurrentGraph(px, py);
	  }
	
	private float[] getLinePointWithDeviation(float x,float y,float newX,float newY){
		
		float[] xy = new float[2];
		float[] onLinePoint = this.naviCaculator.findProjectionToLineInCurrentGraph(newX,newY);
		float deviation = this.currentRoadDeviation;//stepDistance;
		float distance = MathProxy.getDistance(newX, newY, onLinePoint);
		
		if(distance<=deviation) {
			xy[0] = newX;
			xy[1] = newY;
			return xy;
		}
		else {
			if(MathProxy.getDistance(newX, y, onLinePoint)<=deviation) {
				xy[0] = newX;
			}
			else if((newX<onLinePoint[0]+deviation)&&(newX>onLinePoint[0]-deviation)){
				xy[0] = newX;
			}
			else {
				float distance1 = MathProxy.getDistance(newX, newY, onLinePoint[0]-(deviation),onLinePoint[1]);
				float distance2 = MathProxy.getDistance(newX, newY, onLinePoint[0]+(deviation),onLinePoint[1]);
				if(distance1<=distance2) {
					xy[0] = onLinePoint[0]-(deviation);
				}
				else {
					xy[0] = onLinePoint[0]+(deviation);
				}
			}
			
			if(MathProxy.getDistance(x, newY, onLinePoint)<=deviation) {
				xy[1] = newY;
			}
			else if((newY<onLinePoint[1]+deviation)&&(newY>onLinePoint[1]-deviation)){
				xy[1] = newY;
			}
			else {
				float distance1 = MathProxy.getDistance(newX, newY, onLinePoint[0],onLinePoint[1]-(deviation));
				float distance2 = MathProxy.getDistance(newX, newY, onLinePoint[0],onLinePoint[1]+(deviation));
				if(distance1<=distance2) {
					xy[1] = onLinePoint[1]-(deviation);
				}
				else {
					xy[1] = onLinePoint[1]+(deviation);
				}
			}
		}
		
		return xy;//this.naviCaculator.findProjectionToLineInCurrentGraph(px, py);
	  }
	
	
	private class rotateAnimation extends Animation	{
		private float degree;
		private NaviViewItem item;
		private float countDegree;
		private float remainDegree;
		public rotateAnimation(NaviViewItem item,float degree) {
			this.item = item;
			if(Math.abs(degree)>180) {
				if(degree<-180)
					degree+=360;
				else 
					degree-=360;
			}
			this.degree = degree;
			this.countDegree = 0;
			this.remainDegree = degree;
		}
	    @Override
	    public void initialize(int width, int height, int parentWidth, int parentHeight) {
	        super.initialize(width, height, parentWidth, parentHeight);
	        setDuration(500);
	        setFillAfter(true);
	        setInterpolator(new LinearInterpolator());
	    }
	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	    	if(interpolatedTime <= 1) {
	    		float tempDegree = degree*interpolatedTime; 
	    		if(Math.abs(remainDegree)<Math.abs(tempDegree)) {
	    			tempDegree = remainDegree;
	    		}
	    		if(Math.abs(countDegree)<Math.abs(degree)) {
	    			countDegree += tempDegree;
	    			remainDegree -= tempDegree;
	    			item.matrix.preRotate(tempDegree,item.imageWidth/2,item.imageHeight/2);
	    			item.updateImageMatrix();
	    		}		    	
	    	}
	    }
	}
	
	private float[] transPointPixelToTrans(float pixelX,float pixelY) {
		float[] transXY = new float[2]; 
		float[] arrayM = new float[9];		
		this.map.matrix.getValues(arrayM);
		transXY[0] = (arrayM[2]+(pixelX*arrayM[0]));
		transXY[1] = (arrayM[5]+(pixelY*arrayM[4]));
		return transXY;	      
	};
	
	

	public void pointCenter(){
		setLocatePointMatrixToLine();
		RectF locateRect = new RectF(0, 0, this.locatePoint.getWidth(), this.locatePoint.getHeight());
	    locatePoint.matrix.mapRect(locateRect);    
	    float deltaX = 0, deltaY = 0;
	    float[] locatePicCenterSize = this.calculateItemPicCenter(this.locatePoint);
	    //float[] arrayPointM = this.locatePoint.getMatrixValue();
	    int screenHeight = dm.heightPixels;
        int screenWidth = dm.widthPixels;
        Log.v("screenHeight", Integer.toString(screenHeight));
        Log.v("screenWidth", Integer.toString(screenWidth));
        deltaX = (screenWidth/2)-locateRect.left-locatePicCenterSize[0];//-arrayPointM[2];//-convertPixelsToDp(this.locatePoint.getWidth(),this.mContext)*arrayPointM[0];
	    deltaY = (screenHeight/2)-locateRect.top-(locatePicCenterSize[1]);//-arrayPointM[5];//-convertPixelsToDp(this.locatePoint.getHeight(),this.mContext)*arrayPointM[4];
	    this.map.matrix.postTranslate(deltaX, deltaY);
	    this.locatePoint.matrix.postTranslate(deltaX, deltaY);
	    this.route.matrix.postTranslate(deltaX, deltaY);
	    this.friend.matrix.postTranslate(deltaX, deltaY);
	    this.targetpoint.matrix.postTranslate(deltaX, deltaY);
	    this.currentNaivStart.matrix.postTranslate(deltaX, deltaY);
	        
	    this.map.updateImageMatrix();
	    this.locatePoint.updateImageMatrix();
	    this.locatePointMatrixOnLine.setValues(this.locatePoint.getMatrixValue());
	    this.route.updateImageMatrix();
	    this.friend.updateImageMatrix();
	    this.targetpoint.updateImageMatrix();
	    this.currentNaivStart.updateImageMatrix();
	  }
}
