package map.navi.Data;

import java.util.ArrayList;

public class IBeaconLocateData {

	public int major;
	public int minor;
	public String area;
	public int rssi;
	
	public IBeaconLocateData(int major,int minor,String area,int rssi) {
		
		this.major = major;
		this.minor = minor;
		this.area = area;
		this.rssi = rssi;
		
	}
	
}
