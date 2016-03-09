package com.sandh.billanalyzer.transformers.impl;

import com.sandh.billanalyzer.transformers.Transformer;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by hamed on 08/03/2016.
 */
public class GIFTrf  implements Transformer<Mat, InputStream> {

    public static InputStream matToInputStream(Mat imageMatIn) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", imageMatIn, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new ByteArrayInputStream(buffer.toArray());
    }

    @Override
    public InputStream transform(Mat input, String... params) {
        return matToInputStream(input);
    }
}
