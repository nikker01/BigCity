package lib.locate.KNN;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//import com.andvantech.dsnavi_sitesurvey.referencepoints.ReferencePointVO;

public class WifiReferencePointDBHelper extends SQLiteOpenHelper{

	private static String name = WifiReferencePointVO.DBName;
	private static int dbVersion = 1;
	
	public WifiReferencePointDBHelper(Context context) {
		super(context, WifiReferencePointVO.DBName, null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i("WifiReferencePointDBHelper", "onCreate BEGIN");
		db.execSQL(WifiReferencePointVO.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
