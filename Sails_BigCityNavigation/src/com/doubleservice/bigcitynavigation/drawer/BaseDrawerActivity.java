package com.doubleservice.bigcitynavigation.drawer;

import java.util.ArrayList;
import java.util.List;

import com.doubleservice.DataVO.GlobalDataVO;
import com.doubleservice.DataVO.ParkingVO;
import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.FreeHomepage;
import com.doubleservice.bigcitynavigation.MemberHappyGo;
import com.doubleservice.bigcitynavigation.Navigation;
import com.doubleservice.bigcitynavigation.R;
import com.doubleservice.bigcitynavigation.SailsTechActivity;
import com.doubleservice.brand.MainBrandLayout;
import com.doubleservice.dr.DinningRoomList;
import com.doubleservice.dr.Reservation;
import com.doubleservice.findchild.ChildBeaconSetup;
import com.doubleservice.findchild.DetectPage;
import com.doubleservice.findchild.FindChildDescription;
import com.doubleservice.findfriends.FindFriendDes;
import com.doubleservice.findfriends.FriendList;
import com.doubleservice.parking.ParkDescription;
import com.doubleservice.parking.UserParkingInfo;
import com.doubleservice.promotional.MainPromotional;
import com.doubleservice.promotional.PromotionalList;
import com.doubleservice.proxy.PreferenceProxy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BaseDrawerActivity extends Activity {

	private String TAG = "BaseDrawerActivity";

	private DrawerLayout mDrawerLayout = null;
	private ListView mDrawerList;
	private CustomDrawerAdapter adapter;
	private List<DrawerItem> dataList;
	private ActionBarDrawerToggle mDrawerToggle;

	private Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_layout);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		dataList = new ArrayList<DrawerItem>();
		// Add Drawer Item to dataList

		// pos 0
		dataList.add(new DrawerItem(getResources()
				.getString(R.string.menu_home), R.drawable.icon_home));

		// pos 1
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_navigation)));
		// pos 2
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_floor), R.drawable.icon_floor));
		// pos 3
		dataList.add(new DrawerItem(getResources()
				.getString(R.string.menu_food), R.drawable.icon_food));
		// pos 4
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_find_child), R.drawable.icon_find_child));
		// pos 5
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_find_friend), R.drawable.icon_find_friend));
		// pos 6
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_parking), R.drawable.icon_parking));
		// pos 7
		dataList.add(new DrawerItem(getResources()
				.getString(R.string.menu_news)));
		// pos 8
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_find_promotion), R.drawable.icon_find_promotion));
		// pos 9
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_find_brand), R.drawable.icon_find_brand));
		// pos 10
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_member)));
		// pos 11
		dataList.add(new DrawerItem(getResources().getString(
				R.string.menu_happygo), R.drawable.icon_happygo));

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
				dataList);
		mDrawerList.setAdapter(adapter);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		// getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// getActionBar().setCustomView(R.layout.actionbar_layout);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.menu_open, R.string.menu_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				Log.i(TAG, "onDrawerClosed");
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		for (int index = 0; index < menu.size(); index++) {
			MenuItem menuItem = menu.getItem(index);
			if (menuItem != null) {
				// hide the menu items if the drawer is open
				menuItem.setVisible(!drawerOpen);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int position, long id) {
			Log.i("DrawerItemClickListener", "onItemClick =" + position
					+ " last click item = " + GlobalDataVO.CURRENT_DRAWER_ITEM);

			switch (position) {
			case 0: {
				intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseDrawerActivity.this, FreeHomepage.class);
				break;
			}
			case 2: {
				intent = new Intent();
				ApplicationController.navData = null;
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseDrawerActivity.this,
						SailsTechActivity.class);

				break;
			}
			case 3: {
				intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseDrawerActivity.this, DinningRoomList.class);
				break;
			}
			case 4: {
				PreferenceProxy prefProxy = new PreferenceProxy(
						getBaseContext());
				boolean isTracking = prefProxy.isChildTracking();

				intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				if (isTracking) {
					intent.setClass(BaseDrawerActivity.this, DetectPage.class);
				} else {
					intent.setClass(BaseDrawerActivity.this,
							FindChildDescription.class);
				}

				break;
			}
			case 5: {
				// friend list
				PreferenceProxy prefProxy = new PreferenceProxy(
						BaseDrawerActivity.this);

				if (prefProxy.getIsUpdatePhone()) {
					intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(BaseDrawerActivity.this, FriendList.class);
					// GlobalDataVO.CURRENT_DRAWER_ITEM = 5;
				} else {
					intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(BaseDrawerActivity.this,
							FindFriendDes.class);
					// GlobalDataVO.CURRENT_DRAWER_ITEM = 5;
				}
				break;
			}
			case 6: {
				// parking

				ParkingVO vo = new ParkingVO();
				PreferenceProxy prefProxy = new PreferenceProxy(
						getBaseContext());
				vo = prefProxy.getIsParking();

				if (vo.isParking) {
					intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(BaseDrawerActivity.this,
							UserParkingInfo.class);
				} else {
					intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(BaseDrawerActivity.this,
							ParkDescription.class);
				}

				break;
			}
			case 7: {
				break;
			}
			case 8: {
				intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseDrawerActivity.this, MainPromotional.class);
				// intent.setClass(BaseDrawerActivity.this,
				// PromotionalList.class);
				break;
			}
			case 9: {
				intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseDrawerActivity.this, MainBrandLayout.class);
				break;
			}

			case 11: {
				intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(BaseDrawerActivity.this, MemberHappyGo.class);
				break;
			}
			default:
				break;
			}
			// }

			mDrawerList.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerList);

			new Handler().postDelayed(new Runnable() {
				public void run() {
					if (intent != null) {
						startActivity(intent);
					}
				}

			}, 200);

		}

	}

	public void showAlertDialog() {
		// TODO Auto-generated method stub
		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setMessage(getResources().getString(
				R.string.dialog_msg_function_building));

		MyAlertDialog.setPositiveButton(
				getResources().getString(R.string.dialog_btn_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// clearMyReservation();
					}
				});

		MyAlertDialog.setCancelable(false);
		MyAlertDialog.show();
	}

}
