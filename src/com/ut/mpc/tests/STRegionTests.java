package com.ut.mpc.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

public class STRegionTests {

	@Test
	public void testAddPoints() {
		STPoint p1 = new STPoint(0f,8f,23f);
		STPoint p2 = new STPoint(2f,4f,99f);
		
		STRegion range = new STRegion();
		range.addPoint(p1);
		range.addPoint(p2);
		STPoint mins = range.getMins();
		STPoint maxs = range.getMaxs();
		
		assertEquals(0f, mins.getX(), .001);
		assertEquals(4f, mins.getY(), .001);
		assertEquals(23f, mins.getT(), .001);
		
		assertEquals(2f, maxs.getX(), .001);
		assertEquals(8f, maxs.getY(), .001);
		assertEquals(99f, maxs.getT(), .001);
	}
	
	@Test
	public void testAddSinglePoint() {
		STPoint p1 = new STPoint(0f,8f,23f);
		
		STRegion range = new STRegion();
		range.addPoint(p1);
		STPoint mins = range.getMins();
		STPoint maxs = range.getMaxs();
		
		assertEquals(0f, mins.getX(), .001);
		assertEquals(0f, maxs.getX(), .001);
		assertEquals(8f, mins.getY(), .001);
		assertEquals(8f, maxs.getY(), .001);
		assertEquals(23f, mins.getT(), .001);
		assertEquals(23f, maxs.getT(), .001);
	}
	
	@Test
	public void testAddManyPoints() {
		STPoint p1 = new STPoint(10f,8f,23f);
		STPoint p2 = new STPoint(1f,12f,9f);
		STPoint p3 = new STPoint(6f,9f,10f);
		STPoint p4 = new STPoint(2f,2f,11f);
		STPoint p5 = new STPoint(11f,4f,13f);
		
		STRegion range = new STRegion();
		range.addPoint(p1);
		range.addPoint(p2);
		range.addPoint(p3);
		range.addPoint(p4);
		range.addPoint(p5);
		STPoint mins = range.getMins();
		STPoint maxs = range.getMaxs();
		
		assertEquals(1f, mins.getX(), .001);
		assertEquals(2f, mins.getY(), .001);
		assertEquals(9f, mins.getT(), .001);
		
		assertEquals(11f, maxs.getX(), .001);
		assertEquals(12f, maxs.getY(), .001);
		assertEquals(23f, maxs.getT(), .001);
	}

}
