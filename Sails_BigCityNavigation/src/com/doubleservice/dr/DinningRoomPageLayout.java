package com.doubleservice.dr;

import android.content.Context;
import android.widget.ImageView;

import com.doubleservice.bigcitynavigation.R;
import com.james.views.FreeLayout;

public class DinningRoomPageLayout extends FreeLayout {

	public ImageView bottomView;
	public FreeLayout navBtnLayout;
	public ImageView btnNavigation;
	public FreeLayout reserveBtnLayout;
	public ImageView btnReservation;
	public ImageView imgContentView;

	public DinningRoomPageLayout(Context context) {
		super(context);

		this.setFreeLayoutFF();
		this.setPicSize(640, 960, TO_WIDTH);
		this.setBackgroundColor(0xffffffff);

		imgContentView = (ImageView) this.addFreeView(new ImageView(mContext),
				640, 1173);
		imgContentView.setImageResource(R.drawable.content01);
		//imgContentView.setBackgroundResource(R.drawable.content01);

		bottomView = (ImageView) this.addFreeView(new ImageView(mContext), 640,
				218, new int[] { ALIGN_PARENT_BOTTOM });
		bottomView.setBackgroundResource(R.drawable.img_dr_bottom);

		navBtnLayout = (FreeLayout) this.addFreeView(new FreeLayout(mContext),
				320, 180, new int[] { ALIGN_PARENT_BOTTOM });
		navBtnLayout.setPicSize(640, 960, TO_WIDTH);

		btnNavigation = (ImageView) navBtnLayout.addFreeView(new ImageView(
				mContext), 247, 86, new int[] { CENTER_IN_PARENT });
		btnNavigation.setBackgroundResource(R.drawable.btn_navigation);

		reserveBtnLayout = (FreeLayout) this.addFreeView(new FreeLayout(
				mContext), 320, 180, new int[] { ALIGN_PARENT_BOTTOM,
				ALIGN_PARENT_RIGHT });
		reserveBtnLayout.setPicSize(640, 960, TO_WIDTH);

		btnReservation = (ImageView) reserveBtnLayout.addFreeView(
				new ImageView(mContext), 247, 86,
				new int[] { CENTER_IN_PARENT });
		btnReservation.setBackgroundResource(R.drawable.btn_reservation);
	}
}
