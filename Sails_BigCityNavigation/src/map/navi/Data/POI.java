package map.navi.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class POI extends NaviNode {
	
	public String POIName = "";
	
	public POI(String id) {
		super(id);
	}

	public POI(String id,float x,float y) {
		super(id,x,y);
	}
	public POI(String id,float x,float y,String POIName) {
		this(id,x,y);
		this.POIName = POIName;
	}
	

	 public static final Parcelable.Creator<POI> CREATOR = new Parcelable.Creator<POI>() {
		 public POI createFromParcel(Parcel in) {
			 
			 POI poi = new POI(in.readString(),in.readInt(),in.readInt(),in.readString());
			
			 return poi;
		 }
		 public POI[] newArray(int size) {
			 return new POI[size];
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
		dest.writeString(this.getID());
		dest.writeFloat(x);
		dest.writeFloat(y);

		dest.writeString(this.POIName);
	}
	
}
