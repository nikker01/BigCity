package lib.locate.algorithm.Math.component;

public class Vector2 {

	 public Point point ;
	 
	 public Vector2 (float x,float y) {
		 this.point = new Point(x,y);
	 }
	 
	 public Vector2 (Point point) {
		 this.point = point;
	 }
	 
	 //Dor poroduct
	 public float dot(Vector2 dotPoint) {
		 float dotX = this.point.x*dotPoint.point.x;
		 float dotY = this.point.y*dotPoint.point.y;
		 
		 return dotX+dotY;
	 }
}
