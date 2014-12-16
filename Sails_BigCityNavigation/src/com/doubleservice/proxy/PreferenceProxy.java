package com.doubleservice.proxy;

import com.doubleservice.DataVO.ChildDataVO;
import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.DataVO.ReservationVO;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceProxy {

	private Context mContext;
	private String TAG = "PreferenceProxy";

	private static final String CMD_USER_SETTINGS = "_CMD_USER_SETTINGS";

	private static final String CMD_CHILD_BEACON_NUMBER = "_CMD_CHILD_BEACON_NUMBER";
	private static final String CMD_CHILD_WARNING_DIS = "_CMD_CHILD_WARNING_DIS";
	private static final String CMD_CHILD_SOUND_STATE = "_CMD_CHILD_SOUND_STATE";
	private static final String CMD_CHILD_IS_TRACKING = "_CMD_CHILD_IS_TRACKING";
	private static final String CMD_CHILD_BEACON_SETTING = "_CMD_CHILD_BEACON_SETTING";
	
	private static final String CMD_RESERVATION_DONE = "_CMD_RESERVATION_DONE";
	private static final String CMD_RESERVATION_POSITION = "_CMD_RESERVATION_POSITION";

	private static final String CMD_PARKING = "_CMD_PARKING";
	private static final String CMD_PARKING_NOTIFICATION_SOUND = "_CMD_PARKING_NOTIFICATION_SOUND";
	private static final String CMD_PARKING_TIME = "_CMD_PARKING_TIME";

	private static final String CMD_UPDATE_PHONE = "_CMD_UPDATE_PHONE";

	private static final String CMD_GET_ARTICLE_DONE = "_CMD_GET_ARTICLE_DONE";
	private static final String CMD_RESERVATION_TYPE = "_CMD_RESERVATION_TYPE";
	
	private static final String CMD_RESVERVATION_TIME = "_CMD_RESVERVATION_TIME";

	private SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	public PreferenceProxy(Context context) {
		this.mContext = context;
		settings = mContext.getSharedPreferences(CMD_USER_SETTINGS, 0);
		editor = settings.edit();
	}
	
	public void setReservationTime(String time) {
		editor.putString(CMD_RESVERVATION_TIME, time);
		editor.commit();
	}
	
	public String getReservationTime(){
		return settings.getString(CMD_RESVERVATION_TIME, "-1");
	}

	public void setIsArticleDone(boolean isDone) {
		Log.i(TAG, "setIsArticleDone BEGIN");

		editor.putBoolean(CMD_GET_ARTICLE_DONE, isDone);
		editor.commit();

		Log.i(TAG, "setIsArticleDone END");
	}

	public boolean getIsArticleDone() {
		return settings.getBoolean(CMD_GET_ARTICLE_DONE, false);
	}

	public void updatePhoneIsDone() {
		Log.i(TAG, "updatePhoneIsDone BEGIN");

		editor.putBoolean(CMD_UPDATE_PHONE, true);
		editor.commit();

		Log.i(TAG, "updatePhoneIsDone END");
	}

	public boolean getIsUpdatePhone() {
		return settings.getBoolean(CMD_UPDATE_PHONE, false);
	}

	public void setChildIsTracking() {
		Log.i(TAG, "setChildIsTracking BEGIN");

		editor.putBoolean(CMD_CHILD_IS_TRACKING, true);
		editor.commit();

		Log.i(TAG, "setChildIsTracking END");
	}

	public boolean isChildTracking() {
		return settings.getBoolean(CMD_CHILD_IS_TRACKING, false);
	}


	public void setParking(ParkingVO vo) {
		Log.i(TAG, "setParking BEGIN");

		editor.putBoolean(CMD_PARKING, true);
		editor.putBoolean(CMD_PARKING_NOTIFICATION_SOUND, vo.soundOpen);
		editor.putString(CMD_PARKING_TIME, vo.mTime);
		editor.commit();

		Log.i(TAG, "setParking END");
	}

	public ParkingVO getIsParking() {
		ParkingVO vo = new ParkingVO();

		vo.isParking = settings.getBoolean(CMD_PARKING, false);
		vo.soundOpen = settings.getBoolean(CMD_PARKING_NOTIFICATION_SOUND,
				false);
		vo.mTime = settings.getString(CMD_PARKING_TIME, "");

		return vo;
	}

	public void clearParkingData() {
		Log.i(TAG, "clearParkingData BEGIN");

		editor.putBoolean(CMD_PARKING, false);
		editor.putBoolean(CMD_PARKING_NOTIFICATION_SOUND, false);
		editor.putString(CMD_PARKING_TIME, "");
		editor.commit();

		Log.i(TAG, "clearParkingData END");
	}

	public void setChildBeaconData(ChildDataVO vo) {
		Log.i(TAG, "setChildBeaconData BEGIN");

		editor.putString(CMD_CHILD_BEACON_NUMBER, vo.mBeaconNumebr);
		editor.putInt(CMD_CHILD_WARNING_DIS, vo.mWarningDistance);
		editor.putBoolean(CMD_CHILD_SOUND_STATE, vo.bIsSound);
		editor.putBoolean(CMD_CHILD_BEACON_SETTING, true);
		editor.commit();

		Log.i(TAG, "setChildBeaconData END");
	}
	
	public void clearChildTracking() {
		Log.i(TAG, "clearChildTracking BEGIN");

		editor.putString(CMD_CHILD_BEACON_NUMBER, "");
		editor.putInt(CMD_CHILD_WARNING_DIS, 100000);
		editor.putBoolean(CMD_CHILD_SOUND_STATE, false);
		editor.putBoolean(CMD_CHILD_IS_TRACKING, false);
		editor.putBoolean(CMD_CHILD_BEACON_SETTING, false);
		editor.commit();
		
		Log.i(TAG, "clearChildTracking END");
	}

	public ChildDataVO getChildBeaconData() {
		Log.i(TAG, "getChildBeaconData BEGIN");

		ChildDataVO vo = new ChildDataVO();
		vo.mBeaconNumebr = settings.getString(CMD_CHILD_BEACON_NUMBER, "-1");
		vo.mWarningDistance = settings.getInt(CMD_CHILD_WARNING_DIS, -1);
		vo.bIsSound = settings.getBoolean(CMD_CHILD_SOUND_STATE, true);
		vo.bIsBeaconSetting = settings.getBoolean(CMD_CHILD_BEACON_SETTING, false);

		return vo;
	}
	
	public void setReservationType(int type) {
		Log.i(TAG, "setReservationType BEGIN");
		
		editor.putInt(CMD_RESERVATION_TYPE, type);
		editor.commit();
	}
	
	public int getReservationType() {
		return  settings.getInt(CMD_RESERVATION_TYPE, 0);
	}

	public void setDinningRoomReservation(int mDrPosition) {
		Log.i(TAG, "setDinningRoomReservation BEGIN, mDrPosition = "
				+ mDrPosition);
		editor.putBoolean(CMD_RESERVATION_DONE, true);
		editor.putInt(CMD_RESERVATION_POSITION, mDrPosition);
		editor.commit();

		Log.i(TAG, "setDinningRoomReservation END");
	}

	public void clearDinningRoomReservation() {
		Log.i(TAG, "clearDinningRoomReservation BEGIN");

		editor.putBoolean(CMD_RESERVATION_DONE, false);
		editor.putInt(CMD_RESERVATION_POSITION, -999);
		editor.putString(CMD_RESVERVATION_TIME, "-1");
		editor.commit();

		Log.i(TAG, "clearDinningRoomReservation END");
	}

	public ReservationVO getDinningRoomReservation() {
		Log.i(TAG, "getDinningRoomReservation BEGIN");

		ReservationVO vo = new ReservationVO();
		vo.bIsReservation = settings.getBoolean(CMD_RESERVATION_DONE, false);
		vo.mReservationPosition = settings.getInt(CMD_RESERVATION_POSITION,
				-999);

		Log.i(TAG, "getDinningRoomReservation END");

		return vo;
	}
}
