package map.navi.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.graph.BiGraph;
import com.doubleservice.bigcitynavigation.graph.Node;
import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Array;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;
import com.longevitysoft.android.xml.plist.domain.PListObject;

import lib.locate.KNN.WifiReferencePointVO;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

 

public class NaviPlan {
	
	 public BiGraph naviPlanGraph; 
	 public Activity act;
	 public HashMap<String,Object> nodeCollection = new HashMap<String,Object>();
	 public ArrayList<POI> POICollection = new ArrayList<POI>();
	 public HashMap<String,Object> plans = new HashMap<String,Object>();
	 public int planCount = 0;
	 public int[] mapIDs ={R.drawable.map_bigcity /*R.drawable.map_sogo*/,R.drawable.img_map_sogo,R.drawable.map_parking_place};//R.drawable.ds_map};//R.drawable.map_adv_is};//,R.drawable.ds_map};//,R.drawable.map_sogo,R.drawable.map_bigcity};//R.drawable.ds_map};//
	 public String[] mapNames = {"BigCity", "SOGO","B3"};//"DS-OFFICE"};//"���剖央���剖�嚙罷;//,"DS-OFFICE"};//,"Sogo��鞎��楊���耑ig City����,"Big City���剖��文�頦���剖�銴��剖�頦����};//"DS-OFFICE"};//{
	 public String[] pLists = {"beacon.plist","beacon.plist","beacon.plist"};//"BigCityWifiList.plist", "SogoWifiList.plist","SogoWifiList.plist"};//"ADV_IS_WifiAPList.plist"};//,"WifiAPList.plist"};//,"WifiAPList.plist","WifiAPList.plist"};
	 public String[] areaNameInXML = {"BIG CITY","SOGO","B3"};
	 public String[] floorName = {"F4"};//,"F4"};
	 public String toParkingPlaceBeacon = "0_1";

	 public float currentMapPixelperMeter = 0f;
	 public float currentStandard_azimuth = 0f;
	 public float currentRoadDeviation = 0f;
	 public String currentMapName ="";
	 public POI currentParkingElevator ;

	 public HashMap<String,PushMessageNode> MessageCollection = new HashMap<String,PushMessageNode>();
	 public HashMap<String,PushMessageNode> PositionCollection = new HashMap<String,PushMessageNode>();
	 private Handler handler = new Handler();
	 
	 public int getMapIndexByName(String name) {
		 for(int index =0;index<areaNameInXML.length;index++) {
			 if(name.equals(areaNameInXML[index])) {
				 return index;
			 }
		 }
		 for(int index =0;index<mapNames.length;index++) {
			 if(name.equals(mapNames[index])){
				 return index;
			 }
		 }
		 return -1;
	 }
	 
	 public NaviPlan(Activity act){
		  naviPlanGraph = new BiGraph();
		  
		  this.act = act;
		  this.mapNames[2] = this.act.getResources().getString(R.string.map_parking_map_name);
		  this.areaNameInXML[2] = this.act.getResources().getString(R.string.map_parking_map_name);
		  //plans.put("DS", 0);
		 // plans.put("嚙踐�嚙踝蕭�蕭�佇���, 1);
		  //plans.put("嚙踐�嚙踝蕭謕蕭嚙踝蕭, 2);
	 }	 
	 
	 public void setPlan(int planID) {
		 switch(planID) {
		 	case 0:
		 		this.BigCity_PLAN();
		 		//this.SOGO_PLAN();
		 		//this.DS_OFFICE_PLAN();
		 		//this.ADV_IS_PLAN();
		 	break;	
		 	case 1:
		 		this.SOGO_PLAN();
		 		
		 		//this.DS_OFFICE_PLAN();
			break;
		 	case 2:
		 		b3_PLAN();
		 		//this.DS_OFFICE_PLAN();
		 		//this.SOGO_PLAN();
		 	break;
		 	case 3:
		 		//this.BigCity_PLAN();
		 	break;
		 }
	 }
	 
	 /*
	 public void setPlan(String planName) {
		 Set<String> keys = this.plans.keySet();
		 for(String key:keys) {
			 if(planName.contains(key)) {
				 int planNumber = (Integer)this.plans.get(key);
				 switch(planNumber) {
				 	case 0:
				 		this.DS_OFFICE_PLAN();
				 	break;
				 	case 2:
				 		this.SOGO_PLAN();
					break;
				 }
				 break;
			 }
		 }
	 }
	 */
	 private void cleanr() {
		 naviPlanGraph = new BiGraph();
		 nodeCollection.clear();
		 POICollection.clear();
		 MessageCollection.clear();
	 }
	 
	 private void addPOITOGraph(String areaNameInXML,String floor) {
		// ArrayList<POI> POIs = new ArrayList<POI>();
		 for(LocationData data :ApplicationController.locationsData) {
			// Log.i("Plan", data.dataLog());
			 if(data.locationArea.equals(areaNameInXML) && data.locationFloor.equals(floor)) {
				
				POI poi = new POI(data.locationNumber,Integer.parseInt(data.locationPosX),Integer.parseInt(data.locationPosY),data.locationTitle);
				this.addNodeToGraph(poi);
				//POIs.add(poi);
			 }
		 }
		// return POIs;
	 }
	 
