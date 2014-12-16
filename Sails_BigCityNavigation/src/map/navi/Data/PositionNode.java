package map.navi.Data;

public class PositionNode extends NaviNode{
	
	public Object PositionID ;
	public float rssi0 = 0;
	private float alpha = 0;

	public PositionNode(String id,int x,int y,Object PositionID) {
		super(id,x,y);
		this.PositionID = PositionID;
	}
		
	public float calculateDistanceByRssi(float rssi) {
		float distance = (rssi-rssi0)/alpha;
		return distance;
		
	}
}
