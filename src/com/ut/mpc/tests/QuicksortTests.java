package com.ut.mpc.tests;

import com.ut.mpc.kdtree.KDTree;
import com.ut.mpc.utils.KDTreeAdapter;
import com.ut.mpc.utils.Quicksort;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QuicksortTests {
    private List<STPoint> points;

    @Before
    public void setUp(){
        points = new ArrayList<STPoint>();
        points.add(new STPoint(1,3,5));
        points.add(new STPoint(2,2,2));
        points.add(new STPoint(9,1,3));
        points.add(new STPoint(11,2,4));
        points.add(new STPoint(0,0,1));
        points.add(new STPoint(5,4,3));
    }

	@Test
	public void testBasic() {
        Quicksort.sort(points, 0, points.size() - 1, 0);
        assertEquals(0, points.get(0).getX(), .0001);
        assertEquals(1, points.get(1).getX(), .0001);
        assertEquals(2, points.get(2).getX(), .0001);
        assertEquals(5, points.get(3).getX(), .0001);
        assertEquals(9, points.get(4).getX(), .0001);
        assertEquals(11, points.get(5).getX(), .0001);

        Quicksort.sort(points, 0, points.size() - 1, 1);
        assertEquals(0, points.get(0).getY(), .0001);
        assertEquals(1, points.get(1).getY(), .0001);
        assertEquals(2, points.get(2).getY(), .0001);
        assertEquals(2, points.get(3).getY(), .0001);
        assertEquals(3, points.get(4).getY(), .0001);
        assertEquals(4, points.get(5).getY(), .0001);

        Quicksort.sort(points, 0, points.size() - 1, 2);
        assertEquals(1, points.get(0).getT(), .0001);
        assertEquals(2, points.get(1).getT(), .0001);
        assertEquals(3, points.get(2).getT(), .0001);
        assertEquals(3, points.get(3).getT(), .0001);
        assertEquals(4, points.get(4).getT(), .0001);
        assertEquals(5, points.get(5).getT(), .0001);
	}


}
