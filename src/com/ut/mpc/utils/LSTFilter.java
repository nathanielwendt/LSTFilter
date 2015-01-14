package com.ut.mpc.utils;

import static com.ut.mpc.setup.Constants.SpatialType;

import java.util.ArrayList;
import java.util.List;

import com.ut.mpc.setup.Constants;

import ut.mpc.setup.Init;

public class LSTFilter {
	private STStorage structure;
	private SpatialType spatialType = SpatialType.GPS;
	private boolean smartInsert = true;

	public void insert(STPoint item) {
		if (smartInsert)
			this.smartInsert(item);
		else
			this.stdInsert(item);
	}

	public void coverageWindow(STRange range) {
		List<STPoint> points = structure.range(range);
		

	}

	public void findPath(STPoint p1, STPoint p2) {

	}
	
	/**
	 * Retrieves the PoK for a given point
	 * Uses the default space radius to form a region around the point
	 * @param point - point to query around
	 * @return PoK value
	 */
	public double getPointPoK(STPoint point){
		
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
