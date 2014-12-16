package com.doubleservice.proxy;

import java.util.ArrayList;

import com.doubleservice.DataVO.PushMsgVO;
import com.doubleservice.bigcitynavigation.MsgListDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MsgListProxy {

	private Context mContext;
	private MsgListDBHelper dbHelper;
	private SQLiteDatabase db;
	private String[] column = {"_D_NAME", "_POX_X", "_POS_Y", "_MSG_EXIST", "_MSG_STR", "_MSG_URL"};
	private double m_pOwnerRegion = 10;
	
	public MsgListProxy(Context context,double pushLimitMeter) {
		this.mContext = context;
		this.m_pOwnerRegion = pushLimitMeter;
		initDB();
	}

	private void initDB() {
		// TODO Auto-generated method stub
		dbHelper = new MsgListDBHelper(mContext);
		db = dbHelper.getWritableDatabase();
	}
	
	public void createMsgListData(ArrayList<PushMsgVO> data) {
		Log.i("TAG", "createMsgListData BEGIN");
		
		for(int i=0; i<data.size(); i++) {
			ContentValues values = new ContentValues();
			values.put(PushMsgVO.D_NAME, data.get(i).mDeviceName);
			values.put(PushMsgVO.POS_X, data.get(i).mDevicePosX);
			values.put(PushMsgVO.POS_Y, data.get(i).mDevicePosY);
			values.put(PushMsgVO.MSG_EXIST, data.get(i).mDeviceMsgExist);
			values.put(PushMsgVO.MSG_STR, data.get(i).mDevicsMsg);
			values.put(PushMsgVO.MSG_URL, data.get(i).mDeviceMsgUrl);
			
			db.insert(PushMsgVO.TABLE_NAME, null, values);
			Log.i("TAG", "createMsgListData END");
		}
		
	}
	
	public String[] queryMsg(String posX, String posY) {
		
		String userPosX = posX;
		String userPosY = posY;
		String[] msg = {"", "", ""};	//msg[0]=MessageContent, msg[1]=InMessageUrl, msg[2]=DeviceName
		
		try {
			Cursor c = null;
			c = db.query(PushMsgVO.TABLE_NAME, column, "_D_NAME = " + PushMsgVO.D_NAME,
					null, null, null, null);
			if (c.getCount() >= 1) {
				nearestPointSearch: 
				for(int i=0; i<=c.getCount(); i++) {
					c.moveToPosition(i);
					double distance = 999;
					String devicePosX = c.getString(c.getColumnIndex("_POX_X"));
					String devicePosY = c.getString(c.getColumnIndex("_POS_Y"));
					distance = disCalculation(userPosX, userPosY, devicePosX, devicePosY);
					if(distance <= m_pOwnerRegion) {
						if(c.getString(c.getColumnIndex("_MSG_EXIST")).equals("Y")) {
							msg[0] = c.getString(c.getColumnIndex("_MSG_STR"));
							msg[1] =  c.getString(c.getColumnIndex("_MSG_URL"));
							msg[2] = c.getString(c.getColumnIndex("_D_NAME"));
						} 
						
						break nearestPointSearch;
					}
						
				}
			}
			
			return msg;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return msg;
	}

	private double disCalculation(String userPosX, String userPosY,
			String devicePosX, String devicePosY) {
		// TODO Auto-generated method stub
		
		double userX = Double.parseDouble(userPosX);
		double userY = Double.parseDouble(userPosY);
		double deviceX = Double.parseDouble(devicePosX);
		double deviceY = Double.parseDouble(devicePosY);
		
		double res = Math.sqrt((Math.pow((userX-deviceX), 2.0) + Math.pow((userY-deviceY), 2.0)));
		
		return res;
	}
}
