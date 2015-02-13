package com.ut.mpc.tests;

import static org.junit.Assert.*;

import com.ut.mpc.utils.KDTreeAdapter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;
import org.junit.Test;

import com.ut.mpc.kdtree.KDTree;

import java.util.List;

public class KDTreeTests {

	@Test
	public void testBasic() {
		KDTree tree = new KDTree(3);
		tree.insert(new double[]{1,1,1},1);
		tree.insert(new double[]{2,2,2},2);
		tree.insert(new double[]{3,3,3},3);
		Object[] items = tree.range(new double[]{0,0,0}, new double[]{3,3,3});
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

}