	 public void b3_PLAN() {
		 this.cleanr();
			this.currentMapPixelperMeter = 16f;
			this.currentRoadDeviation = 20f;
			this.currentStandard_azimuth = 130f;
			this.currentMapName = mapNames[2];
			NaviNode LineNode1 = new NaviNode("1",1063,411);
			NaviNode LineNode2 = new NaviNode("2",1063,421);
			NaviNode LineNode3 = new NaviNode("3",1063,431);
			NaviNode LineNode4 = new NaviNode("4",1063,441);
			NaviNode LineNode5 = new NaviNode("5",1063,451);
			NaviNode LineNode6 = new NaviNode("6",1063,461);
			NaviNode LineNode7 = new NaviNode("7",1063,471);
			NaviNode LineNode8 = new NaviNode("8",1063,483);
			NaviNode LineNode9 = new NaviNode("9",1063,493);
			NaviNode LineNode10 = new NaviNode("10",1063,503);
			NaviNode LineNode11 = new NaviNode("11",1063,513);
			NaviNode LineNode12 = new NaviNode("12",1063,523);
			NaviNode LineNode13 = new NaviNode("13",1063,533);
			NaviNode LineNode14 = new NaviNode("14",1063,543);
			NaviNode LineNode15 = new NaviNode("15",1065,552);
			NaviNode LineNode16 = new NaviNode("16",1053,553);
			NaviNode LineNode17 = new NaviNode("17",1043,553);
			NaviNode LineNode18 = new NaviNode("18",1033,553);
			NaviNode LineNode19 = new NaviNode("19",1023,553);
			NaviNode LineNode20 = new NaviNode("20",1013,553);
			NaviNode LineNode21 = new NaviNode("21",1004,555);
			NaviNode LineNode22 = new NaviNode("22",994,561);
			NaviNode LineNode23 = new NaviNode("23",984,567);
			NaviNode LineNode24 = new NaviNode("24",974,572);
			NaviNode LineNode25 = new NaviNode("25",964,578);
			NaviNode LineNode26 = new NaviNode("26",958,584);
			NaviNode LineNode27 = new NaviNode("27",944,588);
			NaviNode LineNode28 = new NaviNode("28",934,592);
			NaviNode LineNode29 = new NaviNode("29",924,596);
			NaviNode LineNode30 = new NaviNode("30",914,600);
			NaviNode LineNode31 = new NaviNode("31",908,609);
			NaviNode LineNode32 = new NaviNode("32",915,661);
			this.addNodeToGraph(LineNode1);
			this.addNodeToGraph(LineNode2);
			this.addNodeToGraph(LineNode3);
			this.addNodeToGraph(LineNode4);
			this.addNodeToGraph(LineNode5);
			this.addNodeToGraph(LineNode6);
			this.addNodeToGraph(LineNode7);
			this.addNodeToGraph(LineNode8);
			this.addNodeToGraph(LineNode9);
			this.addNodeToGraph(LineNode10);
			this.addNodeToGraph(LineNode11);
			this.addNodeToGraph(LineNode12);
			this.addNodeToGraph(LineNode13);
			this.addNodeToGraph(LineNode14);
			this.addNodeToGraph(LineNode15);
			this.addNodeToGraph(LineNode16);
			this.addNodeToGraph(LineNode17);
			this.addNodeToGraph(LineNode18);
			this.addNodeToGraph(LineNode19);
			this.addNodeToGraph(LineNode20);
			this.addNodeToGraph(LineNode21);
			this.addNodeToGraph(LineNode22);
			this.addNodeToGraph(LineNode23);
			this.addNodeToGraph(LineNode24);
			this.addNodeToGraph(LineNode25);
			this.addNodeToGraph(LineNode26);
			this.addNodeToGraph(LineNode27);
			this.addNodeToGraph(LineNode28);
			this.addNodeToGraph(LineNode29);
			this.addNodeToGraph(LineNode30);
			this.addNodeToGraph(LineNode31);
			this.addNodeToGraph(LineNode32);
			
			this.addEdgeToGraph(LineNode1,LineNode2);
			this.addEdgeToGraph(LineNode2,LineNode3);
			this.addEdgeToGraph(LineNode3,LineNode4);
			this.addEdgeToGraph(LineNode4,LineNode5);
			this.addEdgeToGraph(LineNode5,LineNode6);
			this.addEdgeToGraph(LineNode6,LineNode7);
			this.addEdgeToGraph(LineNode7,LineNode8);
			this.addEdgeToGraph(LineNode8,LineNode9);
			this.addEdgeToGraph(LineNode9,LineNode10);
			this.addEdgeToGraph(LineNode10,LineNode11);
			this.addEdgeToGraph(LineNode11,LineNode12);
			this.addEdgeToGraph(LineNode12,LineNode13);
			this.addEdgeToGraph(LineNode13,LineNode14);
			this.addEdgeToGraph(LineNode14,LineNode15);
			this.addEdgeToGraph(LineNode15,LineNode16);
			this.addEdgeToGraph(LineNode16,LineNode17);
			this.addEdgeToGraph(LineNode17,LineNode18);
			this.addEdgeToGraph(LineNode18,LineNode19);
			this.addEdgeToGraph(LineNode19,LineNode20);
			this.addEdgeToGraph(LineNode20,LineNode21);
			this.addEdgeToGraph(LineNode21,LineNode22);
			this.addEdgeToGraph(LineNode22,LineNode23);
			this.addEdgeToGraph(LineNode23,LineNode24);
			this.addEdgeToGraph(LineNode24,LineNode25);
			this.addEdgeToGraph(LineNode25,LineNode26);
			this.addEdgeToGraph(LineNode26,LineNode27);
			this.addEdgeToGraph(LineNode27,LineNode28);
			this.addEdgeToGraph(LineNode28,LineNode29);
			this.addEdgeToGraph(LineNode29,LineNode30);
			this.addEdgeToGraph(LineNode30,LineNode31);
			this.addEdgeToGraph(LineNode31,LineNode32);

	 }
	 public void ADV_IS_PLAN() {
			this.cleanr();
			this.currentMapPixelperMeter = 16f;
			this.currentRoadDeviation = 20f;
			this.currentStandard_azimuth = 130f;
			currentMapName = "";
			NaviNode LineNode1 = new NaviNode("1",220,147);
			NaviNode LineNode2 = new NaviNode("2",220,255);
			NaviNode LineNode3 = new NaviNode("3",220,360);
			NaviNode LineNode4 = new NaviNode("4",450,360);
			NaviNode LineNode5 = new NaviNode("5",450,255);
			NaviNode LineNode6 = new NaviNode("6",450,147);
			//NaviNode LineNode7 = new NaviNode("7",725,147);
			//NaviNode LineNode8 = new NaviNode("8",725,255);
			//NaviNode LineNode9 = new NaviNode("9",725,360);
			NaviNode LineNode10 = new NaviNode("10",392,147);
			NaviNode LineNode11 = new NaviNode("11",305,147);
			NaviNode LineNode12 = new NaviNode("12",246,147);
			//NaviNode LineNode13 = new NaviNode("13",505,147);
			//NaviNode LineNode14 = new NaviNode("14",505,255);
			//NaviNode LineNode15 = new NaviNode("15",505,360);
			
			POI POI1 = new POI("16",392,121,"301");
			POI POI2 = new POI("17",305,121,"302");
			POI POI3 = new POI("18",246,121,"303");
			//POI POI4 = new POI("13",333,250,"嚙踐�嚙踝蕭�蕭�ｇ�亦�謜蕭�減�蹓蕭嚙�;
			//PushMessageNode message1 = new PushMessageNode("1"+"_"+"0","Welcome BigCity!",-50f,"demo_promotion.pdf");
			//PushMessageNode message2 = new PushMessageNode("2"+"_"+"0","Welcome 嚙踐�璆蕭謕蕭嚙踐��蕭謕蕭嚙踝蕭",-55f,"ipad_air_1.pdf");
			//"demo_promotion", "ipad_air_1"
			//MessageCollection.put("1"+"_"+"0", message1);//.add(message1);
			//MessageCollection.put("2"+"_"+"0", message2);//add(message2);
				// POI POI4 = new POI("14",290,155,"Shop5");
				// POI POI5 = new POI("15",290,200,"Shop6");
				// POI POI6 = new POI("16");
				// POI POI7 = new POI("17");
				// POI POI8 = new POI("18");
			this.addNodeToGraph(LineNode1);
			this.addNodeToGraph(LineNode2);
			this.addNodeToGraph(LineNode3);
			this.addNodeToGraph(LineNode4);
			this.addNodeToGraph(LineNode5);
			this.addNodeToGraph(LineNode6);
			//this.addNodeToGraph(LineNode7);
			//this.addNodeToGraph(LineNode8);
			//this.addNodeToGraph(LineNode9);
			this.addNodeToGraph(LineNode10);
			this.addNodeToGraph(LineNode11);
			this.addNodeToGraph(LineNode12);
			//this.addNodeToGraph(LineNode13);
			//this.addNodeToGraph(LineNode14);
			//this.addNodeToGraph(LineNode15);
			//this.addNodeToGraph(POI0);
			this.addNodeToGraph(POI1);
			this.addNodeToGraph(POI2);
			this.addNodeToGraph(POI3);
			//this.addNodeToGraph(POI4);
			  //this.addNodeToGraph(POI5);
			  
			this.addEdgeToGraph(LineNode1,LineNode2);
			//this.addEdgeToGraph(LineNode1,LineNode6);
			this.addEdgeToGraph(LineNode1,LineNode12);
			this.addEdgeToGraph(LineNode2,LineNode3);
			this.addEdgeToGraph(LineNode3,LineNode4);
			this.addEdgeToGraph(LineNode4,LineNode5);
			//this.addEdgeToGraph(LineNode4,LineNode9);//LineNode15);
			this.addEdgeToGraph(LineNode5,LineNode6);
			//this.addEdgeToGraph(LineNode5,LineNode14);
			this.addEdgeToGraph(LineNode6,LineNode10);
			//this.addEdgeToGraph(LineNode6,LineNode7);
			//this.addEdgeToGraph(LineNode7,LineNode8);
			//this.addEdgeToGraph(LineNode7,LineNode13);
			//this.addEdgeToGraph(LineNode8,LineNode9);
			//this.addEdgeToGraph(LineNode9,LineNode15);
			this.addEdgeToGraph(LineNode10,LineNode11);
			this.addEdgeToGraph(LineNode11,LineNode12);
			this.addEdgeToGraph(LineNode10,POI1);
			this.addEdgeToGraph(LineNode11,POI2);
			this.addEdgeToGraph(LineNode12,POI3);
			
			//this.addEdgeToGraph(LineNode13,LineNode14);
			//this.addEdgeToGraph(LineNode14,LineNode15);
			
			//this.addEdgeToGraph(LineNode7,POI5);
		 }
		 
	 
	public void BigCity_PLAN() {
		this.cleanr();
		this.currentMapPixelperMeter = 20f;
		this.currentRoadDeviation = 40f;
		this.currentStandard_azimuth = 320f;
		//NaviPlan.loadPList2("bigcity_beacon.plist",act);
		//WifiReferencePointVO.DBName = "/sdcard/"+"bc_bigcity_referencepoint_db.db";
		currentMapName = "BigCity";
		NaviNode LineNode1 = new NaviNode("100",2381,941);
		NaviNode LineNode2 = new NaviNode("200",2159,935);
		NaviNode LineNode3 = new NaviNode("300",2025,930);
		NaviNode LineNode4 = new NaviNode("400",1883,941);
		NaviNode LineNode5 = new NaviNode("500",1559,1069);
		NaviNode LineNode6 = new NaviNode("600",1365,889);
		NaviNode LineNode7 = new NaviNode("700",1465,660);
		NaviNode LineNode8 = new NaviNode("800",1611,559);
		NaviNode LineNode9 = new NaviNode("900",1841,585);
		NaviNode LineNode10 = new NaviNode("1000",2019,741);
		NaviNode LineNode11 = new NaviNode("1100",2167,589);
		NaviNode LineNode12 = new NaviNode("1200",2027,420);
		NaviNode LineNode13 = new NaviNode("1300",1609,421);
		NaviNode LineNode14 = new NaviNode("1400",1100,417);
		NaviNode LineNode15 = new NaviNode("1500",1093,555);
		NaviNode LineNode16 = new NaviNode("1600",910,565);
		NaviNode LineNode17 = new NaviNode("1700",707,553);
		NaviNode LineNode18 = new NaviNode("1800",443,660);
		NaviNode LineNode19 = new NaviNode("1900",447,820);
		NaviNode LineNode20 = new NaviNode("2000",547,883);
		NaviNode LineNode21 = new NaviNode("2100",735,867);
		NaviNode LineNode22 = new NaviNode("2200",869,727);
		NaviNode LineNode23 = new NaviNode("2300",861,1005);
		NaviNode LineNode24 = new NaviNode("2400",191,673);
		NaviNode LineNode25 = new NaviNode("2500",1873,1171);
		NaviNode LineNode26 = new NaviNode("2600",607,589);
		NaviNode LineNode27 = new NaviNode("2700",710,675);
		//NaviNode LineNode28 = new NaviNode("28",221,195);
		//NaviNode LineNode29 = new NaviNode("29",173,210);
		currentParkingElevator = new POI("3000",2370,825,ApplicationController.getInstance().getResources().getString(R.string.parking_elevator));
		//POI POI1 = new POI("30",638,131,"");
		//POI POI2 = new POI("31",706,158,"");
		//POI POI5 = new POI("34",367,148,"");
		//PushMessageNode message1 = new PushMessageNode("1"+"_"+"0","Welcome BigCity!",-50f,"demo_promotion.pdf");
		//PushMessageNode message2 = new PushMessageNode("2"+"_"+"0","Welcome !",-55f,"ipad_air_1.pdf");
		//"demo_promotion", "ipad_air_1"
		//MessageCollection.put("1"+"_"+"0", message1);//.add(message1);
		//MessageCollection.put("2"+"_"+"0", message2);//add(message2);
			// POI POI4 = new POI("14",290,155,"Shop5");
			// POI POI5 = new POI("15",290,200,"Shop6");
			// POI POI6 = new POI("16");
			// POI POI7 = new POI("17");
			// POI POI8 = new POI("18");
		this.addNodeToGraph(LineNode1);
		this.addNodeToGraph(LineNode2);
		this.addNodeToGraph(LineNode3);
		this.addNodeToGraph(LineNode4);
		this.addNodeToGraph(LineNode5);
		this.addNodeToGraph(LineNode6);
		this.addNodeToGraph(LineNode7);
		this.addNodeToGraph(LineNode8);
		this.addNodeToGraph(LineNode9);
		this.addNodeToGraph(LineNode10);
		this.addNodeToGraph(LineNode11);
		this.addNodeToGraph(LineNode12);
		this.addNodeToGraph(LineNode13);
		this.addNodeToGraph(LineNode14);
		this.addNodeToGraph(LineNode15);
		this.addNodeToGraph(LineNode16);
		this.addNodeToGraph(LineNode17);
		this.addNodeToGraph(LineNode18);
		this.addNodeToGraph(LineNode19);
		this.addNodeToGraph(LineNode20);
		this.addNodeToGraph(LineNode21);
		this.addNodeToGraph(LineNode22);
		this.addNodeToGraph(LineNode23);
		this.addNodeToGraph(LineNode24);
		this.addNodeToGraph(LineNode25);
		this.addNodeToGraph(LineNode26);
		this.addNodeToGraph(LineNode27);
		//this.addNodeToGraph(LineNode28);
		//this.addNodeToGraph(LineNode29);
		this.addPOITOGraph(areaNameInXML[0], floorName[0]);
		
		this.addNodeToGraph(currentParkingElevator);
		
		//this.addNodeToGraph(POI0);
		
		
		//this.addNodeToGraph(POI1);
		//this.addNodeToGraph(POI2);
		//this.addNodeToGraph(POI3);
		//this.addNodeToGraph(POI4);
		//this.addNodeToGraph(POI5);
		  
		this.addEdgeToGraph(LineNode1,LineNode2);
		this.addEdgeToGraph(LineNode2,LineNode3);
		this.addEdgeToGraph(LineNode2,LineNode11);
		this.addEdgeToGraph(LineNode3,LineNode4);
		this.addEdgeToGraph(LineNode3,LineNode10);
		this.addEdgeToGraph(LineNode4,LineNode5);
		this.addEdgeToGraph(LineNode4,LineNode25);
		this.addEdgeToGraph(LineNode5,LineNode6);
		this.addEdgeToGraph(LineNode6,LineNode7);
		this.addEdgeToGraph(LineNode7,LineNode8);
		this.addEdgeToGraph(LineNode7,LineNode15);
		this.addEdgeToGraph(LineNode8,LineNode9);
		this.addEdgeToGraph(LineNode8,LineNode13);
		this.addEdgeToGraph(LineNode9,LineNode10);
		this.addEdgeToGraph(LineNode11,LineNode12);
		this.addEdgeToGraph(LineNode12,LineNode13);
		this.addEdgeToGraph(LineNode13,LineNode14);
		this.addEdgeToGraph(LineNode14,LineNode15);
		this.addEdgeToGraph(LineNode15,LineNode16);
		this.addEdgeToGraph(LineNode16,LineNode17);
		this.addEdgeToGraph(LineNode16,LineNode22);
		this.addEdgeToGraph(LineNode17,LineNode26);
		this.addEdgeToGraph(LineNode17,LineNode27);
		this.addEdgeToGraph(LineNode18,LineNode19);
		this.addEdgeToGraph(LineNode18,LineNode24);
		this.addEdgeToGraph(LineNode18,LineNode26);
		this.addEdgeToGraph(LineNode19,LineNode20);
		this.addEdgeToGraph(LineNode20,LineNode21);
		this.addEdgeToGraph(LineNode21,LineNode22);
		this.addEdgeToGraph(LineNode21,LineNode23);
		this.addEdgeToGraph(LineNode21,LineNode27);
		this.addEdgeToGraph(LineNode26,LineNode27);
		//this.naviPlanGraph.print();
		//this.addEdgeToGraph(LineNode20,LineNode27);
		//this.addEdgeToGraph(LineNode22,LineNode23);
		//this.addEdgeToGraph(LineNode23,LineNode24);
		//this.addEdgeToGraph(LineNode24,LineNode26);
		//this.addEdgeToGraph(LineNode24,LineNode29);		
		//this.addEdgeToGraph(LineNode25,LineNode27);
		//this.addEdgeToGraph(LineNode25,LineNode28);
		//this.addEdgeToGraph(LineNode25,LineNode29);
		//this.addEdgeToGraph(LineNode28,LineNode29);
		//this.addEdgeToGraph(LineNode0,POI0);
		//this.addEdgeToGraph(LineNode4,POI1);
		//this.addEdgeToGraph(LineNode3,POI2);
		//this.addEdgeToGraph(LineNode3,POI3);
		//this.addEdgeToGraph(LineNode16,POI4);
		//this.addEdgeToGraph(LineNode19,POI4);
		//this.addEdgeToGraph(LineNode7,POI5);

	 }
	 
