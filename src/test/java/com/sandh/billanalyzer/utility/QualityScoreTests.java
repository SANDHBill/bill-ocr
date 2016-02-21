package com.sandh.billanalyzer.utility;

/**
 * Created by hamed on 21/02/2016.
 */
import org.junit.Assert;
import org.junit.Test;


public class QualityScoreTests {

    @Test
    public void testCharFrequencyQuality100percent(){

        String reference ="HAMED";
        String subject="HAMED";

        QualityScore charFrequencyQualityTest = new CharFrequencyQualityScore(reference);

        QualityScoreDetails result = charFrequencyQualityTest.test(subject);

        Assert.assertTrue(result.isAtleast80percAccurate());
        Assert.assertTrue(result.isAtleast100percAccurate());
    }

    @Test
    public void testCharFrequencyQuality80percent(){

        String reference ="HAMED";
        String subject="HAME";

        QualityScore charFrequencyQualityTest = new CharFrequencyQualityScore(reference);

        QualityScoreDetails result = charFrequencyQualityTest.test(subject);

        Assert.assertTrue(result.isAtleast80percAccurate());
        Assert.assertTrue(!result.isAtleast100percAccurate());
    }

    @Test
    public void testCharFrequencyQuality80percentDuplicateChars(){

        String reference ="HHAMED";
        String subject="HHAME";

        QualityScore charFrequencyQualityTest = new CharFrequencyQualityScore(reference);

        QualityScoreDetails result = charFrequencyQualityTest.test(subject);

        Assert.assertTrue(result.isAtleast80percAccurate());
        Assert.assertTrue(!result.isAtleast100percAccurate());
    }
    @Test
    public void testCharFrequencyQuality0percent(){

        String reference ="HAMED";
        String subject="";

        QualityScore charFrequencyQualityTest = new CharFrequencyQualityScore(reference);

        QualityScoreDetails result = charFrequencyQualityTest.test(subject);

        Assert.assertTrue(!result.isAtleast80percAccurate());
        Assert.assertTrue(!result.isAtleast100percAccurate());
        Assert.assertTrue(result.isAtmost10percAccourate());
    }
}
