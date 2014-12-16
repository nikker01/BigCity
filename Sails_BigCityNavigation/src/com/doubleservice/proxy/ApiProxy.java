package com.doubleservice.proxy;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectPostRequest;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.bigcitynavigation.AES;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.FreeHomepage;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.findfriends.FriendList;

public class ApiProxy {

	String TAG = "ApiProxy";
	FreeHomepage mHomeActivity;
	FriendList mFriendListActivity;
	Navigation mNavActivity;
	Context mContext;
	AES aes = new AES();
	String forgroundActivity = "";
	String[] forgroundActivities = new String[]{"FriendList", "Navigation"};
	
	public ApiProxy(Context context) {
		this.mContext = context;
	}
	
	public ApiProxy(FreeHomepage activity, String method) {
		this.mHomeActivity = activity;
		
		if(method.equals("getFingerprintData")) {
			getFingerprintData();
		} else if(method.equals("getArticle")) {
			getArticle();
		}
	}
	
	public ApiProxy(FriendList activity, String method) {
		this.mFriendListActivity = activity;
		forgroundActivity = forgroundActivities[0];
		if(method.equals("getMemberAll")) {
			getMemberAll();
		} else if(method.equals("getFriendLocation")) {
			getFriendLocation();
		}
	}
	
	public ApiProxy(Navigation activity, String method) {
		this.mNavActivity = activity;
		forgroundActivity = forgroundActivities[1];
		if(method.equals("getFriendLocation")) {
			getFriendLocation();
		}
	}
	
	private void getArticle() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getArticle BEGIN");
		
		String strApiUrl = "http://bigcity-rtls.doubleservice.com/api/bigcity/";//"http://218.211.88.196/api/BigCity/";
		String uuid = ApplicationController.getInstance().getUuid();
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "getArticle");
		mMap.put("UUID", uuid);
		mMap.put("area", "0");
		mMap.put("category", "0");
		mMap.put("lastDT", "0000-00-00 00:00:00");
		Log.i(TAG, "send data = " +mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(strApiUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("getArticle", "onResponse response =" +response.toString());
						mHomeActivity.articleData(true, response);
					}}, new Response.ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("getArticle", "onErrorResponse error = " +error.getMessage());
							mHomeActivity.articleData(false, null);
						}},
				mMap );
		
		ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
	}

	private void getFriendLocation() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getFriendLocation BEGIN");
		
		AES aes = new AES();
		
		String strApiUrl = "http://bigcity-rtls.doubleservice.com/api/bigcity/";//"http://218.211.88.196/api/BigCity/";
		String uuid = ApplicationController.getInstance().getUuid();
		Log.i(TAG, "getFriendLocation by phone: " +mFriendListActivity.mSearchFriendPhone);
		String mPhone = mFriendListActivity.mSearchFriendPhone;
		try {
			mPhone = aes.encrypt_AES(mPhone);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "getFriendLocation");
		mMap.put("UUID", uuid);
		mMap.put("friendPhone", mPhone);
		Log.i(TAG, "send data = " +mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(strApiUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("getFriendLocation", "onResponse response =" +response.toString() 
								+ "forgroundActivity = "+forgroundActivity);
						if(forgroundActivity.equals(forgroundActivities[0])) {
							mFriendListActivity.getFriendLocation(true, response);
						} else if(forgroundActivity.equals(forgroundActivities[1])) {
							//mNavActivity.getFriendLocation(true, response);
						}
						
					}}, new Response.ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("getFriendLocation", "onErrorResponse error = " +error.getMessage());
							//mFriendListActivity.getFriendLocation(false, null);
							if(forgroundActivity.equals(forgroundActivities[0])) {
								mFriendListActivity.getFriendLocation(false, null);
							} else if(forgroundActivity.equals(forgroundActivities[1])) {
								//mNavActivity.getFriendLocation(true, response);
							}
						}},
				mMap );
		
		ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
		
	}

	private void getFingerprintData() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getFingerprintData BEGIN");
		
		String strApiUrl = "http://bigcity-rtls.doubleservice.com/api/bigcity/";//"http://218.211.88.196/api/BigCity/";
		String uuid = ApplicationController.getInstance().getUuid();
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "getFingerprintData");
		mMap.put("UUID", uuid);
		Log.i(TAG, "send data = " +mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(strApiUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("getFingerprintData", "onResponse response =" +response.toString());
						analysisFingerResponse(response);
						
					}}, new Response.ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("getFingerprintData", "onErrorResponse error = " +error.getMessage());
							//mHomeActivity.downloadFingerPrintDB(false, "");
						}},
				mMap );
		
		ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
		
	}

	protected void analysisFingerResponse(JSONObject response) {
		// TODO Auto-generated method stub
		Log.i(TAG, "analysisFingerResponse BEGIN");
		
		try {			
			JSONArray obj = new JSONArray(response.getString("data"));
			if (obj != null) {
				for (int i = 0; i < obj.length(); i++) {
					String strDownloadFileUrl = "";
					JSONObject dataContent = obj.getJSONObject(i);
					strDownloadFileUrl = dataContent.getString("data_path");
					Log.i("analysisFingerResponse", "strDownloadFileUrl = " +strDownloadFileUrl);
					//mHomeActivity.downloadFingerPrintDB(true, strDownloadFileUrl);
				}
			}
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addMember(String userPhoneNumber) {
		Log.i(TAG, "addMember BEGIN");
		
		String mPhone = "";
		try {
			mPhone = aes.encrypt_AES(userPhoneNumber);
		} catch(Exception e) { e.printStackTrace(); }
		
		String strApiUrl = "http://bigcity-rtls.doubleservice.com/api/bigcity/";//"http://218.211.88.196/api/BigCity/";
		String uuid = ApplicationController.getInstance().getUuid();
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "addMember");
		mMap.put("UUID", uuid);
		mMap.put("phone", mPhone);
		Log.i(TAG, "send data = " +mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(strApiUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("addMember", "onResponse response =" +response.toString());
						PreferenceProxy prefProxy = new PreferenceProxy(mContext);
						prefProxy.updatePhoneIsDone();
						//GlobalDataVO.isPhoneNumberUpdate = true;
					}}, new Response.ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("addMember", "onErrorResponse error = " +error.getMessage());
							//GlobalDataVO.isPhoneNumberUpdate = false;
						}},
				mMap );
		
		ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
	
	private void getMemberAll() {
		// TODO Auto-generated method stub
		Log.i(TAG, "getMemberAll BEGIN");
		
		String strApiUrl = "http://bigcity-rtls.doubleservice.com/api/bigcity/";//"http://218.211.88.196/api/BigCity/";
		String uuid = ApplicationController.getInstance().getUuid();
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "getMemberAll");
		mMap.put("UUID", uuid);
		Log.i(TAG, "send data = " +mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(strApiUrl,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("getMemberAll", "onResponse response =" +response.toString());
						mFriendListActivity.getMembers(true, response);
						
					}}, new Response.ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							Log.i("getMemberAll", "onErrorResponse error = " +error.getMessage());
							JSONObject res = null;
							mFriendListActivity.getMembers(false, res);
							
						}},
				mMap );
		
		ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
	}
}
