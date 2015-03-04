package com.ut.mpc.tests;

import static org.junit.Assert.*;

import com.ut.mpc.utils.KDTreeAdapter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;
import org.junit.Test;

import com.ut.mpc.kdtree.KDTree;

import java.util.ArrayList;
import java.util.List;

public class KDTreeTests {

	@Test
	public void testBasic() {
		KDTree tree = new KDTree(3);
		tree.insert(new float[]{1,1,1},1);
		tree.insert(new float[]{2,2,2},2);
		tree.insert(new float[]{3,3,3},3);
		Object[] items = tree.range(new float[]{0,0,0}, new float[]{3,3,3});
        assertEquals(1,items[0]);
        assertEquals(2,items[1]);
        assertEquals(3,items[2]);
	}

    @Test
    public void testAdapter() {
        KDTreeAdapter tree = new KDTreeAdapter(3);
        tree.insert(new STPoint(1,1,1));
        tree.insert(new STPoint(2,2,2));
        tree.insert(new STPoint(3,3,3));
        STRegion range = new STRegion(new STPoint(0,0,0), new STPoint(3,3,3));
        List<STPoint> results = tree.range(range);
        assertEquals(results.get(0), new STPoint(1,1,1));
        assertEquals(results.get(1), new STPoint(2,2,2));
        assertEquals(results.get(2), new STPoint(3,3,3));
    }

    @Test
    public void testMakeBalancedST() {
        List<STPoint> points = new ArrayList<STPoint>();
        points.add(new STPoint(1,1,1));
        points.add(new STPoint(2,2,2));
        points.add(new STPoint(3,3,3));
        points.add(new STPoint(4,4,4));
        points.add(new STPoint(5,5,5));
        points.add(new STPoint(6,6,6));
        points.add(new STPoint(7,7,7));

        KDTreeAdapter tree = KDTreeAdapter.makeBalancedTree(3,0,points);
        STRegion range = new STRegion(new STPoint(0,0,0), new STPoint(3,3,3));
        List<STPoint> results = tree.range(range);
        assertEquals(1, results.get(0).getX(), .0001f);
        assertEquals(2, results.get(1).getX(), .0001f);
        assertEquals(3, results.get(2).getX(), .0001f);
    }

    @Test
    public void testMakeBalancedS() {
        List<STPoint> points = new ArrayList<STPoint>();
        points.add(new STPoint(1,1,1));
        points.add(new STPoint(2,2,2));
        points.add(new STPoint(3,3,3));
        points.add(new STPoint(4,4,4));
        points.add(new STPoint(5,5,5));
        points.add(new STPoint(6,6,6));
        points.add(new STPoint(7,7,7));

        KDTreeAdapter tree = KDTreeAdapter.makeBalancedTree(2,0,points);
        STRegion range = new STRegion(new STPoint(0,0,0), new STPoint(3,3,3));
        List<STPoint> results = tree.range(range);
        assertEquals(1, results.get(0).getX(), .0001f);
        assertEquals(2, results.get(1).getX(), .0001f);
        assertEquals(3, results.get(2).getX(), .0001f);
    }

    @Test
    public void testMakeBalancedT() {
        List<STPoint> points = new ArrayList<STPoint>();
        points.add(new STPoint(1,1,1));
        points.add(new STPoint(2,2,2));
        points.add(new STPoint(3,3,3));
        points.add(new STPoint(4,4,4));
        points.add(new STPoint(5,5,5));
        points.add(new STPoint(6,6,6));
        points.add(new STPoint(7,7,7));

        KDTreeAdapter tree = KDTreeAdapter.makeBalancedTree(1,2,points);
        STRegion range = new STRegion(new STPoint(0,0,0), new STPoint(3,3,3));
        List<STPoint> results = tree.range(range);
        assertEquals(1, results.get(0).getX(), .0001f);
        assertEquals(2, results.get(1).getX(), .0001f);
        assertEquals(3, results.get(2).getX(), .0001f);
    }

}
