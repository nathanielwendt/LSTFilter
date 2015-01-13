package com.ut.mpc.utils;

import static com.ut.mpc.setup.Constants.SpatialType;

import java.util.ArrayList;

public class LSTFilter {
	private SpatialType spatialType = SpatialType.GPS;
	private boolean smartInsert = true;

	public void insert(STPoint item) {
		if (smartInsert)
			this.smartInsert(item);
		else
			this.stdInsert(item);
	}

	public void coverageWindow(STRange range) {

	}

	public void findPath(STPoint p1, STPoint p2) {

	}
	
	private void smartInsert(STPoint item) {

	}

	private void stdInsert(STPoint item) {

	}
}
