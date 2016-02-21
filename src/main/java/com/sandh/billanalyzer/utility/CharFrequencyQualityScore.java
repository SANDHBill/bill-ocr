package com.sandh.billanalyzer.utility;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by hamed on 20/02/2016.
 */
public class CharFrequencyQualityScore implements QualityScore {
    
    CharacterCounter characterCounter = new CharacterCounter();
    private  String reference;
    private Map<Character,Long> refCharFerequency;

    public CharFrequencyQualityScore(Object referenceObj) {
        this.reference = (String)referenceObj;
        this.refCharFerequency =characterCounter.countCharacters(this.reference);
    }

    @Override
    public QualityScoreDetails test(Object subject) {
        String subjectStry = (String) subject;
        
        QualityScoreDetails result = test(subjectStry);

        return result;
    }
    
    public QualityScoreDetails test(String subject){
        Map<Character,Long> subjectResult =characterCounter.countCharacters(subject);
        return produceQualityScoreResult(subjectResult);
    }

    private QualityScoreDetails produceQualityScoreResult(Map<Character, Long> subjectResult) {
        Map results = refCharFerequency.entrySet().stream().map(item -> {
            Map.Entry mappedEntry = evaluateComparisonResult(subjectResult, item);
            return mappedEntry;
        }).collect(Collectors.toMap(
                item -> item.getKey(), item-> item.getValue()
        ));

        QualityScoreDetails qualityScoreDetails = new QualityScoreDetails(
                this.getClass().getName(),results);


        return qualityScoreDetails;
    }

    private static Map.Entry evaluateComparisonResult(Map<Character, Long> subjectResult, Map.Entry<Character, Long> item) {
        Character ch=item.getKey();
        Long countRef=item.getValue();
        Long countSubject=subjectResult.remove(ch);
        StringJoiner resultBuilder = new StringJoiner(";");
        boolean test=countSubject!=null && countRef.longValue()==countSubject.longValue();
        resultBuilder.add(String.valueOf(test));
        resultBuilder.add(countRef+":"+countSubject);
        String evaluationResult = resultBuilder.toString();
        return new AbstractMap.SimpleEntry(ch,evaluationResult);
    }
}
