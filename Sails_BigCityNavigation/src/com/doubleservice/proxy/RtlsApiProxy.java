//package com.doubleservice.dsrtls.proxy;
package com.doubleservice.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectPostRequest;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RtlsApiProxy {

	// static String apiURL = "http://192.168.20.9/api/";

	// private static final Logger logger = LoggerFactory.getLogger();
	public ArrayList<String> floorList = new ArrayList<String>();
	public ArrayList<String> apNameList = new ArrayList<String>();
	public ArrayList<String> apMacList = new ArrayList<String>();
	public double rssiMin = 0;
	public double rssiMax = 0;

	private String flagType = "";

	// private String getAPInfoRes;
	String TAG = "RtlsApiProxy";
	Navigation mActivity;

	public RtlsApiProxy(Navigation activity) {
		this.mActivity = activity;
	}

	public JsonObjectPostRequest updNavigation(String androidID, String x,
			String y, String floor, String location) {
		Log.i(TAG, "updNavigation BEGIN");
		String apiURL = "http://bigcity-rtls.doubleservice.com/api/updNavigation.php";//"http://218.211.88.196/api/updNavigation.php";

		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "updNavigation");
		mMap.put("UUID", androidID);
		mMap.put("Floor", floor);
		mMap.put("pointX", x);//
		mMap.put("pointY", y);
		mMap.put("nickName", "");
		mMap.put("location", location);
		Log.i("JsonObjectPostRequest", "send data = " + mMap.toString());

		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(
				apiURL, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onResponse response ="
								+ response.toString());
						mActivity.parseNavigationMsg(response, true);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onErrorResponse error = "
								+ error.getMessage());
						mActivity.parseNavigationMsg(null, true);
					}
				}, mMap);

		return jsonObjectRequest;
	}

	public JsonObjectPostRequest updateStatus(String apMsg) {
		// TODO Auto-generated method stub
		Log.i(TAG, "updateStatus BEGIN");

		String apiURL = "http://bigcity-rtls.doubleservice.com/api/updMessageLog.php";//"http://218.211.88.196/api/updMessageLog.php";
		
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "updMessageLog");
		mMap.put("UUID", ApplicationController.getInstance().getUuid());
		mMap.put("ap_message", apMsg);
		Log.i("JsonObjectPostRequest", "send data = " + mMap.toString());
		
		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(
				apiURL, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onResponse response ="
								+ response.toString());
						
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onErrorResponse error = "
								+ error.getMessage());
						
					}
				}, mMap);

		return jsonObjectRequest;
	}

	public JsonObjectPostRequest updLocationAvg(String nickName,
			String androidID, String postData, String apiURL) {
		apiURL = "http://192.168.2.118/api/updLocationAverage.php";

		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put("client_id", "4765272503474547");
		mMap.put("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB");
		mMap.put("version", "1.0");
		mMap.put("cmd", "android");
		mMap.put("type", "json");
		mMap.put("method", "updLocation");
		mMap.put("UUID", androidID);
		mMap.put("nickName", nickName);
		mMap.put("location", postData);//
		mMap.put("flag", "y");//
		// Log.i("JsonObjectPostRequest", "send data = " +mMap.toString());

		JsonObjectPostRequest jsonObjectRequest = new JsonObjectPostRequest(
				apiURL, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onResponse response ="
								+ response.toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("updLocationAvg", "onErrorResponse error = "
								+ error.getMessage());
					}
				}, mMap);

		return jsonObjectRequest;
	}
	/*
	 * public static String[] updLocation(String nickName, String androidID,
	 * String postData, String apiURL) { Log.i("RtlsApiProxy",
	 * "post data BEGIN"); // logger.debug("RtlsApiProxy, post data");
	 * 
	 * 
	 * apiURL = "http://192.168.2.118/api/updLocationAverage.php";
	 * 
	 * String[] returnCode = new String[7];
	 * 
	 * HttpPost httpReqeust = new HttpPost(apiURL);
	 * 
	 * List<NameValuePair> params = new ArrayList<NameValuePair>();
	 * params.add(new BasicNameValuePair("client_id", "4765272503474547"));
	 * params.add(new BasicNameValuePair("client_secret",
	 * "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB")); params.add(new
	 * BasicNameValuePair("version", "1.0")); params.add(new
	 * BasicNameValuePair("cmd", "android")); params.add(new
	 * BasicNameValuePair("type", "json")); params.add(new
	 * BasicNameValuePair("method", "updLocation")); params.add(new
	 * BasicNameValuePair("UUID", androidID)); // if(nickname.equals(null)) //
	 * nickname = " ";
	 * 
	 * params.add(new BasicNameValuePair("nickName", nickName)); params.add(new
	 * BasicNameValuePair("location", postData));// params.add(new
	 * BasicNameValuePair("flag", "y"));//
	 * 
	 * Log.i("postData", "updLocation sendData = " + params.toString()); //
	 * logger.debug("postData, updLocation sendData = " + // params.toString());
	 * 
	 * try { HttpParams httpParameters = new BasicHttpParams();
	 * HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
	 * HttpConnectionParams.setSoTimeout(httpParameters, 5000);
	 * 
	 * httpReqeust.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	 * 
	 * HttpResponse httpResponse = new DefaultHttpClient(httpParameters)
	 * .execute(httpReqeust);
	 * 
	 * if (httpResponse.getStatusLine().getStatusCode() == 200) { String
	 * strResult = EntityUtils.toString(httpResponse .getEntity());
	 * 
	 * Log.i("updLocation", "HttpPostResult = " + strResult); //
	 * logger.debug("updLocation, HttpPostResult = " + strResult);
	 * 
	 * try { JSONObject result = new JSONObject(strResult); Log.i("code",
	 * result.getString("code")); Log.i("msg", result.getString("msg"));
	 * Log.i("data", result.getString("data"));
	 * 
	 * JSONObject resultData = new JSONObject( result.getString("data")); String
	 * myID = resultData.getString("seq_no"); String isHotzone =
	 * resultData.getString("hotzone"); String hotzone_id =
	 * resultData.getString("hotzone_id"); String hotzone_message =
	 * resultData.getString("hotzone_message"); returnCode[0] =
	 * Integer.toString(GlobalDataVO.APISUCCESS); returnCode[1] = myID;
	 * returnCode[2] = isHotzone; returnCode[3] = hotzone_id; returnCode[4] =
	 * hotzone_message;
	 * 
	 * String hotzone_file_ext = resultData.getString("hotzone_file_ext");
	 * String hotzone_file = resultData.getString("hotzone_file");
	 * Log.i("RtlsApiProxy", "My MemberID = " + myID); Log.i("RtlsApiProxy",
	 * "is hotzone = " + isHotzone); //
	 * logger.debug("RtlsApiProxy,  My MemberID = " + myID);
	 * 
	 * returnCode[5] = hotzone_file_ext; returnCode[6] = hotzone_file; } catch
	 * (JSONException e) { // TODO Auto-generated catch block
	 * Log.i("updLocation", "JSONException"); //
	 * logger.debug("updLocation, JSONException"); returnCode[0] =
	 * Integer.toString(GlobalDataVO.JSONException); e.printStackTrace(); } }
	 * else { returnCode[0] = httpResponse.getStatusLine().getReasonPhrase();
	 * returnCode[1] = ""; }
	 * 
	 * } catch (IllegalArgumentException e) { Log.i("RtlsApiProxy",
	 * "IllegalArgumentException"); //
	 * logger.debug("RtlsApiProxy , IllegalArgumentException"); returnCode[0] =
	 * Integer.toString(GlobalDataVO.IllegalArgumentException);
	 * e.printStackTrace(); } catch (ConnectTimeoutException e) {
	 * Log.i("RtlsApiProxy", "ConnectTimeoutException"); //
	 * logger.debug("RtlsApiProxy , ConnectTimeoutException"); returnCode[0] =
	 * Integer.toString(GlobalDataVO.ConnectTimeoutException);
	 * e.printStackTrace(); } catch (SocketTimeoutException e) {
	 * e.printStackTrace(); returnCode[0] =
	 * Integer.toString(GlobalDataVO.SocketTimeoutException);
	 * Log.i("RtlsApiProxy", "SocketTimeoutException"); //
	 * logger.debug("RtlsApiProxy , SocketTimeoutException");
	 * 
	 * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); returnCode[0] =
	 * Integer.toString(GlobalDataVO.UnsupportedEncodingException);
	 * Log.i("RtlsApiProxy", "UnsupportedEncodingException"); //
	 * logger.debug("RtlsApiProxy , UnsupportedEncodingException"); } catch
	 * (ClientProtocolException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); returnCode[0] =
	 * Integer.toString(GlobalDataVO.ClientProtocolException);
	 * Log.i("RtlsApiProxy", "ClientProtocolException"); //
	 * logger.debug("RtlsApiProxy , ClientProtocolException"); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * returnCode[0] = Integer.toString(GlobalDataVO.IOException);
	 * Log.i("RtlsApiProxy", "IOException");
	 * 
	 * }
	 * 
	 * httpReqeust.abort();
	 * 
	 * return returnCode; }
	 */
}

