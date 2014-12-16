package com.doubleservice.promotional;

import java.util.ArrayList;
import java.util.List;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.doubleservice.DataVO.RequestCodeVO;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.drawer.BaseDrawerActivity;
import com.doubleservice.brand.BrandList;
import com.doubleservice.brand.MainBrandLayout;
import com.doubleservice.brand.MainBrandLayout.MyOnClickListener;
import com.doubleservice.brand.MainBrandLayout.MyOnPageChangeListener;
import com.doubleservice.brand.MainBrandLayout.MyPagerAdapter;

public class MainPromotional extends BaseDrawerActivity{

	private Context context = null;
	private ViewPager pager = null;
	private LocalActivityManager manager = null;

	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;

	private TextView t1, t2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_frame);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View activityView = layoutInflater.inflate(
				R.layout.main_promo_tag_layout, null, false);
		frameLayout.addView(activityView);

		context = this;
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		//initImageView();
		initTextView();
		initPagerViewer();
		
		getActionBar().setTitle(getResources().getString(R.string.menu_find_promotion));
	}
	
	private void initPagerViewer() {
		// TODO Auto-generated method stub
		pager = (ViewPager) findViewById(R.id.viewpage);

		final ArrayList<View> list = new ArrayList<View>();
		Intent intent[] = new Intent[3];
		for (int i = 0; i < 2; i++) {
			intent[i] = new Intent(context, PromotionalList.class);
			intent[i].putExtra(RequestCodeVO.CMD_BRAND_FLOOR, i);
			list.add(getView("View_" + i, intent[i]));
		}

		pager.setAdapter(new MyPagerAdapter(list));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	public class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				
				t1.setTextColor(0xffa60102);
				t2.setTextColor(0xff000000);
				
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				
				t2.setTextColor(0xffa60102);
				t1.setTextColor(0xff000000);
				
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			//cursorView.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}
	
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);
		}
	};
	
	private void initTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		
		t2.setTextColor(0xff000000);
		t1.setTextColor(0xffa60102);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
	}
	
}
