package com.doubleservice.bigcitynavigation;

import com.doubleservice.DataVO.PushMsgVO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MsgListDBHelper extends SQLiteOpenHelper{

	private static String name = PushMsgVO.DBName;
	private static int dbVersion = 1;
	
	public MsgListDBHelper(Context context) {
		super(context, name, null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(PushMsgVO.CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
