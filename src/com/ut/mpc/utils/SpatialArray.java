package com.ut.mpc.utils;

import java.util.ArrayList;
import java.util.List;

import static com.ut.mpc.setup.Constants.SPATIAL_TYPE;

/**
 * Created by nathanielwendt on 3/4/15.
 */
public class SpatialArray implements STStorage {

    public int size = 0;
    public List<STPoint> points = new ArrayList<STPoint>();

    public void setPoints(List<STPoint> points){
        this.points = points;
    }

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
            if(mins.hasX() && maxs.hasX()){
                if(! (point.getX() >= mins.getX() && point.getX() <= maxs.getX())){
                    continue;
                }
            }

            if(mins.hasY() && maxs.hasY()){
                if(! (point.getY() >= mins.getY() && point.getY() <= maxs.getY())){
                    continue;
                }
            }

            if(mins.hasT() && maxs.hasT()){
                if(! (point.getT() >= mins.getT() && point.getT() <= maxs.getT())){
                    continue;
                }
            }

            results.add(point);
        }
        return results;
    }

    @Override
    public List<STPoint> getSequence(STPoint start, STPoint end) {
        return null;
    }

    @Override
    public void clear() {
        this.points = new ArrayList<STPoint>();
    }

    @Override
    public STRegion getBoundingBox(){
        STPoint min = new STPoint(-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE);
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

    @Override
    public List<STPoint> nearestNeighbor(STPoint needle, STPoint boundValues, int n) {

        //TODO support more than one nearest neighbor
        if(n != 1){
            throw new RuntimeException("Nearest Neighbor does not support N != 1");
        }

        List<STPoint> candPoints = new ArrayList<STPoint>();
        for(int i = 0; i < 20; i++){
            try {
                STRegion miniRegion = GPSLib.getSpaceBoundQuick(needle, boundValues, SPATIAL_TYPE);
                candPoints = range(miniRegion);
                if(candPoints.size() >= n){
                    break;
                }
                int step = (int) Math.pow(i, 3);
                boundValues.setX(step * boundValues.getX());
                boundValues.setY(step * boundValues.getY());
                boundValues.setT(step * boundValues.getT());
            } catch (LSTFilterException e){
                e.printStackTrace();
            }
        }


        STPoint minPoint = null;
        double minVal = Double.MAX_VALUE;
        for(STPoint point: candPoints){
            //ToDo: optimize this since we use the same needle each time, some values should drop out of dist calc.
            //ToDO: optimize by using squared values of distance instead of ones using sqrt (expensive)
            try {
                double distance = GPSLib.distanceBetween(needle, point, SPATIAL_TYPE);
                if(distance < minVal){
                    minVal = distance;
                    minPoint = point;
                }
            } catch (LSTFilterException e){
                e.printStackTrace();
            }
        }

        List<STPoint> minPoints = new ArrayList<STPoint>();
        minPoints.add(minPoint);
        return minPoints;
    }
}
