package lib.locate.algorithm.Math;

import java.util.ArrayList;

import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;

import lib.locate.algorithm.Math.component.Point;
import lib.locate.algorithm.Math.component.LinearLine;
import map.navi.Data.NaviNode;

public class MathProxy {
private static String TAG = "MathProxy";
	
	public static float findMiniDistanceBetweenLineAndPoint(NaviNode sNode,NaviNode tNode,float x,float y) {
		//LinearLine line = new LinearLine(convertNodeToPoint(sNode),convertNodeToPoint(tNode));
		float[] projectionPoints = getProjectionPoint(sNode,tNode,x,y);
		float dis = (float)(Math.sqrt(Math.pow(x-projectionPoints[0], 2)+Math.pow(y-projectionPoints[1], 2)));
		return dis;
	}
	
	public static boolean xyIsBetweenStartAndEndNoEqu(float[] xy,float[] startXY,float[] endXY) {
		if( (xy[0]<startXY[0] && xy[0]>endXY[0]) ||(xy[0]>startXY[0] && xy[0]<endXY[0]) ) {
			if( (xy[1]<startXY[1] && xy[1]>endXY[1]) ||(xy[1]>startXY[1] && xy[1]<endXY[1]) ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean xyIsBetweenStartAndEnd(float[] xy,float[] startXY,float[] endXY) {
		/*if( (xy[0]<=startXY[0] && xy[0]>=endXY[0]) ||(xy[0]>=startXY[0] && xy[0]<=endXY[0]) ||
			(xy[1]<=startXY[1] && xy[1]>=endXY[1]) ||(xy[1]>=startXY[1] && xy[1]<=endXY[1]) ) {
			//if( (xy[1]<=startXY[1] && xy[1]>=endXY[1]) ||(xy[1]>=startXY[1] && xy[1]<=endXY[1]) ) {
				return true;
			//}
		}
		*/
		if( (xy[0]<=startXY[0] && xy[0]>=endXY[0]) ||(xy[0]>=startXY[0] && xy[0]<=endXY[0]) ) {
			if( (xy[1]<=startXY[1] && xy[1]>=endXY[1]) ||(xy[1]>=startXY[1] && xy[1]<=endXY[1]) ) {
				return true;
			}
		}
		return false;
	}
	
	public static float getRotateFormStartToEnd(float[] startXY,float[] endXY) {
		float rotate = 0;
		double xDistance = endXY[0]-startXY[0];
		double yDistance = startXY[1]-endXY[1];
		rotate = (float) (Math.atan2(yDistance,xDistance)/Math.PI*180);
		if (rotate >= 0 && rotate <= 90) {
        	rotate = 90 - rotate;          
        }
        else if (rotate < 0 && rotate >= -90) {
        	rotate = 90 + Math.abs(rotate);
        }
        else if (rotate < -90 && rotate >= -180) {
        	rotate = 90 - rotate;
        }
        else if (rotate > 90 && rotate <= 180) {
        	rotate = 450 - rotate;
        }
		return rotate;
	}
	
	public static float[] getProjectionPoint(NaviNode sNode,NaviNode tNode,float x,float y) {
		Point sPoint = convertNodeToPoint(sNode);
		Point tPoint = convertNodeToPoint(tNode);
		sPoint.y = -(sPoint.y);
		tPoint.y = -(tPoint.y);
		LinearLine line = new LinearLine(sPoint,tPoint);//convertNodeToPoint(sNode),convertNodeToPoint(tNode));
		Point projectionPoints = line.findProjectionPoint(new Point(x,-y));

		//Log.i(TAG, "sNode X = "+sNode.x+",Y = "+sNode.y);
		//Log.i(TAG, "tNode X = "+tNode.x+",Y = "+tNode.y);
		if(!line.pointXCoordinateInLineArea(projectionPoints))
			projectionPoints.x = line.findNearlyStartOrEndPoint(projectionPoints).x;
		if(!line.pointYCoordinateInLineArea(projectionPoints))
			projectionPoints.y = line.findNearlyStartOrEndPoint(projectionPoints).y;
		projectionPoints.y = -(projectionPoints.y);
		
		//Log.i(TAG, "current X = "+x+",Y = "+y);
		//Log.i(TAG, "projectionPoints X = "+projectionPoints.x+",Y = "+projectionPoints.y);
		
		return convetPointTo1DArray(projectionPoints);
	}
	
	private static Point convertNodeToPoint(NaviNode node) {
		return new Point((float)(node.x),(float)(node.y));
	}
	private static float[] convetPointTo1DArray(Point point) {
		float[] xy = new float[2];
		xy[0] = point.x;
		xy[1] = point.y;
		return xy;
	}
	
	public static float getDistance(float x1,float y1,float[] target) {
		return getDistance(x1,y1,target[0],target[1]);
	}
	
	public static float getDistance(float[] source,float[] target) {
		return getDistance(source[0],source[1],target[0],target[1]);
	}
	
	public static float getDistance(float x1,float y1,float x2,float y2) {
		return (float)Math.sqrt(Math.pow(x1 -x2, 2) + Math.pow(y1-y2, 2));
	}
	
	public static float getDistance(float[] xy,NaviNode node) {
		return getDistance(xy[0],xy[1],node.x,node.y);
	}
	
	public static float getDistance(float x,float y,NaviNode node) {
		return getDistance(x,y,node.x,node.y);
	}
	
	public static float getDistance(NaviNode start,NaviNode end) {
		return getDistance(start.x,start.y,end.x,end.y);
	}
	
	private static float getDistance(Point start,Point end) {
		return getDistance(start.x,start.y,end.x,end.y);
		//return (float)Math.sqrt(Math.pow(start.x -end.x, 2) + Math.pow(start.y -end.y, 2));
	}
	
	public static float getLength(NaviNode sNode,NaviNode tNode, float px, float py) {
		return getLength(sNode.x,sNode.y,tNode.x,tNode.y,px,py);
	}
	
	public static float getLength(float lx1,float ly1, float lx2,  float ly2, float px, float py){
		float length = 0.0f;
		float b = getDistance(lx1, ly1, px, py);
		float c = getDistance(lx2, ly2, px, py);
		float a = getDistance(lx1, ly1, lx2, ly2);
		//Log.i(TAG, "a = "+a+",b = "+b+",c = "+c);
		if (c + b == a) {
			length =  0;
		} else if (c * c >= a * a + b * b) {
			length = b;
		} else if (b * b >= a * a + c * c) {
			length = c;
		} else {
			float p = (a + b + c) / 2;
			float s = (float)Math.sqrt(p * (p - a) * (p - b) * (p - c));
			length = 2 * s / a;
		}

		return length;
	}	
/*	
	public static float[] getProjectionCoordinator(float lx1, float ly1, float lx2,
			float ly2, float px, float py) {
		float []coord = new float [2];
		coord[0] = (float)(px*Math.pow(lx2-lx1, 2)+py*(ly2-ly1)*(lx2-lx1)+(lx1*ly2-lx2*ly1)*(ly2-ly1))/(float)(Math.pow(lx2-lx1, 2)+Math.pow(ly2-ly1, 2));
		coord[1] = (float)(px*(lx2-lx1)*(ly2-ly1)+py*Math.pow(ly2-ly1, 2)+(lx2*ly1-lx1*ly2)*(lx2-lx1))/(float)(Math.pow(lx2-lx1, 2)+Math.pow(ly2-ly1, 2));
		return coord;
	}
*/
	
	private void bubbleSort()
	{
		
	}
	
	public static void iBeaconSortingRssiStrongToWeak(ArrayList<IBeacon> sortList) {
		
		IBeacon temp;
		 if (sortList.size()>1) // check if the number of orders is larger than 1
	        {
	            for (int index=0; index<sortList.size(); index++) // bubble sort outer loop
	            {
	                for (int i=0; i < sortList.size()-index-1; i++) 
	                {
	                    if (sortList.get(i).getRssi()<(sortList.get(i+1).getRssi()))
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
