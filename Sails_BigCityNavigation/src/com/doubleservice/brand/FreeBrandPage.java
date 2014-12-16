package com.doubleservice.brand;

import com.doubleservice.DataVO.BrandDataVO;
import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.NavDataVO;
import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.HomepageLayout;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.memorycontroller.ImageLoader;
import com.doubleservice.promotional.FreePromotionalPage;
import com.radiusnetworks.ibeacon.IBeaconManager;

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
import android.widget.Toast;

public class FreeBrandPage extends Activity {

	private String TAG = "BrandContent";
	private String id = "";

	private BrandLayout mpLayout;
	private ImageLoader mImageLoader;

	private BrandDBProxy proxy;
	private NavDataVO navDataVO = new NavDataVO();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mImageLoader = new ImageLoader(this);
		proxy = new BrandDBProxy(this);
		setLayout();

		Bundle params = getIntent().getExtras();
		if (params != null) {
			id = params.getString(RequestCodeVO.CMD_REQ_BRAND_PAGE);
			Log.i(TAG, " open list =" + id);
		}

		if (id != null) {
			findBrandDataById(id);
			navDataVO = ApplicationController.getInstance().getLocationDataByNumber(id);
			if(navDataVO != null)
				navDataVO.MethodID = GlobalDataVO.NAV_METHOD_BRAND;
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(
				getResources().getString(R.string.menu_find_brand));
	}

	@Override
	public void onPause() {
		super.onPause();
		proxy.closeDB();
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	private void findBrandDataById(String id) {
		// TODO Auto-generated method stub
		BrandDataVO vo = new BrandDataVO();
		vo = proxy.findDataById(id);
		//mpLayout.img.setBackgroundResource(R.drawable.bg_pic);
		mImageLoader.DisplayImage(vo.bImg, mpLayout.img, false);
		mpLayout.brandTitle.setText(vo.bTitle);
		if (vo.bArea.equals("1")) {
			mpLayout.brandArea.setText("BigCity-4F");
		} else if (vo.bArea.equals("2")) {
			mpLayout.brandArea.setText("Mall-4F");
		}

		mpLayout.brandContent.setText(vo.bContent);
		mpLayout.btnNav.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToNavigation();
			}});

	}
	
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
						GlobalDataVO.CURRENT_DRAWER_ITEM = 999;
						Intent intent = new Intent();
						intent.setClass(FreeBrandPage.this, Navigation.class);
						intent.putExtra(RequestCodeVO.CMD_NAV, navDataVO);
						ApplicationController.navData = navDataVO;
						startActivity(intent);
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

	private void setLayout() {
		// TODO Auto-generated method stub
		mpLayout = new BrandLayout(this);
		setContentView(mpLayout);

		mpLayout.brandTitle.setText("title");
		mpLayout.brandTitle.setTextSize(17);
		mpLayout.brandTitle.setTextColor(0xff4f4f4f);
		mpLayout.brandArea.setText("area");
		mpLayout.brandArea.setTextColor(0xff838383);
		//mpLayout.brandArea.setTextSize(18);
		mpLayout.brandContent.setText("Content");
		mpLayout.brandContent.setTextColor(0xff282828);
		mpLayout.brandContent.setMovementMethod(new ScrollingMovementMethod());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			break;
		}
		
		return true;
	}
}
