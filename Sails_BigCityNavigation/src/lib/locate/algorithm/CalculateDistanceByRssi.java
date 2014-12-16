package lib.locate.algorithm;

public class CalculateDistanceByRssi {
	static final float e = 2.7182818f;
	public static float calculateDistanceMethod1 (float alpha,float rssi0,float recieveRssi) {
		float distance = (recieveRssi-rssi0)/alpha;
		//distance = (alpha*recieveRssi)/(1-rssi0*recieveRssi);
		//distance = (float) Math.pow(e, ((recieveRssi-rssi0)/alpha));
		return distance;
	}
	
	public static float calculateDistanceMethod2 (float alpha,float rssi0,float recieveRssi) {
		float exponential = (rssi0-recieveRssi)/(10*alpha);
		float distance = (float) Math.pow(10, exponential);
		return distance;
	}

	
	
}
