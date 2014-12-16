package com.doubleservice.DataVO;

import android.provider.BaseColumns;

public class BrandDataVO implements BaseColumns{
	
	public final static String TABLE_NAME = "Brand_List_4F";
	public final static String TABLE_NAME_b = "Promotional_List_4F";
	
	public final static String BrandID = "_brand_id";
	public final static String BrandTitle = "_brand_title";
	public final static String BrandImg = "_brand_img";
	public final static String BrandFloor = "_brand_floor";
	public final static String BrandContent = "_brand_content";
	public final static String BrandPosX = "_brand_posx";
	public final static String BrandPosY = "_brand_posy";
	public final static String BrandArea = "_brand_area";
	public final static String BrandShopList = "_brand_shop_list";
	
	public String bID = "";
	public String bTitle = "";
	public String bImg = "";
	public String bFloor = "";
	public String bContent = "";
	public String bPosX = "";
	public String bPosY = "";
	public String bArea = "";
	public String bShopList = "";
	
	//public final static String DBName = "/sdcard/bigcity_poc_brand_db.db";
	public final static String DBName = "/data/data/com.doubleservice.bigcitynavigation/databases/bigcity_poc_brand_db.db";


	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
			_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			BrandID+ " TEXT, " +
			BrandTitle + " TEXT, " +
			BrandImg + " TEXT, " +
			BrandFloor + " TEXT, " +
			BrandContent + " TEXT, " +
			BrandArea + " TEXT, " +
			BrandShopList + " TEXT, " +
			BrandPosX + " TEXT, " +
			BrandPosY + " TEXT " + ")";
	
	public static final String CREATE_SQL2 = "CREATE TABLE " + TABLE_NAME_b + "(" +
			_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			BrandID+ " TEXT, " +
			BrandTitle + " TEXT, " +
			BrandImg + " TEXT, " +
			BrandFloor + " TEXT, " +
			BrandContent + " TEXT, " +
			BrandArea + " TEXT, " +
			BrandShopList + " TEXT, " +
			BrandPosX + " TEXT, " +
			BrandPosY + " TEXT " + ")";
	
	public static final String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
