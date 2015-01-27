package LSTStructure.src.com.ut.mpc.utils;

import java.util.ArrayList;
import java.util.List;

import LSTStructure.src.com.ut.mpc.setup.Constants;

import static LSTStructure.src.com.ut.mpc.setup.Constants.CoverageWindow;
import static LSTStructure.src.com.ut.mpc.setup.Constants.SPATIAL_TYPE;
import static LSTStructure.src.com.ut.mpc.setup.Constants.SpatialType;

public class LSTFilter {
	private STStorage structure;
	private SpatialType spatialType = SpatialType.GPS;
	private boolean smartInsert = true;

	/**
	 * Creates a new LSTFilter containing the given structure.
	 * @param structure - STStorage structure to include within the LSTFilter
	 */
	public LSTFilter(STStorage structure){
		this.structure = structure;
	}
	
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
	 * Determines the PoK for a given STRegion
	 * @param region - spatial and temporal bounds for which to query for the PoK
	 * @return PoK value summarizing the coverage of the desired region
	 */
	public double windowPoK(STRegion region) {
		
		STPoint mins = region.getMins();
		STPoint maxs = region.getMaxs();
		float xGridGran = CoverageWindow.X_GRID_GRAN;
		float yGridGran = CoverageWindow.Y_GRID_GRAN;
		float tGridGran = CoverageWindow.T_GRID_GRAN;
		int count = 0;
		double totalWeight = 0.0;
		
		float xCenterOffset = xGridGran / 2;
		float yCenterOffset = yGridGran / 2;
		float tCenterOffset = tGridGran / 2;
		
		for(float x = mins.getX(); x < maxs.getX(); x = x + xGridGran){
			for(float y = mins.getY(); x < maxs.getY(); y = y + yGridGran){
				for(float t = mins.getT(); t < maxs.getT(); t = t + tGridGran){

					STPoint centerOfRegion = new STPoint(x + xCenterOffset,
														 y + yCenterOffset,
														 t + tCenterOffset);
					
					double regionWeight = this.pointPoK(centerOfRegion);
					totalWeight += regionWeight;
				}
			}
		}
		
		double maxWeight = count * CoverageWindow.SPACE_WEIGHT;
		return totalWeight / maxWeight * 100;
	}
	
	/**
	 * Retrieves the PoK for a given point.
	 * Uses the default space radius to form a region around the point
	 * @param point - point to query around
	 * @return PoK value
	 */
	public double pointPoK(STPoint point){
		STPoint radiusValues = new STPoint(CoverageWindow.SPACE_RADIUS,
				CoverageWindow.SPACE_RADIUS,
				CoverageWindow.TIME_RADIUS);
		try {
			STRegion miniRegion = GPSLib.getSpaceBoundQuick(point, radiusValues, SPATIAL_TYPE);
			List<STPoint> activePoints = structure.range(miniRegion);
			return this.getPointsPoK(point, activePoints);
		} catch (LSTFilterException e){
			e.printStackTrace();
			return Float.MIN_VALUE;
		}
	}
	
	/**
	 * Finds the path between two points.  If the points don't match any in the structure
	 * the nearest neighbors are chosen.
	 * @param start - beginning point of path
	 * @param end - terminating point of path
	 * @return list of the sequentially (temporal) ordered points
	 */
	public List<STPoint> findPath(STPoint start, STPoint end) {
		return structure.getSequence(start, end);
	}

    /**
     * Convenience method for clearing the structure
     */
    public void clear(){
        structure.clear();
    }

    /**
     * Sets the structure's smart insert flag
     * @param isSmart - value to set the smart insert flag to
     */
    public void setSmartInsert(boolean isSmart){
        this.smartInsert = isSmart;
    }

    /**
     * Gets the size of the structure
     * @return size of the structure, number of data points
     */
    public int getSize(){
        return structure.getSize();
    }
	
	
	/*
	 *  *********************** Private Methods ********************************
	 *  
	 */
	
