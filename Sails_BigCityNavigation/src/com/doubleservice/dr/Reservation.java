package com.doubleservice.dr;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.DataVO.ReservationVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.proxy.PreferenceProxy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Reservation extends Activity {

	private String TAG = "Reservation";
	
	private int mPosition = -999;
	private ImageView btnNavigation;
	private ImageView btnCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dr_reservation_layout);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.layout_title_reservation));
		
		getReservation();
		setComponent();
	}

	private void setComponent() {
		// TODO Auto-generated method stub
		
		ImageView imgTop = (ImageView)findViewById(R.id.imageView1);
		/*
		if(mPosition == 1)
			imgTop.setImageDrawable(getResources().getDrawable(R.drawable.img_reservation1));
		else if(mPosition == 2)
			//imgTop.setImageDrawable(getResources().getDrawable(R.drawable.img_reservation2));
		if(mPosition == 3)
			//imgTop.setImageDrawable(getResources().getDrawable(R.drawable.img_reservation3));
		*/
		btnNavigation = (ImageView)findViewById(R.id.btnNavigation);
		btnNavigation.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//setNavigationData();
			}});
		
		btnCancel = (ImageView)findViewById(R.id.btnReservationCancel);
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder MyAlertDialog = new AlertDialog.Builder(Reservation.this);
				MyAlertDialog.setTitle(getResources().getString(R.string.dialog_title_reservation_cancel));
				MyAlertDialog.setMessage(getResources().getString(R.string.dialog_msg_reservation_cancel));
				
				MyAlertDialog.setPositiveButton(getResources().getString(R.string.dialog_btn_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								clearMyReservation();
							}
						});
				MyAlertDialog.setNegativeButton(getResources().getString(R.string.dialog_btn_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});

				MyAlertDialog.setCancelable(false);
				MyAlertDialog.show();
			}});
	}

	protected void setNavigationData() {
		// TODO Auto-generated method stub
		Log.i(TAG, "setNavigationData BEGIN");
		
		NavDataVO vo = new NavDataVO();
		vo.MethodID = GlobalDataVO.NAV_METHOD_DINNING_ROOM;
		//vo.PositionX = 
		//vo.PositionY = 
		
		Intent intent = new Intent();
		intent.putExtra(RequestCodeVO.CMD_NAV_DINNING_ROOM, vo);
	}

	protected void clearMyReservation() {
		// TODO Auto-generated method stub
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		prefProxy.clearDinningRoomReservation();
		
		Intent intent = new Intent();
		intent.setClass(this, DinningRoomList.class);
		this.startActivity(intent);
		this.finish();
	}

	private void getReservation() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getReservation BEGIN");
		
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		ReservationVO vo = new ReservationVO();
		vo = prefProxy.getDinningRoomReservation();
		if(vo.bIsReservation)
			mPosition = vo.mReservationPosition;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent();
			intent.setClass(this, DinningRoomList.class);
			startActivity(intent);
			break;
		}

		return true;
	}
}
