package lib.locate.KNN;

import java.util.ArrayList;

import android.provider.BaseColumns;

public class WifiReferencePointVO implements BaseColumns{

	public static String DBName = "/sdcard/referencepoint_db.db";
	public final static String TABLE_NAME = "Reference_Points";
	public final static String DISTANCE = "_DISTANCE";
	public final static String PATH_NUMBER = "_PATH_NUMBER";
	public final static String POSITION_X = "_POSITION_X";
	public final static String POSITION_Y = "_POSITION_Y";
	public final static String AZIMUTH = "_AZIMUTH";
	
	public static int ApListSize = 0;
	public static ArrayList<String> aryApList = new ArrayList<String>();
	
	public String mPathNum;
	public String mPosX = "";
	public String mPosY = "";
	public String mAzimuth = "";
	public int[] rssiArray;
	public int[] rssiMeanArray;
	public double[] rssiStandarDeviation;
	
	public static float lastPosX = 0;
	public static float lastPosY = 0;
	
	public static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
			_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			AZIMUTH+ " TEXT, " +
			PATH_NUMBER+ " TEXT, " +
			POSITION_X+ " TEXT, " +
			POSITION_Y+ " TEXT, " ;
}
