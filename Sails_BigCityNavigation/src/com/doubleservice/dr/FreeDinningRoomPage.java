package com.doubleservice.dr;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.DataVO.ReservationVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.brand.BrandLayout;
import com.doubleservice.memorycontroller.ImageLoader;
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
import android.widget.ImageView;
import android.widget.Toast;

public class FreeDinningRoomPage extends Activity {

	private DinningRoomPageLayout mpLayout;

	private String TAG = "FreeDinningRoomPage";

	private int mPosition = -1;
	private String mLocationCode = null;

	private boolean bIsDrReserved = false;
	private NavDataVO navDataVO = new NavDataVO();

	private int[] imgContentUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imgContentUrl = new int[] {
				getResources().getIdentifier("content01", "drawable",
						getPackageName()),
				getResources().getIdentifier("content02", "drawable",
						getPackageName()),
				getResources().getIdentifier("content03", "drawable",
						getPackageName()),
				getResources().getIdentifier("content04", "drawable",
						getPackageName()),
				getResources().getIdentifier("content05", "drawable",
						getPackageName()),
				getResources().getIdentifier("content06", "drawable",
						getPackageName()),
				getResources().getIdentifier("content07", "drawable",
						getPackageName()),
				getResources().getIdentifier("content08", "drawable",
						getPackageName()),
				getResources().getIdentifier("content09", "drawable",
						getPackageName()),
				getResources().getIdentifier("content10", "drawable",
						getPackageName()),
				getResources().getIdentifier("content11", "drawable",
						getPackageName()),
				getResources().getIdentifier("content12", "drawable",
						getPackageName()),
				getResources().getIdentifier("content13", "drawable",
						getPackageName()),
				getResources().getIdentifier("content14", "drawable",
						getPackageName()),
				getResources().getIdentifier("content15", "drawable",
						getPackageName()) };

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mPosition = params.getInt(RequestCodeVO.CMD_REQ_RESERVATION);
			mLocationCode = params
					.getString(RequestCodeVO.CMD_REQ_DINNING_ROOM_CODE);
			Log.i(TAG, "open list =" + mPosition + " code = " + mLocationCode);

