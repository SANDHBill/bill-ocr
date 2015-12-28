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
    private ProcessMaterial outputMaterial;


    public Mat getImageMat() {
        if(used) {
            return imageMatOut.clone();
        }else{
            return imageMat.clone();
        }
    }
    private ImageFilter(Mat inputImage,
                        FilterHistory filterHistory) {
        this.imageMat = inputImage;

        setHistory(filterHistory);
    }
    private ImageFilter(Mat inputImageMat) {
        this(inputImageMat,new FilterHistory());
    }
    private ImageFilter(
            ImageFilter parentImageTransformerFilter) {
        this(parentImageTransformerFilter.imageMatOut,
                parentImageTransformerFilter.getHistory());
        this.setDebugMode(parentImageTransformerFilter.isDebugMode());
        this.setOriginName(parentImageTransformerFilter.getOriginName());

    }

    public static ImageFilter createFilterForMat(Mat inputImageMat){
        ImageFilter imageTransformerFilter =
                new ImageFilter(inputImageMat);

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

        return processPostFilterActions("clearSmallBlackDots_contourSize "+contourSize+" threshold "+threshold);
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
    public ImageFilter findBill(){
        proccessPreFileterActions();

        this.imageMatOut = findBill(this.imageMat);

        return processPostFilterActions("findBill");
    }
    //The input of this function needs to be gray
    private Mat findBill(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        imageMatIn.copyTo(imageMatOut);

        Mat imageMatOut2 = new Mat();
        imageMatIn.copyTo(imageMatOut2);

        double largest_area=0;
        int largest_contour_index=0;
        Rect bounding_rect = new Rect();

        Imgproc.threshold(imageMatIn, imageMatIn, 25, 255, Imgproc.THRESH_BINARY); //Threshold the gray

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        //imageMatIn.adjustROI(1,1,1,1);
        Imgproc.findContours(imageMatIn, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        int i = 0;
        double ar = .9 * imageMatOut2.width()*imageMatOut2.height();

        for(MatOfPoint contour : contours)
        {
            i++;
            double a=Imgproc.contourArea(contour, false);  //  Find the area of contour
            if(a>largest_area && a < ar){
                largest_area=a;
                largest_contour_index=i;                //Store the index of largest contour
                bounding_rect=Imgproc.boundingRect(contour); // Find the bounding rectangle for biggest contour
            }

        }

        //Scalar color( 255,255,255);
        //drawContours( dst, contours,largest_contour_index, color, CV_FILLED, 8, hierarchy ); // Draw the largest contour using previously stored index.
        Imgproc.rectangle(imageMatOut, bounding_rect.tl(), bounding_rect.br(), new Scalar(0, 255, 0), 1, 8, 0);

        imageMatOut = Mat.zeros(imageMatOut2.size(), imageMatOut.channels());
        System.out.println("shahram:"+largest_contour_index+" there are "+contours.size());

        Mat mask_image = new Mat( imageMatOut.size(), CvType.CV_8U, new Scalar(0,0,0));
        Imgproc.drawContours(mask_image, contours, largest_contour_index, new Scalar(0, 255, 0),  -1);
// copy only non-zero pixels from your image to original image
        imageMatOut2.copyTo(imageMatOut, mask_image);

        //Imgproc.drawContours(imageMatOut, contours,largest_contour_index, new Scalar(0, 255, 0),  -1);

        Mat cropped = new Mat(imageMatOut2, new Rect(bounding_rect.x, bounding_rect.y,bounding_rect.width,bounding_rect.height));

        //Mat imageMatOut3 = imageMatOut2(bounding_rect);
        return cropped;
    }

    public ImageFilter detectLines(){
        proccessPreFileterActions();

        this.imageMatOut = findLines(this.imageMat);

        return processPostFilterActions("findLines");
    }

    private Mat findLines(Mat imageMatIn) {
        Mat imageMatOut = new Mat();
        Mat imageMatOut2 = new Mat();
        imageMatIn.copyTo(imageMatOut2);

        Imgproc.Canny(imageMatIn, imageMatOut, 50, 200, 3, true);
        Mat lines = new Mat();
        int threshold = 80;
        int minLineSize = 30;
        int lineGap = 10;

        Imgproc.HoughLinesP(imageMatOut, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);

        for (int x = 0; x < lines.rows(); x++)
        {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            double dx = x1 - x2;
            double dy = y1 - y2;

            double dist = Math.sqrt (dx*dx + dy*dy);

            if(dist>300.d)  // show those lines that have length greater than 300
                Imgproc.line(imageMatOut2, start, end, new Scalar(0, 0, 255),  1);

        }
        return imageMatOut2;
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
        getHistory().add(this);
        final ImageFilter thisFilter = this;
        outputMaterial = new ProcessMaterial() {
            @Override
            public String getAsString() {
                return null;
            }

            @Override
            public ImageFilter getAsImageFilter() {
                return thisFilter;
            }
        };
        return new ImageFilter(this);
    }

    private  synchronized void proccessPreFileterActions() {
        if(used) {
            throw new RuntimeException("Filter has been used. No new operation is permitted.");
        }

        used=true;
    }

    @Override
    public ProcessMaterial getProcessMaterial() {
        return outputMaterial;
    }
}
