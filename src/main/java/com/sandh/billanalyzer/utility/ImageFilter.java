package com.sandh.billanalyzer.utility;

import com.sandh.billanalyzer.transformers.FindBillTransformer;
import com.sandh.billanalyzer.transformers.TransformerMachine;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


/**
 * Created by hamed on 02/12/2015.
 */
public class ImageFilter extends AbstractTraceableOperator {

    private TransformerMachine transformerMachine = new TransformerMachine();

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

    public ImageFilter getImageInfo(){
        proccessPreFileterActions();

        this.imageMatOut = this.imageMat.clone();
        int height = this.imageMatOut.height();
        int width = this.imageMatOut.width();

        String info = "Info["+height+" X "+width+" ] ";

        return processPostFilterActions(info);
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

    public ImageFilter orientImage(){
        proccessPreFileterActions();

        boolean needRotation = doesImageNeedRotation(this.imageMat);
        this.imageMatOut = orientImage(this.imageMat);

        return processPostFilterActions("orientImage["+needRotation+"]");
    }
    private Mat orientImage(Mat imageMatIn){
        Mat imageMatOut = new Mat();
        boolean needRotation = doesImageNeedRotation(this.imageMat);
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

    //The next t3 function coied from
    // http://stackoverflow.com/questions/8667818/opencv-c-obj-c-detecting-a-sheet-of-paper-square-detection
    // I had to use >= (c++), I did as suggested here
    // http://stackoverflow.com/questions/9783538/mat-logic-operator-in-opencv-2-3

    double angle( Point pt1, Point pt2, Point pt0 ) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
    }


    void find_squares(Mat imageMatIn)
    {
        List<MatOfPoint2f> squares = new ArrayList<MatOfPoint2f>();

        // blur will enhance edge detection
        Mat blurred = new Mat();
        imageMatIn.copyTo(blurred);
        Imgproc.medianBlur(imageMatIn, blurred, 9);

        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U);
        Mat gray = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        List<Mat> blurred_l =new ArrayList<Mat>();
        List<Mat> gray0_l =new ArrayList<Mat>();
        blurred_l.add(blurred);
        gray0_l.add(gray0);

        // find squares in every color plane of the image
        for (int c = 0; c < 3; c++)
        {
            int ch[] = {c, 0};
            MatOfInt fromto = new MatOfInt(ch);
            //Core.mixChannels(blurred, 1, gray0, 1, ch, 1);
            Core.mixChannels(blurred_l, gray0_l, fromto);

            // try several threshold levels
            int threshold_level = 2;
            for (int l = 0; l < threshold_level; l++)
            {
                // Use Canny instead of zero threshold level!
                // Canny helps to catch squares with gradient shading
                if (l == 0)
                {
                    Imgproc.Canny(gray0, gray, 10, 20, 3, true); //

                    // Dilate helps to remove potential holes between edge segments
                    Imgproc.dilate(gray, gray, new Mat(), new Point(-1,-1),1);
                    //Imgproc.dilate(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
                    //I
                }
                else
                {
                    Core.compare(gray0, new Scalar((l+1) * 255 / threshold_level), gray ,Core.CMP_GE);
                    //gray = gray0 >= (l+1) * 255 / threshold_level;
                }

                // Find contours and store them in a list
                Mat hierarchy = new Mat();
                Imgproc.findContours(gray, contours,hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                // Test contours
                MatOfPoint2f approx = new MatOfPoint2f();
                MatOfPoint approxf1 = new MatOfPoint();// very stupid solution
                for(MatOfPoint contour : contours)
                {
                    // approximate contour with accuracy proportional
                    // to the contour perimeter
                    MatOfPoint2f new_mat = new MatOfPoint2f( contour.toArray() );
                    Imgproc.approxPolyDP(new_mat, approx, Imgproc.arcLength(new_mat, true)*0.02, true);

                    approx.convertTo(approxf1, CvType.CV_32S);
                    // Note: absolute value of an area is used because
                    // area may be positive or negative - in accordance with the
                    // contour orientation
                    if (approx.total() == 4 &&
                            Math.abs(Imgproc.contourArea(approx)) > 1000 &&
                            Imgproc.isContourConvex(approxf1))
                    {
                        double maxCosine = 0;
                        Point[] points = approx.toArray();

                        for (int j = 2; j < 5; j++)
                        {
                            double cosine = Math.abs(angle(points[j%4], points[j-2], points[j-1]));
                            maxCosine = Math.max(maxCosine, cosine);
                        }

                        if (maxCosine < 0.3)
                            squares.add(approx);
                    }
                }
            }
        }
    }

    Mat debugSquares( List<MatOfPoint> squares, Mat image )
    {
        int i = 0;
        for(MatOfPoint square : squares) {
            // draw contour
            Imgproc.drawContours(image, squares, i, new Scalar(255,0,0), 1);
            //Imgproc.drawContours(image, squares, i, new Scalar(255,0,0), 1, 8, std::vector<cv::Vec4i>(), 0, new Point());

            // draw bounding rect
            Rect rect = Imgproc.boundingRect(square);
            Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0,255,0), 2, 8, 0);

            // draw rotated rect
            MatOfPoint2f square_f = new MatOfPoint2f(square);
            RotatedRect minRect = Imgproc.minAreaRect(square_f);
            Point[] rect_points = new Point[4];
            minRect.points( rect_points );
            for ( int j = 0; j < 4; j++ ) {
                Imgproc.line( image, rect_points[j], rect_points[(j+1)%4], new Scalar(0,0,255), 1); // blue
            }
            i++;
        }

        return image;
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

    public ImageFilter findBill(){
        proccessPreFileterActions();

        this.imageMatOut =
                (Mat)transformerMachine.process(FindBillTransformer.class.getName(),this.imageMat);

        return processPostFilterActions("findBill");
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
