package com.sandh.billanalyzer.utility;

/**
 * Created by hamed on 20/02/2016.
 */
public interface QualityScore {

    String PASSREGEX = "^true;.*";

    QualityScoreDetails test(Object subject);
}
