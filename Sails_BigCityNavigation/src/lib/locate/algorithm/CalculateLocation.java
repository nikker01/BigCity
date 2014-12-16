package lib.locate.algorithm;

import android.util.Log;
import lib.locate.algorithm.Math.PointInTriangle;
import lib.locate.algorithm.Math.PointLine;
import lib.locate.algorithm.Math.calcaulateTwoCircleIntersectionPoints;
import lib.locate.algorithm.Math.component.Circle;
import lib.locate.algorithm.Math.component.Point;
public class CalculateLocation {
	
	public static float threeLinesPointX=0 ;
	public static float threeLinesPointY = 0;
	public static float threeHeaveyPointX=0;
	public static float threeHeaveyPointY=0;
	public static float innerCentrePointX = 0;
	public static float innerCentrePointY = 0;
	public static Point locationPoint1=null,locationPoint2=null,locationPoint3=null;
	public static Point locatePoint;
	//private static float nearlyPoinX=0;
	//private static float nearlyPoinY=0;
	
	public static void threeLinesLocate (String x1,String y1,String x2,String y2,String x3,String y3,float r1,float r2,float r3) {
		float pointx1 = Float.parseFloat(x1);
		float pointy1 = Float.parseFloat(y1);
		//float pointr1 = Float.parseFloat(r1);
		float pointx2 = Float.parseFloat(x2);
		float pointy2 = Float.parseFloat(y2);
		//float pointr2 = Float.parseFloat(r2);	
		float pointx3 = Float.parseFloat(x3);
		float pointy3 = Float.parseFloat(y3);
		//float pointr3 = Float.parseFloat(r3);	
		threeLinesLocate(pointx1, pointy1, pointx2, pointy2, pointx3, pointy3, r1,r2,r3);
	}
	
	public static  void threeLinesLocate(float x1,float y1,float x2,float y2,float x3,float y3,float d1,float d2,float d3) {
		threeLinesPointX = (float) (((y2-y3)*(Math.pow(d1, 2)-Math.pow(d2, 2)-(Math.pow(x1, 2)-Math.pow(x2, 2))-(Math.pow(y1, 2)-Math.pow(y2, 2)))-
									  (y1-y2)*(Math.pow(d2, 2)-Math.pow(d3, 2)-(Math.pow(x2, 2)-Math.pow(x3, 2))-(Math.pow(y2, 2)-Math.pow(y3, 2))))/
									  ((-2*(x1-x2)*(y2-y3))+(2*(x2-x3)*(y1-y2))));
		threeLinesPointY = (float) (((x2-x3)*(Math.pow(d1, 2)-Math.pow(d2, 2)-(Math.pow(x1, 2)-Math.pow(x2, 2))-(Math.pow(y1, 2)-Math.pow(y2, 2)))-
									  (x1-x2)*(Math.pow(d2, 2)-Math.pow(d3, 2)-(Math.pow(x2, 2)-Math.pow(x3, 2))-(Math.pow(y2, 2)-Math.pow(y3, 2))))/
									  ((-2*(y1-y2)*(x2-x3))+(2*(y2-y3)*(x1-x2))));
	}
	
	public static void threeHeavyLocate (String x1,String y1,String x2,String y2,String x3,String y3,float r1,float r2,float r3) {
		if(r1<0)
			r1 = 1;
		if(r2<0)
			r2 = 1;
		if(r3<0)
			r3 = 1;
		Circle circle1 = new Circle(x1,y1,r1);
		Circle circle2 = new Circle(x2,y2,r2);
		Circle circle3 = new Circle(x3,y3,r3);
		threeHeavyLocate(circle1,circle2,circle3);//pointx1, pointy1, pointx2, pointy2, pointx3, pointy3, r1,r2,r3);
	}
	
	public static void threeInCentreLocate (String x1,String y1,String x2,String y2,String x3,String y3,float r1,float r2,float r3) {
		if(r1<0)
			r1 = 1;
		if(r2<0)
			r2 = 1;
		if(r3<0)
			r3 = 1;
		Circle circle1 = new Circle(x1,y1,r1);
		Circle circle2 = new Circle(x2,y2,r2);
		Circle circle3 = new Circle(x3,y3,r3);
		threeInCentreLocate(circle1,circle2,circle3);
	}
	
