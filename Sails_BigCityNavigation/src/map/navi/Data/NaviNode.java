package map.navi.Data;

import com.doubleservice.bigcitynavigation.graph.Node;

import android.os.Parcel;
import android.os.Parcelable;


public class NaviNode extends Node implements Parcelable{
	
	public float x = 0, y = 0;
	
	public NaviNode (String id) {
		super(id);
	}

	public NaviNode (String id,float x,float y) {
	
		super(id);
		this.x = x;
		this.y = y;
	}
	
	public void setCoordinate(float x,float y) {
		this.x = x;
		this.y = y;
	}
	

	
	 public static final Parcelable.Creator<NaviNode> CREATOR = new Parcelable.Creator<NaviNode>() {
		 public NaviNode createFromParcel(Parcel in) {
			 
			 NaviNode node = new NaviNode(in.readString(),in.readInt(),in.readInt());
			
			 return node;
		 }
		 public NaviNode[] newArray(int size) {
			 return new NaviNode[size];
		 }
	 };
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeFloat(x);
		dest.writeFloat(y);
		//dest.writeInt(x);
		//dest.writeInt(y);

	}
}
