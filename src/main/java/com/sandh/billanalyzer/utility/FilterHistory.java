package com.sandh.billanalyzer.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by hamed on 27/12/2015.
 */
public class FilterHistory {

    private List<TraceableOperator> historyItems =new ArrayList<TraceableOperator>();
    public StringJoiner getHistory() {
        return history;
    }

    public void add(TraceableOperator phistoryItem){
        historyItems.add(phistoryItem);
    }

    private final StringJoiner history
            =new StringJoiner(",","[","]");


    public TraceableOperator[] getHistoryItems() {

        TraceableOperator[] traceableOperators = new TraceableOperator[1];
        return historyItems.toArray(traceableOperators);
    }
}
