package com.sandh.billanalyzer.utility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hamed on 01/12/2015.
 */
public class SampleReceiptProvider {
    public static final String TEST_RECEIPTS ="TEST_RECEIPTS";
    public static final String TEST_RECEIPTS_PATH ="src/test/resources/reciepts";


    private String testReceipts;
    private int numberOfReceiptsToProvide =0;
    private String fileName=null;

    public SampleReceiptProvider(){
        this(-1);
    }

    public SampleReceiptProvider(int numberOfReceiptsToProvide){
        setPathVariables();
        this.numberOfReceiptsToProvide = numberOfReceiptsToProvide;
    }

    public SampleReceiptProvider(String name){
        setPathVariables();
        this.numberOfReceiptsToProvide = 1;
        this.fileName=name;
    }

    public  Iterator<SampleReceipt> getSampleRecieptsIterator(){
        return getSampleRecieptsIterator(-1);
    }
    public  Iterator<SampleReceipt> getSampleRecieptsIterator(int pnumberOfRecieptsToProvide){

        int localMaxReciepts = (pnumberOfRecieptsToProvide>-1)?pnumberOfRecieptsToProvide: numberOfReceiptsToProvide;

        List<SampleReceipt> sampleReceipts =new ArrayList();
        List<Path> images = new ArrayList<>();

        Path dir = Paths.get(testReceipts);
        try {
            images = listSourceFiles(dir);
        } catch (IOException e) {
                e.printStackTrace();
        }

        for( Path imagePath : images){
            SampleReceipt sampleReceipt = createSampleReciept(imagePath);
            if(null==fileName){
                sampleReceipts.add(sampleReceipt);
            }else if(sampleReceipt.getImageName().contains(fileName)){
                sampleReceipts.add(sampleReceipt);
            }

            if(localMaxReciepts>-1 && sampleReceipts.size()>=localMaxReciepts){
                break;
            }
        }

        return  sampleReceipts.iterator();
    }

    private SampleReceipt createSampleReciept(Path imagePath) {
        SampleReceipt sampleReceipt = null;
        try {
            URL imageUrl = imagePath.toUri().toURL();
            String textRefFileName = imagePath.toFile().getName().split("[_\\.]")[0]+".txt";

            URL textUrl = imagePath.resolveSibling(textRefFileName).toUri().toURL();
            sampleReceipt = new SampleReceipt(imageUrl,textUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return sampleReceipt;
    }

    private void setPathVariables(){
        String envVariableTestRecieptPath =
                System.getProperties().getProperty(TEST_RECEIPTS);

        if( null == envVariableTestRecieptPath){
            testReceipts = TEST_RECEIPTS_PATH;
        }else{
            testReceipts =envVariableTestRecieptPath;
        }
    }
    private List<Path> listSourceFiles(Path dir) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{jpg}")) {
            for (Path entry: stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }
}
