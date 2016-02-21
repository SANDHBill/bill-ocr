package com.sandh.billanalyzer.utility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hamed on 20/02/2016.
 */
public class QualityScoreDetails {
    private String qualityTestName;
    private LocalDateTime qualityTestTimeStamp;
    private final Map<String,String> testRestults;
    private  double accuracy=0;

    public QualityScoreDetails(String qualityTestName,Map testRestults) {
        this.qualityTestName = qualityTestName;
        this.testRestults=testRestults;
        init();

    }

    private void init(){
        qualityTestTimeStamp =  LocalDateTime.now();
        long passes =testRestults.entrySet()
                .stream()
                .filter(entry -> entry.getValue().matches(QualityScore.PASSREGEX))
                .count();

        accuracy = (double)passes/(double)testRestults.size();
    }


    public LocalDateTime getQualityTestTimeStamp() {
        return qualityTestTimeStamp;
    }

    public Map<String, String> getTestRestults() {
        return testRestults;
    }

    public String getQualityTestName() {
        return qualityTestName;
    }

    public boolean isAtleast80percAccurate() {

        return accuracy>=0.8;
    }

    public boolean isAtleast100percAccurate() {
        return  accuracy==1;
    }

    public boolean isAtmost10percAccourate() {
        return accuracy<0.1;
    }
}
