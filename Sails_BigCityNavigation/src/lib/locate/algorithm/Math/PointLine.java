package lib.locate.algorithm.Math;

import java.util.ArrayList;
import java.util.HashMap;

import lib.locate.algorithm.Math.component.Circle;
import lib.locate.algorithm.Math.component.LinearLine;
import lib.locate.algorithm.Math.component.Point;

public class PointLine {

		public static Point findMinimumDistanceProjectPointOnLines(Circle circle1,Circle circle2,Circle circle3,Point point) {
			
			Point point1 = circle1.getCenterPoint();
			Point point2 = circle2.getCenterPoint();
			Point point3 = circle3.getCenterPoint();
			
			LinearLine line12 = new LinearLine(circle1.getCenterPoint(), circle2.getCenterPoint());
			LinearLine line13 = new LinearLine(circle1.getCenterPoint(), circle3.getCenterPoint());
			LinearLine line23 = new LinearLine(circle2.getCenterPoint(), circle3.getCenterPoint());
			
			Point projectPoint1 = line12.findProjectionPoint(point);
			Point projectPoint2 = line13.findProjectionPoint(point);
			Point projectPoint3 = line23.findProjectionPoint(point);
			
			ArrayList<Point> projectPoints = new ArrayList<Point>();
			ArrayList<LinearLine> lines = new ArrayList<LinearLine>();
			if(line12.pointIsInLineArea(projectPoint1)) {				
				projectPoints.add(projectPoint1);
				lines.add(line12);
			}
			if(line13.pointIsInLineArea(projectPoint2)) {
				projectPoints.add(projectPoint2);
				lines.add(line13);
			}
			if(line23.pointIsInLineArea(projectPoint3)) {
				projectPoints.add(projectPoint3);
				lines.add(line23);
			}
			if(projectPoints.size()==0)
				return findNearlyPoints(point1,point2,point3,point);
			else if(projectPoints.size()==1) {
				Point nearlyPoint = findNearlyPoints(point1,point2,point3,projectPoints.get(0));
				LinearLine projectionLine = lines.get(0);
				if((projectionLine.start==nearlyPoint) || (projectionLine.end==nearlyPoint)) {
					return projectPoints.get(0);
				}
				else
					return nearlyPoint;
			}
			else {
				LinearLine nearlyLine = findNearlyLine(line12,line13,line23,point);
				return nearlyLine.findProjectionPoint(point);
			}
				
		}
		
		private static Point findNearlyPoints(Point point1,Point point2,Point point3,Point basePoint) {
			float dis1 = distance(basePoint,point1);
			float dis2 = distance(basePoint,point2);
			float dis3 = distance(basePoint,point3);
			if(dis1<=dis2 && dis1<=dis3) {
				return point1;
			}
			else if(dis2<=dis1 && dis2<=dis3) {
				return point2;
			}
			else
				return point3;
		}
		
		private static LinearLine findNearlyLine(LinearLine line1,LinearLine line2,LinearLine line3,Point basePoint) {
			float dis1 = line1.findMiniDistanceBetweenLineAndPoint(basePoint);
			float dis2 = line2.findMiniDistanceBetweenLineAndPoint(basePoint);
			float dis3 = line3.findMiniDistanceBetweenLineAndPoint(basePoint);
			if(dis1<=dis2 && dis1<=dis3) {
				return line1;
			}
			else if(dis2<=dis1 && dis2<=dis3) {
				return line2;
			}
			else
				return line3;
		}
		
		private static float distance(Point point1,Point point2) {
			float dis = (float)(Math.sqrt(Math.pow(point1.x-point2.x, 2)+Math.pow(point1.y-point2.y, 2)));
			return dis;
		}
		
		
}
