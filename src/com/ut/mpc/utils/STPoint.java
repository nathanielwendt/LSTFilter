package com.ut.mpc.utils;

import com.ut.mpc.setup.Constants.CoverageWindow;

public class STPoint {
	private static float DEFAULT_VAL = Float.NEGATIVE_INFINITY;
	private float x = DEFAULT_VAL;
	private float y = DEFAULT_VAL;
	private float t = DEFAULT_VAL;
	
	public STPoint(){}

	public STPoint(STPoint other){
		this.x = other.x;
		this.y = other.y;
		this.t = other.t;
	}
	
	//x is typically longitude
	//y is typically latitude
	public STPoint(float x, float y, float t){
		this.x = x;
		this.y = y;
		this.t = t;
	}
	
	public STPoint(float x, float y){
		this(x,y,DEFAULT_VAL);
	}
	
	public STPoint(float t){
		this(DEFAULT_VAL, DEFAULT_VAL, t);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public boolean hasX(){
		return this.x != DEFAULT_VAL;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public boolean hasY(){
		return this.y != DEFAULT_VAL;
	}

	public float getT() {
		return t;
	}

	public void setT(float t) {
		this.t = t;
	}
	
	public boolean hasT(){
		return this.t != DEFAULT_VAL;
	}
	
	public void updateMin(STPoint next){
		if(next.getX() < this.getX()){
			this.x = next.getX();
		}
		if(next.getY() < this.getY()){
			this.y = next.getY();
		}
		if(next.getT() < this.getT()){
			this.t = next.getT();
		}
	}
	
	public void updateMax(STPoint next){
		if(next.getX() > this.getX()){
			this.x = next.getX();
		}
		if(next.getY() > this.getY()){
			this.y = next.getY();
		}
		if(next.getT() > this.getT()){
			this.t = next.getT();
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == this){ 
			return true;
		}
		if(obj == null || obj.getClass() != this.getClass()){ 
			return false; 
		}
		STPoint other = (STPoint) obj;
		
		return (Float.compare(this.getX(), other.getX()) == 0) &&
				(Float.compare(this.getY(), other.getY()) == 0) &&
				(Float.compare(this.getT(), other.getT()) == 0);
	}
	
	public String toString(){
		return " X: " + String.valueOf(this.x) + 
			   " Y: " + String.valueOf(this.y) + 
			   " T: " + String.valueOf(this.t);
	}
	
	//linear decay function with any value for slope
	public double getTimeRelevance(float reference, float decay){
		double referenceDub = (double) reference;
		double thisDub = (double) this.getT();
		double decayDub = (double) decay;
		double offset = Math.abs(referenceDub - thisDub);
		//return ((-1 * decayDub * offset) + thisDub) / thisDub;
		return ((-CoverageWindow.TEMPORAL_WEIGHT / CoverageWindow.TEMPORAL_RADIUS) * offset +
				CoverageWindow.TEMPORAL_WEIGHT) / CoverageWindow.TEMPORAL_WEIGHT;
	}
	
}
