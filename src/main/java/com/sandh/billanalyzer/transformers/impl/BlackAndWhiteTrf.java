package com.sandh.billanalyzer.transformers.impl;

/**
 * Created by hamed on 21/02/2016.
 */
import com.sandh.billanalyzer.transformers.Transformer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;


public class BlackAndWhiteTrf implements Transformer<Mat,Mat> {

    public static final String ADAPTIVE = "Adaptive";

    @Override
    public Mat transform(Mat input, String... params) {
        boolean isAdaptive=checkIsAdaptive(params);

        Mat outPut=null;

        if(!isAdaptive) {
            outPut=blackAndWhiteImageBinary(input);
        }else{
            outPut=blackAndWhiteImageAdaptive(input);
        }
        return outPut;
    }

    private boolean checkIsAdaptive(String[] params) {
        if(params!=null){
            int idx=Arrays.asList(params).indexOf(ADAPTIVE);

            return idx>-1;
        }else{
            return false;
        }
    }

    private Mat blackAndWhiteImageBinary(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        Imgproc.threshold(imageMatIn, imageMatOut, 100, 255, Imgproc.THRESH_BINARY);

        return imageMatOut;
    }

    private Mat blackAndWhiteImageAdaptive(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        Imgproc.adaptiveThreshold(
                imageMatIn,
                imageMatOut,
                255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY,
                5,
                2);

        return imageMatOut;
    }
}
