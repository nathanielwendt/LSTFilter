package com.ut.mpc.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ut.mpc.setup.Constants;
import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.LSTFilterException;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import java.util.ArrayList;
import java.util.List;

public class GPSLibTests {

	@Test
	public void testGetSpaceBoundQuickMetersBasic() {
		STPoint point = new STPoint(1f,2f,3f);
		STPoint radius = new STPoint(3f,3f,3f);
		
		try {
			STRegion region = GPSLib.getSpaceBoundQuick(point, radius, Constants.SpatialType.Meters);
			STPoint mins = region.getMins();
			STPoint maxs = region.getMaxs();
			assertEquals(mins, new STPoint(-2f,-1f,0f));
			assertEquals(maxs, new STPoint(4f,5f,6f));
		} catch (LSTFilterException e){
			fail();
		}
	}
	
	@Test
	public void testGetSpaceBoundQuickMetersNegatives() {
		STPoint point = new STPoint(-1f,0f,1f);
		STPoint radius = new STPoint(12f,11f,9f);
		
		try {
			STRegion region = GPSLib.getSpaceBoundQuick(point, radius, Constants.SpatialType.Meters);
			STPoint mins = region.getMins();
			STPoint maxs = region.getMaxs();
			assertEquals(mins, new STPoint(-13f,-11f,-8f));
			assertEquals(maxs, new STPoint(11f,11f,10f));
		} catch (LSTFilterException e){
			fail();
		}
	}
	
	@Test
	public void testGetSpaceBoundQuickGPS() {
		STPoint point = new STPoint(1f,2f,3f);
		STPoint radius = new STPoint(1f,1f,1f);
		
		try {
			STRegion region = GPSLib.getSpaceBoundQuick(point, radius, Constants.SpatialType.GPS);
			STPoint mins = region.getMins();
			STPoint maxs = region.getMaxs();
			assertEquals(mins, new STPoint(0f,1f,2f));
			assertEquals(maxs, new STPoint(2f,3f,4f));
		} catch (LSTFilterException e){
			fail();
		}
	}
	
	@Test
	public void testSpatialDistanceBetweenMeters(){
		STPoint p1 = new STPoint(2f,3f,1f);
		STPoint p2 = new STPoint(4f,19f,0f);
		try {
			double val = GPSLib.spatialDistanceBetween(p1, p2, Constants.SpatialType.Meters);
			assertEquals(16.12451549, val, .0000001);
		} catch (LSTFilterException e){
			fail();
		}
	}
	
	@Test
	public void testSpatialDistanceBetweenGPS(){
		STPoint p1 = new STPoint(2.2f,3f,1f);
		STPoint p2 = new STPoint(2.4f,3.2f,0f);
		try {
			double val = GPSLib.spatialDistanceBetween(p1, p2, Constants.SpatialType.GPS);
			assertEquals(31.44, val, .05);
		} catch (LSTFilterException e){
			fail();
		}
	}

    @Test
    public void testSpatialDistMethods(){
        STPoint p1 = new STPoint(-122.40521f, 37.79657f, 0f);
        STPoint p2 = new STPoint(-122.4075f, 37.78405f, 0f);

        try {
            double val = GPSLib.spatialDistanceBetween(p1, p2, Constants.SpatialType.GPS);
            double val2 = GPSLib.spatialDistanceBetweenFast(p1, p2, Constants.SpatialType.GPS);

            System.out.println(val);
            System.out.println(val2);

            double x = GPSLib.distanceX(p1, p2, Constants.SpatialType.GPS);
            double y = GPSLib.distanceY(p1, p2, Constants.SPATIAL_TYPE.GPS);
            System.out.println(Math.sqrt(x*x + y*y));

        } catch (LSTFilterException e){
            fail();
        }
    }

    @Test
    public void testSpatialOffset(){
        STPoint ref = new STPoint(-122.40521f, 37.79657f, 0f);

        double x = GPSLib.longOffsetFromDistance(ref, 111);
        double y = GPSLib.latOffsetFromDistance(ref, 111);

        assertEquals(1.263, x, .001f);
        assertEquals(0.998, y, .001f);
    }

}
