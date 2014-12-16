package com.doubleservice.bigcitynavigation;

import java.io.File;
import java.io.IOException;

import com.doubleservice.DataVO.PushMsgVO;
import com.doubleservice.proxy.DownloadFileProxy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PushMsgFileActivity extends Activity {

	private ProgressDialog pd = null;
	private String fileType = "";
	private String filePath = "";
	private PushMsgVO vo = null;
	private String TAG = "PushMsgFileActivity";
	
	private String webUrl = "http://www.fecityonline.com/BigCity/index.do";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_msg_layout);

		vo = (PushMsgVO) getIntent().getSerializableExtra("ap_msg");
		// if(vo)

		if (vo != null) {
			fileType = vo.fileExt;
			filePath = vo.fileName;
			webUrl = vo.url;
		}
		
		WebView webView = (WebView) findViewById(R.id.webView1);     

        WebSettings websettings = webView.getSettings();  
        websettings.setSupportZoom(true);  
        websettings.setBuiltInZoomControls(true);  
        websettings.setJavaScriptEnabled(true);
        websettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        websettings.setUseWideViewPort(true);  
        websettings.setLoadWithOverviewMode(true);  
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient());  
        
        
        Log.i(TAG, "URL = " +webUrl);//"http://m.citypass.tw/member/login_app.php?link="+postData2);
        webView.loadUrl(webUrl);  
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(getResources().getString(R.string.menu_push_msg));
	}

	public void fileDownloadStatus(boolean isDone) {
		Log.i(TAG, "fileDownloadStatus is done" + isDone);
		if (pd != null) {
			pd.dismiss();
			if (isDone) {
				filePath = "/sdcard/Double_Service/" + filePath;
				openFile();
			}
		}
	}

	protected void openFile() {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		if(file!=null&&file.isFile())
        {
            String fileName = file.toString();
            Intent intent;
            if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingImage))){
                intent = OpenFiles.getImageFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingWebText))){
                intent = OpenFiles.getHtmlFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingPackage))){
                intent = OpenFiles.getApkFileIntent(file);
                startActivity(intent);

            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingAudio))){
                intent = OpenFiles.getAudioFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingVideo))){
                intent = OpenFiles.getVideoFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingText))){
                intent = OpenFiles.getTextFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingPdf))){
                intent = OpenFiles.getPdfFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingWord))){
                intent = OpenFiles.getWordFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingExcel))){
                intent = OpenFiles.getExcelFileIntent(file);
                startActivity(intent);
            }else if(checkEndsWithInStringArray(fileName, getResources().
                    getStringArray(R.array.fileEndingPPT))){
                intent = OpenFiles.getPPTFileIntent(file);
                startActivity(intent);
            }else
            {
            	Log.i(TAG, "openFile open fail");
            }
        }else
        {
        	Log.i(TAG, "openFile open fail");
        }
	}

	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}

		return true;
	}
	
}
