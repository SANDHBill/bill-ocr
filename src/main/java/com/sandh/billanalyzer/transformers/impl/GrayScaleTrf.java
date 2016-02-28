package com.sandh.billanalyzer.transformers.impl;

/**
 * Created by hamed on 21/02/2016.
 */

import com.sandh.billanalyzer.transformers.Transformer;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class GrayScaleTrf implements Transformer<Mat,Mat> {

    @Override
    public Mat transform(Mat input, String... params) {
        Mat outPut=convertToGrayScale(input);
        return outPut;
    }

    private Mat convertToGrayScale(Mat imageMatIn) {
        Mat imageMatOut = new Mat();
        Imgproc.cvtColor(imageMatIn, imageMatOut, Imgproc.COLOR_BGR2GRAY, 1);
        return imageMatOut;
    }
}
