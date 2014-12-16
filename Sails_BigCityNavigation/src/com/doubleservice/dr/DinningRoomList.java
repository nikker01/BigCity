package com.doubleservice.dr;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.DataVO.ReservationVO;
import com.doubleservice.bigcitynavigation.BaseAlertMsg;
import com.doubleservice.bigcitynavigation.IGenericAlert;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.findchild.ChildBeaconSetup;
import com.doubleservice.memorycontroller.ImageLoader;
import com.doubleservice.proxy.MsgListProxy;
import com.doubleservice.proxy.PreferenceProxy;

public class DinningRoomList extends BaseDrawerActivity {

	private String TAG = "DinningRoomList";
	private int mReservationPosition;
	private String[] imgUrlList = {
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list01.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list02.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list03.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list04.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list05.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list06.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list07.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list08.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list09.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list10.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list11.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list12.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list13.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list14.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list15.png" };

	private String[] imgUrlListSortByWatingTime = {
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list05.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list07.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list15.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list02.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list09.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list13.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list03.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list10.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list14.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list04.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list06.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list11.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list01.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list08.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list12.png", };

	private String[] imgUrlListSortByType = {
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list01.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list02.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list03.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list04.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list05.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list15.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list06.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list07.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list08.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list09.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list10.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list11.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list12.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list13.png",
			"http://bigcity-rtls.doubleservice.com/u/orderFood/w1080/list14.png", };

	private String[] imgUrlListSortByFloor = {

	};

	private ImageLoader mImageLoader;
	private int mSortingType = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(R.layout.dr_list_layout,
				null, false);
		frameLayout.addView(activityView);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mSortingType = params.getInt(RequestCodeVO.CMD_REQ_SORTING);
			Log.i(TAG, "mSortingType = " + mSortingType);
		}

		mImageLoader = new ImageLoader(this);
		getActionBar().setTitle(getResources().getString(R.string.menu_food));
		setComponent();
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	private void setComponent() {
		// TODO Auto-generated method stub
		ImageView[] view = new ImageView[15];
		view[0] = (ImageView) findViewById(R.id.imageView1);
		view[1] = (ImageView) findViewById(R.id.imageView2);
		view[2] = (ImageView) findViewById(R.id.imageView3);
		view[3] = (ImageView) findViewById(R.id.imageView4);
		view[4] = (ImageView) findViewById(R.id.imageView5);
		view[5] = (ImageView) findViewById(R.id.imageView6);
		view[6] = (ImageView) findViewById(R.id.imageView7);
		view[7] = (ImageView) findViewById(R.id.imageView8);
		view[8] = (ImageView) findViewById(R.id.imageView9);
		view[9] = (ImageView) findViewById(R.id.imageView10);
		view[10] = (ImageView) findViewById(R.id.imageView11);
		view[11] = (ImageView) findViewById(R.id.imageView12);
		view[12] = (ImageView) findViewById(R.id.imageView13);
		view[13] = (ImageView) findViewById(R.id.imageView14);
		view[14] = (ImageView) findViewById(R.id.imageView15);

		if (mSortingType == 0) {
			for (int i = 0; i < imgUrlList.length; i++) {
				mImageLoader.DisplayImage(imgUrlListSortByWatingTime[i],
						view[i], false);
				view[i].setOnClickListener(btnClick);
			}
		} else if (mSortingType == 1) {
			for (int i = 0; i < imgUrlList.length; i++) {
				mImageLoader.DisplayImage(imgUrlListSortByType[i], view[i],
						false);
				view[i].setOnClickListener(btnClick);
			}
		} else if (mSortingType == 2) {
			for (int i = 0; i < imgUrlList.length; i++) {
				mImageLoader.DisplayImage(imgUrlList[i], view[i], false);
				view[i].setOnClickListener(btnClick);
			}
		}

	}

	OnClickListener btnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.imageView1:
				if (mSortingType == 0) {
					openDinningRoomNum(5, "8");
				} else if (mSortingType == 1) {
					openDinningRoomNum(1, "31");
				} else if (mSortingType == 2) {
					openDinningRoomNum(1, "31");
				}
				break;
			case R.id.imageView2:
				if (mSortingType == 0) {
					openDinningRoomNum(7, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(2, "9");
				} else if (mSortingType == 2) {
					openDinningRoomNum(2, "9");
				}
				
				break;
			case R.id.imageView3:
				if (mSortingType == 0) {
					openDinningRoomNum(15, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(3, "7");
				} else if (mSortingType == 2) {
					openDinningRoomNum(3, "7");
				}
				
				break;
			case R.id.imageView4:
				if (mSortingType == 0) {
					openDinningRoomNum(2, "9");
				} else if (mSortingType == 1) {
					openDinningRoomNum(4, "12");
				} else if (mSortingType == 2) {
					openDinningRoomNum(4, "12");
				}
				
				break;
			case R.id.imageView5:
				if (mSortingType == 0) {
					openDinningRoomNum(9, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(5, "8");
				} else if (mSortingType == 2) {
					openDinningRoomNum(5, "8");
				}
				
				break;
			case R.id.imageView6:
				if (mSortingType == 0) {
					openDinningRoomNum(13, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(15, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(6, "");
				}
				
				break;
			case R.id.imageView7:
				if (mSortingType == 0) {
					openDinningRoomNum(3, "7");
				} else if (mSortingType == 1) {
					openDinningRoomNum(6, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(7, "");
				}
				
				break;
			case R.id.imageView8:
				if (mSortingType == 0) {
					openDinningRoomNum(10, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(7, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(8, "");
				}
				
				break;
			case R.id.imageView9:
				if (mSortingType == 0) {
					openDinningRoomNum(14, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(9, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(9, "");
				}
				
				break;
			case R.id.imageView10:
				if (mSortingType == 0) {
					openDinningRoomNum(4, "12");
				} else if (mSortingType == 1) {
					openDinningRoomNum(9, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(10, "");
				}
				
				break;
			case R.id.imageView11:
				if (mSortingType == 0) {
					openDinningRoomNum(6, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(10, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(11, "");
				}
				
				break;
			case R.id.imageView12:
				if (mSortingType == 0) {
					openDinningRoomNum(11, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(11, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(12, "");
				}
				
				break;
			case R.id.imageView13:
				if (mSortingType == 0) {
					openDinningRoomNum(1, "31");
				} else if (mSortingType == 1) {
					openDinningRoomNum(12, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(13, "");
				}
				
				break;
			case R.id.imageView14:
				if (mSortingType == 0) {
					openDinningRoomNum(8, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(13, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(14, "");
				}
				
				break;
			case R.id.imageView15:
				if (mSortingType == 0) {
					openDinningRoomNum(12, "");
				} else if (mSortingType == 1) {
					openDinningRoomNum(14, "");
				} else if (mSortingType == 2) {
					openDinningRoomNum(15, "");
				}
				
				break;
			}
		}

	};

	protected void openDinningRoomNum(int i, String code) {
		// TODO Auto-generated method stub
		Log.i(TAG, "openDinningRoomNum BEGIN");

		Intent intent = new Intent();
		intent.putExtra(RequestCodeVO.CMD_REQ_DINNING_ROOM_CODE, code);
		intent.putExtra(RequestCodeVO.CMD_REQ_RESERVATION, i);
		// intent.setClass(this, DinningRoomPage.class);
		intent.setClass(this, FreeDinningRoomPage.class);
		this.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action if it is present.
		getMenuInflater().inflate(R.menu.dr_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.intent_sort:
			openSortingDialog();
			break;
		case R.id.intent_reservation:
			getReservation();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getReservation() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getReservation BEGIN");

		PreferenceProxy prefProxy = new PreferenceProxy(this);
		int type = prefProxy.getReservationType();
		ReservationVO vo = new ReservationVO();
		vo = prefProxy.getDinningRoomReservation();
		Log.i(TAG, "getReservation type = " + type + " IsReservation = "
				+ vo.bIsReservation);

		if (!vo.bIsReservation) {
			Log.i(TAG, "getReservation no reservation");
			openNoReservationDialog();
		} else {
			if (type == 0) {
				Intent intent = new Intent();
				intent.putExtra(RequestCodeVO.CMD_REQ_RESERVATION,
						vo.mReservationPosition);
				// intent.setClass(this, ReservationByNumber.class);
				intent.setClass(this, FreeReservationByNumber.class);
				this.startActivity(intent);
			} else if (type == 1) {
				Intent intent = new Intent();
				intent.putExtra(RequestCodeVO.CMD_REQ_RESERVATION,
						vo.mReservationPosition);
				// intent.setClass(this, ReservationInfo.class);
				intent.setClass(this, FreeReservationInfo.class);
				this.startActivity(intent);
			}

		}

	}

	private void openNoReservationDialog() {
		// TODO Auto-generated method stub
		Builder MyAlertDialog = new AlertDialog.Builder(this);
		MyAlertDialog.setTitle(getResources().getString(
				R.string.dialog_title_no_reservation));
		MyAlertDialog.setMessage(getResources().getString(
				R.string.dialog_msg_no_reservation));

		MyAlertDialog.setPositiveButton(
				getResources().getString(R.string.dialog_btn_close),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		MyAlertDialog.setCancelable(false);
		MyAlertDialog.show();
	}

	private void openSortingDialog() {
		// TODO Auto-generated method stub
		mSortingType = 0;
		String[] mSorting = new String[] {
				getResources().getString(R.string.menu_dr_list_sort1),
				getResources().getString(R.string.menu_dr_list_sort2),
				getResources().getString(R.string.menu_dr_list_sort3) };

		Resources res = getResources();
		String mPositiveMsg = res.getString(R.string.dialog_btn_confirm);
		String mNegativeMsg = res.getString(R.string.dialog_btn_cancel);
		
		mSortingType = 0;

		BaseAlertMsg.pushSingleChoiceAlert(this, mSorting, mPositiveMsg,
				mNegativeMsg, new IGenericAlert() {

					@Override
					public void PositiveMethod(DialogInterface dialog, int id) {
						// TODO Auto-generated method stub
						mSortingType = id;
						Intent i = new Intent();
						i.setClass(DinningRoomList.this, DinningRoomList.class);
						i.putExtra(RequestCodeVO.CMD_REQ_SORTING, mSortingType);
						DinningRoomList.this.finish();
						startActivity(i);
					}

					@Override
					public void NegativeMethod(DialogInterface dialog, int id) {
						// TODO Auto-generated method stub

					}
				});
	}

}
