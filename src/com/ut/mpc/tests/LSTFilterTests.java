package com.ut.mpc.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ut.mpc.setup.Constants;
import com.ut.mpc.setup.Constants.CoverageWindow;
import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.LSTFilterException;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;
import com.ut.mpc.utils.STStorage;

public class LSTFilterTests {

	public static class SpatialArray implements STStorage {
		public int size = 0;
		public List<STPoint> points = new ArrayList<STPoint>();

		@Override
		public int getSize() {
			return this.size;
		}

		@Override
		public void insert(STPoint point) {
			this.points.add(point);
		}

		@Override
		public List<STPoint> range(STRegion range) {
			List<STPoint> results = new ArrayList<STPoint>();
			STPoint mins = range.getMins();
			STPoint maxs = range.getMaxs();
			for(STPoint point : this.points){
				if(point.getX() >= mins.getX() && point.getX() <= maxs.getX() && 
				   point.getY() >= mins.getY() && point.getY() <= maxs.getY() &&
				   point.getT() >= mins.getT() && point.getT() <= maxs.getT()){
					results.add(point);
				}
			}
			return results;
		}

		@Override
		public List<STPoint> nearestNeighbor(STPoint needle, int n) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<STPoint> getSequence(STPoint start, STPoint end) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void clear() {
			this.points = new ArrayList<STPoint>();
		}
	}
	
	@Before
	public void setUp(){
		Constants.CoverageWindow.SPACE_RADIUS = 1;
		Constants.CoverageWindow.GRID_DEFAULT = true;
		Constants.SPATIAL_TYPE = Constants.SpatialType.Meters;
	}
	
//	@Test
//	public void testWindowPoK() {
//		LSTFilter filter = new LSTFilter(new SpatialArray());
//		filter.setSmartInsert(false);
//		filter.insert(new STPoint(1,1,10));
//		filter.insert(new STPoint(2,2,20));
//		filter.insert(new STPoint(3,3,30));
//		filter.insert(new STPoint(4,4,40));
//		
//		STPoint min = new STPoint(0,0,0);
//		STPoint max = new STPoint(5,5,100);
//		System.out.println("before");
//		STRegion queryRegion = new STRegion(min,max);
//		double result = filter.windowPoK(queryRegion);
//		System.out.println(result);
//	}
	
	@Test
	public void testPointPoKSweepTemporal(){
		LSTFilter filter = new LSTFilter(new SpatialArray());
		filter.setSmartInsert(false);
		filter.insert(new STPoint(1,1,10));
		
		double result = filter.pointPoK(new STPoint(1,1,10f));
		assertEquals(1f, result, .0001);
		
		result = filter.pointPoK(new STPoint(1,1,9.5f));
		assertEquals(.75f, result, .0001);
		
		result = filter.pointPoK(new STPoint(1,1,10.5f));
		assertEquals(.75f, result, .0001);
		
		//Point is included in range, so temporal contribution is none, weighted at 50% of total contr.
		result = filter.pointPoK(new STPoint(1,1,11f));
		assertEquals(.5f, result, .0001);
		
		//Point is not included in the range, so result is 0
		result = filter.pointPoK(new STPoint(1,1,11.1f));
		assertEquals(0f, result, .0001);
	}
	
	@Test
	public void testPointPoKSweepSpatial(){
		LSTFilter filter = new LSTFilter(new SpatialArray());
		filter.setSmartInsert(false);
		filter.insert(new STPoint(1,1,10));
		//filter.insert(new STPoint(2,2,20));
		//filter.insert(new STPoint(3,3,30));
		//filter.insert(new STPoint(4,4,40));

		double result = filter.pointPoK(new STPoint(1f,1f,10f));
		assertEquals(1f, result, .0001);
		
		result = filter.pointPoK(new STPoint(0.5f,1f,10f));
		assertEquals(.75f, result, .0001);
		
		result = filter.pointPoK(new STPoint(0.5f,0.5f,10f));
		assertEquals(.646446f, result, .0001);
		
		result = filter.pointPoK(new STPoint(0f,0f,10f));
		assertEquals(.50f, result, .0001);
		
		result = filter.pointPoK(new STPoint(0f,0f,10f), false);
		
		result = filter.pointPoK(new STPoint(-0.01f,-0.01f,10f));
		assertEquals(0f, result, .0001);
	}
	
	@Test
	public void testWindowPoKSweepSpatial(){
		LSTFilter filter = new LSTFilter(new SpatialArray());
		filter.setSmartInsert(false);
		filter.insert(new STPoint(1,1,10));
		
		STPoint min = new STPoint(0f,1f,0f);
		STPoint max = new STPoint(2f,2f,10f);
		double result = filter.windowPoK(new STRegion(min, max));
		System.out.println(result);
	}

}
