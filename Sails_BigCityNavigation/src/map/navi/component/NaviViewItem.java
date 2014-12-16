package map.navi.component;


import java.io.InputStream;

import com.doubleservice.bigcitynavigation.ApplicationController;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NaviViewItem extends ImageView{
	private String TAG = "NaviItem";
	private Context context;
	private float[] matrixValue; 
	
	public Matrix matrix;
	public Bitmap bitmap;
	public int imageHeightOrigin,imageWidthOrigin;
	public int imageHeight,imageWidth;
	//private BitmapLruCache cache;
	private String key ="";
	
	
	public NaviViewItem(Context context,ViewGroup.LayoutParams params) {
		super(context);
		this.context = context;
		this.matrix = new Matrix();
		matrixValue = new float[9];
		this.setViewParameters(params);		
	}
	
	public void setImage(int id) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        
        BitmapFactory.decodeResource(getResources(),id,opts);
		this.imageHeightOrigin = opts.outHeight;
		this.imageWidthOrigin = opts.outWidth;
		Log.i(TAG, "ImageORI width = "+this.imageWidthOrigin+",Height = "+this.imageHeightOrigin);
		if(ApplicationController.cache.containBitmap(""+id)) {
			this.bitmap = ApplicationController.cache.getBitmap(""+id);
		}
		else { 
			this.bitmap = readBitMap(this.context,id);//BitmapFactory.decodeResource(getResources(),id);
			ApplicationController.cache.putBitmap(""+id, this.bitmap);
		}
		this.imageHeight = this.bitmap.getHeight();
		this.imageWidth = this.bitmap.getWidth();
		
		this.setImageBitmap(bitmap);
	}
	
	/**
	* 以最省記憶體的方式讀取本地資源的圖片
	* @param context
	* @param resId
	* @return
	*/ 
	public static Bitmap readBitMap(Context context, int resId){  
	    BitmapFactory.Options opt = new BitmapFactory.Options();  
	    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
	    opt.inPurgeable = true;  
	    opt.inInputShareable = true; 
	  
	    //獲取資源圖片  
	    InputStream is = context.getResources().openRawResource(resId);  
	    return BitmapFactory.decodeStream(is,null,opt);  
	}
	
	public void setImage(Bitmap bitmap) {
		this.setImageBitmap(bitmap);
		this.bitmap = bitmap;
		this.imageHeight = bitmap.getHeight();
		this.imageWidth = bitmap.getWidth();
		this.imageHeightOrigin = bitmap.getHeight();
		this.imageWidthOrigin = bitmap.getWidth();
		//this.matrix = this.getImageMatrix(); 
	}
	
	public int getImageHeight() {
		if(this.getHeight()>0)
			this.imageHeight = this.getHeight();
		return this.imageHeight;
	}
	
	public int getImageWidth() {
		if(this.getWidth()>0)
			this.imageWidth = this.getWidth();
		return this.imageWidth;
	}
	
	public void setViewParameters(ViewGroup.LayoutParams params) {
		this.setAdjustViewBounds(true);
		this.setScaleType(ImageView.ScaleType.MATRIX);
		this.setLayoutParams(params);
	}

	public void updateImageMatrix() {
		this.setImageMatrix(this.matrix);
		//this.LogMatrix();
	}
	public void updateImageMatrix(Matrix matrix) {
		this.matrix.set(matrix);
		this.updateImageMatrix();
	}
	public void updateImageMatrix(float[] matrixValue) {
		this.matrix.setValues(matrixValue);
		this.updateImageMatrix();
	}
		
	public float[] getMatrixValue() {
		this.matrix.getValues(matrixValue);
		return this.matrixValue;
	}
	
	public void LogMatrix() {
		float [] value = this.getMatrixValue();
		Log.i(TAG, "Item Matrix X = "+value[0]+" "+value[1]+" "+value[2]);
		Log.i(TAG, "Item Matrix Y = "+value[3]+" "+value[4]+" "+value[5]);
	}
	
}
