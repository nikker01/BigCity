package com.doubleservice.dr;

import android.content.Context;
import android.widget.ImageView;

import com.doubleservice.bigcitynavigation.R;
import com.james.views.FreeLayout;

public class ReservationLayout extends FreeLayout {

	public ImageView bottomView;
	public FreeLayout navBtnLayout;
	public ImageView btnNavigation;
	public FreeLayout reserveBtnLayout;
	public ImageView btnReservationCancel;
	public ImageView imgContentView;

	public ReservationLayout(Context context) {
		super(context);

		this.setFreeLayoutFF();
		this.setPicSize(640, 960, TO_WIDTH);

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

		btnReservationCancel = (ImageView) reserveBtnLayout.addFreeView(
				new ImageView(mContext), 247, 86,
				new int[] { CENTER_IN_PARENT });
		btnReservationCancel.setBackgroundResource(R.drawable.btn_reservation_cancel);
	}
}

