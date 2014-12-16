package com.doubleservice.bigcitynavigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.toolbox.JsonObjectPostRequest;
import com.doubleservice.DataVO.BrandDataVO;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.brand.BrandDBProxy;
import com.doubleservice.brand.FreeBrandPage;
import com.doubleservice.brand.MainBrandLayout;
import com.doubleservice.dr.DinningRoomList;
import com.doubleservice.findchild.DetectPage;
import com.doubleservice.findchild.FindChildDescription;
import com.doubleservice.findfriends.FindFriendDes;
import com.doubleservice.findfriends.FriendList;
import com.doubleservice.parking.ParkDescription;
import com.doubleservice.parking.UserParkingInfo;
import com.doubleservice.promotional.MainPromotional;
import com.doubleservice.promotional.PromotionalList;
import com.doubleservice.proxy.ApiProxy;
import com.doubleservice.proxy.DownloadFileProxy;
import com.doubleservice.proxy.PreferenceProxy;
import com.radiusnetworks.ibeacon.IBeaconManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class FreeHomepage extends Activity {

	private String TAG = "FreeHomepage";
	private HomepageLayout mpLayout;
	private ProgressDialog apiRequestDialog;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setLayout();
		
		intiAppData();
		//initService();
	
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent(this, IbeaconService.class);
		stopService(intent);
		Log.i(TAG, "onDestroy BEGIN");
	}

	
	private void initService() {
		ApplicationController.getInstance().onIBeaconServiceStart();
		
		Intent intent = new Intent(this, IbeaconService.class);
		startService(intent);
		if (IBeaconManager.getInstanceForApplication(this).checkAvailability()) {

		} else {
			/*
			Toast.makeText(this,
					getResources().getString(R.string.toast_str_ble_not_work),
					Toast.LENGTH_LONG).show();
					*/
			String mTitle = getResources().getString(R.string.dialog_title_bluetooth_inavailable);
			String mContent = getResources().getString(R.string.dialog_msg_blutooth_inavailable);
			String mBtnConfirm = getResources().getString(R.string.dialog_btn_confirm);
			String mBtnCancel = getResources().getString(R.string.dialog_btn_cancel);
			BaseAlertMsg.pushGeneralAlert(this, mTitle, mContent, mBtnConfirm, mBtnCancel, new IGenericAlert(){
				@Override
				public void PositiveMethod(DialogInterface dialog, int id) {
					// TODO Auto-generated method stub
					Intent i = new Intent("/");
					ComponentName cm = new ComponentName("com.android.settings","com.android.settings.bluetooth.BluetoothSettings");
					i.setComponent(cm);
					i.setAction("android.intent.action.VIEW");
					startActivityForResult(i , 0);
				}

				@Override
				public void NegativeMethod(DialogInterface dialog, int id) {
					// TODO Auto-generated method stub
					//this.finish();
				}});

		}
		
	}

	private void intiAppData() {
		// TODO Auto-generated method stub
		File docFolder = new File(Environment.getExternalStorageDirectory(),
				"Double_Service");
		if(!docFolder.exists()) {
			docFolder.mkdirs();
		}
		
		/*
		try {
			copydatabase("bc_bigcity_referencepoint_db.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "copy file bc_bigcity_referencepoint_db fail!");
		}
		try {
			copydatabase("sogo_bigcity_referencepoint_db.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "copy file sogo_bigcity_referencepoint_db fail!");
		}
		try {
			copydatabase("bigcity_poc_msglist_db.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "copy file bigcity_poc_msglist_db fail!");
		}
		*/
		
		MoveFiles moveFile = new MoveFiles(this);
		try {
			moveFile.moveFileToExternalStorage("bc_bigcity_referencepoint_db.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			moveFile.moveFileToExternalStorage("sogo_bigcity_referencepoint_db.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			moveFile.moveFileToExternalStorage("bigcity_poc_msglist_db.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PreferenceProxy prefProxy = new PreferenceProxy(this);
		if(!prefProxy.getIsArticleDone()) {
			getArticle();
		}
	}


	public void getArticle() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getArticle BEGIN");
		apiRequestDialog = ProgressDialog
				.show(this,
						getResources().getString(
								R.string.progress_dialog_title_download),
						getResources().getString(
								R.string.progress_dialog_msg_download));

		new Thread() {
			@Override
			public void run() {
				ApiProxy apiProxy = new ApiProxy(FreeHomepage.this, "getArticle");
			}
		}.start();
		
	}

	public void articleData(boolean isDone, JSONObject response) {
		Log.i(TAG, "articleData BEGIN");
		apiRequestDialog.dismiss();
		if (isDone) {

			BrandDBProxy proxy = new BrandDBProxy(this);
			PreferenceProxy prefProxy = new PreferenceProxy(this);

			try {
				JSONArray obj = new JSONArray(response.getString("data"));
				if (obj != null) {
					for (int i = 0; i < obj.length(); i++) {
						JSONObject dataContent = obj.getJSONObject(i);

						Log.i(TAG,
								"getArticle id = "
										+ dataContent.getString("ID"));
						Log.i(TAG,
								"getArticle image = "
										+ dataContent.getString("image"));
						Log.i(TAG,
								"getArticle title = "
										+ dataContent.getString("title"));
						Log.i(TAG,
								"getArticle content = "
										+ dataContent.getString("content"));
						Log.i(TAG,
								"getArticle xpoint = "
										+ dataContent.getString("xpoint"));
						Log.i(TAG,
								"getArticle ypoint = "
										+ dataContent.getString("ypoint"));
						Log.i(TAG,
								"getArticle category = "
										+ dataContent.getString("category"));
						Log.i(TAG,
								"getArticle area = "
										+ dataContent.getString("area"));
						// Log.i(TAG, "getArticle shoplist = "
						// +dataContent.getString("shoplist"));
						if (dataContent.getString("category").equals("2")) {
							Log.i(TAG,
									"getArticle shoplist = "
											+ dataContent.getString("shoplist"));
							BrandDataVO vo = new BrandDataVO();
							vo.bID = dataContent.getString("ID");
							vo.bImg = dataContent.getString("image");
							vo.bTitle = dataContent.getString("title");
							vo.bContent = dataContent.getString("content");
							vo.bPosX = dataContent.getString("xpoint");
							vo.bPosY = dataContent.getString("ypoint");
							vo.bArea = dataContent.getString("area");
							String[] temp = dataContent.getString("shoplist")
									.split(",");
							vo.bShopList = temp[0];

							proxy.onCreatePromotionalInitData(vo);
						}

						if (dataContent.getString("category").equals("1")) {
							BrandDataVO vo = new BrandDataVO();
							vo.bID = dataContent.getString("ID");
							vo.bImg = dataContent.getString("image");
							vo.bTitle = dataContent.getString("title");
							vo.bContent = dataContent.getString("content");
							vo.bPosX = dataContent.getString("xpoint");
							vo.bPosY = dataContent.getString("ypoint");
							vo.bArea = dataContent.getString("area");

							proxy.onCreateBrandInitData(vo);
						}

					}
					
					prefProxy.setIsArticleDone(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
				prefProxy.setIsArticleDone(false);
			}

			proxy.closeDB();
		} else {
			BaseAlertMsg alert = new BaseAlertMsg(this, GlobalDataVO.AlertMsg.GetArticleFail);
		}
	}

	private void getFingerPrint() {
		// TODO Auto-generated method stub
		pd = ProgressDialog
				.show(this,
						getResources().getString(
								R.string.progress_dialog_title_download),
						getResources().getString(
								R.string.progress_dialog_msg_download));

		new Thread() {
			@Override
			public void run() {
				ApiProxy apiProxy = new ApiProxy(FreeHomepage.this,
						"getFingerprintData");
			}
		}.start();
	}
/*
	public void downloadFingerPrintDB(boolean isGettingUrl, String url) {
		if (isGettingUrl) {
			DownloadFileProxy dfProxy = new DownloadFileProxy(this);
			dfProxy.startDownload(url);
		} else {
			GlobalDataVO.isFingerPrintDownloading = false;
			Toast.makeText(
					this,
					getResources().getString(
							R.string.toast_str_fingerprint_download_fail),
					Toast.LENGTH_LONG).show();
			pd.dismiss();
		}
	}
	*/

	public void isFileDownloadingSuccess(boolean isDone) {
		Log.i(TAG, "isFileDownloadingSuccess =" + isDone);
		if (isDone) {
			GlobalDataVO.isFingerPrintDownloading = true;
			Toast.makeText(
					this,
					getResources().getString(
							R.string.toast_str_fingerprint_download_ok),
					Toast.LENGTH_LONG).show();
		} else {
			GlobalDataVO.isFingerPrintDownloading = false;
			Toast.makeText(
					this,
					getResources().getString(
							R.string.toast_str_fingerprint_download_fail),
					Toast.LENGTH_LONG).show();
		}
		pd.dismiss();
	}

	private void setLayout() {
		// TODO Auto-generated method stub
		mpLayout = new HomepageLayout(this);
		setContentView(mpLayout);

		mpLayout.mFloor.setOnClickListener(homepageBtnClick);
		mpLayout.mFood.setOnClickListener(homepageBtnClick);
		mpLayout.mFindfriend.setOnClickListener(homepageBtnClick);
		mpLayout.mFindchild.setOnClickListener(homepageBtnClick);
		mpLayout.mParking.setOnClickListener(homepageBtnClick);
		mpLayout.mPromotion.setOnClickListener(homepageBtnClick);
		mpLayout.mBrand.setOnClickListener(homepageBtnClick);
		mpLayout.mHappygo.setOnClickListener(homepageBtnClick);
	}

	public void showAlertDialog() {
		// TODO Auto-generated method stub
		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setMessage(getResources().getString(
				R.string.dialog_msg_function_building));

		MyAlertDialog.setPositiveButton(
				getResources().getString(R.string.dialog_btn_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// clearMyReservation();
					}
				});

		MyAlertDialog.setCancelable(false);
		MyAlertDialog.show();
	}

	OnClickListener homepageBtnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i(TAG, "id = " + v.getTag());
			Intent intent = null;
			switch ((Integer) v.getTag()) {
			case R.string.menu_floor:
				//String floor = ApplicationController.getInstance().getFloor();
				//Toast.makeText(FreeHomepage.this, "Floor = "+floor, Toast.LENGTH_LONG).show();
				
				intent = new Intent();
				ApplicationController.navData = null;
				//intent.setClass(FreeHomepage.this, Navigation.class);
				intent.setClass(FreeHomepage.this, SailsTechActivity.class);
				
				GlobalDataVO.CURRENT_DRAWER_ITEM = 2;
				
				break;
			case R.string.menu_food:
				intent = new Intent();
				intent.setClass(FreeHomepage.this, DinningRoomList.class);
				GlobalDataVO.CURRENT_DRAWER_ITEM = 3;
				break;
			case R.string.menu_find_child: {
				PreferenceProxy prefProxy = new PreferenceProxy(
						getBaseContext());
				boolean isTracking = prefProxy.isChildTracking();

				intent = new Intent();
				if (isTracking) {
					intent.setClass(FreeHomepage.this, DetectPage.class);
				} else {
					intent.setClass(FreeHomepage.this,
							FindChildDescription.class);
				}

				GlobalDataVO.CURRENT_DRAWER_ITEM = 4;
				break;
			}
			case R.string.menu_find_friend: {
				PreferenceProxy prefProxy = new PreferenceProxy(
						FreeHomepage.this);

				if (prefProxy.getIsUpdatePhone()) {
					intent = new Intent();
					intent.setClass(FreeHomepage.this, FriendList.class);
					GlobalDataVO.CURRENT_DRAWER_ITEM = 5;
				} else {
					intent = new Intent();
					intent.setClass(FreeHomepage.this, FindFriendDes.class);
					GlobalDataVO.CURRENT_DRAWER_ITEM = 5;
				}

			}
				break;
			case R.string.menu_parking:
				ParkingVO vo = new ParkingVO();
				PreferenceProxy prefProxy = new PreferenceProxy(
						getBaseContext());
				vo = prefProxy.getIsParking();
				GlobalDataVO.CURRENT_DRAWER_ITEM = 6;

				if (vo.isParking) {
					intent = new Intent();
					intent.setClass(FreeHomepage.this, UserParkingInfo.class);
				} else {
					intent = new Intent();
					intent.setClass(FreeHomepage.this, ParkDescription.class);
				}
				break;
			case R.string.menu_find_promotion:
				intent = new Intent();
				intent.setClass(FreeHomepage.this, MainPromotional.class);
				//intent.setClass(FreeHomepage.this, PromotionalList.class);
				GlobalDataVO.CURRENT_DRAWER_ITEM = 8;
				break;
			case R.string.menu_find_brand:
				intent = new Intent();
				intent.setClass(FreeHomepage.this, MainBrandLayout.class);
				// intent.setClass(FreeHomepage.this, FreeBrandPage.class);
				GlobalDataVO.CURRENT_DRAWER_ITEM = 9;
				break;
			case R.string.menu_happygo:
				intent = new Intent();
				intent.setClass(FreeHomepage.this, MemberHappyGo.class);
				GlobalDataVO.CURRENT_DRAWER_ITEM = 11;
				break;
			}

			if (intent != null) {
				startActivity(intent);
			}
		}

	};
}
