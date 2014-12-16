package lib.locate.algorithm.Math;

import lib.locate.algorithm.Math.component.Point;
import lib.locate.algorithm.Math.component.Vector2;

public class PointInTriangle {
	
	public static boolean isInTriangle(Point tri_A,Point tri_B,Point tri_C,Point check_P) {
		
		Vector2 v0 = new Vector2(tri_C.x-tri_A.x,tri_C.y-tri_A.y); // vector C→A
		Vector2 v1 = new Vector2(tri_B.x-tri_A.x,tri_B.y-tri_A.y); // vector B→A
		Vector2 v2 = new Vector2(check_P.x-tri_A.x,check_P.y-tri_A.y); // vector P→A
		
		float dot00 = v0.dot(v0);
		float dot01 = v0.dot(v1);
		float dot02 = v0.dot(v2);
		float dot11 = v1.dot(v1);
		float dot12 = v1.dot(v2);
		float inverDeno = 1/((dot11*dot00)-(dot01*dot01));
		
		float u = ((dot02*dot11)-(dot12*dot01))*inverDeno;
		float v = ((dot12*dot00)-(dot02*dot01))*inverDeno;
		
		if(u>=0 && v>=0 && (u+v)<=1) {
			return true;
		}
		else
			return false;
	}

}
