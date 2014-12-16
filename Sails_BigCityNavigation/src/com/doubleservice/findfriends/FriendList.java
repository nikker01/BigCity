package com.doubleservice.findfriends;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.AES;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.FreeHomepage;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.proxy.ApiProxy;

public class FriendList extends BaseDrawerActivity {

	private ArrayList<String> allMemberList = new ArrayList<String>();
	private ArrayList<HashMap> dataList;
	private ListView mListview;
	private FriendListAdapter adapter;

	private String TAG = "FriendList";

	private ProgressDialog apiRequestDialog;
	public String mSearchFriendPhone = "";

	private NavDataVO friendNavDataVO = new NavDataVO();
	private ImageView emptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(R.layout.friend_list_layout,
				null, false);
		frameLayout.addView(activityView);

		emptyView = (ImageView)findViewById(R.id.img_empty);
		getActionBar().setTitle(
				getResources().getString(R.string.menu_find_friend));
		// ApiProxy apiProxy = new ApiProxy(this, "getMemberAll");

		getContactList();

	}

	private void getContactList() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getContactList BEGIN");
		apiRequestDialog = ProgressDialog.show(
				this,
				getResources().getString(
						R.string.progress_dialog_title_contactlist_refresh),
				getResources().getString(
						R.string.progress_dialog_msg_contactlist_refresh));

		new Thread() {
			@Override
			public void run() {
				ApiProxy apiProxy = new ApiProxy(FriendList.this,
						"getMemberAll");
			}
		}.start();
	}

	public void getFriendLocation(boolean isDone, JSONObject response) {
		AES aes = new AES();
		apiRequestDialog.dismiss();
		if (isDone) {
			try {
				JSONObject obj = new JSONObject(response.getString("data"));
				if (obj != null) {

					// JSONObject dataContent = obj.getJSONObject(i);
					String strFlag = "";
					String phone = "";
					String posX = "";
					String posY = "";
					String floor = "";

					try {

						strFlag = obj.getString("flag");
						//phone = obj.getString(aes.decrypt_AES("phone"));
						phone = aes.decrypt_AES_ParsingSomeChar(obj.getString("phone"));
						posX = obj.getString("pointX");
						posY = obj.getString("pointY");
						floor = obj.getString("floor");

						if (strFlag.equals("y")) {
							Log.i(TAG, "getFriendLocation flag = " + strFlag
									+ " phone = " + phone + "posX = " + posX
									+ "posY = " + posY);

							friendNavDataVO.ItemName = phone;
							friendNavDataVO.MethodID = GlobalDataVO.NAV_METHOD_FIND_FRIEND;
							friendNavDataVO.PositionX = posX;
							friendNavDataVO.PositionY = posY;
							friendNavDataVO.Area = floor;
							friendNavDataVO.FriendTel = mSearchFriendPhone;
							//friendNavDataVO.Floor = floor;

							goToNavigation(friendNavDataVO);
						} else if (strFlag.equals("n")) {
							getFriendLocationFail();
						}

					} catch (Exception e) {
						e.printStackTrace();
						getFriendLocationFail();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				getFriendLocationFail();
			}
		} else {
			getFriendLocationFail();
		}
	}
	
	private void getFriendLocationFail() {
		Log.i(TAG, "getFriendLocationFail");
		Toast.makeText(this, getResources().getString(R.string.toast_str_findfriend_fail), 
				Toast.LENGTH_LONG).show();
	}

	private void goToNavigation(NavDataVO vo) {
		// TODO Auto-generated method stub
		Log.i(TAG, "goToNavigation");
		
		Intent intent = new Intent();
		intent.putExtra(RequestCodeVO.CMD_NAV, vo);
		intent.setClass(this, Navigation.class);
		ApplicationController.navData = vo;
		this.startActivity(intent);
		GlobalDataVO.CURRENT_DRAWER_ITEM = 999;
	}

	private void searchFriendFail() {
		// TODO Auto-generated method stub
		Log.i(TAG, "searchFriendFail BEGIN");
		//apiRequestDialog.dismiss();
		/*
		Toast.makeText(this,
				getResources().getString(R.string.toast_str_findfriend_fail),
				Toast.LENGTH_LONG).show();
		*/
		String alertTitle = getResources().getString(R.string.setting_warn);
		String alertMsg = getResources().getString(R.string.dialog_msg_get_contact_list_fail);
		String btnRetry = getResources().getString(R.string.dialog_btn_retry);
		String btnCancel = getResources().getString(R.string.dialog_btn_cancel);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(alertTitle);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(btnRetry, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				getContactList();
			}
		});
		builder.setNegativeButton(btnCancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				FriendList.this.finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void getMembers(boolean isDone, JSONObject response) {
		// TODO Auto-generated method stub
		AES aes = new AES();
		if (isDone) {
			try {
				JSONArray obj = new JSONArray(response.getString("data"));
				if (obj != null) {
					for (int i = 0; i < obj.length(); i++) {
						String strMemberPhone = "";
						JSONObject dataContent = obj.getJSONObject(i);

						try {
							strMemberPhone = aes
									.decrypt_AES_ParsingSomeChar(dataContent
											.getString("phone"));
							Log.i(TAG, "phone = " + strMemberPhone);
							allMemberList.add(strMemberPhone);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			setupView();
		} else {
			// Toast.makeText(this, text, duration)
			//finishLoadingData();
			apiRequestDialog.dismiss();
			searchFriendFail();
		}
	}

	private void setupView() {
		// TODO Auto-generated method stub

		dataList = getData();
		finishLoadingData();

		mListview = (ListView) findViewById(R.id.listViewPage1);
		adapter = new FriendListAdapter(this, dataList);
		mListview.setAdapter(adapter);
		mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// ListView listView = (ListView) arg0;
				mSearchFriendPhone = (String) dataList.get(arg2).get("phone");
				searchFriendLocation(mSearchFriendPhone);
			}
		});
	}

	protected void searchFriendLocation(String friendPhone) {
		// TODO Auto-generated method stub
		Log.i(TAG, "searchFriendLocation phone = " + friendPhone);

		apiRequestDialog = ProgressDialog.show(
				this,
				getResources().getString(R.string.progress_dialog_title_wait),
				getResources().getString(
						R.string.progress_dialog_msg_get_friend_location));

		new Thread() {
			@Override
			public void run() {
				ApiProxy apiProxy = new ApiProxy(FriendList.this,
						"getFriendLocation");
				// ApiProxy apiProxy = new ApiProxy(FriendList.this,
				// "getMemberAll");
			}
		}.start();
	}

	private void finishLoadingData() {
		// TODO Auto-generated method stub
		apiRequestDialog.dismiss();
		//searchFriendFail();
	}

	private ArrayList<HashMap> getData() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getData BEGIN");

		boolean addToContactList = false;

		ArrayList<HashMap> list = new ArrayList<HashMap>();
		HashMap map = new HashMap();

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);
		if (cursor.getCount() > 0) {

			while (cursor.moveToNext()) {

				String contact_id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				int hasPhoneNumber = Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {
					Cursor phoneCursor = contentResolver
							.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
									null,
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID
											+ " = ?",
									new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						String phoneNumber = phoneCursor
								.getString(phoneCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						//String replacePhone = phoneNumber.replace("-", "");
						String replacePhone = phoneNumber.replaceAll("[^0-9.]", "");
						int idx = allMemberList.indexOf(replacePhone);
						if (idx >= 0) {
							addToContactList = true;
							map = new HashMap<String, Object>();
							map.put("phone", replacePhone);
							map.put("id", name);
							list.add(map);
						}
					}

					phoneCursor.close();
				}

			}
			cursor.close();
		}

		Log.i(TAG, "getData END");

		if(list.size() >= 1) {
			emptyView.setVisibility(View.GONE);
		}
		
		return list;
	}
}
