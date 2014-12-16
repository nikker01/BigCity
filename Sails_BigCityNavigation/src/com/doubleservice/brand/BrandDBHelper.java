package com.doubleservice.brand;

import com.doubleservice.DataVO.BrandDataVO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BrandDBHelper extends SQLiteOpenHelper{

	private String TAG = "BrandDBHelper";
	
	private static String name = BrandDataVO.DBName;
	private static int dbVersion = 1;
	private boolean dbCreateFlag = false;
	
	public BrandDBHelper(Context context) {
		super(context, name, null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(TAG, "BrandDBHelper onCreate");
		
		db.execSQL(BrandDataVO.CREATE_SQL);
		db.execSQL(BrandDataVO.CREATE_SQL2);
		dbCreateFlag = true;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public synchronized void close() {
		super.close();
	}
}
