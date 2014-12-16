package com.doubleservice.parking;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.PushMessageService;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.proxy.PreferenceProxy;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class ParkAlarmReceiver extends BroadcastReceiver {

	private String TAG = "ParkAlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onReceive BEGIN");
		
		ParkingVO vo = new ParkingVO();
		PreferenceProxy prefProxy = new PreferenceProxy(context);
		vo = prefProxy.getIsParking();
		
		if(vo.isParking) {
			Log.i(TAG, "user is parking");
			
			Intent service = new Intent(context, PushMessageService.class);
			service.putExtra(RequestCodeVO.CMD_ALARM_NOTIFICATION, 
					GlobalDataVO.NOTIFICATION_PARKING_ALARM_ID);
			context.startService(service);

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(context.getResources().getString(R.string.dialog_title_parking_warning));
			builder.setMessage(context.getResources().getString(R.string.dialog_msg_parking_ontime));
			builder.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
						}
					});
			AlertDialog alert = builder.create();
			alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			alert.show();
			
			if(vo.soundOpen) {
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(context, notification);
				r.play();
			}
			
			prefProxy.clearParkingData();
		}
			
	}

}
