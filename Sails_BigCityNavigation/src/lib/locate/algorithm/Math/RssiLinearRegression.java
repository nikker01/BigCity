package lib.locate.algorithm.Math;

import java.util.ArrayList;

public class RssiLinearRegression {
	private ArrayList<Float> rssi = new ArrayList<Float>(); //rssi
	private ArrayList<Float> distance = new ArrayList<Float>(); //distance
	private float avgRssi=0,avgDistance=0;
	public float rssi0,alpha ;
	
	public RssiLinearRegression(ArrayList<Float> Y,ArrayList<Float> X) {
		this.rssi = Y;
		this.distance = X;
		calculateAvgRssi();
		calculateAvgDistance();
		alpha = calculateAlpha();
		rssi0 = avgRssi - alpha*avgDistance;
	}	
	
	private void calculateAvgRssi() {
		for(int index=0;index<rssi.size();index++) {
			avgRssi = avgRssi+rssi.get(index);
		}
		this.avgRssi = avgRssi/rssi.size();
	}
	
	private void calculateAvgDistance() {
		for(int index=0;index<distance.size();index++) {
			float p = (float) (-10*(Math.log10(distance.get(index))));
			avgDistance = avgDistance+p;
		}
		this.avgDistance = avgDistance/distance.size();
	}
	
	private float calculateAlpha() {
		float a =0,b=0,n;
		for(int index=0;index<distance.size();index++) {
			float p = (float) (-10*(Math.log10(distance.get(index))));
			a = a +((p-avgDistance)*this.rssi.get(index));
			b = (float) (b + Math.pow((p-avgDistance), 2));
		}
		n = a/b;
		return n;
	}
	
}
