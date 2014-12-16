package lib.locate.KNN;

import java.util.ArrayList;

public class RssiMeanFilter {

	private int avg;
	public RssiMeanFilter(ArrayList<Integer> data){
		
		int total = 0;
		
		if(data.size() > 0) {
			for(int index = 0; index < data.size(); index++) {
				total = total + data.get(index);
			}
			
			avg = total/data.size();
		}
	}
	
	public int getMean() {
		return avg;
	}
	
}