	 public void SOGO_PLAN() {
		 this.cleanr();
		 this.currentMapPixelperMeter = 15f;
		 this.currentRoadDeviation = 30f;
		 this.currentStandard_azimuth = 320f;
		 currentMapName = "SOGO";
		// NaviPlan.loadPList2("sogo_beacon.plist",act);//
		// WifiReferencePointVO.DBName = "/sdcard/"+"sogo_bigcity_referencepoint_db.db";
		 NaviNode LineNode1 = new NaviNode("1",690,400);
		 NaviNode LineNode2 = new NaviNode("2",911,400);
		 NaviNode LineNode3 = new NaviNode("3",911,727);
		 NaviNode LineNode4 = new NaviNode("4",680,720);
		 NaviNode LineNode5 = new NaviNode("5",547,720);
		 NaviNode LineNode6 = new NaviNode("6",487,795);
		 NaviNode LineNode7 = new NaviNode("7",485,999);
		 NaviNode LineNode8 = new NaviNode("8",615,999);
		 NaviNode LineNode9 = new NaviNode("9",693,940);
		 NaviNode LineNode10 = new NaviNode("10",760,999);
		 NaviNode LineNode11 = new NaviNode("11",920,999);
		 NaviNode LineNode12 = new NaviNode("12",920,1200);
		 NaviNode LineNode13 = new NaviNode("13",689,1080);
		 NaviNode LineNode14 = new NaviNode("14",689,1200);
		 NaviNode LineNode15 = new NaviNode("15",495,1200);
		 NaviNode LineNode16 = new NaviNode("16",490,1475);
		 NaviNode LineNode17 = new NaviNode("17",700,1475);
		 NaviNode LineNode18 = new NaviNode("18",920,1475);
		 NaviNode LineNode19 = new NaviNode("19",920,1700);
		 NaviNode LineNode20 = new NaviNode("20",1180,1700);
		 NaviNode LineNode21 = new NaviNode("21",689,1700);
		 NaviNode LineNode22 = new NaviNode("22",490,1700);
		 NaviNode LineNode23 = new NaviNode("23",350,1700);
		 NaviNode LineNode24 = new NaviNode("24",1180,1820);
		 NaviNode LineNode25 = new NaviNode("25",715,225);
		 NaviNode LineNode26 = new NaviNode("26",1270,200);
		 currentParkingElevator = new POI("30",1200,90,ApplicationController.getInstance().getResources().getString(R.string.parking_elevator));
			
		 //POI POI0 = new POI("10",410,77,"NIKE");
		 //POI POI1 = new POI("11",520,154,"Shop2");
		 //POI POI2 = new POI("12",332,160,"Shop3");
		 //POI POI3 = new POI("13",530,225,"Shop4");
		 //POI POI4 = new POI("14",290,155,"Shop5");
		 // POI POI5 = new POI("15",290,200,"Shop6");
		 //PushMessageNode message1 = new PushMessageNode("1"+"_"+"0","Welcome Sogo!",-50f,"demo_promotion.pdf");
		 //PushMessageNode message2 = new PushMessageNode("2"+"_"+"0","Welcome NIKE!",-60f, "ipad_air_1.pdf");
		 //MessageCollection.put("1"+"_"+"0", message1);//.add(message1);
		 //MessageCollection.put("2"+"_"+"0", message2);//add(message2);			
		 // POI POI6 = new POI("16");
		 // POI POI7 = new POI("17");
		 // POI POI8 = new POI("18");
		 //this.addNodeToGraph(LineNode0);
		 this.addNodeToGraph(LineNode1);
		 this.addNodeToGraph(LineNode2);
		 this.addNodeToGraph(LineNode3);
		 this.addNodeToGraph(LineNode4);
		 this.addNodeToGraph(LineNode5);
		 this.addNodeToGraph(LineNode6);
		 this.addNodeToGraph(LineNode7);
		 this.addNodeToGraph(LineNode8);
		 this.addNodeToGraph(LineNode9);
		 this.addNodeToGraph(LineNode10);
		 this.addNodeToGraph(LineNode11);
		 this.addNodeToGraph(LineNode12);
		 this.addNodeToGraph(LineNode13);
		 this.addNodeToGraph(LineNode14);
		 this.addNodeToGraph(LineNode15);
		 this.addNodeToGraph(LineNode16);
		 this.addNodeToGraph(LineNode17);
		 this.addNodeToGraph(LineNode18);
		 this.addNodeToGraph(LineNode19);
		 this.addNodeToGraph(LineNode20);
		 this.addNodeToGraph(LineNode21);
		 this.addNodeToGraph(LineNode22);
		 this.addNodeToGraph(LineNode23);
		 this.addNodeToGraph(LineNode24);
		 this.addNodeToGraph(LineNode25);
		 this.addNodeToGraph(LineNode26);
		 
		 this.addPOITOGraph(areaNameInXML[1], floorName[0]);
		 this.addNodeToGraph(currentParkingElevator);
		 //this.addNodeToGraph(POI0);
		 //this.addNodeToGraph(POI1);
		 //this.addNodeToGraph(POI2);
		 //this.addNodeToGraph(POI3);
		 //this.addNodeToGraph(POI4);
		 //this.addNodeToGraph(POI5);
		  
		 this.addEdgeToGraph(LineNode1,LineNode2);
		 this.addEdgeToGraph(LineNode1,LineNode25);
		 this.addEdgeToGraph(LineNode1,LineNode4);
		 this.addEdgeToGraph(LineNode2,LineNode3);
		 this.addEdgeToGraph(LineNode3,LineNode4);
		 this.addEdgeToGraph(LineNode3,LineNode11);
		
		 this.addEdgeToGraph(LineNode4,LineNode5);
		 this.addEdgeToGraph(LineNode4,LineNode9);
		 this.addEdgeToGraph(LineNode5,LineNode6);
		 this.addEdgeToGraph(LineNode6,LineNode7);
		 this.addEdgeToGraph(LineNode7,LineNode8);
		 this.addEdgeToGraph(LineNode7,LineNode15);
		 this.addEdgeToGraph(LineNode8,LineNode9);
		 this.addEdgeToGraph(LineNode8,LineNode13);
		 this.addEdgeToGraph(LineNode9,LineNode10);
		 this.addEdgeToGraph(LineNode10,LineNode11);
		 this.addEdgeToGraph(LineNode10,LineNode13);
		 this.addEdgeToGraph(LineNode11,LineNode12);
		 this.addEdgeToGraph(LineNode12,LineNode14);
		 this.addEdgeToGraph(LineNode12,LineNode18);
		 this.addEdgeToGraph(LineNode13,LineNode14);
		 this.addEdgeToGraph(LineNode14,LineNode15);
		 this.addEdgeToGraph(LineNode15,LineNode16);
		 this.addEdgeToGraph(LineNode16,LineNode17);
		 this.addEdgeToGraph(LineNode16,LineNode22);
		 this.addEdgeToGraph(LineNode17,LineNode18);
		 this.addEdgeToGraph(LineNode17,LineNode21);
		 this.addEdgeToGraph(LineNode18,LineNode19);
		 this.addEdgeToGraph(LineNode19,LineNode20);
		 this.addEdgeToGraph(LineNode19,LineNode21);
		 this.addEdgeToGraph(LineNode20,LineNode24);
		 this.addEdgeToGraph(LineNode21,LineNode22);
		 this.addEdgeToGraph(LineNode22,LineNode23);
		 this.addEdgeToGraph(LineNode25,LineNode26);
	 }	 
	 
