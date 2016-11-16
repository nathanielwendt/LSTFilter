package com.ut.mpc.utils;

/**
 * Created by nathanielwendt on 11/16/16.
 */
public class QueryWindow {
    private STRegion region;
    private STPoint gridGran;

    public QueryWindow(STRegion region, STPoint gridGran){
        this.region = region;
        this.gridGran = gridGran;
    }

    public STPoint getMins(){
        return this.region.getMins();
    }

    public STPoint getMaxs(){
        return this.region.getMaxs();
    }

    public STRegion getRegion(){
        return this.region;
    }

    public STPoint getGridGran(){
        return this.gridGran;
    }

    public boolean hasTimeBounds(){
        STPoint mins = this.region.getMins();
        return mins.hasT();
    }

    public boolean hasSpaceBounds(){
        STPoint mins = this.region.getMins();
        return mins.hasX() && mins.hasY();
    }

}
