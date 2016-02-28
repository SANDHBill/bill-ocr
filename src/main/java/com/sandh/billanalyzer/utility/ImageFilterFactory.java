package com.sandh.billanalyzer.utility;

import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by hamed on 02/12/2015.
 */
public class ImageFilterFactory {

    public static ImageFilter createFilterForMat(Mat inputImageMat){

        return  new ImageFilter(inputImageMat);
    }

    public static ImageFilter createFilterForInputStream(InputStream inputImageStream)
            throws IOException {

        Mat imageMat = Utility.readInputStreamIntoMat(inputImageStream);

        return  createFilterForMat(imageMat);
    }

    public static ImageFilter createFilterForFile(File imageFile)
            throws IOException {

        URL imageURL = imageFile.toURI().toURL();
        Mat imageMat = Utility.readInputStreamIntoMat(imageURL.openStream());

        return  createFilterForMat(imageMat);
    }

    public static ImageFilter createFilterForURL(URL imageURL)
            throws IOException {

        Mat imageMat = Utility.readInputStreamIntoMat(imageURL.openStream());

        return  createFilterForMat(imageMat);
    }

}
