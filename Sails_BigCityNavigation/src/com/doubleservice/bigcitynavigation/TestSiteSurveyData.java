package com.doubleservice.bigcitynavigation;

import java.util.ArrayList;

public class TestSiteSurveyData {

	public float positionX,positionY ;
	public ArrayList<TestIBeaconScanResult> results ;
	
	public TestSiteSurveyData(float positionX,float positionY,ArrayList<TestIBeaconScanResult> results) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.results = results;
	}
}
