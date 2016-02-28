package com.sandh.billanalyzer.transformers.impl;

/**
 * Created by hamed on 21/02/2016.
 */

import com.sandh.billanalyzer.transformers.Transformer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class DetectLinesTrf implements Transformer<Mat,Mat> {

    @Override
    public Mat transform(Mat input, String... params) {
        Mat outPut=findLines(input);
        return outPut;
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
    double angle(Point pt1, Point pt2, Point pt0 ) {
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
}
