package com.ut.mpc.utils;

import com.ut.mpc.setup.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.ut.mpc.setup.Constants.CoverageWindow;
import static com.ut.mpc.setup.Constants.SPATIAL_TYPE;
import static com.ut.mpc.setup.Constants.SpatialType;

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
		//TODO: add internal structure function to hand off state during executions
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
	 * Convenience method for windowPoK where snap will be set to false
	 * @param region - spatial and temporal bounds for which to query for the PoK
	 * @return PoK value summarizing the coverage of the desired region
	 */
	public double windowPoK(STRegion region){
		return this.windowPoK(region, false);
	}

	/**
	 * Determines the PoK for a given STRegion
	 * @param region - spatial and temporal bounds for which to query for the PoK
	 * @param snap - will return a PoK value ignoring empty space beyond bounds of structure
	 * @return PoK value summarizing the coverage of the desired region
	 */
	public double windowPoK(STRegion region, boolean snap) {
		STPoint mins = region.getMins();
		STPoint maxs = region.getMaxs();

        float xGridGran = CoverageWindow.X_GRID_GRAN;
        float yGridGran = CoverageWindow.Y_GRID_GRAN;
        float tGridGran = CoverageWindow.T_GRID_GRAN;

        float xCenterOffset = xGridGran / 2;
        float yCenterOffset = yGridGran / 2;
        float tCenterOffset = tGridGran / 2;

        //these points must have 3 dimensions
        List<STPoint> boundPoints = structure.range(new STRegion(mins, maxs));

        if(boundPoints.size() == 0){
            return 0.0;
        }

        STPoint minBounds = new STPoint();
        STPoint maxBounds = new STPoint();
        for(STPoint point : boundPoints){
            minBounds.updateMin(point);
            maxBounds.updateMax(point);
        }

        System.out.println(minBounds);
        System.out.println(maxBounds);

        //center align region
        STPoint.add(minBounds, new STPoint(-xCenterOffset, -yCenterOffset, -tCenterOffset));
        STPoint.add(maxBounds, new STPoint(xCenterOffset, yCenterOffset, tCenterOffset));


        //building cache index might not have all 3 dimensions, must create accordingly
        double totalGridCount = 0.0;
        KDTreeAdapter kdtree;
        if(mins.hasX() && mins.hasY() && mins.hasT()){
            //spatiotemporal query
            kdtree = KDTreeAdapter.makeBalancedTree(3,0,boundPoints);
            if(snap){
                totalGridCount = this.getGridCount(minBounds, maxBounds, new STPoint(xGridGran, yGridGran, tGridGran));
            } else {
                totalGridCount = this.getGridCount(mins,maxs, new STPoint(xGridGran, yGridGran, tGridGran));
            }
        } else if(mins.hasX() && mins.hasY() && !mins.hasT()){
            //spatial query
            kdtree = KDTreeAdapter.makeBalancedTree(2,0,boundPoints);
            totalGridCount = this.getGridCount(minBounds, maxBounds, new STPoint(xGridGran, yGridGran, tGridGran));
        } else if(!mins.hasX() && !mins.hasY() && mins.hasT()){
            //temporal query
            kdtree = KDTreeAdapter.makeBalancedTree(1,2,boundPoints);
            totalGridCount = this.getGridCount(minBounds, maxBounds, new STPoint(xGridGran, yGridGran, tGridGran));
        } else {
            return 0.0;
        }

        System.out.println(kdtree.getSize());
        System.out.println(boundPoints.size());

        STPoint boundValues = new STPoint(CoverageWindow.SPACE_RADIUS,
                CoverageWindow.SPACE_RADIUS,
                CoverageWindow.TEMPORAL_RADIUS);

        double totalWeight = 0.0;
        double regionWeight = 0.0;
		for(float x = minBounds.getX(); x < maxBounds.getX(); x = x + xGridGran){
			for(float y = minBounds.getY(); y < maxBounds.getY(); y = y + yGridGran){
				for(float t = minBounds.getT(); t < maxBounds.getT(); t = t + tGridGran){

					STPoint centerOfRegion = new STPoint(x + xCenterOffset,
														 y + yCenterOffset,
														 t + tCenterOffset);
                    try {
                        STRegion miniRegion = GPSLib.getSpaceBoundQuick(centerOfRegion, boundValues, SPATIAL_TYPE);
                        List<STPoint> activePoints = kdtree.range(miniRegion);
                        //List<STPoint> activePoints = structure.range(miniRegion);
                        regionWeight = this.getPointsPoK(centerOfRegion, activePoints);
                    } catch (LSTFilterException e){
                        e.printStackTrace();
                    }
					totalWeight += regionWeight;
				}
			}
		}
        return totalWeight / totalGridCount;
//		if(snap){
//			double effectiveGridCount = this.getGridCount(mins,maxs, new STPoint(xGridGran, yGridGran, tGridGran));
//			return totalWeight / effectiveGridCount;
//		} else {
//			return totalWeight / totalGridCount;
//		}
	}

