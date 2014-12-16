package com.doubleservice.findfriends;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.proxy.ApiProxy;

public class FindFriendDes extends BaseDrawerActivity {

	private String TAG = "FindFriendDes";
	
	private EditText phoneEditView;
	private ImageView confirmBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(
				R.layout.find_friends_des_layout, null, false);
		frameLayout.addView(activityView);

		getActionBar().setTitle(
				getResources().getString(R.string.menu_find_friend));
		setupView();
	}

	private void setupView() {
		// TODO Auto-generated method stub
		phoneEditView = (EditText) findViewById(R.id.edit_phone);

		confirmBtn = (ImageView) findViewById(R.id.btnStart);
		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isRegularPhoneNumber = phoneEditView.getText()
						.toString().matches("[0-9]{10}");
				phoneCheckRes(isRegularPhoneNumber);
			}
		});
	}
	
	protected void phoneCheckRes(boolean isRegularPhoneNumber) {
		// TODO Auto-generated method stub
		Log.i(TAG, "phoneCheckRes BEGIN, edit number correct = " +isRegularPhoneNumber);
		
		if(isRegularPhoneNumber) {
			Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(getResources().getString(
					R.string.setting_warn));
			alertDialog.setMessage(getResources().getString(
					R.string.dialog_msg_find_friend_tips));
			alertDialog.setPositiveButton(getResources().getString(
					R.string.dialog_btn_confirm),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							addMember();
						}
					});

			alertDialog.setCancelable(false);
			alertDialog.show();
		} else {
			Toast.makeText(this, getResources().getString(R.string.toast_str_wrong_phone_number), 
					Toast.LENGTH_SHORT).show();
		}
		
		//Toast.makeText(this, "Phone numebr you input is "+isRegularPhoneNumber, Toast.LENGTH_SHORT).show();
	}

	protected void addMember() {
		// TODO Auto-generated method stub
		String mPhoneNumber =  phoneEditView.getText().toString();
		
		ApiProxy apiProxy = new ApiProxy(this);
		apiProxy.addMember(mPhoneNumber);
		
		goToFriendListPage();
	}

	protected void goToFriendListPage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(this, FriendList.class);
		this.startActivity(intent);
	}
}
