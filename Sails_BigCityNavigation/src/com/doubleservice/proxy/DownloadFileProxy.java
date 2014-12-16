package com.doubleservice.proxy;

import java.io.File;
import com.doubleservice.bigcitynavigation.FreeHomepage;
import com.doubleservice.bigcitynavigation.PushMsgFileActivity;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class DownloadFileProxy {

	private String TAG = "DownloadFileProxy";
	private DownloadManager downloadManager;  
	//private Context mContext;
	//position_1F mHomeActivity;
	PushMsgFileActivity msgActivity;
	
	/*
	public DownloadFileProxy(position_1F activity){
		this.mHomeActivity = activity;
	}
	*/
	
	public DownloadFileProxy(PushMsgFileActivity activity) {
		this.msgActivity = activity;
	}
	
	public void startDownload(String downloadUrl, String fileName, String fileExtName) {
		
		Log.i("DownloadFileProxy", "startDownload BEGIN");
		
		downloadManager = (DownloadManager)msgActivity.getSystemService("download");  
		
		String url = downloadUrl;
		Uri resource = Uri.parse(url);  
		DownloadManager.Request request = new DownloadManager.Request(resource);   
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);   
        request.setAllowedOverRoaming(false);   
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();  
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));  
        request.setMimeType(mimeString);  
        
        request.setShowRunningNotification(true);  
        request.setVisibleInDownloadsUi(true);  
        
        File docFolder = new File(Environment.getExternalStorageDirectory(),
				"Double_Service");
        if(!docFolder.exists()) {
        	docFolder.mkdirs();
        }
		
        request.setDestinationInExternalPublicDir("/Double_Service/", fileName);  
        request.setTitle("");   
        long id = downloadManager.enqueue(request); 
        
        msgActivity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	public void fileDone(boolean isDone) {
		//mHomeActivity.isFileDownloadingSuccess(isDone);
		Log.i(TAG, "file downloading done"+isDone);
		msgActivity.fileDownloadStatus(isDone);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {   
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("intent", ""+arg1.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));  
            queryDownloadStatus();   
		}   
    };
    
    private void queryDownloadStatus() {   
        DownloadManager.Query query = new DownloadManager.Query();   
        //query.setFilterById(prefs.getLong(DL_ID, 0));   
        Cursor c = downloadManager.query(query);   
        if(c.moveToFirst()) {   
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));   
            switch(status) {   
            case DownloadManager.STATUS_PAUSED:   
                Log.i("down", "STATUS_PAUSED");  
                fileDone(false);
            case DownloadManager.STATUS_PENDING:   
                Log.i("down", "STATUS_PENDING");  
                fileDone(false);
            case DownloadManager.STATUS_RUNNING:   
                Log.i("down", "STATUS_RUNNING");  
                break;   
            case DownloadManager.STATUS_SUCCESSFUL:   
                Log.i("down", "STATUS_SUCCESSFUL");  
                fileDone(true);
                break;   
            case DownloadManager.STATUS_FAILED:   
                Log.i("down", "STATUS_FAILED");  
                fileDone(false);
                //downloadManager.remove(prefs.getLong(DL_ID, 0));   
               // prefs.edit().clear().commit();   
                break;   
            }   
        }  
    }  
}
