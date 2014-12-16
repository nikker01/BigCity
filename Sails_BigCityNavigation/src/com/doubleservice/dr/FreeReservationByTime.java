package com.doubleservice.dr;

import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.proxy.PreferenceProxy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

public class FreeReservationByTime extends Activity {

	private ReservationByTimeLayout mpLayout;
	private String TAG = "FreeReservationByTime";

	private int hour = 19;
	private int min = 30;

	private int mPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(
				getResources().getString(R.string.layout_title_reservation));

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mPosition = params.getInt(RequestCodeVO.CMD_REQ_RESERVATION);
			Log.i(TAG, "mPosition = " + mPosition);
		}
		setupView();
	}

	private void setupView() {
		// TODO Auto-generated method stub
		mpLayout = new ReservationByTimeLayout(this);
		setContentView(mpLayout);

		mpLayout.topViewContent.setText("  "+getResources().getString(
				R.string.layout_reservation_des));
		mpLayout.topViewContent.setTextSize(17);
		
		mpLayout.txtReservationPeople.setText(getResources().getString(
				R.string.layout_reservation_people));
		mpLayout.txtReservationPeople.setTextSize(20);
		
		mpLayout.txtReservationTime.setText(getResources().getString(
				R.string.layout_reservation_time));
		mpLayout.txtReservationTime.setTextSize(20);
		
		mpLayout.peoplePicker.setText("2");
		mpLayout.peoplePicker.setTextSize(20);
		mpLayout.timePicker.setText("19:30");
		mpLayout.timePicker.setTextSize(20);
/*
		mpLayout.timePicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openTimePicker();
			}
		});
*/
		
		/*
		mpLayout.peoplePicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openPeoplePicker();
			}
		});
*/
		mpLayout.btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FreeReservationByTime.this.finish();
			}
		});

		mpLayout.btnComplete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToReservationInfo();
			}
		});
	}

	protected void openTimePicker() {
		// TODO Auto-generated method stub

		LayoutInflater inflater = LayoutInflater.from(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View view = inflater.inflate(R.layout.park_time_setting, null);
		final TimePicker timePicker = (TimePicker) view
				.findViewById(R.id.timePicker);
		timePicker.setCurrentHour(19);
		timePicker.setCurrentMinute(30);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				hour = hourOfDay;
				min = minute;
			}
		});

		builder.setView(view);
		builder.setTitle(getResources().getString(
				R.string.layout_reservation_time));
		builder.setNegativeButton(R.string.dialog_btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		builder.setPositiveButton(R.string.dialog_btn_confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String strHour = "";
						String strMin = "";
						if (hour < 10)
							strHour = "0" + Integer.toString(hour);
						else
							strHour = Integer.toString(hour);

						if (min < 10)
							strMin = "0" + Integer.toString(min);
						else
							strMin = Integer.toString(min);
						mpLayout.timePicker.setText(strHour + ":" + strMin);
					}

				});
		final AlertDialog dialog;
		dialog = builder.create();
		dialog.show();
	}

	protected void openPeoplePicker() {
		// TODO Auto-generated method stub
		String[] m = new String[] {
				getResources().getString(R.string.dialog_msg_people1),
				getResources().getString(R.string.dialog_msg_people2),
				getResources().getString(R.string.dialog_msg_people3),
				getResources().getString(R.string.dialog_msg_people4),
				getResources().getString(R.string.dialog_msg_people5) };

		new AlertDialog.Builder(this)
				.setTitle(
						getResources().getString(
								R.string.layout_reservation_people))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(m, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// dialog.dismiss();
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

							}
						}).show();
	}

	protected void goToReservationInfo() {
		// TODO Auto-generated method stub
		Toast.makeText(this,
				getResources().getString(R.string.toast_str_reserve_ok),
				Toast.LENGTH_LONG).show();

		PreferenceProxy prefProxy = new PreferenceProxy(this);
		prefProxy.setDinningRoomReservation(mPosition);
		prefProxy.setReservationType(1);

		Intent intent = new Intent();
		intent.putExtra(RequestCodeVO.CMD_REQ_RESERVATION, mPosition);
		//intent.setClass(this, ReservationInfo.class);
		intent.setClass(this, FreeReservationInfo.class);
		this.startActivity(intent);
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			this.finish();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}
