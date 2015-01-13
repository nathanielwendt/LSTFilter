package com.ut.mpc.utils;

import com.ut.mpc.setup.Constants;
import com.ut.mpc.setup.Constants.SpatialType;



public class GPSLib {
	STPoint t;
	
	public static void main(String[] args){
		GPSLib tester = new GPSLib();
		double[] temp = getCoordFromDist(37.75134, -122.39488, 10, 180);
		System.out.println(temp[0]);
		System.out.println(temp[1]);
	}
	
	/**
	 * Returns a bounding region from the given set of min and max points.
	 * Bounds set by SPACE_RADIUS
	 * @param min - STPoint containing minimum values in all dimensions
	 * @param max - STPoint containing maximum values in all dimensions
	 * @return STRegion bound of the given min max points
	 */
	public static STRegion getRegionBound(STPoint min, STPoint max){
		STPoint minExt = new STPoint();
		STPoint maxExt = new STPoint();
		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
			STPoint temp;
			temp = GPSLib.getCoordFromDist(max.getX(), max.getY(), Constants.CoverageWindow.SPACE_RADIUS, 270);
			maxExt.setY(temp.getY());
			temp = GPSLib.getCoordFromDist(min.getX(), min.getY(), Constants.CoverageWindow.SPACE_RADIUS, 90);
			minExt.setY(temp.getY());
			temp = GPSLib.getCoordFromDist(min.getX(), min.getY(), Constants.CoverageWindow.SPACE_RADIUS, 180);
			minExt.setX(temp.getX());
			temp = GPSLib.getCoordFromDist(max.getX(), max.getY(), Constants.CoverageWindow.SPACE_RADIUS, 0);
			maxExt.setX(temp.getX());
		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters){
			minExt.setX(min.getX() - Constants.CoverageWindow.SPACE_RADIUS);
			minExt.setY(min.getY() - Constants.CoverageWindow.SPACE_RADIUS);
			maxExt.setX(max.getX() + Constants.CoverageWindow.SPACE_RADIUS);
			maxExt.setY(max.getY() + Constants.CoverageWindow.SPACE_RADIUS);		
		}
		return new STRegion(minExt, maxExt);
	}

	public static STRegion getSpaceBoundQuick(STPoint min, STPoint max){
		STPoint minExt = new STPoint();
		STPoint maxExt = new STPoint();
		STRegion region = null;
		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
			//Possible idea - get accurate distance for first point, find what distance is and use that fixed value
			float paddingX = Constants.CoverageWindow.LONG_KM_EST * Constants.CoverageWindow.SPACE_RADIUS;
			float paddingY = Constants.CoverageWindow.LAT_KM_EST * Constants.CoverageWindow.SPACE_RADIUS;
			minExt.setX(min.getX() - paddingX);
			minExt.setY(min.getY() - paddingY);
			maxExt.setX(max.getX() + paddingX);
			maxExt.setY(max.getY() + paddingY);
			region = new STRegion(minExt, maxExt);
		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters){
			region = getRegionBound(min, max);
		}
		return region;
	}
	
	//latitude in degrees
	//longitude in degrees
	//distance in km
	//bearing in degrees
	//formula from http://www.movable-type.co.uk/scripts/latlong.html
	public static STPoint getCoordFromDist(float longitude, float latitude, float distance, float bearing){
		double R = 6371;
		double latRad = Math.toRadians(latitude);
		double lonRad = Math.toRadians(longitude);
		double brng = Math.toRadians(bearing);
		double latResult = Math.asin( Math.sin(latRad)*Math.cos(distance/R) + 
	              Math.cos(latRad)*Math.sin(distance/R)*Math.cos(brng) );
		double lonResult = lonRad + Math.atan2(Math.sin(brng)*Math.sin(distance/R)*Math.cos(latResult), 
	                     Math.cos(distance/R)-Math.sin(latRad)*Math.sin(latResult));
		STPoint point = new STPoint();
		point.setX((float) Math.toDegrees(lonResult));
		point.setY((float) Math.toDegrees(latResult));
		return point;
	}
	
	//points in degrees (longitude,latitude)
	//returns -1 if Constants.SpatialType is undefined
	public static double getDistanceBetween(double[] p1, double[] p2){
		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
			double R = 6371;
			double lat1 = Math.toRadians(p1[0]);
			double lat2 = Math.toRadians(p2[0]);
			double long1 = Math.toRadians(p1[1]);
			double long2 = Math.toRadians(p2[1]);
			double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
							     Math.cos(lat1) * Math.cos(lat2) *
							     Math.cos(long2 - long1)) * R;
			return d;
		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters) {
			return Math.sqrt(Math.pow(p1[0] - p2[0],2) + Math.pow(p1[1] - p2[1],2));
		} else {
			return -1;
		}
	}
}