//
//    private double gridCompST(STPoint mins, STPoint maxs, float xGridGran, float yGridGran, float tGridGran){
//        double totalWeight = 0.0;
//        double regionWeight = 0.0;
//
//        float xCenterOffset = xGridGran / 2;
//        float yCenterOffset = yGridGran / 2;
//        float tCenterOffset = tGridGran / 2;
//
//        List<STPoint> boundPoints = structure.range(new STRegion(mins, maxs));
//        KDTreeAdapter kdtree = KDTreeAdapter.makeBalancedTree(3,0,boundPoints);
//
//
//
//        for(float x = mins.getX(); x < maxs.getX(); x = x + xGridGran){
//            for(float y = mins.getY(); y < maxs.getY(); y = y + yGridGran){
//                for(float t = mins.getT(); t < maxs.getT(); t = t + tGridGran){
//
//                    STPoint centerOfRegion = new STPoint(x + xCenterOffset,
//                            y + yCenterOffset,
//                            t + tCenterOffset);
//
//                    try {
//                        STRegion miniRegion = GPSLib.getSpaceBoundQuick(centerOfRegion, boundValues, SPATIAL_TYPE);
//                        List<STPoint> activePoints = kdtree.range(miniRegion);
//                        //List<STPoint> activePoints = structure.range(miniRegion);
//                        regionWeight = this.getPointsPoK(centerOfRegion, activePoints);
//                    } catch (LSTFilterException e){
//                        e.printStackTrace();
//                    }
//                    totalWeight += regionWeight;
//                }
//            }
//        }
//        return totalWeight;
//    }
//
//    private double gridCompS(STPoint mins, STPoint maxs, float xGridGran, float yGridGran){
//        double totalWeight = 0.0;
//        double regionWeight = 0.0;
//
//        float xCenterOffset = xGridGran / 2;
//        float yCenterOffset = yGridGran / 2;
//
//        List<STPoint> boundPoints = structure.range(new STRegion(mins, maxs));
//        KDTreeAdapter kdtree = KDTreeAdapter.makeBalancedTree(2,0,boundPoints);
//
//        STPoint boundValues = new STPoint(CoverageWindow.SPACE_RADIUS,
//                CoverageWindow.SPACE_RADIUS);
//
//        for(float x = mins.getX(); x < maxs.getX(); x = x + xGridGran){
//            for(float y = mins.getY(); y < maxs.getY(); y = y + yGridGran){
//                STPoint centerOfRegion = new STPoint(x + xCenterOffset,
//                                                        y + yCenterOffset);
//                try {
//                    STRegion miniRegion = GPSLib.getSpaceBoundQuick(centerOfRegion, boundValues, SPATIAL_TYPE);
//                    List<STPoint> activePoints = kdtree.range(miniRegion);
//                    //List<STPoint> activePoints = structure.range(miniRegion);
//                    regionWeight = this.getPointsPoK(centerOfRegion, activePoints);
//                } catch (LSTFilterException e){
//                    e.printStackTrace();
//                }
//                totalWeight += regionWeight;
//            }
//        }
//        return totalWeight;
//    }
//
//    private double gridCompT(STPoint mins, STPoint maxs, float tGridGran){
//        double totalWeight = 0.0;
//        double regionWeight = 0.0;
//
//        float tCenterOffset = tGridGran / 2;
//
//        List<STPoint> boundPoints = structure.range(new STRegion(mins, maxs));
//        //effectively a binary search tree
//        KDTreeAdapter kdtree = KDTreeAdapter.makeBalancedTree(1,2,boundPoints);
//
//        STPoint boundValues = new STPoint();
//        boundValues.setT(CoverageWindow.TEMPORAL_RADIUS);
//
//        for(float t = mins.getT(); t < maxs.getT(); t = t + tGridGran){
//            STPoint centerOfRegion = new STPoint();
//            centerOfRegion.setT(t + tCenterOffset);
//            try {
//                STRegion miniRegion = GPSLib.getSpaceBoundQuick(centerOfRegion, boundValues, SPATIAL_TYPE);
//                List<STPoint> activePoints = kdtree.range(miniRegion);
//                //List<STPoint> activePoints = structure.range(miniRegion);
//                regionWeight = this.getPointsPoK(centerOfRegion, activePoints);
//            } catch (LSTFilterException e){
//                e.printStackTrace();
//            }
//            totalWeight += regionWeight;
//        }
//
//        return totalWeight;
//    }
	
	private double getGridCount(STPoint ref1, STPoint ref2, STPoint granularities){
		double xComp = (Math.abs(ref1.getX() - ref2.getX())) / granularities.getX();
		double xIter = Math.floor(xComp) + Math.ceil(xComp - Math.floor(xComp));
		double yComp = (Math.abs(ref1.getY() - ref2.getY())) / granularities.getY();
		double yIter = Math.floor(yComp) + Math.ceil(yComp - Math.floor(yComp));		
		double tComp = (Math.abs(ref1.getT() - ref2.getT())) / granularities.getT();
		double tIter = Math.floor(tComp) + Math.ceil(tComp - Math.floor(tComp));
		return xIter * yIter * tIter;
	}
	
	/**
	 * Retrieves the PoK for a given point.
	 * Uses the default space bound values to form a region around the point
	 * @param point - point to query around
	 * @return PoK value
	 */
	public double pointPoK(STPoint point){
		STPoint boundValues = new STPoint(CoverageWindow.SPACE_RADIUS,
				CoverageWindow.SPACE_RADIUS,
				CoverageWindow.TEMPORAL_RADIUS);
		try {
			STRegion miniRegion = GPSLib.getSpaceBoundQuick(point, boundValues, SPATIAL_TYPE);
			List<STPoint> activePoints = structure.range(miniRegion);
            System.out.println(activePoints.size());
			return this.getPointsPoK(point, activePoints);
		} catch (LSTFilterException e){
			e.printStackTrace();
			return Float.NaN;
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
		double spatialDistance, temporalDistance, spatialCont, temporalCont, tileWeight, contribution;

		List<Double> nearby = new ArrayList<Double>();
		tileWeight = 0;
        for(STPoint currPoint : points){
			try {

				spatialDistance = GPSLib.spatialDistanceBetween(center, currPoint, SPATIAL_TYPE);
                temporalDistance = GPSLib.temporalDistanceBetween(center, currPoint);
			} catch (LSTFilterException e){
				e.printStackTrace();
				spatialDistance = 0.0;
                temporalDistance = 0.0;
			}
			
			//temporalDistance = Math.abs(center.getT() - currPoint.getT());
			spatialCont =  (-CoverageWindow.SPACE_DECAY * Math.min(spatialDistance,CoverageWindow.SPACE_RADIUS));
			temporalCont = (-CoverageWindow.TEMPORAL_DECAY * Math.min(temporalDistance,CoverageWindow.TEMPORAL_RADIUS));
			contribution = spatialCont + temporalCont + CoverageWindow.TOTAL_WEIGHT;
			nearby.add(contribution / CoverageWindow.TOTAL_WEIGHT);
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
				tileWeight = aggResults[0];
			else
				tileWeight = 0.0;
		}
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
	public void getAggPoK(double[] sum, List<Double> active, List<Double> rest){
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
        if(pok > 0.0001f){
            System.out.println(pok);
        }
		if(pok <= Constants.SmartInsert.INS_THRESH){
			this.stdInsert(point);
		}
	}

	private void stdInsert(STPoint point) {
		structure.insert(point);
	}
}
