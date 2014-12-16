package com.doubleservice.DataVO;

public class GlobalDataVO {

	public static int CURRENT_DRAWER_ITEM = 999;
	public static boolean isDetectPage = false;
	public static boolean isFingerPrintDownloading = false;
	public static boolean isPhoneNumberUpdate = false;
	
	public static final int NOTIFICATION_PARKING_ALARM_ID 			= 30001; 
	public static final int NOTIFICATION_RESERVATION_ID				= 30002;
	public static final int NOTIFICATION_PARKING_SHORT_ALERT 	= 30003; 
	
	public static final int NAV_METHOD_DINNING_ROOM 			= 50001;
	public static final int NAV_METHOD_FIND_FRIEND 				= 50002;
	public static final int NAV_METHOD_PARKING						= 50003;
	public static final int NAV_METHOD_PROMOTIONAL				= 50004;
	public static final int NAV_METHOD_BRAND						= 50005;
	
	public enum AlertMsg {
		GetArticleFail
	}
}
