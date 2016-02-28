package com.sandh.billanalyzer.transformers.impl;

/**
 * Created by hamed on 21/02/2016.
 */

import com.sandh.billanalyzer.transformers.Transformer;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class BlurTrf implements Transformer<Mat,Mat> {

    @Override
    public Mat transform(Mat input, String... params) {
        Mat outPut=gaussianBlur(input);
        return outPut;
    }

    private Mat gaussianBlur(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        Imgproc.GaussianBlur(imageMatIn, imageMatOut, new Size(11, 11), 0);

        return imageMatOut;
    }
}
