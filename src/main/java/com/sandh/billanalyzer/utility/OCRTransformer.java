package com.sandh.billanalyzer.utility;

import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.bytedeco.javacpp.lept.pixRead;

/**
 * Created by hamed on 02/12/2015.
 */
public class OCRTransformer extends AbstractTraceableOperator implements Transformer {

    private ProcessMaterial processMaterial;

    public OCRTransformer(FilterHistory history){
        this.setHistory(history);
    }

    @Override
    public String transform(ImageFilter imageFilter) throws IOException {
        final String item;

        this.setOriginName(imageFilter.getOriginName());
        this.setDebugMode(imageFilter.isDebugMode());

        Mat imageMat = imageFilter.getImageMat();
        item = applyOCRToImage(imageMat)[0];

        processMaterial = new ProcessMaterial() {
            @Override
            public String getAsString() {
                return item;
            }

            @Override
            public ImageFilter getAsImageFilter() {
                return null;
            }
        };
        this.lastOperation="OCRTransforming";
        getHistory().add(this);
        return item;
    }

    private String[] applyOCRToImage(Mat preparedImageMat) {
        String items[] =new String[1];

        String ocrOutPutString = applyOCR(Utility.matToInputStream(preparedImageMat));
        items[0]=ocrOutPutString;
        return items;
    }
    private String applyOCR(InputStream imageStreamIn){
        String outText=null;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        String tessdataPath = System.getProperties().getProperty("TESSDATA_PREFIX");
        int tessInitCodeInt = api.Init(tessdataPath,"ENG");
        if (tessInitCodeInt != 0) {
            throw new RuntimeException("Unable to initialise OCR lib");
        }

        File tempFile = Utility.storeImageStreamInTempFile(imageStreamIn, this,"OCRIN","png");

        // Open input image with leptonica library
        lept.PIX image = pixRead(tempFile.getAbsolutePath());
        api.SetImage(image);


        // Get OCR result
        outText = api.GetUTF8Text().getString();

        try {
            Files.delete(tempFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outText;
    }

    @Override
    public ProcessMaterial getProcessMaterial() {
        return processMaterial;
    }
}
