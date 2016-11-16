package com.ut.mpc.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.ut.mpc.utils.SpatialArray;
import org.junit.Before;
import org.junit.Test;

import com.ut.mpc.setup.Constants;
import com.ut.mpc.setup.Constants.PoK;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

public class LSTFilterTests {
	
	@Before
	public void setUp(){
		PoK.GRID_DEFAULT = true;
		Constants.SPATIAL_TYPE = Constants.SpatialType.Meters;

        Constants.PoK.TOTAL_WEIGHT = 2;
        PoK.SPACE_WEIGHT = 1;
        Constants.PoK.TEMPORAL_WEIGHT = 1;

        Constants.PoK.SPACE_RADIUS = 1;
        Constants.PoK.TEMPORAL_RADIUS = 1;

        PoK.GRID_DEFAULT = false;
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
        assertEquals(1.0f, result, .0001f);
		
		max = new STPoint(1f,0.5f,0.5f);
		result = filter.windowPoK(new STRegion(min, max));
        //value is .5f because query should optimize to not need to compute over the extra grid
        //still divide by 2 since snap is false
		assertEquals(.5f, result, .0001);
	}

//	@Test
//	public void testWindowPoKSnap(){
//		LSTFilter filter = new LSTFilter(new SpatialArray());
//		filter.setSmartInsert(false);
//		for(float i=0; i<5; i+=.25f){
//			for(float j=0; j<5; j+=.25f){
//				for(float k=0; k<5; k+=.25f){
//					filter.insert(new STPoint(i,j,k));
//				}
//			}
//		}
//
//		STPoint min = new STPoint(0f,0f,0f);
//		STPoint max = new STPoint(5f,5f,20f);
//
//		//points are set to be perfectly aligned at grid spaces, snapping should always give 1.0
//		double result = filter.windowPoK(new STRegion(min, max));
//		assertEquals(1.0f, result, .00001);
//
//		max = new STPoint(10f,20f,200f);
//		result = filter.windowPoK(new STRegion(min, max));
//		assertEquals(1.0f, result, .00001);
//
//		max = new STPoint(9f,2000f,22f);
//		result = filter.windowPoK(new STRegion(min, max));
//		assertEquals(1.0f, result, .00001);
//	}

    @Test
    public void testWindowPoKExcludeTemporal(){
        LSTFilter filter = new LSTFilter(new SpatialArray());
        filter.setSmartInsert(false);
        filter.insert(new STPoint(0.25f,0.25f,0.25f));

        STPoint min = new STPoint(0f,0f);
        STPoint max = new STPoint(0.5f,0.5f);
        double result = filter.windowPoK(new STRegion(min, max));
        assertEquals(1.0f, result, .0001f);
    }

    @Test
    public void testWindowPoKExcludeSpatial(){
        LSTFilter filter = new LSTFilter(new SpatialArray());
        filter.setSmartInsert(false);
        filter.insert(new STPoint(0.25f,0.25f,0.25f));

        STPoint min = new STPoint();
        min.setT(0f);
        STPoint max = new STPoint();
        max.setT(.5f);
        double result = filter.windowPoK(new STRegion(min, max));
        assertEquals(1.0f, result, .0001f);
    }

    @Test
    public void testWindowPoKNoPoints(){
        LSTFilter filter = new LSTFilter(new SpatialArray());
        filter.setSmartInsert(false);
        filter.insert(new STPoint(0.25f,0.25f,0.25f));

        STPoint min = new STPoint();
        min.setT(.5f);
        STPoint max = new STPoint();
        max.setT(.5f);
        double result = filter.windowPoK(new STRegion(min, max));
        assertEquals(0f, result, .0001f);
    }

//    @Test
//    public void testNN(){
//        SpatialArray arr = new SpatialArray();
//        arr.insert(new STPoint(0,0,0));
//        arr.insert(new STPoint(1,1,1));
//        arr.insert(new STPoint(2,2,2));
//        arr.insert(new STPoint(3,3,3));
//
//        List<STPoint> nn = arr.nearestNeighbor(new STPoint(3,3,4), 1);
//        assertEquals(new STPoint(3,3,3), nn.get(0));
//
//        nn = arr.nearestNeighbor(new STPoint(2.6f,2.6f,2.6f), 1);
//        assertEquals(new STPoint(3,3,3), nn.get(0));
//    }
//
//    @Test
//    public void testNNSparse(){
//        SpatialArray arr = new SpatialArray();
//        arr.insert(new STPoint(100,100,100));
//        arr.insert(new STPoint(101,101,101));
//        arr.insert(new STPoint(102,102,103));
//        arr.insert(new STPoint(104,104,104));
//
//        List<STPoint> nn = arr.nearestNeighbor(new STPoint(0,0,0), 1);
//        assertEquals(new STPoint(100,100,100), nn.get(0));
//
//        nn = arr.nearestNeighbor(new STPoint(2000,2000,2000), 1);
//        assertEquals(new STPoint(104,104,104), nn.get(0));
//    }
}
