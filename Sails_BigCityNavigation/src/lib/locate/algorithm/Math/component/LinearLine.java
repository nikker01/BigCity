package lib.locate.algorithm.Math.component;

import android.util.Log;

public class LinearLine {

		public Point start,end;
		public float a,b;//y=ax+b;
		//private LinearLine lodretLine; 
		public float lodret_a,lodret_b;
		
		public LinearLine(Point start,Point end) {
			this.start = start;
			this.end = end;
			this.calculateCoefficientAndConstance();
		}
	
		private boolean lineIsVerticalX_axis() {
			if(start.x==end.x) {
				return true;
			}
			return false;
		}
		
		private boolean lineIsVerticalY_axis() {
			if(start.y==end.y) {
				return true;
			}
			return false;
		}
		
		private void calculateCoefficientAndConstance() {
			if(this.lineIsVerticalX_axis())
				this.a = Float.POSITIVE_INFINITY;
			else if(this.lineIsVerticalY_axis())
				this.a = 0;
			else
				this.a = (float)(start.y-end.y)/(float)(start.x-end.x);
			this.b = start.y-(a*start.x);
		}
		
		private void calculateLodretLineCoefficientAndConstance(Point point) {
			if(this.lineIsVerticalX_axis())
				this.lodret_a = 0;
			else if(this.lineIsVerticalY_axis())
				this.lodret_a = Float.POSITIVE_INFINITY;
			else
				this.lodret_a = (float)(-((start.x-end.x))/(float)(start.y-end.y));
			this.lodret_b = point.y-(lodret_a*point.x);
		}
		
		public Point findProjectionPoint(Point point) {
			this.calculateLodretLineCoefficientAndConstance(point);
			float projectionPointX = 0,projectionPointY = 0;
			if(this.lineIsVerticalX_axis()) {
				projectionPointX = this.start.x;
				projectionPointY = point.y;
			}
			else if(this.lineIsVerticalY_axis()) {
				projectionPointX = point.x;
				projectionPointY = this.start.y;
			}
			else {
				projectionPointX = (float)(this.lodret_b-this.b)/(float)(this.a-this.lodret_a);
				projectionPointY = this.a*projectionPointX+this.b;
			}
			return new Point(projectionPointX,projectionPointY);
		}
		
		public float findMiniDistanceBetweenLineAndPoint(Point point) {
			Point projectionPoint = findProjectionPoint(point);
			return calculatePointDistance(point,projectionPoint);
		}
		
		private float calculatePointDistance(Point point1,Point point2) {
			float distance = (float)(Math.sqrt(Math.pow(point1.x-point2.x, 2)+Math.pow(point1.y-point2.y, 2)));
			return distance;
		}
		
		public boolean pointIsInLineArea(Point point) {		
			if((point.x>this.start.x&&point.x<this.end.x) || (point.x<this.start.x && point.x>this.end.x )) {
				if((point.y>this.start.y&&point.y<this.end.y) || (point.y<this.start.y && point.y>this.end.y ))
					return true;
				else
					return false;
			}
			else
				return false;
		}
		
		public boolean pointXCoordinateInLineArea(Point point) {
			if((point.x>=this.start.x&&point.x<=this.end.x) || (point.x<=this.start.x && point.x>=this.end.x ))
				return true;
			else
				return false;
		}
		public boolean pointYCoordinateInLineArea(Point point) {
			if((point.y>=this.start.y&&point.y<=this.end.y) || (point.y<=this.start.y && point.y>=this.end.y ))
				return true;
			else
				return false;
		}
		public Point findNearlyStartOrEndPoint(Point basePoint) {
			float dis1 = distance(basePoint,start);
			float dis2 = distance(basePoint,end);
			if(dis1<dis2){
				return start;
			}
			else if(dis2<dis1)
				return end;
			else
				return new Point((start.x+end.x)/2,(start.y+end.y)/2);
		}
		private float distance(Point point1,Point point2) {
			float dis = (float)(Math.sqrt(Math.pow(point1.x-point2.x, 2)+Math.pow(point1.y-point2.y, 2)));
			return dis;
		}
}
