package com.doubleservice.bigcitynavigation;

import java.io.IOException;

import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MemberHappyGo extends BaseDrawerActivity{

	String webUrl = "http://m.happygocard.com.tw/HappyGoMobile/memWeb/pointChoose.do";
	private static final String INJECTION_TOKEN = "**injection**";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(
				R.layout.member_layout, null, false);
		frameLayout.addView(activityView);

		setComponent();
		
		getActionBar().setTitle(getResources().getString(R.string.menu_happygo));
	}

	private void setComponent() {
		// TODO Auto-generated method stub
		String myURL = webUrl;         
        WebView myBrowser=(WebView)findViewById(R.id.webView1);
        
        WebSettings websettings = myBrowser.getSettings();  
        websettings.setSupportZoom(true);  
        websettings.setBuiltInZoomControls(true);   
        websettings.setJavaScriptEnabled(true);  
        
        //myBrowser.loadDataWithBaseURL( "file:///android_asset/", html, "text/html", 
        	//	"utf-8", null ); 
        myBrowser.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response = super.shouldInterceptRequest(view, url);
                if(url != null && url.contains(INJECTION_TOKEN)) {
                    String assetPath = url.substring(url.indexOf(INJECTION_TOKEN) + INJECTION_TOKEN.length(), url.length());
                    try {
                        response = new WebResourceResponse(
                                "application/javascript",
                                "UTF8",
                                MemberHappyGo.this.getAssets().open(assetPath)
                        );
                    } catch (IOException e) {
                        e.printStackTrace(); // Failed to load asset file
                    }
                }
                return response;
            }
        });
         
        myBrowser.setWebViewClient(new WebViewClient());  
  
        myBrowser.loadUrl(myURL);
	}
}
