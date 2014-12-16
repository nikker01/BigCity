package com.doubleservice.bigcitynavigation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import map.navi.Data.POI;
import map.navi.component.NavigationRouteSetting;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectPostRequest;
import com.doubleservice.DataVO.PushMsgVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
//import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sails.engine.LocationRegion;
import com.sails.engine.SAILS;
import com.sails.engine.MarkerManager;
import com.sails.engine.PathRoutingManager;
import com.sails.engine.PinMarkerManager;
import com.sails.engine.SAILSMapView;
import com.sails.engine.core.model.GeoPoint;
import com.sails.engine.overlay.Marker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
//import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class SailsTechActivity extends BaseDrawerActivity {

	 static SAILS mSails;
	    static SAILSMapView mSailsMapView;
	    ImageView zoomin;
	    ImageView zoomout;
	    ImageView lockcenter;
	    Button endRouteButton,btnToNavigation,btnNavigationTarget;
	    //Button pinMarkerButton;
	    //TextView distanceView;
	    //TextView currentFloorDistanceView;
	    //TextView msgView;
	   // SlidingMenu menu;
	    ActionBar actionBar;
	    //ExpandableListView expandableListView;
	    //ExpandableAdapter eAdapter;
	    Vibrator mVibrator;
	    Spinner floorList;
	    //ArrayAdapter<String> adapter;
	   // ailsNavigationPopupView popupView ;
	    byte zoomSav = 0;
	    boolean zoominOnce = false;
	    List<LocationRegion> allPoiRegion;
	    ArrayList<POI> allPoiNames;
	    public static final int REQUSET_CODE_ROUTESETTING =100;
	    private boolean isFromonActivityResult;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	    	FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View activityView = layoutInflater.inflate(R.layout.sailstech_main, null, false);
			frameLayout.addView(activityView);
			
	        //setContentView(R.layout.sailstech_main);
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	       // menu = new SlidingMenu(this);
	        //menu.setMode(SlidingMenu.LEFT);
	        //menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	        //menu.setFadeDegree(0.0f);
	        //menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	        //menu.setMenu(R.layout.expantablelist);
	       
	        
	        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
	        actionBar = getActionBar();
	        actionBar.setHomeButtonEnabled(true);
	        actionBar.setDisplayHomeAsUpEnabled(true);
	        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	        zoomin = (ImageView) findViewById(R.id.zoomin);
	        zoomout = (ImageView) findViewById(R.id.zoomout);
	        lockcenter = (ImageView) findViewById(R.id.lockcenter);
	        endRouteButton = (Button) findViewById(R.id.stopRoute);
	        btnToNavigation = (Button) findViewById(R.id.btn_sails_toNavigation);
	        btnNavigationTarget = (Button) findViewById(R.id.btn_sails_navigation_target);
	        //pinMarkerButton = (Button) findViewById(R.id.pinMarker);
	        //distanceView = (TextView) findViewById(R.id.distanceView);
	        //distanceView.setVisibility(View.INVISIBLE);
	        //currentFloorDistanceView = (TextView) findViewById(R.id.currentFloorDistanceView);
	        //currentFloorDistanceView.setVisibility(View.INVISIBLE);
	        //msgView = (TextView) findViewById(R.id.msgView);
	        //msgView.setVisibility(View.INVISIBLE);


	        floorList = (Spinner) findViewById(R.id.spinner);

	        zoomin.setOnClickListener(controlListener);
	        zoomout.setOnClickListener(controlListener);
	        lockcenter.setOnClickListener(controlListener);
	        endRouteButton.setOnClickListener(controlListener);
	        endRouteButton.setVisibility(View.INVISIBLE);
	        btnToNavigation.setOnClickListener(controlListener);
	        //pinMarkerButton.setOnClickListener(controlListener);
	        //pinMarkerButton.setVisibility(View.VISIBLE);
	      //  expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
	      //  expandableListView.setOnChildClickListener(childClickListener);

	        LocationRegion.FONT_LANGUAGE = LocationRegion.NORMAL;

	        //new a SAILS engine.
	        mSails = new SAILS(this);
	        //set location mode.
	        mSails.setMode(SAILS.BLE_GFP_IMU);//.BLE_GFP_IMU);
	        //set floor number sort rule from descending to ascending.
	        mSails.setReverseFloorList(true);
	        
	        //create location change call back.
	        mSails.setOnLocationChangeEventListener(new SAILS.OnLocationChangeEventListener() {
	            @Override
	            public void OnLocationChange() {

	                if (mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor() && !mSails.getFloor().equals("") && mSails.isLocationFix()) {
	                    //set the map that currently location engine recognize.
	                    mSailsMapView.loadCurrentLocationFloorMap();
	                    mSailsMapView.getMapViewPosition().setZoomLevel((byte) 20);
	                    Toast t = Toast.makeText(getBaseContext(), mSails.getFloorDescription(mSails.getFloor()), Toast.LENGTH_SHORT);
	                    t.show();
	                    //mSailsMapView.setAnimationToZoom((byte)20);
	                }
	                
	                if(mSails.isLocationFix() && !zoominOnce) {
	                	//mSailsMapView.setAnimationToZoom((byte)20);
	                	zoominOnce = true;
	                	mSailsMapView.getMapViewPosition().setZoomLevel((byte) 20);
	                }
	            }
	        });
	        
	        mSails.setInitialPositionTimeoutCallback(10000, new SAILS.OnPositionInitialTimeOutCallback() {
	            @Override
	            public void onTimeOut() {
	                new AlertDialog.Builder(SailsTechActivity.this)
	                        .setTitle("Positioning Timeout")
	                        .setMessage("Put some time out message!")
	                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialoginterface, int i) {
	                                mSailsMapView.setMode(SAILSMapView.GENERAL);
	                            }
	                        }).setPositiveButton("Retry", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        mSails.startLocatingEngine();
	                    }
	                }).show();
	            }
	        });

	        //new and insert a SAILS MapView from layout resource.
	        mSailsMapView = new SAILSMapView(this);
	        ((FrameLayout) findViewById(R.id.SAILSMap)).addView(mSailsMapView);
	        //configure SAILS map after map preparation finish.
	        mSailsMapView.post(new Runnable() {
	            @Override
	            public void run() {
	                //please change token and building id to your own building project in cloud.
	                //mSails.loadCloudBuilding("bd10034e8a284673945c1497634087cc", "537db26eab5c03141300071e", new SAILS.OnFinishCallback() {
	            	//mSails.loadCloudBuilding("2196e512b24049ecbda4c926343f87ed", "541ba6fc6d0056100d00016b", new SAILS.OnFinishCallback() {
	            	//mSails.loadCloudBuilding("b126603eafd04fc4a483c1fadcf9f641", "543f72f41579fded7e00011a", new SAILS.OnFinishCallback() {
	                //mSails.loadCloudBuilding("e476cbbf53e847b5988300e390417416", "54583f621579fded7e0017f5", new SAILS.OnFinishCallback() {
	            	 mSails.loadCloudBuilding("e476cbbf53e847b5988300e390417416", "548a4e90d15e7a254b0000bd", new SAILS.OnFinishCallback() {
	 		             
	                	
	            		@Override
	                    public void onSuccess(String response) {
	                        runOnUiThread(new Runnable() {
	                            @Override
	                            public void run() {
	                                mapViewInitial();
	                                routingInitial();
	                                startLocateEngine();
	                                //slidingMenuInitial();
	                            }
	                        });

	                    }

	                    @Override
	                    public void onFailed(String response) {
	                        Toast t = Toast.makeText(getBaseContext(), "Load cloud project fail, please check network connection.", Toast.LENGTH_SHORT);
	                        t.show();
	                    }
	                });
	            }
	        });
	    }

	    void mapViewInitial() {
	        //establish a connection of SAILS engine into SAILS MapView.
	        mSailsMapView.setSAILSEngine(mSails);

	        //set location pointer icon.
	        mSailsMapView.setLocationMarker(R.drawable.circle, R.drawable.sail_arrow, null, 35);

	        //set location marker visible.
	        mSailsMapView.setLocatorMarkerVisible(true);

	        //load first floor map in package.
	        mSailsMapView.loadFloorMap(mSails.getFloorNameList().get(0));
	        actionBar.setTitle(this.getResources().getString(R.string.map_name));

	        //Auto Adjust suitable map zoom level and position to best view position.
	        mSailsMapView.autoSetMapZoomAndView();
	        
	        //set location region click call back.
	        mSailsMapView.setOnRegionClickListener(new SAILSMapView.OnRegionClickListener() {
	            @Override
	            public void onClick(List<LocationRegion> locationRegions) {
	            	
	            	/*
	                LocationRegion lr = locationRegions.get(0);
	                //begin to routing
	                if (mSails.isLocationEngineStarted()) {
	                    //set routing start point to current user location.
	                    mSailsMapView.getRoutingManager().setStartRegion(PathRoutingManager.MY_LOCATION);

	                    //set routing end point marker icon.
	                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

	                    //set routing path's color.
	                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);

	                    endRouteButton.setVisibility(View.VISIBLE);
	                    //currentFloorDistanceView.setVisibility(View.VISIBLE);
	                    //msgView.setVisibility(View.VISIBLE);

	                } else {
	                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.map_destination)));
	                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF85b038);
	                    if (mSailsMapView.getRoutingManager().getStartRegion() != null)
	                        endRouteButton.setVisibility(View.VISIBLE);
	                }

	                //set routing end point location.
	                mSailsMapView.getRoutingManager().setTargetRegion(lr);

	                //begin to route.
	                mSailsMapView.getRoutingManager().enableHandler();
	                //if (mSailsMapView.getRoutingManager().enableHandler())
	                    //distanceView.setVisibility(View.VISIBLE);
	               */
	                     
	            }
	        });

	        mSailsMapView.getPinMarkerManager().setOnPinMarkerClickCallback(new PinMarkerManager.OnPinMarkerClickCallback() {
	            @Override
	            public void OnClick(MarkerManager.LocationRegionMarker locationRegionMarker) {
	                Toast.makeText(getApplication(), "(" + Double.toString(locationRegionMarker.locationRegion.getCenterLatitude()) + "," +
	                        Double.toString(locationRegionMarker.locationRegion.getCenterLongitude()) + ")", Toast.LENGTH_SHORT).show();
	            }
	        });

	        //set location region long click call back.
	        mSailsMapView.setOnRegionLongClickListener(new SAILSMapView.OnRegionLongClickListener() {
	            @Override
	            public void onLongClick(List<LocationRegion> locationRegions) {
	                if (mSails.isLocationEngineStarted())
	                    return;

	                mVibrator.vibrate(70);
	                mSailsMapView.getMarkerManager().clear();
	                mSailsMapView.getRoutingManager().setStartRegion(locationRegions.get(0));
	                mSailsMapView.getMarkerManager().setLocationRegionMarker(locationRegions.get(0), Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));
	            }
	        });

	        //design some action in floor change call back.
	        mSailsMapView.setOnFloorChangedListener(new SAILSMapView.OnFloorChangedListener() {
	            @Override
	            public void onFloorChangedBefore(String floorName) {
	                //get current map view zoom level.
	                zoomSav = mSailsMapView.getMapViewPosition().getZoomLevel();
	            }

	            @Override
	            public void onFloorChangedAfter(final String floorName) {
	                Runnable r = new Runnable() {
	                    @Override
	                    public void run() {
	                        //check is locating engine is start and current brows map is in the locating floor or not.
	                        if (mSails.isLocationEngineStarted() && mSailsMapView.isInLocationFloor()) {
	                            //change map view zoom level with animation.
	                            mSailsMapView.setAnimationToZoom(zoomSav);
	                        }
	                    }
	                };
	                new Handler().postDelayed(r, 1000);

	                int position = 0;
	                for (String mS : mSails.getFloorNameList()) {
	                    if (mS.equals(floorName))
	                        break;
	                    position++;
	                }
	                floorList.setSelection(position);
	            }
	        });

	        //design some action in mode change call back.
	        mSailsMapView.setOnModeChangedListener(new SAILSMapView.OnModeChangedListener() {
	            @Override
	            public void onModeChanged(int mode) {
	                if (((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) && ((mode & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)) {
	                    lockcenter.setImageDrawable(getResources().getDrawable(R.drawable.center3));
	                } else if ((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) {
	                    lockcenter.setImageDrawable(getResources().getDrawable(R.drawable.center2));
	                } else {
	                    lockcenter.setImageDrawable(getResources().getDrawable(R.drawable.center1));
	                }
	            }
	        });

	        
	       allPoiNames = new ArrayList<POI> ();
	       allPoiRegion = new ArrayList<LocationRegion>();
	        
	        //List<List<Map<String, LocationRegion>>> childs = new ArrayList<List<Map<String, LocationRegion>>>();
	        for (String mS : mSails.getFloorNameList()) {
//	            Map<String, String> group_item = new HashMap<String, String>();
	  //          group_item.put("group", mSails.getFloorDescription(mS));
	    //        groups.add(group_item);

	      //      List<Map<String, LocationRegion>> child_items = new ArrayList<Map<String, LocationRegion>>();
	            for (LocationRegion mlr : mSails.getLocationRegionList(mS)) {
	                if (mlr.getName() == null || mlr.getName().length() == 0)
	                    continue;
	                POI poi = new POI("0",0,0,mlr.getName());
	                allPoiNames.add(poi);//mlr.getName());
	                allPoiRegion.add(mlr);
	        //        Map<String, LocationRegion> childData = new HashMap<String, LocationRegion>();
	          //      childData.put("child", mlr);
	            //    child_items.add(childData);
	            }
	            //childs.add(child_items);
	        }
	        
	        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,allPoiNames);// mSails.getFloorDescList());
	        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        //floorList.setAdapter(adapter);
	        floorList.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	                //if (!mSailsMapView.getCurrentBrowseFloorName().equals(mSails.getFloorNameList().get(position)))
	                   // mSailsMapView.loadFloorMap(mSails.getFloorNameList().get(position));
	            	LocationRegion lr = allPoiRegion.get(position);
	                if (mSails.isLocationEngineStarted()) {
	                    //set routing start point to current user location.
	                    mSailsMapView.getRoutingManager().setStartRegion(PathRoutingManager.MY_LOCATION);

	                    //set routing end point marker icon.
	                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

	                    //set routing path's color.
	                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);

	                    endRouteButton.setVisibility(View.VISIBLE);
	                    //currentFloorDistanceView.setVisibility(View.VISIBLE);
	                    //msgView.setVisibility(View.VISIBLE);

	                } else {
	                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.map_destination)));
	                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF85b038);
	                    if (mSailsMapView.getRoutingManager().getStartRegion() != null)
	                        endRouteButton.setVisibility(View.VISIBLE);
	                }

	                //set routing end point location.
	                mSailsMapView.getRoutingManager().setTargetRegion(lr);

	                //begin to route.
	                mSailsMapView.getRoutingManager().enableHandler();
	                //if (mSailsMapView.getRoutingManager().enableHandler())
	                   // distanceView.setVisibility(View.VISIBLE);
	            }

	            @Override
	            public void onNothingSelected(AdapterView<?> parent) {

	            }
	        });
	       
	    }

	    void routingInitial() {
	        mSailsMapView.getRoutingManager().setStartMakerDrawable(Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));
	        mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.map_destination)));
	        mSailsMapView.getRoutingManager().setOnRoutingUpdateListener(new PathRoutingManager.OnRoutingUpdateListener() {
	            @Override
	            public void onArrived(LocationRegion targetRegion) {
	                Toast.makeText(getApplication(), "Arrive.", Toast.LENGTH_SHORT).show();
	            }

	            @Override
	            public void onRouteSuccess() {
	                List<GeoPoint> gplist = mSailsMapView.getRoutingManager().getCurrentFloorRoutingPathNodes();
	                mSailsMapView.autoSetMapZoomAndView(gplist);
	            }

	            @Override
	            public void onRouteFail() {
	                Toast.makeText(getApplication(), "Route Fail.", Toast.LENGTH_SHORT).show();
	            }

	            @Override
	            public void onPathDrawFinish() {
	            }

	            @Override
	            public void onTotalDistanceRefresh(int distance) {
	                //distanceView.setText("Total Routing Distance: " + Integer.toString(distance) + " (m)");
	            }

	            @Override
	            public void onReachNearestTransferDistanceRefresh(int distance, int nodeType) {
	                switch (nodeType) {
	                    case PathRoutingManager.SwitchFloorInfo.ELEVATOR:
	                   //     currentFloorDistanceView.setText("To Nearest Elevator Distance: " + Integer.toString(distance) + " (m)");
	                        break;
	                    case PathRoutingManager.SwitchFloorInfo.ESCALATOR:
	                     //   currentFloorDistanceView.setText("To Nearest Escalator Distance: " + Integer.toString(distance) + " (m)");
	                        break;
	                    case PathRoutingManager.SwitchFloorInfo.STAIR:
	                       // currentFloorDistanceView.setText("To Nearest Stair Distance: " + Integer.toString(distance) + " (m)");
	                        break;
	                    case PathRoutingManager.SwitchFloorInfo.DESTINATION:
	                       // currentFloorDistanceView.setText("To Destination Distance: " + Integer.toString(distance) + " (m)");
	                        break;
	                }
	            }

	            @Override
	            public void onSwitchFloorInfoRefresh(List<PathRoutingManager.SwitchFloorInfo> infoList, int nearestIndex) {

	                //set markers for every transfer location
	                for (PathRoutingManager.SwitchFloorInfo mS : infoList) {
	                    if (mS.direction != PathRoutingManager.SwitchFloorInfo.GO_TARGET)
	                        mSailsMapView.getMarkerManager().setLocationRegionMarker(mS.fromBelongsRegion, Marker.boundCenter(getResources().getDrawable(R.drawable.transfer_point)));
	                }

	                //when location engine not turn,there is no current switch floor info.
	                if (nearestIndex == -1)
	                    return;

	                PathRoutingManager.SwitchFloorInfo sf = infoList.get(nearestIndex);

	                switch (sf.nodeType) {
	                    case PathRoutingManager.SwitchFloorInfo.ELEVATOR:
	                        //if (sf.direction == PathRoutingManager.SwitchFloorInfo.UP)
	                          //  msgView.setText("�ɯ责��: \n�зf�q��W�Ӧ�" + mSails.getFloorDescription(sf.toFloorname));
	                        //else if (sf.direction == PathRoutingManager.SwitchFloorInfo.DOWN)
	                          //  msgView.setText("�ɯ责��: \n�зf�q��U�Ӧ�" + mSails.getFloorDescription(sf.toFloorname));
	                        break;

	                    case PathRoutingManager.SwitchFloorInfo.ESCALATOR:
	                        //if (sf.direction == PathRoutingManager.SwitchFloorInfo.UP)
	                          //  msgView.setText("�ɯ责��: \n�зf��߱�W�Ӧ�" + mSails.getFloorDescription(sf.toFloorname));
	                        //else if (sf.direction == PathRoutingManager.SwitchFloorInfo.DOWN)
	                          //  msgView.setText("�ɯ责��: \n�зf��߱�U�Ӧ�" + mSails.getFloorDescription(sf.toFloorname));
	                        break;

	                    case PathRoutingManager.SwitchFloorInfo.STAIR:
	                        //if (sf.direction == PathRoutingManager.SwitchFloorInfo.UP)
	                          //  msgView.setText("�ɯ责��: \n�Ш��ӱ�W�Ӧ�" + mSails.getFloorDescription(sf.toFloorname));
	                        //else if (sf.direction == PathRoutingManager.SwitchFloorInfo.DOWN)
	                          //  msgView.setText("�ɯ责��: \n�Ш��ӱ�U�Ӧ�" + mSails.getFloorDescription(sf.toFloorname));
	                        break;

	                    case PathRoutingManager.SwitchFloorInfo.DESTINATION:
	                        //msgView.setText("�ɯ责��: \n�e��" + sf.fromBelongsRegion.getName());
	                        break;
	                }
	            }
	        });
	    }

	    /*
	    void slidingMenuInitial() {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                //1st stage groups
	                List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
	                //2nd stage groups
	                List<List<Map<String, LocationRegion>>> childs = new ArrayList<List<Map<String, LocationRegion>>>();
	                for (String mS : mSails.getFloorNameList()) {
	                    Map<String, String> group_item = new HashMap<String, String>();
	                    group_item.put("group", mSails.getFloorDescription(mS));
	                    groups.add(group_item);

	                    List<Map<String, LocationRegion>> child_items = new ArrayList<Map<String, LocationRegion>>();
	                    for (LocationRegion mlr : mSails.getLocationRegionList(mS)) {
	                        if (mlr.getName() == null || mlr.getName().length() == 0)
	                            continue;

	                        Map<String, LocationRegion> childData = new HashMap<String, LocationRegion>();
	                        childData.put("child", mlr);
	                        child_items.add(childData);
	                    }
	                    childs.add(child_items);
	                }
	                eAdapter = new ExpandableAdapter(getBaseContext(), groups, childs);
	                expandableListView.setAdapter(eAdapter);
	            }
	        });
	    }

	    ExpandableListView.OnChildClickListener childClickListener = new ExpandableListView.OnChildClickListener() {
	        @Override
	        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

	            LocationRegion lr = eAdapter.childs.get(groupPosition).get(childPosition).get("child");

	            if (!lr.getFloorName().equals(mSailsMapView.getCurrentBrowseFloorName())) {
	                mSailsMapView.loadFloorMap(lr.getFloorName());
	                mSailsMapView.getMapViewPosition().setZoomLevel((byte) 19);
	                Toast.makeText(getBaseContext(), mSails.getFloorDescription(lr.getFloorName()), Toast.LENGTH_SHORT).show();
	            }
	            GeoPoint poi = new GeoPoint(lr.getCenterLatitude(), lr.getCenterLongitude());
	            mSailsMapView.setAnimationMoveMapTo(poi);
	            menu.showContent();
	            
	            if (mSails.isLocationEngineStarted()) {
	                //set routing start point to current user location.
	                mSailsMapView.getRoutingManager().setStartRegion(PathRoutingManager.MY_LOCATION);

	                //set routing end point marker icon.
	                mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

	                //set routing path's color.
	                mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);

	                endRouteButton.setVisibility(View.VISIBLE);
	                currentFloorDistanceView.setVisibility(View.VISIBLE);
	                msgView.setVisibility(View.VISIBLE);

	            } else {
	                mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.map_destination)));
	                mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF85b038);
	                if (mSailsMapView.getRoutingManager().getStartRegion() != null)
	                    endRouteButton.setVisibility(View.VISIBLE);
	            }

	            //set routing end point location.
	            mSailsMapView.getRoutingManager().setTargetRegion(lr);

	            //begin to route.
	            if (mSailsMapView.getRoutingManager().enableHandler())
	                distanceView.setVisibility(View.VISIBLE);
	            return false;
	        }
	    };
	*/
	    public void lockCneter() {
	    	 if (!mSails.isLocationFix() || !mSails.isLocationEngineStarted()) {
                 Toast t = Toast.makeText(getBaseContext(), "Location Not Found or Location Engine Turn Off.", Toast.LENGTH_SHORT);
                 t.show();
                 return;
             }
             if (!mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor()) {
                 //set the map that currently location engine recognize.
                 mSailsMapView.loadCurrentLocationFloorMap();

                 Toast t = Toast.makeText(getBaseContext(), "Go Back to Locating Floor First.", Toast.LENGTH_SHORT);
                 t.show();
                 return;
             }
             //set map mode.
             //FOLLOW_PHONE_HEADING: the map follows the phone's heading.
             //LOCATION_CENTER_LOCK: the map locks the current location in the center of map.
             //ALWAYS_LOCK_MAP: the map will keep the mode even user moves the map.
             if (mSailsMapView.isCenterLock()) {
                 if ((mSailsMapView.getMode() & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)
                     //if map control mode is follow phone heading, then set mode to location center lock when button click.
                     mSailsMapView.setMode(mSailsMapView.getMode() & ~SAILSMapView.FOLLOW_PHONE_HEADING);
                 else
                     //if map control mode is location center lock, then set mode to follow phone heading when button click.
                     mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.FOLLOW_PHONE_HEADING);
             } else {
                 //if map control mode is none, then set mode to loction center lock when button click.
                 mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.LOCATION_CENTER_LOCK);
             }
	    }
	    
	    public void closeNavigation() {
	    	  endRouteButton.setVisibility(View.INVISIBLE);
             
              mSailsMapView.getRoutingManager().disableHandler();
	    }
	    
	    View.OnClickListener controlListener = new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            if (v == zoomin) {
	                //set map zoomin function.
	                mSailsMapView.zoomIn();
	                
	            } else if (v == zoomout) {
	                //set map zoomout function.
	                mSailsMapView.zoomOut();
	            } else if (v == lockcenter) {
	                if (!mSails.isLocationFix() || !mSails.isLocationEngineStarted()) {
	                   if(!mSails.isLocationEngineStarted()) {
	                	   startLocateEngine();
	                	   zoominOnce = false;
	                   }
	                	//Toast t = Toast.makeText(getBaseContext(), "Location Not Found or Location Engine Turn Off.", Toast.LENGTH_SHORT);
	                    //t.show();
	                    //return;
	                }
	                if (!mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor()) {
	                    //set the map that currently location engine recognize.
	                    mSailsMapView.loadCurrentLocationFloorMap();

	                    Toast t = Toast.makeText(getBaseContext(), "Go Back to Locating Floor First.", Toast.LENGTH_SHORT);
	                    t.show();
	                    return;
	                }
	                //set map mode.
	                //FOLLOW_PHONE_HEADING: the map follows the phone's heading.
	                //LOCATION_CENTER_LOCK: the map locks the current location in the center of map.
	                //ALWAYS_LOCK_MAP: the map will keep the mode even user moves the map.
	                if (mSailsMapView.isCenterLock()) {
	                    if ((mSailsMapView.getMode() & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)
	                        //if map control mode is follow phone heading, then set mode to location center lock when button click.
	                        mSailsMapView.setMode(mSailsMapView.getMode() & ~SAILSMapView.FOLLOW_PHONE_HEADING);
	                    else
	                        //if map control mode is location center lock, then set mode to follow phone heading when button click.
	                        mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.FOLLOW_PHONE_HEADING);
	                } else {
	                    //if map control mode is none, then set mode to loction center lock when button click.
	                    mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.LOCATION_CENTER_LOCK);
	                }
	            } else if (v == endRouteButton) {
	                endRouteButton.setVisibility(View.INVISIBLE);
	                //distanceView.setVisibility(View.INVISIBLE);
	                //currentFloorDistanceView.setVisibility(View.INVISIBLE);
	                //msgView.setVisibility(View.INVISIBLE);
	                //end route.
	                mSailsMapView.getRoutingManager().disableHandler();
	                btnNavigationTarget.setVisibility(btnNavigationTarget.INVISIBLE);
    				btnToNavigation.setVisibility(btnToNavigation.VISIBLE);
	            } /*else if (v == pinMarkerButton) {
	                Toast.makeText(getApplication(), "Please Touch Map and Set PinMarker.", Toast.LENGTH_SHORT).show();
	                mSailsMapView.getPinMarkerManager().setOnPinMarkerGenerateCallback(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.parking_target)), new PinMarkerManager.OnPinMarkerGenerateCallback() {
	                    @Override
	                    public void OnGenerate(MarkerManager.LocationRegionMarker locationRegionMarker) {
	                        Toast.makeText(getApplication(), "One PinMarker Generated.", Toast.LENGTH_SHORT).show();
	                    }
	                });
	            }*/
	            else if(v == btnToNavigation) {
	            	Intent intent = new Intent();
	                intent.setClass(SailsTechActivity.this,NavigationRouteSetting.class);
	                Bundle bundle = new Bundle();
	                bundle.putParcelableArrayList("POI",allPoiNames);//.putDouble("height",height );
	                intent.putExtras(bundle);
	                
	                startActivityForResult(intent, REQUSET_CODE_ROUTESETTING);//.startActivity(intent);
	                
	            }
	        }
	    };
		

	    
	    public void startLocateEngine() {
	    	 if (mSails.checkMode(SAILS.WIFI_GFP_IMU)) {
                 if (!mSails.isWiFiTurnOn()) {
                     Toast.makeText(this, "Please Turn on WiFi.", Toast.LENGTH_SHORT).show();
                     
                 }
             } else if (mSails.checkMode(SAILS.BLE_GFP_IMU) || mSails.checkMode(SAILS.BTE_ADVERTISING)) {
                 if (!mSails.isBluetoothTurnOn()) {
                     Toast.makeText(this, "Please Turn on Bluetooth.", Toast.LENGTH_SHORT).show();
                     
                 }

                 if (!mSails.isBLEAvailable()) {
                     Toast.makeText(this, "Your device not have BT4.0 capability.", Toast.LENGTH_SHORT).show();
                     
                 }
             }

             if (!mSails.isLocationEngineStarted()) {
                 mSails.startLocatingEngine();
                 mSailsMapView.setLocatorMarkerVisible(true);
                 Toast.makeText(this, "Start Location ", Toast.LENGTH_SHORT).show();
                 mSailsMapView.setMode(SAILSMapView.LOCATION_CENTER_LOCK | SAILSMapView.FOLLOW_PHONE_HEADING);
                 lockcenter.setVisibility(View.VISIBLE);
                 endRouteButton.setVisibility(View.INVISIBLE);
             }
	    }
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        //getMenuInflater().inflate(R.menu.menu_sailstech, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	       /* int id = item.getItemId();
	        switch (id) {
	            /*case android.R.id.home:
	                if (menu.isMenuShowing())
	                    menu.showContent();
	                else
	                    menu.showMenu();


	                //collapse all expandable groups.
	                if (eAdapter != null) {
	                    for (int i = 0; i < eAdapter.groups.size(); i++)
	                        expandableListView.collapseGroup(i);
	                }

	                return true;
	
	            case R.id.start_location_engine:
	                if (mSails.checkMode(SAILS.WIFI_GFP_IMU)) {
	                    if (!mSails.isWiFiTurnOn()) {
	                        Toast.makeText(this, "Please Turn on WiFi.", Toast.LENGTH_SHORT).show();
	                        return true;
	                    }
	                } else if (mSails.checkMode(SAILS.BLE_GFP_IMU) || mSails.checkMode(SAILS.BTE_ADVERTISING)) {
	                    if (!mSails.isBluetoothTurnOn()) {
	                        Toast.makeText(this, "Please Turn on Bluetooth.", Toast.LENGTH_SHORT).show();
	                        return true;
	                    }

	                    if (!mSails.isBLEAvailable()) {
	                        Toast.makeText(this, "Your device not have BT4.0 capability.", Toast.LENGTH_SHORT).show();
	                        return true;
	                    }
	                }

	                if (!mSails.isLocationEngineStarted()) {
	                    mSails.startLocatingEngine();
	                    mSailsMapView.setLocatorMarkerVisible(true);
	                    Toast.makeText(this, "Start Location Engine", Toast.LENGTH_SHORT).show();
	                    mSailsMapView.setMode(SAILSMapView.LOCATION_CENTER_LOCK | SAILSMapView.FOLLOW_PHONE_HEADING);
	                    lockcenter.setVisibility(View.VISIBLE);
	                    endRouteButton.setVisibility(View.INVISIBLE);
	                }

	                return true;

	            case R.id.stop_location_engine:
	                if (mSails.isLocationEngineStarted()) {
	                    mSails.stopLocatingEngine();
	                    mSailsMapView.setLocatorMarkerVisible(false);
	                    mSailsMapView.setMode(SAILSMapView.GENERAL);
	                    mSailsMapView.getRoutingManager().disableHandler();
	                    //pinMarkerButton.setVisibility(View.VISIBLE);
	                    endRouteButton.setVisibility(View.INVISIBLE);
	                    //distanceView.setVisibility(View.INVISIBLE);
	                    //currentFloorDistanceView.setVisibility(View.INVISIBLE);
	                    //msgView.setVisibility(View.INVISIBLE);
	                    Toast.makeText(this, "Stop Location Engine", Toast.LENGTH_SHORT).show();
	                }
	                return true;

	            default:
	                return super.onOptionsItemSelected(item);
	        }
	        */
	    	return super.onOptionsItemSelected(item);
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	       // mSailsMapView.onResume();
	        IbeaconService.sailsTechActivity = this;
	        ApplicationController.isToSailTechMode = true;
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	        //mSailsMapView.onPause();
	        ApplicationController.isToSailTechMode = false;
	    }

	    @Override
	    public void onBackPressed() {
	        finish();
	        super.onBackPressed();
	        ((FrameLayout) findViewById(R.id.SAILSMap)).removeAllViews();
	    }
	    
	    @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			isFromonActivityResult = true;
			switch(requestCode){
	        case REQUSET_CODE_ROUTESETTING :
	        	if(resultCode!=0) {
	        		
	        		int startPositionIndex = data.getExtras().getInt("start");
	        		int endPositionIndex = data.getExtras().getInt("end");
	        		POI endPOINode = this.allPoiNames.get(endPositionIndex);
	        		//currentEndPOINode = endPOINode;
	        		String s = "Start: ";
	        		
	        		if(startPositionIndex>0) {
	        			if (mSails.isLocationEngineStarted()) {
	        				LocationRegion slr = allPoiRegion.get(startPositionIndex-1);
	        			
	        				LocationRegion elr = allPoiRegion.get(endPositionIndex);
	        			
	        				mSailsMapView.getMarkerManager().clear();
	        				mSailsMapView.getRoutingManager().setStartRegion(slr);
	        				mSailsMapView.getMarkerManager().setLocationRegionMarker(slr, Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));
	 	            
	        				mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

	        				//set routing path's color.
	        				mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);

	        				endRouteButton.setVisibility(View.VISIBLE);
	                    
	        				mSailsMapView.getRoutingManager().setTargetRegion(elr);

	        				//begin to route.
	        				mSailsMapView.getRoutingManager().enableHandler();
	        				btnNavigationTarget.setText(endPOINode.POIName);
	        				btnNavigationTarget.setVisibility(btnNavigationTarget.VISIBLE);
	        				btnToNavigation.setVisibility(btnToNavigation.INVISIBLE);
	        				//Toast.makeText(SailsTechActivity.this, slr.getName()+" And "+elr.getName(), 300).show();
	        			}
	        		}
	        		else {
	        			s += "Current Position\n";
	        			if (mSails.isLocationEngineStarted()) {
		                    //set routing start point to current user location.
		                    mSailsMapView.getRoutingManager().setStartRegion(PathRoutingManager.MY_LOCATION);

		                    //set routing end point marker icon.
		                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

		                    //set routing path's color.
		                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);

		                    endRouteButton.setVisibility(View.VISIBLE);
		                    //currentFloorDistanceView.setVisibility(View.VISIBLE);
		                    //msgView.setVisibility(View.VISIBLE);
		                    LocationRegion elr = allPoiRegion.get(endPositionIndex);
		                    
		                    mSailsMapView.getRoutingManager().setTargetRegion(elr);

			                //begin to route.
			                mSailsMapView.getRoutingManager().enableHandler();
			                btnNavigationTarget.setText(endPOINode.POIName);
			                btnNavigationTarget.setVisibility(btnNavigationTarget.VISIBLE);
	        				btnToNavigation.setVisibility(btnToNavigation.INVISIBLE);
		                    //Toast.makeText(SailsTechActivity.this, "current position And "+elr.getName(), 300).show();
	        			}
	        			
	        		}
	        	}
	        	
	        break;
	        }
		}
	/*
	    class ExpandableAdapter extends BaseExpandableListAdapter {

	        private Context context;
	        List<Map<String, String>> groups;
	        List<List<Map<String, LocationRegion>>> childs;

	        public ExpandableAdapter(Context context, List<Map<String, String>> groups, List<List<Map<String, LocationRegion>>> childs) {
	            this.context = context;
	            this.groups = groups;
	            this.childs = childs;
	        }

	        @Override
	        public int getGroupCount() {
	            return groups.size();
	        }

	        @Override
	        public int getChildrenCount(int groupPosition) {
	            return childs.get(groupPosition).size();
	        }

	        @Override
	        public Object getGroup(int groupPosition) {
	            return groups.get(groupPosition);
	        }

	        @Override
	        public Object getChild(int groupPosition, int childPosition) {
	            return childs.get(groupPosition).get(childPosition);
	        }

	        @Override
	        public long getGroupId(int groupPosition) {
	            return groupPosition;
	        }

	        @Override
	        public long getChildId(int groupPosition, int childPosition) {
	            return childPosition;
	        }

	        @Override
	        public boolean hasStableIds() {
	            return false;
	        }

	        @Override
	        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.group, null);
	            String text = ((Map<String, String>) getGroup(groupPosition)).get("group");
	            TextView tv = (TextView) linearLayout.findViewById(R.id.group_tv);
	            tv.setText(text);
	            linearLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
	            tv.setTextColor(getResources().getColor(android.R.color.white));
	            return linearLayout;
	        }

	        @Override
	        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.child, null);
	            LocationRegion lr = ((Map<String, LocationRegion>) getChild(groupPosition, childPosition)).get("child");
	            TextView tv = (TextView) linearLayout.findViewById(R.id.child_tv);
	            tv.setText(lr.getName());
	            ImageView imageView = (ImageView) linearLayout.findViewById(R.id.child_iv);
	            imageView.setImageResource(R.drawable.expand_item);
	            return linearLayout;
	        }

	        @Override
	        public boolean isChildSelectable(int groupPosition, int childPosition) {
	            return true;
	        }
	    }
	    */
	    private ArrayList<HashMap> aryBeaconMsg = new ArrayList<HashMap>();
		private boolean isMsgDialogOpen = false;
		private boolean isNeedToOpenFile = false;
		private String TAG;
		
		public void parseNavigationMsg(JSONObject response, boolean isDone) {
			Log.i(TAG, "parseNavigationMsg BEGIN");
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
										
										//RtlsApiProxy proxy = new RtlsApiProxy(this);
										JsonObjectPostRequest jsonObjectRequest =
												updateStatus(dataContent.getString("ap_message"));
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

			Intent intent = new Intent();
			intent.putExtra("ap_msg", vo);
			intent.setClass(this, PushMsgFileActivity.class);
			this.startActivity(intent);
		}
		
		public JsonObjectPostRequest updNavigation(String androidID, String x,
				String y, String floor, String location) {
			Log.i(TAG, "updNavigation BEGIN");
			String apiURL = "http://218.211.88.196/api/updNavigation.php";

			Map<String, String> mMap = new HashMap<String, String>();
			mMap.put("client_id", "4765272503474547");
			mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
			mMap.put("version", "1.0");
			mMap.put("cmd", "android");
			mMap.put("type", "json");
			mMap.put("method", "updNavigation");
			mMap.put("UUID", androidID);
			mMap.put("Floor", floor);
			mMap.put("pointX", x);//
			mMap.put("pointY", y);
			mMap.put("nickName", "");
			mMap.put("location", location);
			Log.i("JsonObjectPostRequest", "send data = " + mMap.toString());

			JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(
					apiURL, new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							// TODO Auto-generated method stub
							Log.i("updLocationAvg", "onResponse response ="
									+ response.toString());
							parseNavigationMsg(response, true);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("updLocationAvg", "onErrorResponse error = "
									+ error.getMessage());
							parseNavigationMsg(null, true);
						}
					}, mMap);

			return jsonObjectRequest;
		}

		public JsonObjectPostRequest updateStatus(String apMsg) {
			// TODO Auto-generated method stub
			Log.i(TAG, "updateStatus BEGIN");

			String apiURL = "http://218.211.88.196/api/updMessageLog.php";
			
			Map<String, String> mMap = new HashMap<String, String>();
			mMap.put("client_id", "4765272503474547");
			mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
			mMap.put("version", "1.0");
			mMap.put("cmd", "android");
			mMap.put("type", "json");
			mMap.put("method", "updMessageLog");
			mMap.put("UUID", ApplicationController.getInstance().getUuid());
			mMap.put("ap_message", apMsg);
			Log.i("JsonObjectPostRequest", "send data = " + mMap.toString());
			
			JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(
					apiURL, new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							// TODO Auto-generated method stub
							Log.i("updLocationAvg", "onResponse response ="
									+ response.toString());
							
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("updLocationAvg", "onErrorResponse error = "
									+ error.getMessage());
							
						}
					}, mMap);

			return jsonObjectRequest;
		}
}