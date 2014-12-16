package com.doubleservice.promotional;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.doubleservice.bigcitynavigation.R;
import com.james.views.FreeLayout;

public class PromotionalLayout extends FreeLayout {

	public ImageView bottomView;
	public FreeLayout bottomLayout;
	public ImageView btnNav;
	public FreeLayout topLayout;
	public ImageView bgPic;
	public ImageView img;
	public ImageView img_location_small;
	public TextView brandTitle;
	public TextView brandArea;
	public ImageView line;
	public TextView brandContent;
	
	public PromotionalLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setFreeLayoutFF();
		this.setPicSize(720, 1280, TO_WIDTH);
		this.setBackgroundColor(0xffffffff);

		

		topLayout = (FreeLayout) this.addFreeView(new FreeLayout(mContext), 0,
				22, 720, 450);
		topLayout.setPicSize(720, 1280, TO_WIDTH);

		bgPic = (ImageView) topLayout.addFreeView(new ImageView(mContext), 58,
				1, 601, 447);
		bgPic.setBackgroundResource(R.drawable.bg_pic);

		img = (ImageView) topLayout.addFreeView(new ImageView(mContext), 72,
				18, 570, 367);
		img.setImageResource(R.drawable.img_defaut_big);

		// img.setBackgroundResource(R.drawable.img_defaut_big);

		img_location_small = (ImageView) this.addFreeView(new ImageView(
				mContext), 53, 497, 25, 25);
		img_location_small
				.setBackgroundResource(R.drawable.icon_location_small);

		brandTitle = (TextView) this.addFreeView(new TextView(mContext), 53,
				450, 900, 50);

		brandArea = (TextView) this.addFreeView(new TextView(mContext), 87,
				490, 500, 40);

		line = (ImageView) this.addFreeView(new ImageView(mContext), 0, 540,
				720, 1);
		line.setBackgroundResource(R.drawable.view_line1);

		brandContent = (TextView) this.addFreeView(new TextView(mContext), 50,
				553, 600, 1000);
		
		bottomLayout = (FreeLayout) this.addFreeView(new FreeLayout(mContext),
				720, 155, new int[] { ALIGN_PARENT_BOTTOM });
		bottomLayout.setPicSize(720, 1280, TO_WIDTH);

		bottomView = (ImageView) bottomLayout.addFreeView(new ImageView(
				mContext), 720, 157, new int[] { ALIGN_PARENT_BOTTOM });
		bottomView.setBackgroundResource(R.drawable.bg_btm);

		btnNav = (ImageView) bottomLayout.addFreeView(new ImageView(mContext),
				325, 113, new int[] { CENTER_IN_PARENT });
		btnNav.setBackgroundResource(R.drawable.btn_navigation);
	}
	
}
