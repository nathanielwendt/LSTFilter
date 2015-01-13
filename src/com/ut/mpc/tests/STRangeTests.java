package com.ut.mpc.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRange;

public class STRangeTests {

	@Test
	public void testAddPoints() {
		STPoint p1 = new STPoint(0f,4f,2333333L);
		STPoint p2 = new STPoint(2f,8f,9999999L);
		
		STRange range = new STRange();
		range.addPoint(p1);
		range.addPoint(p2);
		STPoint mins = range.getMins();
		STPoint maxs = range.getMaxs();
		
		System.out.println(mins);
		assertEquals(0f, mins.getX(), .001);
	}

}
