package lib.locate.algorithm.Math;

import java.util.ArrayList;

import android.util.Log;

public class LinearRegression {
	private ArrayList<Float> Y = new ArrayList<Float>(); //rssi
	private ArrayList<Float> X = new ArrayList<Float>(); //distance
	public float a;  //rssi0
	public float b ;//alpha
	public float r ;//Correlation coefficient

	public LinearRegression(ArrayList<Float> Y,ArrayList<Float> X) {
		this.Y = Y;
		this.X = X;
		//Reciprocal(Y,X);
		this.a = this.calculateA();
		this.b = (this.calculateB());
		this.r =  Math.abs(this.calculateCorrelationCoefficient());
	}	
		
	private float calculateA() {
		float a =0,sum_x=0,sum_y=0;
		for(int index=0;index<this.X.size();index++) {
			sum_x =sum_x+this.X.get(index);
			sum_y = sum_y+this.Y.get(index);
		}
		float avgY = sum_y/this.Y.size();
		float avgX = sum_x/this.X.size();
		a = avgY - this.calculateB()*avgX;
		return a;
	}
	
	private float calculateB() {
		Log.i("calculateB", "BEGIN");
		//Log.i("", msg)
		float b = this.calculateSxy()/this.calculateSxx();
		return b;
	}
	
	private float calculateCorrelationCoefficient() {
		float sxy = this.calculateSxy()/this.X.size();
		float sxx = this.calculateSxx()/this.X.size();
		float syy = this.calculateSyy()/this.X.size();
		float r =  (float) (sxy/Math.sqrt(sxx*syy));
		return r;
	}
	
	private float calculateSxx() {
		float sum_x_square=0,sum_x=0,Sxx_result=0;
		for(Float x :this.X) {
			sum_x_square = (float) (sum_x_square+Math.pow(x, 2));
			sum_x = sum_x+x;
		}
		float square_sum_x = (float) Math.pow(sum_x, 2);
		Sxx_result = this.X.size()*sum_x_square-square_sum_x;
		return Sxx_result;
	}
	
	private float calculateSyy() {
		float sum_y_square=0,sum_y=0,Syy_result=0;
		for(Float y :this.Y) {
			sum_y_square = (float) (sum_y_square+Math.pow(y, 2));
			sum_y = sum_y+y;
		}
		float square_sum_y = (float) Math.pow(sum_y, 2);
		Syy_result = this.Y.size()*sum_y_square-square_sum_y;
		return Syy_result;
	}
	
	private float calculateSxy() {
		float sum_xy=0,sum_x=0,sum_y=0,Sxy_result=0;
		for(int index=0;index<this.X.size();index++) {
			float x = this.X.get(index);
			float y = this.Y.get(index);
			sum_xy = sum_xy+(x*y);
			sum_x = sum_x +x;
			sum_y = sum_y+y;
		}
		
		Sxy_result = this.X.size()*sum_xy-(sum_x*sum_y);
		return Sxy_result;
	}
}
