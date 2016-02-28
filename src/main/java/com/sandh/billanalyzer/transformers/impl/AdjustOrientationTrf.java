package com.sandh.billanalyzer.transformers.impl;

/**
 * Created by hamed on 21/02/2016.
 */

import com.sandh.billanalyzer.transformers.Transformer;
import org.opencv.core.Core;
import org.opencv.core.Mat;



public class AdjustOrientationTrf implements Transformer<Mat,Mat> {

    @Override
    public Mat transform(Mat input, String... params) {
        Mat outPut=orientImage(input);
        return outPut;
    }

    private Mat orientImage(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        boolean needRotation = doesImageNeedRotation(imageMatIn);
        int flipCode =1;

        if(needRotation){
            imageMatIn = imageMatIn.t();
            Core.flip(imageMatIn,imageMatOut,flipCode);
        }else{
            imageMatOut=imageMatIn.clone();
        }


        return imageMatOut;
    }
    private boolean doesImageNeedRotation(Mat imageMatIn) {
        int height = imageMatIn.height();
        int width = imageMatIn.width();

        return height<width;
    }
}
