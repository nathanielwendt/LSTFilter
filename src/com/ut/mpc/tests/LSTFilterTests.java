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
			this.size++;
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
		
		@Override
		public STRegion getBoundingBox(){
			STPoint min = new STPoint(Float.MIN_VALUE,Float.MIN_VALUE,Float.MIN_VALUE);
			STPoint max = new STPoint(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
			List<STPoint> allPoints = this.range(new STRegion(min,max));
			STPoint minBounds = new STPoint();
			STPoint maxBounds = new STPoint();
			for(STPoint point : allPoints){
				minBounds.updateMin(point);
				maxBounds.updateMax(point);
			}
			return new STRegion(minBounds,maxBounds);
		}
	}
	
	@Before
	public void setUp(){
		Constants.CoverageWindow.SPACE_RADIUS = 1;
		Constants.CoverageWindow.GRID_DEFAULT = true;
		Constants.SPATIAL_TYPE = Constants.SpatialType.Meters;
	}
	
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
		
		result = filter.pointPoK(new STPoint(-0.01f,-0.01f,10f));
		assertEquals(0f, result, .0001);
	}
	
	@Test
	public void testPointPoKMultiple(){
		LSTFilter filter = new LSTFilter(new SpatialArray());
		filter.setSmartInsert(false);
		filter.insert(new STPoint(1,1,1));
		filter.insert(new STPoint(1.1f,1.1f,1.1f));
		filter.insert(new STPoint(1.2f,1.2f,1.2f));
		filter.insert(new STPoint(1.3f,1.3f,1.3f));
		double result = filter.pointPoK(new STPoint(1f,1f,1f));
		//System.out.println(result);
	}
	
	@Test
	public void testAggPoK(){
		LSTFilter filter = new LSTFilter(new SpatialArray());
		List<Double> items = new ArrayList<Double>();
		double item1 = .000005;
		double item2 = .000005;
		items.add(item1);
		items.add(item2);
		for(float i=.8f; i < .99f; i+=.05f){
			items.add((double)i);
		}
		double[] result = new double[]{0,0};
		filter.getAggPoK(result, new ArrayList<Double>(), items);
		
		//System.out.println(item1 + item2 - (item1 * item2));
		//System.out.println(result[0]);
	}
	
	@Test
	public void testWindowPoK(){
		LSTFilter filter = new LSTFilter(new SpatialArray());
		filter.setSmartInsert(false);
		filter.insert(new STPoint(0.25f,0.25f,0.25f));
		
		STPoint min = new STPoint(0f,0f,0f);
		STPoint max = new STPoint(0.5f,0.5f,0.5f);
		double result = filter.windowPoK(new STRegion(min, max));
		assertEquals(1.0f, result, .0001);
		
		max = new STPoint(1f,0.5f,0.5f);
		result = filter.windowPoK(new STRegion(min, max));
		assertEquals(.5f, result, .0001);
	}

	@Test
	public void testWindowPoKSnap(){
		LSTFilter filter = new LSTFilter(new SpatialArray());
		filter.setSmartInsert(false);
		for(float i=0; i<5; i+=.25f){
			for(float j=0; j<5; j+=.25f){
				for(float k=0; k<5; k+=.25f){
					filter.insert(new STPoint(i,j,k));
				}
			}	
		}
		
		STPoint min = new STPoint(0f,0f,0f);
		STPoint max = new STPoint(5f,5f,20f);
		double result = filter.windowPoK(new STRegion(min, max), false);
		assertEquals(.25f, result, .0001);
		
		//points are set to be perfectly aligned at grid spaces, snapping should always give 1.0
		result = filter.windowPoK(new STRegion(min, max), true);
		assertEquals(1.0f, result, .00001);
		
		max = new STPoint(10f,20f,200f);
		result = filter.windowPoK(new STRegion(min, max), true);
		assertEquals(1.0f, result, .00001);
		
		max = new STPoint(9f,2000f,22f);
		result = filter.windowPoK(new STRegion(min, max), true);
		assertEquals(1.0f, result, .00001);
	}

}