/*
 * package com.doubleservice.dsnavi.proxy;
 * 
 * import java.io.IOException; import java.io.UnsupportedEncodingException;
 * import java.net.SocketTimeoutException; import java.util.ArrayList; import
 * java.util.List;
 * 
 * import org.apache.http.HttpResponse; import org.apache.http.NameValuePair;
 * import org.apache.http.client.ClientProtocolException; import
 * org.apache.http.client.HttpClient; import
 * org.apache.http.client.entity.UrlEncodedFormEntity; import
 * org.apache.http.client.methods.HttpPost; import
 * org.apache.http.conn.ConnectTimeoutException; import
 * org.apache.http.impl.client.DefaultHttpClient; import
 * org.apache.http.message.BasicNameValuePair; import
 * org.apache.http.params.BasicHttpParams; import
 * org.apache.http.params.HttpConnectionParams; import
 * org.apache.http.params.HttpParams; import org.apache.http.protocol.HTTP;
 * import org.apache.http.util.EntityUtils; import org.json.JSONArray; import
 * org.json.JSONException; import org.json.JSONObject;
 * 
 * import com.doubleservice.DataVO.GlobalDataVO; import
 * com.google.code.microlog4android.Logger; import
 * com.google.code.microlog4android.LoggerFactory;
 * 
 * import android.content.Context; import android.util.Log; import
 * android.widget.Toast;
 * 
 * public class RtlsApiProxy {
 * 
 * // static String apiURL = "http://192.168.20.9/api/";
 * 
 * //private static final Logger logger = LoggerFactory.getLogger(); public
 * ArrayList<String> floorList = new ArrayList<String>(); public
 * ArrayList<String> apNameList = new ArrayList<String>(); public
 * ArrayList<String> apMacList = new ArrayList<String>(); public double rssiMin
 * = 0; public double rssiMax = 0;
 * 
 * //private String getAPInfoRes;
 * 
 * public int getApList(String androidID, String apiURL) { //RtlsApiProxy proxy
 * = new RtlsApiProxy(); //proxy.clearAPListData();
 * 
 * int apiCallBackRes = 0; ArrayList<String> m_FloorList = new
 * ArrayList<String>(); ArrayList<String> m_ApNameList = new
 * ArrayList<String>(); ArrayList<String> m_ApMacList = new ArrayList<String>();
 * double m_RssiMin = 0; double m_RssiMax = 0;
 * 
 * HttpPost httpReqeust = new HttpPost(apiURL);
 * 
 * List<NameValuePair> params = new ArrayList<NameValuePair>(); params.add(new
 * BasicNameValuePair("client_id", "4765272503474547")); params.add(new
 * BasicNameValuePair("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB"));
 * params.add(new BasicNameValuePair("version", "1.0")); params.add(new
 * BasicNameValuePair("cmd", "android")); params.add(new
 * BasicNameValuePair("type", "json")); params.add(new
 * BasicNameValuePair("method", "getApList")); params.add(new
 * BasicNameValuePair("UUID", androidID)); params.add(new
 * BasicNameValuePair("flag", "w"));
 * 
 * Log.i("RtlsApiProxy", "getApList sendData = " + params.toString());
 * 
 * try {
 * 
 * HttpParams httpParameters = new BasicHttpParams();
 * HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
 * HttpConnectionParams.setSoTimeout(httpParameters, 5000);
 * 
 * httpReqeust.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
 * 
 * HttpResponse httpResponse = new DefaultHttpClient(httpParameters)
 * .execute(httpReqeust);
 * 
 * Log.i("getApList", "httpResponse = " +
 * httpResponse.getStatusLine().getStatusCode());
 * 
 * if (httpResponse.getStatusLine().getStatusCode() == 200) { String strResult =
 * EntityUtils.toString(httpResponse .getEntity());
 * 
 * Log.i("getApList", "HttpPostResult = " + strResult);
 * 
 * try { JSONObject result = new JSONObject(strResult); Log.v("code",
 * result.getString("code")); Log.v("msg", result.getString("msg"));
 * Log.v("data", result.getString("data"));
 * 
 * if (result.getString("code").equals("1")) { clearAPListData(); apiCallBackRes
 * = GlobalDataVO.APISUCCESS;
 * 
 * JSONArray resultData = new JSONArray( result.getString("data")); for (int i =
 * 0; i < resultData.length(); i++) { JSONObject dataContent =
 * resultData.getJSONObject(i); Log.i("RtlsApiProxy", "getApList floor = " +
 * dataContent.getString("floor"));
 * m_FloorList.add(dataContent.getString("floor")); Log.i("RtlsApiProxy",
 * "getApList SSID = " + dataContent.getString("SSID"));
 * m_ApNameList.add(dataContent.getString("SSID"));
 * 
 * Log.i("RtlsApiProxy", "getApList macAddress = " +
 * dataContent.getString("macAddress"));
 * m_ApMacList.add(dataContent.getString("macAddress"));
 * 
 * Log.i("RtlsApiProxy", "getApList rssiMin = " +
 * dataContent.getString("rssiMin")); m_RssiMin =
 * Double.parseDouble(dataContent.getString("rssiMin"));
 * 
 * Log.i("RtlsApiProxy", "getApList rssiMax = " +
 * dataContent.getString("rssiMax")); m_RssiMax =
 * Double.parseDouble(dataContent.getString("rssiMax"));
 * 
 * } }
 * 
 * } catch (JSONException e) { // TODO Auto-generated catch block
 * Log.i("RtlsApiProxy", "JSONException"); e.printStackTrace(); apiCallBackRes =
 * GlobalDataVO.JSONException; } catch (IllegalArgumentException e) {
 * Log.i("RtlsApiProxy", "IllegalArgumentException"); apiCallBackRes =
 * GlobalDataVO.IllegalArgumentException; e.printStackTrace(); }
 * 
 * } else if (httpResponse.getStatusLine().getStatusCode() == 404) {
 * apiCallBackRes = httpResponse.getStatusLine().getStatusCode();
 * //Log.i("RtlsApiProxy", " getApList " + getAPInfoRes); }
 * 
 * } catch (IllegalArgumentException e) { Log.i("RtlsApiProxy",
 * "IllegalArgumentException"); apiCallBackRes =
 * GlobalDataVO.IllegalArgumentException; e.printStackTrace(); } catch
 * (ConnectTimeoutException e) { Log.i("RtlsApiProxy",
 * "ConnectTimeoutException"); e.printStackTrace(); apiCallBackRes =
 * GlobalDataVO.ConnectTimeoutException;
 * 
 * } catch (SocketTimeoutException e) { e.printStackTrace();
 * Log.i("RtlsApiProxy", "SocketTimeoutException"); apiCallBackRes =
 * GlobalDataVO.SocketTimeoutException;
 * 
 * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); Log.i("RtlsApiProxy", "UnsupportedEncodingException");
 * apiCallBackRes = GlobalDataVO.UnsupportedEncodingException;
 * 
 * } catch (ClientProtocolException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); Log.i("RtlsApiProxy", "ClientProtocolException");
 * apiCallBackRes = GlobalDataVO.ClientProtocolException;
 * 
 * } catch (IOException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); Log.i("RtlsApiProxy", "IOException"); apiCallBackRes =
 * GlobalDataVO.IOException; }
 * 
 * setApListData(m_FloorList, m_ApNameList, m_ApMacList, m_RssiMin, m_RssiMax);
 * httpReqeust.abort(); return apiCallBackRes; }
 * 
 * 
 * private void setApListData(ArrayList<String> m_FloorList, ArrayList<String>
 * m_ApNameList, ArrayList<String> m_ApMacList, double m_RssiMin, double
 * m_RssiMax) { // TODO Auto-generated method stub floorList = m_FloorList;
 * apNameList = m_ApNameList; apMacList = m_ApMacList; rssiMin = m_RssiMin;
 * rssiMax = m_RssiMax; }
 * 
 * public double getMaxRssi() { return rssiMax; }
 * 
 * public double getMinRssi() { return rssiMin; }
 * 
 * public ArrayList<String> getAPMacList() { return apMacList; }
 * 
 * public ArrayList<String> getAPNameList() { return apNameList; }
 * 
 * public ArrayList<String> getFloorList() { return floorList; }
 * 
 * 
 * private void clearAPListData() { // TODO Auto-generated method stub
 * floorList.clear(); apNameList.clear(); apMacList.clear(); }
 * 
 * 
 * public static void updAlive(String androidID, String postData, String apiURL)
 * { Log.i("RtlsApiProxy", "updAlive BEGIN");
 * 
 * if (!apiURL.contains("http")) { apiURL = "http://" + apiURL + "/api/"; }
 * HttpPost httpReqeust = new HttpPost(apiURL);
 * 
 * List<NameValuePair> params = new ArrayList<NameValuePair>(); params.add(new
 * BasicNameValuePair("client_id", "4765272503474547")); params.add(new
 * BasicNameValuePair("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB"));
 * params.add(new BasicNameValuePair("version", "1.0")); params.add(new
 * BasicNameValuePair("cmd", "android")); params.add(new
 * BasicNameValuePair("type", "json")); params.add(new
 * BasicNameValuePair("method", "updAlive")); params.add(new
 * BasicNameValuePair("UUID", androidID)); params.add(new
 * BasicNameValuePair("location", postData));//
 * 
 * 
 * Log.i("postData", "updAlive sendData = " + params.toString()); //
 * logger.debug("postData, updLocation sendData = " + // params.toString());
 * 
 * try { HttpParams httpParameters = new BasicHttpParams();
 * HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
 * HttpConnectionParams.setSoTimeout(httpParameters, 5000);
 * 
 * httpReqeust.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
 * 
 * HttpResponse httpResponse = new DefaultHttpClient(httpParameters)
 * .execute(httpReqeust);
 * 
 * if (httpResponse.getStatusLine().getStatusCode() == 200) { String strResult =
 * EntityUtils.toString(httpResponse .getEntity());
 * 
 * Log.i("updAlive", "HttpPostResult = " + strResult);
 * 
 * try { JSONObject result = new JSONObject(strResult); Log.i("code",
 * result.getString("code")); Log.i("msg", result.getString("msg"));
 * Log.i("data", result.getString("data"));
 * 
 * } catch (JSONException e) { // TODO Auto-generated catch block
 * Log.i("updAlive", "JSONException"); e.printStackTrace(); } }else {
 * Log.i("updAlive", "code = " +httpResponse.getStatusLine()); }
 * 
 * } catch (IllegalArgumentException e) { Log.i("RtlsApiProxy",
 * "IllegalArgumentException"); e.printStackTrace(); } catch
 * (ConnectTimeoutException e) { Log.i("RtlsApiProxy",
 * "ConnectTimeoutException"); e.printStackTrace(); } catch
 * (SocketTimeoutException e) { e.printStackTrace(); Log.i("RtlsApiProxy",
 * "SocketTimeoutException"); } catch (UnsupportedEncodingException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); Log.i("RtlsApiProxy",
 * "UnsupportedEncodingException"); } catch (ClientProtocolException e) { //
 * TODO Auto-generated catch block e.printStackTrace(); Log.i("RtlsApiProxy",
 * "ClientProtocolException"); } catch (IOException e) { // TODO Auto-generated
 * catch block e.printStackTrace(); Log.i("RtlsApiProxy", "IOException"); }
 * 
 * httpReqeust.abort(); }
 * 
 * public static String[] updLocation(String nickName, String androidID, String
 * postData, String apiURL) { Log.i("RtlsApiProxy", "post data BEGIN"); //
 * logger.debug("RtlsApiProxy, post data");
 * 
 * if (!apiURL.contains("http")) { apiURL = "http://" + apiURL + "/api/"; }
 * 
 * String[] returnCode = new String[11];
 * 
 * HttpPost httpReqeust = new HttpPost(apiURL);
 * 
 * List<NameValuePair> params = new ArrayList<NameValuePair>(); params.add(new
 * BasicNameValuePair("client_id", "4765272503474547")); params.add(new
 * BasicNameValuePair("client_secret", "niUQ2nYjRu8dBVvNENwELqtouWM3eqKB"));
 * params.add(new BasicNameValuePair("version", "1.0")); params.add(new
 * BasicNameValuePair("cmd", "android")); params.add(new
 * BasicNameValuePair("type", "json")); params.add(new
 * BasicNameValuePair("method", "updLocationTriangle")); params.add(new
 * BasicNameValuePair("UUID", androidID)); // if(nickname.equals(null)) //
 * nickname = " ";
 * 
 * params.add(new BasicNameValuePair("nickName", nickName)); params.add(new
 * BasicNameValuePair("location", postData));// params.add(new
 * BasicNameValuePair("flag", "w"));//
 * 
 * // logger.debug("postData, updLocation sendData = " + // params.toString());
 * 
 * try { HttpParams httpParameters = new BasicHttpParams();
 * HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
 * HttpConnectionParams.setSoTimeout(httpParameters, 5000);
 * 
 * httpReqeust.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
 * 
 * HttpResponse httpResponse = new DefaultHttpClient(httpParameters)
 * .execute(httpReqeust);
 * 
 * if (httpResponse.getStatusLine().getStatusCode() == 200) { String strResult =
 * EntityUtils.toString(httpResponse .getEntity());
 * 
 * Log.i("updLocation", "HttpPostResult = " + strResult); //
 * logger.debug("updLocation, HttpPostResult = " + strResult);
 * 
 * try { JSONObject result = new JSONObject(strResult); Log.i("code",
 * result.getString("code")); Log.i("msg", result.getString("msg"));
 * Log.i("data", result.getString("data"));
 * 
 * JSONObject resultData = new JSONObject( result.getString("data")); String
 * myID = resultData.getString("seq_no"); String isHotzone =
 * resultData.getString("hotzone"); String hotzone_id =
 * resultData.getString("hotzone_id"); String hotzone_message =
 * resultData.getString("hotzone_message");
 * 
 * Log.i("RtlsApiProxy", "My MemberID = " + myID); Log.i("RtlsApiProxy",
 * "is hotzone = " + isHotzone); // logger.debug("RtlsApiProxy,  My MemberID = "
 * + myID);
 * 
 * returnCode[0] = Integer.toString(GlobalDataVO.APISUCCESS); returnCode[1] =
 * myID; returnCode[2] = isHotzone; returnCode[3] = hotzone_id; returnCode[4] =
 * hotzone_message; returnCode[5] = resultData.getString("uodFlag");
 * returnCode[6] = resultData.getString("pointx"); returnCode[7] =
 * resultData.getString("pointy"); returnCode[8] =
 * resultData.getString("dintance3"); //return for get rssiDiff returnCode[9] =
 * resultData.getString("rssi0"); returnCode[10] =
 * resultData.getString("alpha");
 * 
 * } catch (JSONException e) { // TODO Auto-generated catch block
 * Log.i("updLocation", "JSONException"); //
 * logger.debug("updLocation, JSONException"); returnCode[0] =
 * Integer.toString(GlobalDataVO.JSONException); e.printStackTrace(); } } else {
 * returnCode[0] = httpResponse.getStatusLine().getReasonPhrase(); returnCode[1]
 * = ""; }
 * 
 * } catch (IllegalArgumentException e) { Log.i("RtlsApiProxy",
 * "IllegalArgumentException"); //
 * logger.debug("RtlsApiProxy , IllegalArgumentException"); returnCode[0] =
 * Integer.toString(GlobalDataVO.IllegalArgumentException); e.printStackTrace();
 * } catch (ConnectTimeoutException e) { Log.i("RtlsApiProxy",
 * "ConnectTimeoutException"); //
 * logger.debug("RtlsApiProxy , ConnectTimeoutException"); returnCode[0] =
 * Integer.toString(GlobalDataVO.ConnectTimeoutException); e.printStackTrace();
 * } catch (SocketTimeoutException e) { e.printStackTrace(); returnCode[0] =
 * Integer.toString(GlobalDataVO.SocketTimeoutException); Log.i("RtlsApiProxy",
 * "SocketTimeoutException"); //
 * logger.debug("RtlsApiProxy , SocketTimeoutException");
 * 
 * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); returnCode[0] =
 * Integer.toString(GlobalDataVO.UnsupportedEncodingException);
 * Log.i("RtlsApiProxy", "UnsupportedEncodingException"); //
 * logger.debug("RtlsApiProxy , UnsupportedEncodingException"); } catch
 * (ClientProtocolException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); returnCode[0] =
 * Integer.toString(GlobalDataVO.ClientProtocolException); Log.i("RtlsApiProxy",
 * "ClientProtocolException"); //
 * logger.debug("RtlsApiProxy , ClientProtocolException"); } catch (IOException
 * e) { // TODO Auto-generated catch block e.printStackTrace(); returnCode[0] =
 * Integer.toString(GlobalDataVO.IOException); Log.i("RtlsApiProxy",
 * "IOException");
 * 
 * }
 * 
 * httpReqeust.abort();
 * 
 * return returnCode; }
 * 
 * }
 */