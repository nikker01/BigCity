package map.navi.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.doubleservice.bigcitynavigation.Navigation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;




public class NavigationAPIProxy {

	private Navigation activity;
	private String apiURL = "http://"+"rtls.doubleservice.com"+"/api/";
	private String saveImgIndex;
	private ProgressDialog pd;
	private ProgressDialog simpleWaitDialog;
	public ArrayList<String> mMapImageList = new ArrayList<String>();
	private int pDwnIndex = 0;
	public boolean downLoadComplete = false;
	//private Handler handler;
	
	public NavigationAPIProxy(Navigation activity) {
		//this.activity = activity;
		//this.downLoadComplete = false;
		//siteSurveyAPIProxy.apiURL = this.apiURL;
		//this.handler = handler;
	}
	
	/*
	public void downLoadNavigationData() {
		//pd = ProgressDialog.show(this.activity, "", this.activity.getResources().getString(R.string.progress_load));
		this.apiProgress();
	}
	
	private void apiProgress() {
		// TODO Auto-generated method stub
		pd = ProgressDialog.show(this.activity, "", this.activity.getResources().getString(R.string.progress_load));
		new Thread() {
			@Override
			public void run() {
				String android_id = Secure.getString(activity.getApplication().getContentResolver(),
						Secure.ANDROID_ID);
				siteSurveyAPIProxy.getApList(android_id);
				//siteSurveyAPIProxy.getApSuggest(android_id);
				//siteSurveyAPIProxy.getMapUrl(android_id);
				handlerGetMapList.sendEmptyMessage(0);
			}

		}.start();
	}
	
	Handler handlerGetMapList = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			//activity.initActionBar();
			//getMapList();
			
		}
	};
	
	public void getMapList() {
		// TODO Auto-generated method stub
		Log.i("Survey Tool", "getMapList");
		
		if (!apiURL.contains("http")) {
			//apiURL = "http://" + apiURL + "/api/";
		}
		mMapImageList = siteSurveyAPIProxy.getMapList();
		
		if(mMapImageList.size()>0)
		{
			File docFolder = new File(Environment.getExternalStorageDirectory(),
					"Double_Service");
			docFolder.mkdirs();
			
			Log.i("Survey Tool", "getMapPicData");
			String downloadUrl = mMapImageList.get(0);
			new ImageDownloader().execute(downloadUrl);
		}	
	}
	
	public void picDownloadCheck() {
		// TODO Auto-generated method stub
		Log.i("Survey Tool", "picDownloadCheck BEGIN ");
		String downloadUrl = null;

		pDwnIndex = pDwnIndex + 1;
		Log.i("Map DownLoad","current index = "+ pDwnIndex+", mMapImageList = "+ mMapImageList.size());
		
		if (pDwnIndex < mMapImageList.size()) {
			Log.i("Survey tool", "new ImgDownload");

			downloadUrl = mMapImageList.get(pDwnIndex);
			new ImageDownloader().execute(downloadUrl);
		} 
		//else
			//activity.initActionBar();
	}
	public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... param) {
			// TODO Auto-generated method stub
			return downloadBitmap(param[0]);
		}

		@Override
		protected void onPreExecute() {
			Log.i("ImageDownloader", "onPreExecute BEGIN");
			simpleWaitDialog = ProgressDialog.show(activity, "",
					activity.getResources().getString(R.string.progress_load));

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			Log.i("ImageDownloader", "onPostExecute BEGIN saveImgIndex = "
					+ saveImgIndex);
			// downloadedImg.setImageBitmap(result);

			if (result != null)
				saveMyBitmap(result);

			simpleWaitDialog.dismiss();

			// if()
			picDownloadCheck();

		}

		private void saveMyBitmap(Bitmap result) {
			// TODO Auto-generated method stub

			File f = new File("/sdcard/Double_Service/" + "Map_Image_"
					+ saveImgIndex + ".jpg");
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i("Error", "Error in saving bitmap");

			}
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			result.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private Bitmap downloadBitmap(String urlID) {
			// initilize the default HTTP client object

			String[] tmpUrl = urlID.split("###");
			String url = tmpUrl[1];
			saveImgIndex = tmpUrl[0];

			Log.i("downloadBitmap", "downloadBitmap url = " + url);
			Log.i("downloadBitmap", "downloadBitmap saveImgIndex = "
					+ saveImgIndex);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);
			
			final DefaultHttpClient client = new DefaultHttpClient(httpParameters);

			// forming a HttoGet request
			final HttpGet getRequest = new HttpGet(url);
			try {

				HttpResponse response = client.execute(getRequest);

				// check 200 OK for success
				final int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					Log.i("ImageDownloader", "Error " + statusCode
							+ " while retrieving bitmap from " + url);
					return null;

				}

				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						// getting contents from the stream
						inputStream = entity.getContent();

						// decoding stream data back into image Bitmap that
						// android understands
						final Bitmap bitmap = BitmapFactory
								.decodeStream(inputStream);

						return bitmap;
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				// You Could provide a more explicit error message for
				// IOException
				getRequest.abort();
				Log.i("ImageDownloader",
						"Something went wrong while retrieving bitmap from "
								+ url + e.toString());
			}

			return null;
		}
	}
	*/
}
