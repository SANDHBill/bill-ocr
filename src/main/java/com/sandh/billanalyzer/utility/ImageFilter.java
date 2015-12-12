package com.sandh.billanalyzer.utility;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
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
    public ImageFilter clearSmallBlackDots(int contourSize, double threshold){
        proccessPreFileterActions();

        this.imageMatOut = clearSmallBlackDots(this.imageMat, contourSize, threshold);

        return processPostFilterActions("clearSmallBlackDots:contourSize "+contourSize+" threshold "+threshold);
    }
    private Mat clearSmallBlackDots(Mat imageMatIn,int contourSize, double threshold){
        Mat imageMatOut = new Mat();
        imageMatIn.copyTo(imageMatOut);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(imageMatIn, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);;
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
    private Mat morphologicalOperations(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        //Mat kernel = Mat.ones(3, 3, CvType.CV_8UC1);
        //System.out.println("Kernel Mat"+kernel.dump());
        //Imgproc.dilate(imageMatIn, imageMatOut, kernel, new Point(-1,-1),1);
        //Imgproc.threshold(imageMatOut, imageMatOut, 220, 255, Imgproc.THRESH_BINARY );
        //Imgproc.dilate(imageMatOut, imageMatOut, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        //Imgproc.erode(imageMatIn, imageMatOut, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8,8)));
        Imgproc.erode(imageMatIn, imageMatOut, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
        //Imgproc.dilate(imageMatOut, imageMatOut, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4,4)));
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
