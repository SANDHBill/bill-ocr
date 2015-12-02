package com.sandh.billanalyzer.utility;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.StringJoiner;


/**
 * Created by hamed on 02/12/2015.
 */
public class ImageFilter extends AbstractTraceableOperator {

    private volatile boolean used=false;
    private Mat imageMat;



    private  Mat imageMatOut;
    private final StringJoiner history
            =new StringJoiner(",","[","]");

    public Mat getImageMat() {
        if(used) {
            return imageMatOut.clone();
        }else{
            return imageMat.clone();
        }
    }
    private ImageFilter(Mat inputImage,
                        StringJoiner history) {
        this.imageMat = inputImage;
        if(null!=history) {
            this.history.merge(history);
        }
    }
    private ImageFilter(Mat inputImageMat) {
        this(inputImageMat,null);
    }
    private ImageFilter(
            ImageFilter parentImageTransformerFilter) {
        this(parentImageTransformerFilter.imageMatOut,
                parentImageTransformerFilter.history);
        this.setDebugMode(parentImageTransformerFilter.isDebugMode());
        this.setOriginName(parentImageTransformerFilter.getOriginName());

    }

    public static ImageFilter createFilterForMat(Mat inputImageMat){
        ImageFilter imageTransformerFilter =
                new ImageFilter(inputImageMat,null);

        return  imageTransformerFilter;
    }


    public ImageFilter convertToGrayScale(){
        proccessPreFileterActions();

        this.imageMatOut = convertToGrayScale(this.imageMat);

        return processPostFilterActions("convertToGrayScale");
    }

    private Mat convertToGrayScale(Mat imageMatIn) {
        Mat imageMatOut = new Mat();
        Imgproc.cvtColor(imageMatIn, imageMatOut, Imgproc.COLOR_BGR2GRAY, 1);
        return imageMatOut;
    }

    public ImageFilter blackAndWhiteImage(){
        proccessPreFileterActions();

        this.imageMatOut = blackAndWhiteImageBinary(this.imageMat);

        return processPostFilterActions("blackAndWhiteImage");
    }

    private Mat blackAndWhiteImageBinary(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        Imgproc.threshold(imageMatIn, imageMatOut, 100, 255, Imgproc.THRESH_BINARY);

        return imageMatOut;
    }

    public ImageFilter gaussianBlur(){
        proccessPreFileterActions();

        this.imageMatOut = gaussianBlur(this.imageMat);

        return processPostFilterActions("gaussianBlure");
    }
    private Mat gaussianBlur(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        Imgproc.GaussianBlur(imageMatIn, imageMatOut, new Size(11, 11), 0);
        return imageMatOut;
    }

    public ImageFilter blackAndWhiteImageAdaptive(){
        proccessPreFileterActions();

        this.imageMatOut = blackAndWhiteImageAdaptive(this.imageMat);

        return processPostFilterActions("blackAndWhiteImageAdaptive");
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



    private ImageFilter processPostFilterActions(String operation) {
        this.lastOperation = operation;
        history.add(lastOperation);

        if(isDebugMode()){
            Utility.storeImageMatInTempFile(this.imageMatOut, this);
        }

        return new ImageFilter(this);
    }
    private  synchronized void proccessPreFileterActions() {
        if(used) {
            throw new RuntimeException("Filter has been used. No new operation is permitted.");
        }

        used=true;
    }
}