			if (mLocationCode != null) {
				navDataVO.MethodID = GlobalDataVO.NAV_METHOD_DINNING_ROOM;
				navDataVO = ApplicationController.getInstance()
						.getLocationDataByNumber(mLocationCode);
			}

		}
		getReservation();
		setLayout();

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(getResources().getString(R.string.menu_food));
	}

	private void getReservation() {
		// TODO Auto-generated method stub
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		ReservationVO vo = new ReservationVO();
		vo = prefProxy.getDinningRoomReservation();
		if (vo.bIsReservation) {
			// if(vo.mReservationPosition == mPosition) {
			bIsDrReserved = true;
			// }
		}
	}

	private void setLayout() {
		// TODO Auto-generated method stub
		mpLayout = new DinningRoomPageLayout(this);
		setContentView(mpLayout);

		mpLayout.imgContentView.setImageResource(imgContentUrl[mPosition - 1]);

		if (bIsDrReserved) //
		{
			mpLayout.btnReservation.setEnabled(false);
			// btnReservation.setAlpha(0.5f);
			mpLayout.btnReservation.setBackgroundResource(R.drawable.btn_reservation_disable);
			//mpLayout.btnReservation.setImageDrawable(getResources()
				//	.getDrawable(R.drawable.btn_reservation_disable));
		}

		mpLayout.btnReservation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openReservationDialog();
			}
		});

		mpLayout.btnNavigation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mLocationCode.length() >= 1) {
					goToNavigation();
				} else {
					goToOtherFloor();
				}
			}
		});
		
		switch(mPosition){
		case 5:
		case 7:
		case 15:
			mpLayout.bottomView.setBackgroundResource(R.drawable.bg_btm_nowaiting);
			break;
		case 2:
		case 9:
		case 13:
			mpLayout.bottomView.setBackgroundResource(R.drawable.bg_btm_45min);
			break;
		case 3:
		case 10:
		case 14:
			mpLayout.bottomView.setBackgroundResource(R.drawable.bg_btm_1h30);
			break;
		case 4:
		case 6:
		case 11:
			mpLayout.bottomView.setBackgroundResource(R.drawable.bg_btm_1h50);
			break;
		case 1:
		case 8:
		case 12:
			mpLayout.bottomView.setBackgroundResource(R.drawable.bg_btm_2h30);
			break;
		}
		
	}

	protected void goToNavigation() {
		// TODO Auto-generated method stub
		String alertTitle = getResources().getString(R.string.setting_warn);
		String alertMsg = getResources().getString(R.string.dialog_msg_gotomap);
		String btnConfirm = getResources().getString(
				R.string.dialog_btn_confirm);
		String btnCancel = getResources().getString(R.string.dialog_btn_cancel);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(alertTitle);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(btnConfirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.i(TAG, "Nav no= "+navDataVO.Number);

						//ApplicationController.navData.MethodID = 
						navDataVO.MethodID	=	GlobalDataVO.NAV_METHOD_DINNING_ROOM;
						ApplicationController.navData = navDataVO;
						Intent intent = new Intent();
						intent.setClass(FreeDinningRoomPage.this,
								Navigation.class);
						ApplicationController.navData = navDataVO;
						intent.putExtra(RequestCodeVO.CMD_NAV, navDataVO);
						startActivity(intent);
						GlobalDataVO.CURRENT_DRAWER_ITEM = 999;
					}
				});
		builder.setNegativeButton(btnCancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void goToOtherFloor() {
		// TODO Auto-generated method stub
		Toast.makeText(this,
				getResources().getString(R.string.toast_str_goto7f),
				Toast.LENGTH_LONG).show();
	}

	protected void setReservation() {
		// TODO Auto-generated method stub
		Log.i(TAG, "setReservation BEGIN, position = " + mPosition);

		PreferenceProxy prefProxy = new PreferenceProxy(this);
		prefProxy.setDinningRoomReservation(mPosition);
		// btnReservation.setEnabled(false);
		mpLayout.btnReservation.setImageDrawable(getResources().getDrawable(
				R.drawable.btn_reservation_disable));

		Builder MyAlertDialog = new AlertDialog.Builder(this);
		MyAlertDialog.setTitle(getResources().getString(
				R.string.dialog_title_reservation_success));
		MyAlertDialog.setMessage(getResources().getString(
				R.string.dialog_msg_reservation_success)
				+ getResources().getString(
						R.string.dialog_msg_reservation_content));

		MyAlertDialog.setPositiveButton(
				getResources().getString(R.string.dialog_btn_close),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						goToReservation();
					}
				});

		MyAlertDialog.setCancelable(false);
		MyAlertDialog.show();

		Log.i(TAG, "setReservation END");
	}

	private int whichItem = 0;

	protected void openReservationDialog() {
		// TODO Auto-generated method stub
		String[] mSorting = new String[] {
				getResources().getString(R.string.dialog_msg_reservation1),
				getResources().getString(R.string.dialog_msg_reservation2) };

		new AlertDialog.Builder(this)
				.setTitle(
						getResources().getString(
								R.string.dialog_title_reservation_method))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(mSorting, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// dialog.dismiss();
								whichItem = which;
							}
						})
				.setCancelable(false)
				.setNegativeButton(
						getResources().getString(R.string.dialog_btn_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.setPositiveButton(
						getResources().getString(R.string.dialog_btn_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								if (whichItem == 0) {
									PreferenceProxy prefProxy = new PreferenceProxy(
											FreeDinningRoomPage.this);
									prefProxy
											.setDinningRoomReservation(mPosition);
									prefProxy.setReservationType(0);
								}

								goToReservationPageByChoice(whichItem);
							}
						}).show();
	}
	
	protected void goToReservationPageByChoice(int which) {
		// TODO Auto-generated method stub
		Log.i(TAG, "goToReservationPageByChoice");
		
		Intent intent = new Intent();
		intent.putExtra(RequestCodeVO.CMD_REQ_RESERVATION, mPosition);
		intent.putExtra(RequestCodeVO.CMD_REQ_DINNING_ROOM_CODE, mLocationCode);
		if(which == 0) {
			//intent.setClass(this, ReservationByNumber.class);
			intent.setClass(this, FreeReservationByNumber.class);
			Toast.makeText(this, getResources().getString(R.string.toast_str_reserve_ok), 
					Toast.LENGTH_LONG).show();
		} else if(which == 1) {
			//intent.setClass(this, ReservationByTime.class);
			intent.setClass(this, FreeReservationByTime.class);
		}
		this.startActivity(intent);
	
	}

	protected void goToReservation() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(this, Reservation.class);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}

		return true;
	}
}