	/**
	 * Finds the PoK for the given points about the center point
	 * The PoK is found using the center point as the reference point for finding distances
	 * @param center - Reference point from which to perform the calculation
	 * @param points - points used to determine impact on PoK w.r.t. center point
	 */
	private double getPointsPoK(STPoint center, List<STPoint> points){
		double distFromPoint, spatialContribution, temporalContribution, tileWeight;
		List<Double> nearby = new ArrayList<Double>();
		tileWeight = 0;
        for(STPoint currPoint : points){
			//iterations++;
			try {
				distFromPoint = GPSLib.spatialDistanceBetween(center, currPoint, SPATIAL_TYPE);
			} catch (LSTFilterException e){
				e.printStackTrace();
				distFromPoint = 0.0;
			}
			
			spatialContribution = (-CoverageWindow.SPACE_WEIGHT / CoverageWindow.SPACE_RADIUS) * distFromPoint 
						 + CoverageWindow.SPACE_WEIGHT;
			if(spatialContribution > CoverageWindow.SPACE_TRIM){
				spatialContribution /= 100;
				temporalContribution = currPoint.getTimeRelevance(CoverageWindow.CURRENT_TIMESTAMP, 
																  CoverageWindow.REFERENCE_TIMESTAMP,
																  CoverageWindow.TEMPORAL_DECAY);
				nearby.add(spatialContribution * temporalContribution);
			}
		}
		
		//TODO: do we still need this with r-tree?
		if(points.size() > 0 && nearby.size() > 0){ //make sure not to add a point with an empty activePoints tree
			double[] aggResults = new double[2];
			aggResults[0] = 0;
			aggResults[1] = 0;
			List<Double> empty = new ArrayList<Double>();
			int numTrimmed = this.trimNearby(nearby);
			this.getAggPoK(aggResults,empty,nearby);
			//recurseIterations += aggResults[1];
			if(aggResults[0] > 0)
				tileWeight = aggResults[0] * 100;
			else
				tileWeight = 0.0;
		}
		
		//if(tileWeight > 100){
			//Init.DebugPrint("size of nearby: " + nearby.size(), 1);
			//Init.DebugPrint("non-opt print overflow of space weight: greater probabilty than possible", 1);
			//Init.DebugPrint("tileWeight: " + tileWeight, 1);
		//}
		return tileWeight;
	}
	
	/** 
	 * Trims the list of nearby points until it satisfies the TRIM_THRESH
	 * Values are first sorted before trimming so most influential points will be kept
	 * Sort after building the list as suggested in: http://stackoverflow.com/questions/168891/
	 * @param nearby - list of points to be trimmed
	 * @return number of trimmed points
	 */
	private int trimNearby(List<Double> nearby){
		int initialSize = nearby.size();
		Quicksort qs = new Quicksort();
		qs.sort(nearby, 0, nearby.size() - 1);
		while(nearby.size() > CoverageWindow.TRIM_THRESH){
			nearby.remove(0);
		}
		return initialSize - nearby.size();
	}
	
	/**
	 * Uses the inclusion-exclusion principle to determine the aggregate probability of points
	 * Each possible combination of points is generated and summed or subtracted according to the incl-excl principle
	 * @param sum - store the result in sum[0], iteration count in sum[1]
	 * @param active - active list of points, pass in an empty List
	 * @param rest - remaining list of points, pass in the list of points to be computed
	 */
	private void getAggPoK(double[] sum, List<Double> active, List<Double> rest){
		sum[1]++;
		if(rest.size() == 0){
			double sign;
			if((active.size() + 1) % 2 == 1) //this acts as the (-1)^(k-1) term for alternating subtraction and addition
				sign = -1;
			else
				sign = 1;
			//perform the probability equivalent of AND of the "set", which is multiplication of the probabilities
			double andValue = 0.0;
			for(int i = 0; i < active.size(); i++){
				if(i == 0)
					andValue = active.get(i);
				else
					andValue *= active.get(i);
			}
			sum[0] += andValue * sign;
		} else {
			//shallow copy of lists
			List<Double> next1 = new ArrayList<Double>(active);
			List<Double> next2 = new ArrayList<Double>(rest);
			List<Double> next3 = new ArrayList<Double>(active);
			next1.add(rest.get(0));
			next2.remove(0);

			//recursively call subsets
			this.getAggPoK(sum,next1,next2);
			this.getAggPoK(sum,next3,next2);
		}
	}
	
	private void smartInsert(STPoint point) {
		double pok = this.pointPoK(point);
		if(pok <= Constants.SmartInsert.INS_THRESH){
			this.stdInsert(point);
		}
	}

	private void stdInsert(STPoint point) {
		structure.insert(point);
	}
}
