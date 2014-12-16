package com.doubleservice.bigcitynavigation.pushmsg;

import android.provider.BaseColumns;

public class MsgDBDataVO implements BaseColumns{

	public static final String TABLE_NAME = "MSG_INFO";
	
	public static final String ADDRESS = "_ADDRESS";
	public static final String POSX = "_POS_X";
	public static final String POSY = "_POS_Y";
	public static final String FLOOR = "_FLOOR";
	public static final String MSG = "_MSG";
	
	public final static String DBName = "/sdcard/bigcity_poc_msg_db.db";
	
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
			_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			ADDRESS+ " TEXT, " +
			POSX + " TEXT, " +
			POSY + " TEXT, " +
			FLOOR + " TEXT, " +
			MSG + " TEXT " + ")";
}
