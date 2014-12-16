package com.doubleservice.bigcitynavigation;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.RequestCodeVO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiverUtil extends BroadcastReceiver {

	private String TAG = "AlarmReceiverUtil";
	private int requestCode;
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		Bundle params = intent.getExtras();
		if(params != null) {
			requestCode = params.getInt("REQ_CODE");
			Log.i(TAG, "onReceive BEGIN, Code = " +requestCode);
			pushNotificationByService(requestCode);
		}
		
		
	}

	private void pushNotificationByService(int code) {
		// TODO Auto-generated method stub
		Log.i(TAG, "pushNotificationByService BEGIN");
		Intent service = new Intent(mContext, PushMessageService.class);
		service.putExtra(RequestCodeVO.CMD_ALARM_NOTIFICATION, code);
		mContext.startService(service);
	}

}
