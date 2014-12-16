package map.navi.Data;

import android.util.Log;

public class PushMessageNode extends NaviNode{

	public String message;
	public String fileName;
	public String extension;
	public float openMessageRssiLimit;
	public boolean hasFile ; 
	
	public PushMessageNode(String id,int x,int y,String message,float openMessageRssi) {
		super(id,x,y);
		this.message = message;
		this.hasFile = false;
		this.openMessageRssiLimit = openMessageRssi;
	}
	
	public PushMessageNode(String id,String message,float openMessageRssi) {
		this(id,0,0,message,openMessageRssi);
	}
	
	public PushMessageNode(String id,int x,int y,String message,float openMessageRssi,String file) {
		super(id,x,y);
		this.message = message;
		this.hasFile = true;
		this.openMessageRssiLimit = openMessageRssi;
		
		int i = file.lastIndexOf('.');
		if (i > 0) {
		    this.extension = file.substring(i);
		    this.fileName = file.substring(0, i);
		    //Log.i("MessageNode", "file = "+fileName+", extension = "+this.extension);
		}
		else
			this.fileName = file;
		//Log.i("MessageNode", "file = "+fileName+", extension = "+this.extension);
	}
	
	public PushMessageNode(String id,String message,float openMessageRssi,String file) {
		this(id,0,0,message,openMessageRssi,file);
	}
}
