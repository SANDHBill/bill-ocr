package com.sandh.billanalyzer.utility;

/**
 * Created by hamed on 02/12/2015.
 */
public interface TraceableOperator {
    String getOriginName();

    void setOriginName(String originName);

    String getParameters();

    void setParameters(String parameters);

    boolean isDebugMode();

    void setDebugMode(boolean debugMode);

    String getOperation();
}
