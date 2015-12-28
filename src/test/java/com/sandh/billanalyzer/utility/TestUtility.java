package com.sandh.billanalyzer.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hamed on 02/12/2015.
 */
public class TestUtility {
    private static final Logger LOG;

    static {
        LOG = LoggerFactory.getLogger(TestUtility.class.getName());
    }

    public static final String SEPARATOR = "--------------------";

    public static String getSampleRecieptDetails(SampleReceipt sampleReceipt){
        StringBuilder recieptDetails = new StringBuilder();

        stringBuilderAppendl(recieptDetails, SEPARATOR);
        stringBuilderAppendl(recieptDetails, sampleReceipt.getResultProccessingParameters());
        stringBuilderAppendl(recieptDetails, sampleReceipt.getImageName());
        stringBuilderAppendl(recieptDetails, sampleReceipt.getResult());
        stringBuilderAppendl(recieptDetails,SEPARATOR);

        return recieptDetails.toString();

    }

    public static void stringBuilderAppendl(StringBuilder sbuilder, String string){
        sbuilder.append(string);
        sbuilder.append(System.lineSeparator());
    }

    public static void printClassPath(){
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            LOG.info(url.getFile());
        }

    }

    public static void sampleRecieptTestExecuter(String testname,
            SampleReceiptProvider sampleReceiptProvider,
            List<SampleReceipt> results,
            ScenarioExecuter<ImageFilter> scenarioExecuter){

        Iterator<SampleReceipt> sampleReceipts;
        sampleReceipts = sampleReceiptProvider.getSampleRecieptsIterator();

        while(sampleReceipts.hasNext()){
            SampleReceipt sampleReceipt =sampleReceipts.next();
            try (InputStream imageIn = sampleReceipt.getImageInputStream()) {

                ImageFilter imageFilter =
                        ImageFilterFactory.createFilterForInputStream(imageIn);
                imageFilter.setOriginName(sampleReceipt.getImageName());
                imageFilter.setDebugMode(true);

                ImageFilter newImage =scenarioExecuter.executeScenario(imageFilter);


                OCRTransformer ocrTransformer = new OCRTransformer(newImage.getHistory());

                sampleReceipt.setResult(ocrTransformer.transform(newImage));
                sampleReceipt.setHistory(ocrTransformer.getHistory());
                results.add(sampleReceipt);

                SaveHistoryToFile(testname,sampleReceipt);
                LOG.info(TestUtility.getSampleRecieptDetails(sampleReceipt));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static void SaveHistoryToFile(String testName,SampleReceipt sampleReceipt) {
        for(TraceableOperator filter:sampleReceipt.getHistory().getHistoryItems()){
            if(null!= filter.getProcessMaterial().getAsImageFilter()) {
                Utility.storeImageMatInTempFile(
                        filter.getProcessMaterial().getAsImageFilter().getImageMat(),
                        filter,testName);
            }else if(null!= filter.getProcessMaterial().getAsString()){
                Utility.storeTextInTempFile(
                        filter.getProcessMaterial().getAsString(),
                        filter,testName);
            }
            LOG.info(filter.getOperation());
        }
    }
}
