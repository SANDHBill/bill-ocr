package com.sandh.billanalyzer.utility;

import java.util.List;

/**
 * Created by hamed on 02/12/2015.
 */
public interface TraceableOperator {
    String getOriginName();

    void setOriginName(String originName);

    boolean isDebugMode();

    void setDebugMode(boolean debugMode);

    TraceableOperator getLastOperation();

    List<TraceableOperator> getChain();

    ProcessMaterial getOutput();
    String getFilterName();



}
