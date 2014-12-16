package map.navi.component;


import java.util.ArrayList;

import com.doubleservice.bigcitynavigation.ApplicationController;
import com.doubleservice.bigcitynavigation.R;



//import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

public class drawLineHelper extends View {
	private Paint paint;
	private Canvas canv;
	private Bitmap mBitmap;
	private int width,height;

	public drawLineHelper(Context context, int width, int height,int mapID) {
		super(context);
		paint = new Paint();
		paint.setColor(Color.parseColor("#d00000"));//Color.RED);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(ApplicationController.convertDpToPixel(3));
		paint.setStyle(Style.STROKE);
		if(ApplicationController.cache.containBitmap("draw_line_"+mapID)) {
			mBitmap = ApplicationController.cache.getBitmap("draw_line_"+mapID);
		}
		else { 
			mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
			ApplicationController.cache.putBitmap("draw_line_"+mapID, this.mBitmap);
		}
		
		canv = new Canvas(mBitmap);
		this.width = width;
		this.height = height;
	}
	
	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0, 0, null);
		// super.onDraw(canvas);
	}

	public void clean() {
		this.mBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
	}
	
	public Bitmap drawLine(int startNodeX,int startNodeY,int endNodeX,int endNodeY) {
		canv.drawLine(startNodeX, startNodeY, endNodeX, endNodeY, paint);
		return mBitmap;
	}
	public Bitmap drawPath(ArrayList<Integer> pathX,ArrayList<Integer>pathY){
		Path path = new Path();
		path.moveTo(pathX.get(0), pathY.get(0));
		for(int i=1;i<pathX.size();i++){
			path.lineTo(pathX.get(i), pathY.get(i));
		}
		canv.drawPath(path, paint);
		return mBitmap;
	}
	public Bitmap drawTriangle() {
		Path path = new Path();
		path.moveTo(300, 600);
		path.lineTo(600, 200);
		path.lineTo(900, 600);
		path.lineTo(300, 600);
		canv.drawPath(path, paint);
		return mBitmap;
	}

	public Bitmap drawRect() {
		canv.drawRect(new Rect(150, 150, 500, 500), paint);
		return mBitmap;
	}
	public void clear(){
		mBitmap.recycle();
		System.gc();
	}

}