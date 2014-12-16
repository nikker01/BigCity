package com.doubleservice.dr;

import com.doubleservice.bigcitynavigation.R;
import com.james.views.FreeLayout;
import com.james.views.FreeTextView;

import android.content.Context;
import android.widget.ImageView;

public class ReservationNumberLayout extends FreeLayout {

	public ImageView topView;
	public ImageView viewNumber;
	public ImageView imgBottom;
	public FreeLayout bottomReserveLayout;
	public ImageView btnNavigation;
	public FreeLayout bottomCancelLayout;
	public ImageView btnCancel;
	public FreeTextView txtReservition;

	public ReservationNumberLayout(Context context) {
		super(context);

		this.setFreeLayoutFF();
		this.setPicSize(1080, 1920, TO_WIDTH);
/*
		topView = (ImageView) this.addFreeView(new ImageView(mContext), 1080,
				343);
		topView.setBackgroundResource(R.drawable.bg_reservation_default);

		viewNumber = (ImageView) this.addFreeView(new ImageView(mContext), 0,
				343, 1080, 636);
		viewNumber.setBackgroundResource(R.drawable.reservation_info_number);
*/
		
		topView = (ImageView)this.addFreeView(new ImageView(mContext),
				1080, 343);
		topView.setBackgroundResource(R.drawable.bg_reservation_default);

		viewNumber = (ImageView)this.addFreeView(new ImageView(mContext),
				0, 343, 1080, 636);
		viewNumber.setBackgroundResource(R.drawable.reservation_info_number);

		txtReservition = (FreeTextView)this.addFreeView(new FreeTextView(mContext),
				590, 725, 600, 150);

		imgBottom = (ImageView) this.addFreeView(new ImageView(mContext), 1092,
				372, new int[] { ALIGN_PARENT_BOTTOM });
		imgBottom.setBackgroundResource(R.drawable.img_dr_bottom);

		bottomReserveLayout = (FreeLayout) this.addFreeView(new FreeLayout(
				mContext), 540, 301, new int[] { ALIGN_PARENT_BOTTOM,
				ALIGN_PARENT_LEFT });
		bottomReserveLayout.setPicSize(1080, 1920, TO_WIDTH);

		btnNavigation = (ImageView) bottomReserveLayout.addFreeView(
				new ImageView(mContext), 494, 171,
				new int[] { CENTER_IN_PARENT });
		btnNavigation.setBackgroundResource(R.drawable.btn_navigation);

		bottomCancelLayout = (FreeLayout) this.addFreeView(new FreeLayout(
				mContext), 540, 301, new int[] { ALIGN_PARENT_BOTTOM,
				ALIGN_PARENT_RIGHT });
		bottomCancelLayout.setPicSize(1080, 1920, TO_WIDTH);

		btnCancel = (ImageView) bottomCancelLayout.addFreeView(new ImageView(
				mContext), 488, 169, new int[] { CENTER_IN_PARENT });
		btnCancel.setBackgroundResource(R.drawable.btn_reservation_cancel);

	}
}