	 public void DS_OFFICE_PLAN() {
		this.cleanr();
		 
		this.currentMapPixelperMeter = 40f;
		this.currentRoadDeviation = 20f;
		this.currentStandard_azimuth = 275f;
		
		NaviNode LineNode1 = new NaviNode("1",36,100);
		NaviNode LineNode2 = new NaviNode("2",50,100);
		NaviNode LineNode3 = new NaviNode("3",129,100);
		NaviNode LineNode4 = new NaviNode("4",129,61);
		NaviNode LineNode5 = new NaviNode("5",129,129);
		NaviNode LineNode6 = new NaviNode("6",129,154);
		NaviNode LineNode7 = new NaviNode("7",129,60);
		NaviNode LineNode8 = new NaviNode("8",143,60);
		NaviNode LineNode9 = new NaviNode("9",174,60);
		NaviNode LineNode10 = new NaviNode("10",204,60);
		NaviNode LineNode11 = new NaviNode("11",204,80);
		NaviNode LineNode12 = new NaviNode("12",204,211);
		NaviNode LineNode13 = new NaviNode("13",245,60);
		NaviNode LineNode14 = new NaviNode("14",288,60);
		NaviNode LineNode15 = new NaviNode("15",324,60);
		NaviNode LineNode16 = new NaviNode("16",324,87);
		NaviNode LineNode17 = new NaviNode("17",324,209);
		NaviNode LineNode18 = new NaviNode("18",358,60);
		NaviNode LineNode19 = new NaviNode("19",378,60);
		NaviNode LineNode20 = new NaviNode("20",420,60);
		NaviNode LineNode21 = new NaviNode("21",420,110);
		NaviNode LineNode22 = new NaviNode("22",420,137);
		NaviNode LineNode23 = new NaviNode("23",420,195);
		NaviNode LineNode24 = new NaviNode("24",420,256);
		NaviNode LineNode25 = new NaviNode("25",420,369);
		NaviNode LineNode26 = new NaviNode("26",456,195);
		NaviNode LineNode27 = new NaviNode("27",574,195);
		NaviNode LineNode28 = new NaviNode("28",610,195);
		NaviNode LineNode29 = new NaviNode("29",610,235);
		NaviNode LineNode30 = new NaviNode("30",610,369);
		NaviNode LineNode31 = new NaviNode("31",496,195);
		
		 POI POI0 = new POI("32",17,100,"嚙踐�嚙踝蕭謕蕭");
		 POI POI1 = new POI("33",640,195,"嚙踐��蕭謕蕭嚙踐�甇蕭謕�嚙賜�嚙賡豲");
		 POI POI2 = new POI("34",383,369,"嚙踐��仿���蕭�佇���1");
		 POI POI3 = new POI("35",640,369,"嚙踐��仿���蕭�佇���2");
		// POI POI4 = new POI("35",290,155,"Shop5");
		 //POI POI5 = new POI("36",290,200,"Shop6");
		
		  this.addNodeToGraph(LineNode1);
		  this.addNodeToGraph(LineNode2);
		  this.addNodeToGraph(LineNode3);
		  this.addNodeToGraph(LineNode4);
		  this.addNodeToGraph(LineNode5);
		  this.addNodeToGraph(LineNode6);
		  this.addNodeToGraph(LineNode7);
		  this.addNodeToGraph(LineNode8);
		  this.addNodeToGraph(LineNode9);
		  this.addNodeToGraph(LineNode10);
		  this.addNodeToGraph(LineNode11);
		  this.addNodeToGraph(LineNode12);
		  this.addNodeToGraph(LineNode13);
		  this.addNodeToGraph(LineNode14);
		  this.addNodeToGraph(LineNode15);
		  this.addNodeToGraph(LineNode16);
		  this.addNodeToGraph(LineNode17);
		  this.addNodeToGraph(LineNode18);
		  this.addNodeToGraph(LineNode19);
		  this.addNodeToGraph(LineNode20);
		  this.addNodeToGraph(LineNode21);
		  this.addNodeToGraph(LineNode22);
		  this.addNodeToGraph(LineNode23);
		  this.addNodeToGraph(LineNode24);
		  this.addNodeToGraph(LineNode25);
		  this.addNodeToGraph(LineNode26);
		  this.addNodeToGraph(LineNode27);
		  this.addNodeToGraph(LineNode28);
		  this.addNodeToGraph(LineNode29);
		  this.addNodeToGraph(LineNode30);
		  this.addNodeToGraph(LineNode31);
		  
		  
		  this.addNodeToGraph(POI0);
		  this.addNodeToGraph(POI1);
		  this.addNodeToGraph(POI2);
		  this.addNodeToGraph(POI3);

		  
		  this.addEdgeToGraph(LineNode1,LineNode2);
		  //this.addEdgeToGraph(LineNode2,LineNode3);
		  this.addEdgeToGraph(LineNode2,LineNode4);
		  this.addEdgeToGraph(LineNode2,LineNode5);
		  this.addEdgeToGraph(LineNode3,LineNode4);
		  this.addEdgeToGraph(LineNode3,LineNode5);
		  this.addEdgeToGraph(LineNode4,LineNode7);
		  this.addEdgeToGraph(LineNode4,LineNode8);
		  this.addEdgeToGraph(LineNode5,LineNode6);
		  this.addEdgeToGraph(LineNode7,LineNode8);
		  this.addEdgeToGraph(LineNode8,LineNode9);
		  this.addEdgeToGraph(LineNode9,LineNode10);
		  this.addEdgeToGraph(LineNode9,LineNode11);
		  this.addEdgeToGraph(LineNode10,LineNode11);
		  this.addEdgeToGraph(LineNode10,LineNode13);
		  this.addEdgeToGraph(LineNode11,LineNode12);
		  this.addEdgeToGraph(LineNode11,LineNode13);
		  this.addEdgeToGraph(LineNode13,LineNode14);
		  this.addEdgeToGraph(LineNode14,LineNode15);
		  this.addEdgeToGraph(LineNode14,LineNode16);
		  this.addEdgeToGraph(LineNode15,LineNode16);
		  //this.addEdgeToGraph(LineNode15,LineNode18);
		  this.addEdgeToGraph(LineNode15,LineNode19);
		  
		  this.addEdgeToGraph(LineNode16,LineNode17);
		  //this.addEdgeToGraph(LineNode16,LineNode18);
		  this.addEdgeToGraph(LineNode16,LineNode19);
		  
		  //this.addEdgeToGraph(LineNode18,LineNode19);
		  this.addEdgeToGraph(LineNode19,LineNode20);
		  this.addEdgeToGraph(LineNode19,LineNode21);
		  this.addEdgeToGraph(LineNode20,LineNode21);
		  this.addEdgeToGraph(LineNode21,LineNode22);
		  this.addEdgeToGraph(LineNode22,LineNode23);
		  this.addEdgeToGraph(LineNode22,LineNode26);
		  this.addEdgeToGraph(LineNode23,LineNode24);
		  this.addEdgeToGraph(LineNode23,LineNode26);
		  this.addEdgeToGraph(LineNode24,LineNode25);
		  this.addEdgeToGraph(LineNode24,LineNode26);
		  this.addEdgeToGraph(LineNode26,LineNode31);
		  this.addEdgeToGraph(LineNode27,LineNode28);
		  this.addEdgeToGraph(LineNode27,LineNode29);
		  this.addEdgeToGraph(LineNode28,LineNode29);
		  this.addEdgeToGraph(LineNode29,LineNode30);
		  this.addEdgeToGraph(LineNode31,LineNode27);
		  this.addEdgeToGraph(LineNode22,LineNode31);
		  this.addEdgeToGraph(LineNode24,LineNode31);
		  
		  
		  this.addEdgeToGraph(POI0,LineNode1);
		  this.addEdgeToGraph(POI1,LineNode28);
		  this.addEdgeToGraph(POI2,LineNode25);
		  this.addEdgeToGraph(POI3,LineNode30);

		//New thread to load and parser plist
		  //testLoadPList();
		 
		 // this.handler.postDelayed(testingRunner, 100);
		  
	}
	 
