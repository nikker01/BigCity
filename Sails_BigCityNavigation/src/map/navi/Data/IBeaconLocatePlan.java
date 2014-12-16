package map.navi.Data;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;

public class IBeaconLocatePlan {

	//private ArrayList<IBeaconLocateData> locateBeaconsData = new ArrayList<IBeaconLocateData>(); 
	public String currentArea ="BigCity";
	
	private int majorNumberLowestLimit = 0,majorNumberHeightestLimit = 0;
	private int minorNumberLowestLimit = 1,minorNumberHeightestLimit = 72;
	private HashMap<String,IBeaconLocateData> locateBeaconsData = new  HashMap<String,IBeaconLocateData> ();
	private int totalCountIndex = 0;
	private int totalCount = 1;
	private int totalNothing = 0;
	private int totalSOGO = 0;
	private int totalBigCity = 0;
	public IBeaconLocatePlan() {
		
		for(int index = majorNumberLowestLimit;index<=majorNumberHeightestLimit;index++) {
			for(int index2 = minorNumberLowestLimit;index2<=minorNumberHeightestLimit;index2++) {
				if(index2<40) {
					IBeaconLocateData data = new IBeaconLocateData(index,index2,"BigCity",0);
					locateBeaconsData.put(index+"_"+index2, data);
					//this.locateBeaconsData.add(data);
				}
				else {
					IBeaconLocateData data = new IBeaconLocateData(index,index2,"SOGO",0);
					locateBeaconsData.put(index+"_"+index2, data);
					
					//this.locateBeaconsData.add(data);
				}
				
			}
		}
	}
	
	public void analyzeCurrentArea(ArrayList<String> scanList,int rssi[]) {
		int countBestRssi = 1;
		int countBigCity = 0;
		int countSoGO = 0;
		int index = 0;
		
		
		if(scanList.size()<countBestRssi) {
			countBestRssi = scanList.size();
		}
		ArrayList<IBeaconLocateData> datas = new ArrayList<IBeaconLocateData>();
		
		for(int i=0;i<scanList.size();i++) {
			String[] beacon = scanList.get(i).split("_");
			int scanRssi = rssi[i];
			if(scanRssi==0) {
				scanRssi = Integer.MIN_VALUE;
			}
			IBeaconLocateData  data = new IBeaconLocateData(Integer.parseInt(beacon[0]),Integer.parseInt(beacon[1]),"",scanRssi);
			datas.add(data);
		}
		
		this.iBeaconSortingRssiStrongToWeak(datas);
		
		//for(IBeaconLocateData ibeacon:datas) {
			//Log.i("Beacon Scan", ibeacon.major+"_"+ibeacon.minor+",rssi:"+ibeacon.rssi);
		//}
		
		for(IBeaconLocateData ibeacon:datas) {
			if(index<countBestRssi) {
				IBeaconLocateData data = locateBeaconsData.get(ibeacon.major+"_"+ibeacon.minor);
				//Log.i("Beacon Scan", ibeacon.major+"_"+ibeacon.minor);
				if(data!=null) {
					if(data.area.equals("BigCity") && data.rssi!=Integer.MIN_VALUE) {
						countBigCity++;						
					}
					else if(data.area.equals("SOGO") && data.rssi!=Integer.MIN_VALUE) {
						countSoGO++;						
					}
					//Log.i("Beacon Scan", "countBigCity = "+countBigCity+", countSoGO = "+countSoGO);
				}
				index++;
			}
			else
				break;
		}
		if(totalCountIndex<=totalCount) {
			if(countBigCity>countSoGO && countBigCity>=((countBestRssi/2)+1))
				this.totalBigCity++;
			else if(countBigCity<countSoGO && countSoGO>=((countBestRssi/2)+1))
				this.totalSOGO++;
			else
				this.totalNothing++;
			
			totalCountIndex++; 
		}
		else {
			if(totalBigCity>totalSOGO)
				this.currentArea = "BigCity";
			else if(totalBigCity<totalSOGO)
				this.currentArea = "SOGO";
			else
				this.currentArea ="";
			totalCountIndex = 0;
			Log.i("!!!!!!!!!CUrrent","!!!!!!!"+this.currentArea);
		}
	}
	
	public static void iBeaconSortingRssiStrongToWeak(ArrayList<IBeaconLocateData> sortList) {
		
		IBeaconLocateData temp;
		 if (sortList.size()>1) // check if the number of orders is larger than 1
	        {
	            for (int index=0; index<sortList.size(); index++) // bubble sort outer loop
	            {
	                for (int i=0; i < sortList.size()-index-1; i++) 
	                {
	                    if (sortList.get(i).rssi<(sortList.get(i+1).rssi) )
	                    {
	                        temp = sortList.get(i);
	                        sortList.set(i,sortList.get(i+1) );
	                        sortList.set(i+1, temp);
	                    }
	                }
	            }
	        }
		
	}
	
	
	
}
