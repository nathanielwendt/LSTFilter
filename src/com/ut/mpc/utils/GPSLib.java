package com.ut.mpc.utils;

import com.ut.mpc.setup.Constants;

public class GPSLib {

    public static void main(String[] args){
        System.out.println(Math.toRadians(0));
    }

	private static float longitudeKM = .009f;
	private static float latitudeKM = .001f;

	/*
	 * Retrieves a space bound around a given point with given radius values
	 * @param point - point around which to form the bound
	 * @param boundValues - point representing expansion values in each dimension.
	 */
	public static STRegion getSpaceBoundQuick(STPoint point, STPoint boundValues, Constants.SpatialType type) throws LSTFilterException{
        STPoint newMin = new STPoint();
        STPoint newMax = new STPoint();
        if(point.hasX() && boundValues.hasX()){
            newMin.setX(point.getX() - boundValues.getX());
            newMax.setX(point.getX() + boundValues.getX());
        }

        if(point.hasY() && boundValues.hasY()){
            newMin.setY(point.getY() - boundValues.getY());
            newMax.setY(point.getY() + boundValues.getY());
        }

        if(point.hasT() && boundValues.hasT()){
            newMin.setT(point.getT() - boundValues.getT());
            newMax.setT(point.getT() + boundValues.getT());
        }

		return new STRegion(newMin, newMax);
	}

    public static double distanceX(STPoint p1, STPoint p2, Constants.SpatialType type) throws LSTFilterException {
        if(type == Constants.SpatialType.GPS) {
            double R = 6371;
            double long1 = Math.toRadians(p1.getX());
            double long2 = Math.toRadians(p2.getX());
            double d = Math.acos(1 * Math.cos(long2 - long1)) * R;
            return d;
        } else if(type == Constants.SpatialType.Meters) {
            return Math.abs(p2.getX() - p1.getX());
        } else {
            throw new LSTFilterException("Unrecognized coordinate type for GPSLib");
        }
    }

