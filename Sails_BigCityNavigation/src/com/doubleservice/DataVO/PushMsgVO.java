package com.doubleservice.DataVO;

import java.io.Serializable;

import android.provider.BaseColumns;

public class PushMsgVO implements Serializable, BaseColumns{

	public String pushMsg = "";
	public String flag = "";
	public String timeLimit = "";
	public String fileExt = "";
	public String fileName = "";
	public String url = "";
	
public static final String TABLE_NAME = "MSG_LIST";
	
	public static final String D_NAME = "_D_NAME";
	public static final String POS_X = "_POX_X";
	public static final String POS_Y = "_POS_Y";
	public static final String MSG_EXIST = "_MSG_EXIST";
	public static final String MSG_STR = "_MSG_STR";
	public static final String MSG_URL = "_MSG_URL";
	
	public String mDeviceName = "";
	public String mDevicePosX = "";
	public String mDevicePosY = "";
	public String mDeviceMsgExist = "";
	public String mDevicsMsg = "";
	public String mDeviceMsgUrl = "";
	
	public final static String DBName = "/sdcard/bigcity_poc_msglist_db.db";
	//public final static String DBName = "/data/data/com.doubleservice.bigcitynavigation/databases/bigcity_poc_msglist_db.db";
	
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
			_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			D_NAME+ " TEXT, " +
			POS_X + " TEXT, " +
			POS_Y + " TEXT, " +
			MSG_EXIST + " TEXT, " +
			MSG_STR + " TEXT, " +
			MSG_URL + " TEXT " + ")";
			
}
