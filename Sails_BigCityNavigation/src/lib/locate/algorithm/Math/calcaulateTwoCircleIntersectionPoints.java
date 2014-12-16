package lib.locate.algorithm.Math;

import lib.locate.algorithm.Math.component.Circle;
import lib.locate.algorithm.Math.component.Point;

public class calcaulateTwoCircleIntersectionPoints {

	private static float x1,y1,r1;
	private static float x2,y2,r2;
	//public static double pointX1,pointY1,pointX2,pointY2;
	public static Point point1,point2;//,pointY2;
	public static boolean hasOnePoint= false,hasTwoPoints = false,noPoint = false;
	private static double parameter_M,parameter_K,parameter_a,parameter_b,parameter_c;
	
	public calcaulateTwoCircleIntersectionPoints (Circle circle1,Circle circle2) {//float x1,float y1,float r1,float x2,float y2,float r2) {
		this.x1 = circle1.pointX;
		this.y1 = circle1.pointY;
		this.r1 = circle1.R;
		this.x2 = circle2.pointX;
		this.y2 = circle2.pointY;
		this.r2 = circle2.R;	
		 findIntersectionPoints();
	}
	
	private static void calculateParameterInYcoordinateDifferent() {
		parameter_M = (x1-x2)/(y2-y1);
		parameter_K = (Math.pow(r1, 2)-Math.pow(r2, 2)+Math.pow(x2, 2)-Math.pow(x1, 2)+Math.pow(y2, 2)-Math.pow(y1, 2))/(2*(y2-y1));
		parameter_a = 1+Math.pow(parameter_M, 2);	
		parameter_b = 2*(parameter_M*parameter_K-parameter_M*y2-x2);
		parameter_c = Math.pow(x2, 2)+Math.pow(y2, 2)+Math.pow(parameter_K, 2)-(2*parameter_K*y2)-Math.pow(r2, 2);	
	}
	
	private static void calculateParameterInYcoordinateSame() {
		point1.x = (float) (-(Math.pow(x1, 2)-Math.pow(x2, 2)-Math.pow(r1, 2)+Math.pow(r2, 2))/(2*x2-2*x1));
		//pointX1 = -(Math.pow(x1, 2)-Math.pow(x2, 2)-Math.pow(r1, 2)+Math.pow(r2, 2))/(2*x2-2*x1);
		parameter_a = 1;
		parameter_b = -2*(y1);
		parameter_c = Math.pow(point1.x, 2)+Math.pow(x1, 2)-(2*x1*point1.x)+Math.pow(y1, 2)-Math.pow(r1, 2);
		//parameter_c = Math.pow(pointX1, 2)+Math.pow(x1, 2)-(2*x1*pointX1)+Math.pow(y1, 2)-Math.pow(r1, 2);	
	}
	
	
	public static void findIntersectionPoints() {
		point1 = new Point();
		point2 = new Point();
		if(y1 != y2) {
			calculateParameterInYcoordinateDifferent();
			double variable = Math.pow(parameter_b, 2)-(4*parameter_a*parameter_c); //b*b-4ac
			
			/*
			 *  b*b-4ac >0 ���ۥ榳���I
			 *  b*b-4ac =0 ���ۥ榳�@�I
			 *  //pointX1 = ((-parameter_b)+Math.sqrt(variable))/(2*parameter_a);
				//pointY1 = parameter_M*pointX1+parameter_K;
				//pointX2 = ((-parameter_b)-Math.sqrt(variable))/(2*parameter_a);
				//pointY2 = parameter_M*pointX2+parameter_K;
				 
			 */
			if(variable>=0) { //two circles have intersection point 
				noPoint = false;
				point1.x = (float) (((-parameter_b)+Math.sqrt(variable))/(2*parameter_a));
				point1.y = (float) (parameter_M*point1.x+parameter_K);
				if(variable>0) {
					hasOnePoint= false;
					hasTwoPoints = true;
					point2.x = (float) (((-parameter_b)-Math.sqrt(variable))/(2*parameter_a));
					point2.y = (float) (parameter_M*point2.x+parameter_K);
				}
				else {					
					hasOnePoint= true;
					hasTwoPoints = false;
				}					
			}
			else
				noPoint = true;
		}
		else {
			calculateParameterInYcoordinateSame();
			double variable = Math.pow(parameter_b, 2)-4*parameter_a*parameter_c; //b*b-4ac
			/*
			 *  b*b-4ac >0 ���ۥ榳���I
			 *  b*b-4ac =0 ���ۥ榳�@�I 
			 *  pointY1 = ((-parameter_b)+Math.sqrt(variable))/(2*parameter_a);
			 *	 pointY2 = ((-parameter_b)-Math.sqrt(variable))/(2*parameter_a);
			 *	 pointX2 = pointX1;
			 */
			if(variable>=0) { //two circles have intersection point
				noPoint = false;				
				point1.y = (float) (((-parameter_b)+Math.sqrt(variable))/(2*parameter_a));	
				if(variable>0) {
					 hasOnePoint= false;
					 hasTwoPoints = true;
					 point2.x = point1.x;
					 point2.y =  (float) (((-parameter_b)-Math.sqrt(variable))/(2*parameter_a));
				}
				else {
					hasOnePoint= true;
					hasTwoPoints = false;
				}					
			}
			else
				noPoint = true;
		}
	}
	
	private static void findIntersectionPoints2() {
		point1 = new Point();
		point2 = new Point();
		if(y1 != y2) {
			calculateParameterInYcoordinateDifferent();
		}
		else {
			calculateParameterInYcoordinateSame();
		}
		double variable = Math.pow(parameter_b, 2)-(4*parameter_a*parameter_c);
		
		if(variable>0) {
			noPoint = false;	
			hasOnePoint= false;
			hasTwoPoints = true;
			if(y1 != y2) {
				point1.x = (float) (((-parameter_b)+Math.sqrt(variable))/(2*parameter_a));
				point1.y = (float) (parameter_M*point1.x+parameter_K);
				point2.x = (float) (((-parameter_b)-Math.sqrt(variable))/(2*parameter_a));
				point2.y = (float) (parameter_M*point2.x+parameter_K);
			}
			else {
				point1.y = (float) (((-parameter_b)+Math.sqrt(variable))/(2*parameter_a));	
				point2.x = point1.x;
				point2.y =  (float) (((-parameter_b)-Math.sqrt(variable))/(2*parameter_a));
			}
		}
		else if(variable == 0) {
			noPoint = false;	
			hasOnePoint= true;
			hasTwoPoints = false;
			if(y1 != y2) {
				point1.x = (float) (((-parameter_b)+Math.sqrt(variable))/(2*parameter_a));
				point1.y = (float) (parameter_M*point1.x+parameter_K);
			}
			else {
				point1.y = (float) (((-parameter_b)+Math.sqrt(variable))/(2*parameter_a));
			}
		}
		else {
			noPoint = true;
		}
	}	
	
}
