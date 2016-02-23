package com.sandh.billanalyzer.transformers.impl;

import com.sandh.billanalyzer.transformers.Transformer;
import org.bytedeco.javacpp.tesseract;
import org.opencv.core.Mat;

/**
 * Created by hamed on 22/02/2016.
 */
public class OCRTransformer implements Transformer<Mat,String> {
    private tesseract.TessBaseAPI api;

    public OCRTransformer() {
        api = new tesseract.TessBaseAPI();
        String tessdataPath = System.getProperties().getProperty("TESSDATA_PREFIX");
        int tessInitCodeInt = api.Init(tessdataPath, "ENG");
        if (tessInitCodeInt != 0) {
            throw new RuntimeException("Unable to initialise OCR lib");
        }
    }



    @Override
    public String transform(Mat input, String... params) {
        String ocrOutPutString = null;

        byte buff[] = new byte[(int) (input.total() * input.channels())];
        input.get(0, 0, buff);
        api.SetImage(buff, input.width(), input.height(), (int) input.elemSize(), (int) input.step1(0));
        ocrOutPutString = api.GetUTF8Text().getString();

        return ocrOutPutString;
    }
}
