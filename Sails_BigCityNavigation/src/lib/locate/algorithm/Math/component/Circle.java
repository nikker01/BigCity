package lib.locate.algorithm.Math.component;

public class Circle {
	
	public float pointX ,pointY,R;
	
	public Circle (float pointX,float pointY,float R) {
		this.pointX = pointX;
		this.pointY = pointY;
		this.R = R;
	}
	
	public Circle (String pointX,String pointY,float R) {
		this.pointX = Float.parseFloat(pointX);
		this.pointY = Float.parseFloat(pointY);
		this.R = R;
	}
	
	public Circle (String pointX,String pointY,String R) {
		this.pointX = Float.parseFloat(pointX);
		this.pointY = Float.parseFloat(pointY);
		this.R = Float.parseFloat(R);
	}
	
	public Point getCenterPoint() {
		return new Point(pointX,pointY);	
		
	}
	
}