    public static double distanceY(STPoint p1, STPoint p2, Constants.SpatialType type) throws LSTFilterException {
        if(type == Constants.SpatialType.GPS) {
            double R = 6371;
            double lat1 = Math.toRadians(p1.getY());
            double lat2 = Math.toRadians(p2.getY());
            double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                    Math.cos(lat1) * Math.cos(lat2)) * R;
            return d;
        } else if(type == Constants.SpatialType.Meters) {
            return Math.abs(p2.getY() - p1.getY());
        } else {
            throw new LSTFilterException("Unrecognized coordinate type for GPSLib");
        }
    }

    public static double distanceT(STPoint p1, STPoint p2){
        return Math.abs(p1.getT() - p2.getT());
    }
	
	public static double spatialDistanceBetween(STPoint p1, STPoint p2, Constants.SpatialType type) throws LSTFilterException{
		if(type == Constants.SpatialType.GPS){
			double R = 6371;
			double lat1 = Math.toRadians(p1.getY());
			double lat2 = Math.toRadians(p2.getY());
			double long1 = Math.toRadians(p1.getX());
			double long2 = Math.toRadians(p2.getX());
			double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
							     Math.cos(lat1) * Math.cos(lat2) *
							     Math.cos(long2 - long1)) * R;
			return d;
		} else if(type == Constants.SpatialType.Meters) {
			return Math.sqrt(Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2));
			//return Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2);
		} else {
			throw new LSTFilterException("Unrecognized coordinate type for GPSLib");
		}
	}

    //Has a tolerance of about 10m when used as distance calculation 1km
    //Drastically decreases in accuracy with increasing distance calculations
    public static double spatialDistanceBetweenFast(STPoint p1, STPoint p2, Constants.SpatialType type) throws LSTFilterException {
        if(type == Constants.SpatialType.GPS){
            double R = 6371;
            double lat1 = Math.toRadians(p1.getY());
            double lat2 = Math.toRadians(p2.getY());
            double long1 = Math.toRadians(p1.getX());
            double long2 = Math.toRadians(p2.getX());

            double x = (long2 - long1) * Math.cos((lat1 - lat2)/2);
            double y = (lat2 - lat1);
            double d = Math.sqrt(x*x + y*y) * R;
            return d;
        } else if(type == Constants.SpatialType.Meters) {
            return Math.sqrt(Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2));
            //return Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2);
        } else {
            throw new LSTFilterException("Unrecognized coordinate type for GPSLib");
        }
    }

    public static double temporalDistanceBetween(STPoint p1, STPoint p2){
        return Math.abs(p1.getT() - p2.getT());
    }

    public static double spatialTemporalDistanceBetween(STPoint p1, STPoint p2, Constants.SpatialType type) throws LSTFilterException {
        if(type == Constants.SpatialType.GPS){
            double R = 6371;
            double lat1 = Math.toRadians(p1.getY());
            double lat2 = Math.toRadians(p2.getY());
            double long1 = Math.toRadians(p1.getX());
            double long2 = Math.toRadians(p2.getX());
            double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                    Math.cos(lat1) * Math.cos(lat2) *
                            Math.cos(long2 - long1)) * R;
            return Math.sqrt(Math.pow(d,2) + Math.pow(p1.getT() - p2.getT(), 2));
        } else if(type == Constants.SpatialType.Meters) {
            return Math.sqrt(Math.pow(p1.getX() - p2.getX(),2)
                    + Math.pow(p1.getY() - p2.getY(),2)
                    + Math.pow(p1.getT() - p2.getT(),2));
        } else {
            throw new LSTFilterException("Unrecognized coordinate type for GPSLib");
        }
    }

    //This method is STPoint dimension safe for all dimensions - it supports inclusion-exclusion of any dimension for both p1 and p2
    public static double distanceBetween(STPoint p1, STPoint p2, Constants.SpatialType type) throws LSTFilterException {
        if(!p1.hasT() || !p2.hasT()){
            return spatialDistanceBetween(p1, p2, type);
        } else if((!p1.hasX() || !p2.hasX())){
            p1.setX(-Float.MAX_VALUE);
            p2.setX(-Float.MAX_VALUE);
            return spatialTemporalDistanceBetween(p1, p2, type);
        } else if(!p1.hasY() || !p2.hasY()){
            p1.setY(-Float.MAX_VALUE);
            p2.setY(-Float.MAX_VALUE);
            return spatialTemporalDistanceBetween(p1, p2, type);
        } else {
            return spatialTemporalDistanceBetween(p1, p2, type);
        }
    }


    public static float latOffsetFromDistance(STPoint reference, double dist) {
        double distRad = dist / 6371;
        double bearing = 0;
        double lat1 = Math.toRadians(reference.getY());
        double long1 = Math.toRadians(reference.getX());

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distRad) + Math.cos(lat1) * Math.sin(distRad));
        double long2 = long1 + Math.atan2(0, Math.cos(distRad) - Math.sin(lat1) * Math.sin(lat2));

        return (float) Math.toDegrees(Math.abs(lat2 - lat1));

//        var φ2 = Math.asin( Math.sin(φ1)*Math.cos(d/R) +
//                Math.cos(φ1)*Math.sin(d/R)*Math.cos(brng) );
//        var λ2 = λ1 + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(φ1),
//                Math.cos(d/R)-Math.sin(φ1)*Math.sin(φ2));

    }

    public static float longOffsetFromDistance(STPoint reference, double dist){
        double distRad = dist / 6371;
        double bearing = 90;
        double lat1 = Math.toRadians(reference.getY());
        double long1 = Math.toRadians(reference.getX());

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distRad));
        double long2 = long1 + Math.atan2(Math.sin(distRad) * Math.cos(lat1), Math.cos(distRad) - Math.sin(lat1) * Math.sin(lat2));

        return (float) Math.toDegrees(Math.abs(long2 - long1));

//        φ2 = asin( sin φ1 ⋅ cos δ + cos φ1 ⋅ sin δ ⋅ cos θ )
//        λ2 = λ1 + atan2( sin θ ⋅ sin δ ⋅ cos φ1, cos δ − sin φ1 ⋅ sin φ2 )
    }