	public static void loadPList2(String plist, Context mContext) {
		PListXMLParser parser = new PListXMLParser();
		PListXMLHandler plistHandler = new PListXMLHandler();
		parser.setHandler(plistHandler);
		ArrayList<String> lists = new ArrayList<String>();
		try {
			parser.parse(mContext.getAssets().open(plist));//pLists[index]));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		PList actualPList = ((PListXMLHandler) parser.getHandler())
				.getPlist();
		Dict root = (Dict) actualPList.getRootElement();
		Map<String, PListObject> list = root.getConfigMap();
		WifiReferencePointVO.aryApList.clear();
		for (int i = 0; i < list.keySet().size(); i++) {
			Dict provinceRoot = (Dict) list.get(String.valueOf(i));
			Map<String, PListObject> area = provinceRoot.getConfigMap();

			String areaName = area.keySet().iterator().next();
			//Log.i("Plist Parser", "Area Name = " + areaName);

			Dict pointsRoot = (Dict) area.get(areaName);
			Map<String, PListObject> points = pointsRoot.getConfigMap();
			
			for (int j = 0; j < points.keySet().size(); j++) {
				Dict city = (Dict) points.get(String.valueOf(j));
				String pointName = city.getConfigMap().keySet()
						.iterator().next();
				//Log.i("Plist Parser", "pointName");
				Array districts = city.getConfigurationArray(pointName);
				WifiReferencePointVO.ApListSize = districts.size();

				for (int k = 0; k < districts.size(); k++) {
					com.longevitysoft.android.xml.plist.domain.String district = (com.longevitysoft.android.xml.plist.domain.String) districts
							.get(k);
					//Log.i("Plist Parser",
						//	"points = " + district.getValue());
					WifiReferencePointVO.aryApList.add(district
							.getValue());
					String strReplace = district.getValue().replace(
							":", "_");
					
					if (k == districts.size() - 1) {
						WifiReferencePointVO.CREATE_TABLE = WifiReferencePointVO.CREATE_TABLE
								+ "AP_" + strReplace + " TEXT "  + ")";
					} else {
						WifiReferencePointVO.CREATE_TABLE = WifiReferencePointVO.CREATE_TABLE
								+ "AP_" + strReplace + " TEXT, " ;
					}
					
				}
		
			}

		}
	
	}
	
	public void testLoadPList(int index) {
		PListXMLParser parser = new PListXMLParser();
			PListXMLHandler plistHandler = new PListXMLHandler();
			parser.setHandler(plistHandler);
			
			try {
				parser.parse(act.getAssets().open(pLists[index]));//"WifiAPList.plist"));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			PList actualPList = ((PListXMLHandler) parser.getHandler())
					.getPlist();
			Dict root = (Dict) actualPList.getRootElement();
			Map<String, PListObject> list = root.getConfigMap();
			WifiReferencePointVO.aryApList.clear();
			for (int i = 0; i < list.keySet().size(); i++) {
				Dict provinceRoot = (Dict) list.get(String.valueOf(i));
				Map<String, PListObject> area = provinceRoot.getConfigMap();

				String areaName = area.keySet().iterator().next();
				//Log.i("Plist Parser", "Area Name = " + areaName);

				Dict pointsRoot = (Dict) area.get(areaName);
				Map<String, PListObject> points = pointsRoot.getConfigMap();

				for (int j = 0; j < points.keySet().size(); j++) {
					Dict city = (Dict) points.get(String.valueOf(j));
					String pointName = city.getConfigMap().keySet()
							.iterator().next();
					//Log.i("Plist Parser", "pointName");
					Array districts = city.getConfigurationArray(pointName);
					WifiReferencePointVO.ApListSize = districts.size();
					
					for (int k = 0; k < districts.size(); k++) {
						com.longevitysoft.android.xml.plist.domain.String district = (com.longevitysoft.android.xml.plist.domain.String) districts
								.get(k);
						//Log.i("Plist Parser",
							//	"points = " + district.getValue());
						WifiReferencePointVO.aryApList.add(district.getValue());
						String strReplace = district.getValue().replace(":", "_");
						
						if( k== districts.size() - 1) {
							WifiReferencePointVO.CREATE_TABLE = WifiReferencePointVO.CREATE_TABLE + 
									"AP_" + strReplace + " TEXT " + ")";
						} else {
							WifiReferencePointVO.CREATE_TABLE = WifiReferencePointVO.CREATE_TABLE + 
									"AP_" + strReplace + " TEXT, ";
						}
					}
				}

			}
	}
	
	private void addNodeToGraph(Node node){
		this.naviPlanGraph.add(node);
		this.nodeCollection.put(node.getID(), node);
		String className = node.getClass().getSimpleName();
		if(className.equals("POI")) {
			this.POICollection.add((POI)node);
		}
	}
	
	private void addEdgeToGraph(Node sourceNode,Node targetNode) {
		NaviNode sNode = (NaviNode) sourceNode;
		NaviNode tNode = (NaviNode) targetNode;
		Float distance = (float) Math.sqrt(Math.pow(sNode.x-tNode.x,2)+Math.pow(sNode.y-tNode.y,2));
		naviPlanGraph.newEdge(sourceNode, targetNode, sourceNode.getID()+"-"+targetNode.getID(),distance);
		naviPlanGraph.newEdge(targetNode, sourceNode, targetNode.getID()+"-"+sourceNode.getID(),distance);
	}
	
	public Node getNodeById(int id) {
		Node node = (Node) this.nodeCollection.get(String.valueOf(id));
		return node;
	}
}
