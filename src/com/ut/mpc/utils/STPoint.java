package com.ut.mpc.utils;

public class STPoint {
	private float x;
	private float y;
	private Long t;
	
	public STPoint(){}
	
	//x is typically longitude
	//y is typically latitude
	public STPoint(float x, float y, Long t){
		this.x = x;
		this.y = y;
		this.t = t;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Long getT() {
		return t;
	}

	public void setT(Long t) {
		this.t = t;
	}
	
	public boolean isEmpty(){
		return this.t == null && this.x == 0.0f && this.y == 0.0f;
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
	
	public String toString(){
		return " X: " + String.valueOf(this.x) + 
			   " Y: " + String.valueOf(this.y) + 
			   " T: " + String.valueOf(this.t);
	}
	
}
