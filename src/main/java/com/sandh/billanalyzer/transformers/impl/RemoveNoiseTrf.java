package com.sandh.billanalyzer.transformers.impl;

/**
 * Created by hamed on 21/02/2016.
 */

import com.sandh.billanalyzer.transformers.Transformer;
import com.sandh.billanalyzer.utility.TransformParameters;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class RemoveNoiseTrf implements Transformer<Mat,Mat> {

    public static final String CONTOUR_SIZE="ContourSize";
    public static final String THRESHOLD="Threshold";


    @Override
    public Mat transform(Mat input, String... params) {
        TransformParameters transformParameters= new TransformParameters(params);

        Mat outPut=clearSmallBlackDots(
                input,
                transformParameters.getInt(CONTOUR_SIZE),
                transformParameters.getDouble(THRESHOLD));
        return outPut;
    }

    private Mat clearSmallBlackDots(Mat imageMatIn,int contourSize, double threshold){
        Mat imageMatOut = new Mat();
        imageMatIn.copyTo(imageMatOut);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(imageMatIn, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for(MatOfPoint contour : contours)
        {
            Rect R = Imgproc.boundingRect(contour);
            if( R.width*R.height < contourSize )
            {
                Mat roi = new Mat(imageMatIn,R);
                if (Core.countNonZero(roi) < R.width*R.height*threshold ) {
                    Imgproc.rectangle(imageMatOut, R.tl(), R.br(), new Scalar(255, 255, 255));
                    Mat croi = new Mat(imageMatOut, R);
                    croi.setTo(new Scalar(255, 255, 255)); // this line is to clear small dots
                }
            }
        }
        return imageMatOut;
    }
}
