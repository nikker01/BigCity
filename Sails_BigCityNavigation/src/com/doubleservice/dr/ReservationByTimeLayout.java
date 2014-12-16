package com.doubleservice.dr;

import com.doubleservice.bigcitynavigation.R;
import com.james.views.FreeLayout;
import com.james.views.FreeTextView;

import android.content.Context;
import android.widget.ImageView;

public class ReservationByTimeLayout extends FreeLayout {

	public ImageView topViewBackground;
	public FreeTextView topViewContent;
	public FreeTextView txtReservationTime;
	public FreeTextView timePicker;
	public ImageView imgViewLine;
	public FreeTextView txtReservationPeople;
	public FreeTextView peoplePicker;
	public ImageView imgViewLine2;
	public FreeLayout bottmCancelLayout;
	public ImageView btnCancel;
	public FreeLayout bottomOkLayout;
	public ImageView btnComplete;

	public ReservationByTimeLayout(Context context) {
		super(context);

		this.setFreeLayoutFF();
		this.setPicSize(1080, 1920, TO_WIDTH);
		this.setBackgroundColor(0xffffffff);

		topViewBackground = (ImageView) this.addFreeView(
				new ImageView(mContext), 1092, 180);
		topViewBackground.setBackgroundResource(R.drawable.bg_name_field);

		topViewContent = (FreeTextView) this.addFreeView(new FreeTextView(
				mContext), 2, 49, 1080, 130);

		txtReservationTime = (FreeTextView) this.addFreeView(new FreeTextView(
				mContext), 52, 190, 540, 100);

		timePicker = (FreeTextView) this.addFreeView(
				new FreeTextView(mContext), 200, 100,
				new int[] { ALIGN_PARENT_RIGHT }, txtReservationTime,
				new int[] { ALIGN_BOTTOM });

		imgViewLine = (ImageView) this.addFreeView(new ImageView(mContext), 15,
				287, 1022, 2);
		imgViewLine.setBackgroundResource(R.drawable.view_line1);

		txtReservationPeople = (FreeTextView) this.addFreeView(
				new FreeTextView(mContext), 51, 298, 540, 100);

		peoplePicker = (FreeTextView) this.addFreeView(new FreeTextView(
				mContext), 200, 100, new int[] { ALIGN_PARENT_RIGHT },
				txtReservationPeople, new int[] { ALIGN_BOTTOM });

		imgViewLine2 = (ImageView) this.addFreeView(new ImageView(mContext),
				12, 414, 1022, 2);
		imgViewLine2.setBackgroundResource(R.drawable.view_line1);

		bottmCancelLayout = (FreeLayout) this.addFreeView(new FreeLayout(
				mContext), 540, 320, new int[] { ALIGN_PARENT_BOTTOM,
				ALIGN_PARENT_LEFT });
		bottmCancelLayout.setPicSize(1080, 1920, TO_WIDTH);

		btnCancel = (ImageView) bottmCancelLayout.addFreeView(new ImageView(
				mContext), 462, 160, new int[] { CENTER_IN_PARENT });
		btnCancel.setBackgroundResource(R.drawable.btn_cancel);

		bottomOkLayout = (FreeLayout) this.addFreeView(
				new FreeLayout(mContext), 540, 320, new int[] {
						ALIGN_PARENT_BOTTOM, ALIGN_PARENT_RIGHT });
		bottomOkLayout.setPicSize(1080, 1920, TO_WIDTH);

		btnComplete = (ImageView) bottomOkLayout.addFreeView(new ImageView(
				mContext), 462, 160, new int[] { CENTER_IN_PARENT });
		btnComplete.setBackgroundResource(R.drawable.btn_complete);

	}
}