//	public static void main(String[] args){
//		GPSLib tester = new GPSLib();
//		double[] temp = getCoordFromDist(37.75134, -122.39488, 10, 180);
//		System.out.println(temp[0]);
//		System.out.println(temp[1]);
//	}
//	
//	/**
//	 * Returns a bounding region from the given set of min and max points.
//	 * Bounds set by SPACE_RADIUS
//	 * @param min - STPoint containing minimum values in all dimensions
//	 * @param max - STPoint containing maximum values in all dimensions
//	 * @return STRegion bound of the given min max points
//	 */
//	public static STRegion getRegionBound(STPoint min, STPoint max){
//		STPoint minExt = new STPoint();
//		STPoint maxExt = new STPoint();
//		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
//			STPoint temp;
//			temp = GPSLib.getCoordFromDist(max.getX(), max.getY(), Constants.PoK.SPACE_RADIUS, 270);
//			maxExt.setY(temp.getY());
//			temp = GPSLib.getCoordFromDist(min.getX(), min.getY(), Constants.PoK.SPACE_RADIUS, 90);
//			minExt.setY(temp.getY());
//			temp = GPSLib.getCoordFromDist(min.getX(), min.getY(), Constants.PoK.SPACE_RADIUS, 180);
//			minExt.setX(temp.getX());
//			temp = GPSLib.getCoordFromDist(max.getX(), max.getY(), Constants.PoK.SPACE_RADIUS, 0);
//			maxExt.setX(temp.getX());
//		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters){
//			minExt.setX(min.getX() - Constants.PoK.SPACE_RADIUS);
//			minExt.setY(min.getY() - Constants.PoK.SPACE_RADIUS);
//			maxExt.setX(max.getX() + Constants.PoK.SPACE_RADIUS);
//			maxExt.setY(max.getY() + Constants.PoK.SPACE_RADIUS);
//		}
//		return new STRegion(minExt, maxExt);
//	}
//
//	public static STRegion getSpaceBoundQuick(STPoint min, STPoint max){
//		STPoint minExt = new STPoint();
//		STPoint maxExt = new STPoint();
//		STRegion region = null;
//		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
//			//Possible idea - get accurate distance for first point, find what distance is and use that fixed value
//			float paddingX = Constants.PoK.LONG_KM_EST * Constants.PoK.SPACE_RADIUS;
//			float paddingY = Constants.PoK.LAT_KM_EST * Constants.PoK.SPACE_RADIUS;
//			minExt.setX(min.getX() - paddingX);
//			minExt.setY(min.getY() - paddingY);
//			maxExt.setX(max.getX() + paddingX);
//			maxExt.setY(max.getY() + paddingY);
//			region = new STRegion(minExt, maxExt);
//		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters){
//			region = getRegionBound(min, max);
//		}
//		return region;
//	}
//	
//	//latitude in degrees
//	//longitude in degrees
//	//distance in km
//	//bearing in degrees
//	//formula from http://www.movable-type.co.uk/scripts/latlong.html
//	public static STPoint getCoordFromDist(float longitude, float latitude, float distance, float bearing){
//		double R = 6371;
//		double latRad = Math.toRadians(latitude);
//		double lonRad = Math.toRadians(longitude);
//		double brng = Math.toRadians(bearing);
//		double latResult = Math.asin( Math.sin(latRad)*Math.cos(distance/R) + 
//	              Math.cos(latRad)*Math.sin(distance/R)*Math.cos(brng) );
//		double lonResult = lonRad + Math.atan2(Math.sin(brng)*Math.sin(distance/R)*Math.cos(latResult), 
//	                     Math.cos(distance/R)-Math.sin(latRad)*Math.sin(latResult));
//		STPoint point = new STPoint();
//		point.setX((float) Math.toDegrees(lonResult));
//		point.setY((float) Math.toDegrees(latResult));
//		return point;
//	}
//	
//	//points in degrees (longitude,latitude)
//	//returns -1 if Constants.SpatialType is undefined
//	public static double getDistanceBetween(double[] p1, double[] p2){
//		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
//			double R = 6371;
//			double lat1 = Math.toRadians(p1[0]);
//			double lat2 = Math.toRadians(p2[0]);
//			double long1 = Math.toRadians(p1[1]);
//			double long2 = Math.toRadians(p2[1]);
//			double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
//							     Math.cos(lat1) * Math.cos(lat2) *
//							     Math.cos(long2 - long1)) * R;
//			return d;
//		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters) {
//			return Math.sqrt(Math.pow(p1[0] - p2[0],2) + Math.pow(p1[1] - p2[1],2));
//		} else {
//			return -1;
//		}
//	}
}
