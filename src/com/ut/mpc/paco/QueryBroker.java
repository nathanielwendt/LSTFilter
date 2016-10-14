package com.ut.mpc.paco;

import com.ut.mpc.utils.STRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nathanielwendt on 10/13/16.
 */
public class QueryBroker {
    Map<String, QueryBudget> budgets = new HashMap<String, QueryBudget>();

    public QueryBroker(){

    }

    public QueryBudget account(String appId){
        return budgets.get(appId);
    }

}