	public static void threeInCentreLocate (Circle circle1,Circle circle2,Circle circle3) {
		
		Point locatePoint1 = getNearCenterOfCirclePointFromTwoCircleIntersectionPoints(circle1.getCenterPoint(),circle2,circle3);//new Point();
		Point locatePoint2 = getNearCenterOfCirclePointFromTwoCircleIntersectionPoints(circle2.getCenterPoint(),circle1,circle3);
		Point locatePoint3 = getNearCenterOfCirclePointFromTwoCircleIntersectionPoints(circle3.getCenterPoint(),circle1,circle2);
		//distanceBetweenPoint1Point2
		float  distance1_2 = calculateDistance(locatePoint1.x,locatePoint1.y,locatePoint2.x,locatePoint2.y);
		//distanceBetweenPoint1Point3
		float  distance1_3= calculateDistance(locatePoint1.x,locatePoint1.y,locatePoint3.x,locatePoint3.y);
		//distanceBetweenPoint2Point3
		float  distance2_3 = calculateDistance(locatePoint2.x,locatePoint2.y,locatePoint3.x,locatePoint3.y);
		
		innerCentrePointX = ((locatePoint1.x*distance2_3)+(locatePoint2.x*distance1_3)+(locatePoint3.x*distance1_2))/(distance1_2+distance1_3+distance2_3);
		innerCentrePointY = ((locatePoint1.y*distance2_3)+(locatePoint2.y*distance1_3)+(locatePoint3.y*distance1_2))/(distance1_2+distance1_3+distance2_3);
	}

	public static void threeHeavyLocate(Circle circle1,Circle circle2,Circle circle3) {//float x1,float y1,float x2,float y2,float x3,float y3,float d1,float d2,float d3) {
		
		//float point1X = 0,point1Y=0,point2X=0,point2Y=0,point3X=0,point3Y=0;
		Point locatePoint1 = getNearCenterOfCirclePointFromTwoCircleIntersectionPoints(circle1.getCenterPoint(),circle2,circle3);//new Point();
		Point locatePoint2 = getNearCenterOfCirclePointFromTwoCircleIntersectionPoints(circle2.getCenterPoint(),circle1,circle3);
		Point locatePoint3 = getNearCenterOfCirclePointFromTwoCircleIntersectionPoints(circle3.getCenterPoint(),circle1,circle2);
		
		if(!PointInTriangle.isInTriangle(circle1.getCenterPoint(), circle2.getCenterPoint(), circle3.getCenterPoint(), locatePoint1)) {
			locatePoint1 = PointLine.findMinimumDistanceProjectPointOnLines(circle1, circle2, circle3, locatePoint1);
		}
		if(!PointInTriangle.isInTriangle(circle1.getCenterPoint(), circle2.getCenterPoint(), circle3.getCenterPoint(), locatePoint2)) {
			locatePoint2 = PointLine.findMinimumDistanceProjectPointOnLines(circle1, circle2, circle3, locatePoint2);
		}
		if(!PointInTriangle.isInTriangle(circle1.getCenterPoint(), circle2.getCenterPoint(), circle3.getCenterPoint(), locatePoint3)) {
			locatePoint3 = PointLine.findMinimumDistanceProjectPointOnLines(circle1, circle2, circle3, locatePoint3);
		}
		
		locationPoint1 = locatePoint1;
		locationPoint2 = locatePoint2;
		locationPoint3 = locatePoint3;
		threeHeaveyPointX = (locatePoint1.x+locatePoint2.x+locatePoint3.x)/3;
		threeHeaveyPointY = (locatePoint1.y+locatePoint2.y+locatePoint3.y)/3;
		locatePoint = new Point(threeHeaveyPointX,threeHeaveyPointY);
	}

	private static Point getNearCenterOfCirclePointFromTwoCircleIntersectionPoints(Point centerOfCircle,Circle circle1,Circle circle2) {
		calcaulateTwoCircleIntersectionPoints method = new calcaulateTwoCircleIntersectionPoints(circle1,circle2);//x1,y1,d1,x2,y2,d2);
		return findNearlyPoint(method,centerOfCircle);
	}
	
	private static Point findNearlyPoint(calcaulateTwoCircleIntersectionPoints method,Point centerPoint) {
		if(!method.noPoint && method.hasTwoPoints) {
			Point point1 = method.point1;
			Point point2 = method.point2;
			float dis1 = calculateDistance(point1.x,point1.y,centerPoint.x,centerPoint.y);
			float dis2 = calculateDistance(point2.x,point2.y,centerPoint.x,centerPoint.y);
			if(dis1>=dis2) {
				return point2;
			}
			else {
				return point1;
			}				
		}
		else if(!method.noPoint && method.hasOnePoint) {
			return method.point1;
		}
		else
			return centerPoint ;
	}
	
	
	private static float calculateDistance(float sourceX,float sourceY,float destinationX,float destinationY) {
		float distance =(float) Math.sqrt((Math.pow((destinationX-sourceX), 2)+Math.pow((destinationY-sourceY), 2)));
		Log.i("Distance", "Distance = "+distance);
		return distance;
	}
}
