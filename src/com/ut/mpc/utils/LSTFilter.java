package com.ut.mpc.utils;

import static com.ut.mpc.setup.Constants.SpatialType;

import java.util.ArrayList;
import java.util.List;

import com.ut.mpc.setup.Constants;


public class LSTFilter {
	private STStorage structure;
	private SpatialType spatialType = SpatialType.GPS;
	private boolean smartInsert = true;

	/**
	 * Inserts a point into the structure.  Smart insertion is
	 * determined from the LSTFilter's smartInsert member variable
	 * @param item point to insert into the structure
	 */
	public void insert(STPoint item) {
		if (smartInsert)
			this.smartInsert(item);
		else
			this.stdInsert(item);
	}

	/**
	 * Determines the PoK for a given STRange
	 * @param range range for which to query for the PoK
	 * @return PoK value summarizing the coverage of the desired range
	 */
	public double coverageWindow(STRange range) {
		List<STPoint> points = structure.range(range);
		return 0.0;

	}

	/**
	 * Finds the path between two points
	 * @param p1 first point
	 * @param p2 second point
	 * @return list of the sequentially (temporal) ordered points
	 */
	public List<STPoint> findPath(STPoint p1, STPoint p2) {
		return null;
	}
	
	/**
	 * Retrieves the PoK for a given point.
	 * Uses the default space radius to form a region around the point
	 * @param point - point to query around
	 * @return PoK value
	 */
	public double getPointPoK(STPoint point){
		return 0.0;
	}
	
	private void smartInsert(STPoint point) {
		double pok = this.getPointPoK(point);
		if(pok <= Constants.SmartInsert.INS_THRESH)
			this.stdInsert(point);
	}

	private void stdInsert(STPoint point) {
		structure.insert(point);
	}
}
