package com.doubleservice.promotional;

import com.doubleservice.DataVO.BrandDataVO;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.brand.BrandDBProxy;
import com.doubleservice.brand.BrandLayout;
import com.doubleservice.memorycontroller.ImageLoader;
import com.doubleservice.parking.UserParkingInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FreePromotionalPage extends Activity {

	private String TAG = "PromotionalContent";
	private String id = "";
	private ImageLoader mImageLoader;

	private BrandDBProxy proxy;// = new BrandDBProxy(this);
	private PromotionalLayout mpLayout;

	private NavDataVO navDataVO = new NavDataVO();
	private boolean getNavData = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promotional_content_layout);

		mImageLoader = new ImageLoader(this);
		proxy = new BrandDBProxy(this);
		setupView();

		Bundle params = getIntent().getExtras();
		if (params != null) {
			id = params.getString(RequestCodeVO.CMD_REQ_PROMOTIONAL);
			Log.i(TAG, "open list =" + id);
			
		}

		if (id != null) {
			/*
			if(id.equals("123"))
				findBrandDataById("57");
			else if(id.equals("124")) 
				findBrandDataById("36");
			else */
			findBrandDataById(id);
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(
				getResources().getString(R.string.menu_find_promotion));
	}

	private void findBrandDataById(String id) {
		// TODO Auto-generated method stub
		BrandDataVO vo = new BrandDataVO();
		vo = proxy.findPromotionalDataById(id);
		mImageLoader.DisplayImage(vo.bImg, mpLayout.img, false);
		mpLayout.brandTitle.setText(vo.bTitle);
		if (vo.bArea.equals("1")) {
			mpLayout.brandArea.setText("BigCity-4F");
		} else if (vo.bArea.equals("2")) {
			mpLayout.brandArea.setText("Mall-4F");
		}
		mpLayout.brandContent.setText(vo.bContent);

		if (vo.bShopList != null) {
			if (vo.bShopList.length() > 1) {
				findNavigationData(vo.bShopList);
			} else {
				mpLayout.bottomLayout.setVisibility(View.GONE);
			}
				
		}
	}

	private void findNavigationData(String bShopList) {
		// TODO Auto-generated method stub
		Log.i(TAG, "findNavigationData");
		getNavData = true;
		
		if(bShopList.equals("4051")) {
			navDataVO = ApplicationController.getInstance()
					.getLocationDataByNumber("57");
			navDataVO.MethodID = GlobalDataVO.NAV_METHOD_PROMOTIONAL;
		} else if(bShopList.equals("4030")){
			navDataVO = ApplicationController.getInstance()
					.getLocationDataByNumber("36");
			navDataVO.MethodID = GlobalDataVO.NAV_METHOD_PROMOTIONAL;
		}
		
		
	}

	private void setupView() {
		// TODO Auto-generated method stub
		mpLayout = new PromotionalLayout(this);
		setContentView(mpLayout);

		mpLayout.brandTitle.setText("title");
		mpLayout.brandTitle.setTextSize(17);
		mpLayout.brandTitle.setTextColor(0xff4f4f4f);
		mpLayout.brandArea.setText("area");
		mpLayout.brandArea.setTextColor(0xff838383);
		// mpLayout.brandArea.setTextSize(18);
		mpLayout.brandContent.setText("Content");
		mpLayout.brandContent.setTextColor(0xff282828);
		mpLayout.brandContent.setMovementMethod(new ScrollingMovementMethod());
		mpLayout.btnNav.setOnClickListener(btnClick);
	}
	
	OnClickListener btnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClick");
			// TODO Auto-generated method stub
			goToNavigation();
		}
		
	};

	protected void goToNavigation() {
		// TODO Auto-generated method stub
		Log.i(TAG, "goToNavigation");

		String alertTitle = getResources().getString(R.string.setting_warn);
		String alertMsg = getResources().getString(R.string.dialog_msg_gotomap);
		String btnConfirm = getResources().getString(
				R.string.dialog_btn_confirm);
		String btnCancel = getResources().getString(R.string.dialog_btn_cancel);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(alertTitle);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(btnConfirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent();
						intent.setClass(FreePromotionalPage.this, Navigation.class);
						intent.putExtra(RequestCodeVO.CMD_NAV, navDataVO);
						ApplicationController.navData = navDataVO;
						startActivity(intent);
						GlobalDataVO.CURRENT_DRAWER_ITEM = 999;
					}
				});
		builder.setNegativeButton(btnCancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onPause() {
		super.onPause();
		proxy.closeDB();
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}

		return true;
	}
}
