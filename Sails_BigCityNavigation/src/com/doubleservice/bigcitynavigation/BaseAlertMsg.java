package com.doubleservice.bigcitynavigation;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.doubleservice.DataVO.GlobalDataVO;

public class BaseAlertMsg {
	
	private String TAG = "BaseAlertMsg";
	
	FreeHomepage homepageActivity;

	public BaseAlertMsg(FreeHomepage activity, GlobalDataVO.AlertMsg msg) {
		this.homepageActivity = activity;
		
		if(msg == GlobalDataVO.AlertMsg.GetArticleFail) {
			Builder alertDialog = new AlertDialog.Builder(activity);
			alertDialog.setTitle(ApplicationController
					.getStringByResId(R.string.dialog_title_download_fail));
			alertDialog
					.setMessage(ApplicationController
							.getStringByResId(R.string.dialog_msg_download_fail_tips));
			alertDialog.setPositiveButton(
					ApplicationController.getStringByResId(R.string.dialog_btn_retry),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							homepageActivity.getArticle();
						}
					});
			alertDialog.setNegativeButton(
					ApplicationController.getStringByResId(R.string.dialog_btn_cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});

			alertDialog.setCancelable(false);
			alertDialog.show();
		}
	}
	public static void pushGeneralAlert(Context mContext, String mAlertTitle,
			String mAlertMsg, String mPositiveMsg, String mNegativeMsg,
			final IGenericAlert target) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setTitle(mAlertTitle)
				.setMessage(mAlertMsg)
				.setCancelable(false)
				.setPositiveButton(mPositiveMsg,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								target.PositiveMethod(dialog, id);
							}
						})
				.setNegativeButton(mNegativeMsg,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								target.NegativeMethod(dialog, id);
							}
						});

		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		alert.show();
	}
	
	static int choiceId = 0;
	public static void pushSingleChoiceAlert(Context mContext, String[] choice,
			String mPositiveMsg, String mNegativeMsg, final IGenericAlert target) {
		choiceId = 0;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setSingleChoiceItems(choice, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						choiceId = which;
					}
				})
				.setPositiveButton(mPositiveMsg,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								target.PositiveMethod(dialog, choiceId);
							}
						})
				.setNegativeButton(mNegativeMsg,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								target.NegativeMethod(dialog, id);
							}
						});

		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		alert.show();
	}
}
