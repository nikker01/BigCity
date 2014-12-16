package com.doubleservice.dr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.doubleservice.DataVO.ReservationVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.proxy.PreferenceProxy;

public class FreeReservation extends Activity {

	private String TAG = "FreeReservation";

	private int mPosition = -999;
	private ImageView btnNavigation;
	private ImageView btnCancel;
	
	private ReservationLayout mpLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dr_reservation_layout);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(
				getResources().getString(R.string.layout_title_reservation));

		getReservation();
		setComponent();
	}
	
	private void setComponent() {
		// TODO Auto-generated method stub
		mpLayout = new ReservationLayout(this);
		setContentView(mpLayout);
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
}
