package com.doubleservice.brand;

import java.util.ArrayList;

import com.doubleservice.DataVO.BrandDataVO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BrandDBProxy {

	private Context mContext;
	private BrandDBHelper dbHelper;
	private SQLiteDatabase db;
	private String TAG = "BrandDBProxy";

	private int mCount;
	private String[] brandAreas;
	private String[] brandContents;
	private String[] brandIDs;
	private String[] brandImgs;
	private String[] brandTitles;
	private String[] posX;
	private String[] posY;
	private String[] shopList;

	private String[] column = { "_brand_id", "_brand_title", "_brand_img",
			"_brand_floor", "_brand_content", "_brand_posx", "_brand_posy",
			"_brand_area", "_brand_shop_list" };

	public BrandDBProxy(Context context) {
		this.mContext = context;
		initDB();
	}

	public void initDB() {
		// TODO Auto-generated method stub
		dbHelper = new BrandDBHelper(mContext);
		db = dbHelper.getWritableDatabase();
	}

	public void closeDB() {
		dbHelper.close();
		db.close();
	}

	public void onCreateBrandInitData(BrandDataVO vo) {

		Log.i(TAG, "onCreateInitData BEGIN");

		ContentValues values = new ContentValues();
		values.put(BrandDataVO.BrandArea, vo.bArea);
		values.put(BrandDataVO.BrandContent, vo.bContent);
		values.put(BrandDataVO.BrandID, vo.bID);
		values.put(BrandDataVO.BrandImg, vo.bImg);
		values.put(BrandDataVO.BrandPosX, vo.bPosX);
		values.put(BrandDataVO.BrandPosY, vo.bPosY);
		values.put(BrandDataVO.BrandTitle, vo.bTitle);
		db.insert(BrandDataVO.TABLE_NAME, null, values);
	}

	public void onCreatePromotionalInitData(BrandDataVO vo) {
		Log.i(TAG, "onCreatePromotionalInitData BEGIN");

		ContentValues values = new ContentValues();
		values.put(BrandDataVO.BrandArea, vo.bArea);
		values.put(BrandDataVO.BrandContent, vo.bContent);
		values.put(BrandDataVO.BrandID, vo.bID);
		values.put(BrandDataVO.BrandImg, vo.bImg);
		values.put(BrandDataVO.BrandPosX, vo.bPosX);
		values.put(BrandDataVO.BrandPosY, vo.bPosY);
		values.put(BrandDataVO.BrandTitle, vo.bTitle);
		values.put(BrandDataVO.BrandShopList, vo.bShopList);
		db.insert(BrandDataVO.TABLE_NAME_b, null, values);
	}

	public BrandDataVO findPromotionalDataById(String id) {
		Log.i(TAG, "findPromotionalDataById BEGIN");

		BrandDataVO vo = new BrandDataVO();
		try {

			Cursor c = null;
			c = db.query(BrandDataVO.TABLE_NAME_b, column, "_brand_id = " + id,
					null, null, null, null);

			if (c.getCount() >= 1) {
				c.moveToPosition(0);
				for (int i = 0; i < c.getColumnCount(); i++) {
					String strData = c.getString(i);
					// Log.i("findDataById", "str"+i+" = "+strData );
					switch (i) {
					case 1:
						vo.bTitle = strData;
						break;
					case 2:
						vo.bImg = strData;
						break;
					case 4:
						vo.bContent = strData;
						break;
					case 7:
						vo.bArea = strData;
						break;
					case 8:
						vo.bShopList = strData;
						break;
					}
				}

				return vo;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public BrandDataVO findDataById(String id) {
		Log.i(TAG, "findDataById");

		BrandDataVO vo = new BrandDataVO();
		try {
			Cursor c = null;
			c = db.query(BrandDataVO.TABLE_NAME, column, "_brand_id = " + id,
					null, null, null, null);

			if (c.getCount() >= 1) {
				c.moveToPosition(0);
				for (int i = 0; i < c.getColumnCount(); i++) {
					String strData = c.getString(i);
					// Log.i("findDataById", "str"+i+" = "+strData );
					switch (i) {
					case 1:
						vo.bTitle = strData;
						break;
					case 2:
						vo.bImg = strData;
						break;
					case 4:
						vo.bContent = strData;
						break;
					case 7:
						vo.bArea = strData;
						break;
					case 8:
						vo.bShopList = strData;
						break;
					}
				}

				return vo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<BrandDataVO> queryDataByFloor(int floor) {
		ArrayList<BrandDataVO> aryBrandVO = new ArrayList<BrandDataVO>();
		try {
			Cursor c = null;
			c = db.query(BrandDataVO.TABLE_NAME_b, column, "_brand_area="
					+ Integer.toString(floor), null, null, null, null);

			mCount = c.getCount();

			if (c.getCount() >= 1) {
				for (int cIndex = 0; cIndex < c.getCount(); cIndex++) {
					c.moveToPosition(cIndex);
					BrandDataVO vo = new BrandDataVO();
					for (int i = 0; i < c.getColumnCount(); i++) {
						String strData = c.getString(i);
						switch (i) {
						case 0:
							vo.bID = strData;
							break;
						case 1:
							vo.bTitle = strData;
							break;
						case 2:
							vo.bImg = strData;
							break;
						case 4:
							vo.bContent = strData;
							break;
						case 5:
							vo.bArea = strData;
							break;
						case 8:
							vo.bShopList = strData;
							break;
						}

					}
					aryBrandVO.add(vo);
				}

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return aryBrandVO;
	}

	public void queryPromotionalData(int floor) {
		Log.i(TAG, "queryBrandData BEGIN");

		ArrayList<BrandDataVO> aryBrandVO = new ArrayList<BrandDataVO>();

		try {
			Cursor c = null;
			c = db.query(BrandDataVO.TABLE_NAME_b, column, "_brand_area="
					+Integer.toString(floor), null, null, null, null);

			mCount = c.getCount();

			brandAreas = new String[mCount];
			brandContents = new String[mCount];
			brandIDs = new String[mCount];
			brandImgs = new String[mCount];
			brandTitles = new String[mCount];
			posX = new String[mCount];
			posY = new String[mCount];
			shopList = new String[mCount];

			if (c.getCount() >= 1) {
				for (int cIndex = 0; cIndex < c.getCount(); cIndex++) {
					c.moveToPosition(cIndex);
					for (int i = 0; i < c.getColumnCount(); i++) {
						BrandDataVO vo = new BrandDataVO();

						String strData = c.getString(i);
						switch (i) {
						case 0:
							brandIDs[cIndex] = strData;
							break;
						case 1:
							brandTitles[cIndex] = strData;
							break;
						case 2:
							brandImgs[cIndex] = strData;
							break;
						case 4:
							brandContents[cIndex] = strData;
							break;
						case 5:
							brandAreas[cIndex] = strData;
							break;
						case 8:
							shopList[cIndex] = strData;
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void queryBrandData(int floor) {
		Log.i(TAG, "queryBrandData BEGIN");

		try {

			Cursor c = null;
			c = db.query(BrandDataVO.TABLE_NAME, column, "_brand_area="
					+Integer.toString(floor), null, null, null, null);

			mCount = c.getCount();
			brandAreas = new String[mCount];
			brandContents = new String[mCount];
			brandIDs = new String[mCount];
			brandImgs = new String[mCount];
			brandTitles = new String[mCount];
			posX = new String[mCount];
			posY = new String[mCount];

			if (c.getCount() >= 1) {
				for (int cIndex = 0; cIndex < c.getCount(); cIndex++) {
					c.moveToPosition(cIndex);
					for (int i = 0; i < c.getColumnCount(); i++) {
						String strData = c.getString(i);
						switch (i) {
						case 0:
							brandIDs[cIndex] = strData;
							break;
						case 1:
							brandTitles[cIndex] = strData;
							break;
						case 2:
							brandImgs[cIndex] = strData;
							break;
						case 4:
							brandContents[cIndex] = strData;
							break;
						case 5:
							brandAreas[cIndex] = strData;
							break;

						}
					}
				}
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getBrandCount() {
		return mCount;
	}

	public String[] getShopList() {
		return shopList;
	}

	public String[] getBrandAreas() {
		return brandAreas;
	}

	public String[] getBrandContent() {
		return brandContents;
	}

	public String[] getBrandID() {
		return brandIDs;
	}

	public String[] getBrandImg() {
		return brandImgs;
	}

	public String[] getBrandTitles() {
		return brandTitles;
	}

	public String[] getBrandPosX() {
		return posX;
	}

	public String[] getBrandPosY() {
		return posY;
	}

}
