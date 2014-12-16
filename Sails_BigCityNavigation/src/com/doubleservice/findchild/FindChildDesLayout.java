package com.doubleservice.findchild;

import com.doubleservice.bigcitynavigation.R;
import com.james.views.FreeLayout;
import com.james.views.FreeTextView;

import android.content.Context;
import android.widget.ImageView;

public class FindChildDesLayout extends FreeLayout {

	public ImageView bottomView;
	public ImageView icon;
	public FreeTextView txtMethod;
	public ImageView imgViewLine;
	public FreeTextView txtDes;
	public ImageView bynStart;
	Context mContext;

	public FindChildDesLayout(Context context) {
		super(context);
		this.mContext = context;

		this.setFreeLayoutFF();
		this.setPicSize(1080, 1920, TO_WIDTH);
		this.setBackgroundColor(0xffffffff);

		bottomView = (ImageView) this.addFreeView(new ImageView(mContext),
				1092, 367, new int[] { ALIGN_PARENT_BOTTOM });
		bottomView.setBackgroundResource(R.drawable.bg_friend_des_bottom);

		icon = (ImageView) this.addFreeView(new ImageView(mContext), 262, 180,
				174, 144);
		icon.setBackgroundResource(R.drawable.icon_child_small);

		txtMethod = (FreeTextView) this.addFreeView(new FreeTextView(mContext),
				481, 180, 600, 200);

		imgViewLine = (ImageView) this.addFreeView(new ImageView(mContext), 36,
				366, 1022, 2);
		imgViewLine.setBackgroundResource(R.drawable.view_line);

		txtDes = (FreeTextView) this.addFreeView(new FreeTextView(mContext), 135, 480,
				800, 600);

		bynStart = (ImageView) this.addFreeView(new ImageView(mContext), 289,
				900, 491, 170);
		bynStart.setBackgroundResource(R.drawable.btn_start_to_use);
	}
}
