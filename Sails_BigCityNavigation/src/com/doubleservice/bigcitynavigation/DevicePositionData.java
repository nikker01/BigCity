package com.doubleservice.bigcitynavigation;

public class DevicePositionData {
	
	public String deviceName = "";
	public String devicePosX = "";
	public String devicePosY = "";
	
	public void setDeviceName(String name) {
		this.deviceName = name;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setDevicePosX(String x) {
		this.devicePosX = x;
	}
	
	public String getDevicePosX() {
		return devicePosX;
	}
	
	public void setDevicePosY(String y) {
		this.devicePosY = y;
	}
	
	public String getDevicePosY() {
		return devicePosY;
	}

}
