package com.sandh.billanalyzer.utility;

/**
 * Created by hamed on 02/12/2015.
 */
@FunctionalInterface
public interface ScenarioExecuter<T> {

    T executeScenario(T imageFilter);
}
