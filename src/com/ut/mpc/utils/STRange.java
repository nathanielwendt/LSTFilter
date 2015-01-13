package com.ut.mpc.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a range object commonly used for queries
 * The range can be conceptualized as 2 STPoints, one for minimums and
 * one for maximums.  In 3 dimensions, the range can be visualized as
 * a cube with the mins point in one of the upper corners and the maxs
 * point in one of the lower corners
 */
public class STRange {
	private STPoint mins = new STPoint();
	private STPoint maxs = new STPoint();
	
	public STRange(){}
	
	public STRange(STPoint p1, STPoint p2){
		mins.updateMin(p1);
		mins.updateMin(p2);
		maxs.updateMax(p1);
		maxs.updateMax(p2);
	}
	
	/**
	 * Adds a point to the range, if the point does not contribute to the range's
	 * minimum or maximum values, it will not affect the range
	 * @param point point to add to the range
	 */
	public void addPoint(STPoint point){
		if(this.mins.isEmpty() || this.maxs.isEmpty()){
			this.mins = point;
			this.maxs = point;
		} else {
			mins.updateMin(point);
			maxs.updateMax(point);	
		}
	}
	
	/**
	 * Retrieves the range's minimum values
	 * @return STPoint with minimum values for each dimension
	 */
	public STPoint getMins(){
		return this.mins;
	}
	
	/**
	 * Retrieves the range's maximum values
	 * @return STPoint with maximum values for each dimension
	 */
	public STPoint getMaxs(){
		return this.maxs;
	}
}
