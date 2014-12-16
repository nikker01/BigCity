package map.navi.Data;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

public class BitmapLruCache extends LruCache{
    private static final int DEFAULT_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;

    public BitmapLruCache() {
        this(DEFAULT_CACHE_SIZE);
    }

    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    public Bitmap getBitmap(String url) {
        return (Bitmap) get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    public boolean containBitmap(String key) {
    	Log.i("BitmapLruCache", "Cache Size "+this.size());
    	Bitmap map = getBitmap(key);
    	//Log.i("BitmapLruCache", " bitMap "+map);
    	if(map==null)
    		return false;
    	else
    		return true;
    }
    
    protected int sizeOf(String key, Bitmap value) {
        return value == null ? 0 : value.getRowBytes() * value.getHeight() / 1024;
    }
    
    public void log() {
    	Log.i("BitmapLruCache", "Cache Size "+this.size());
    	
    }
}