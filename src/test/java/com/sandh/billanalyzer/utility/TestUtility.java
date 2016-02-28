package com.sandh.billanalyzer.utility;

import org.opencv.core.Mat;
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

                processSampleReciept(sampleReceipt,imageIn,scenarioExecuter);
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

    private static void processSampleReciept(SampleReceipt sampleReceipt,
                                             InputStream imageIn,
                                             ScenarioExecuter<ImageFilter> scenarioExecuter) throws IOException {
        ImageFilter imageFilter = getImageFilter(sampleReceipt, imageIn);

        ImageFilter newImage =scenarioExecuter.executeScenario(imageFilter);

        TraceableOperator[] history =
                newImage.getChain().toArray(new TraceableOperator[newImage.getChain().size()]);


        sampleReceipt.setResult(history[history.length-2].getOutput().getAsString());
        sampleReceipt.setHistory(history);
    }

    private static ImageFilter getImageFilter(SampleReceipt sampleReceipt, InputStream imageIn) throws IOException {
        ImageFilter imageFilter =
                ImageFilterFactory.createFilterForInputStream(imageIn);

        imageFilter.setOriginName(sampleReceipt.getImageName());
        imageFilter.setDebugMode(true);
        return imageFilter;
    }

    private static void SaveHistoryToFile(String testName,SampleReceipt sampleReceipt) {
        for(TraceableOperator filter:sampleReceipt.getHistory()){
            ProcessMaterial output = filter.getOutput();
            if(output==null){
                continue;
            }
            Utility.storeOuputInTempFile(filter,testName);
            LOG.info(filter.getFilterName());
        }
    }
}
