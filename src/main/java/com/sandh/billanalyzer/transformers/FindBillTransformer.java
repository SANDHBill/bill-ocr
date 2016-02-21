package com.sandh.billanalyzer.transformers;

/**
 * Created by hamed on 21/02/2016.
 */
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FindBillTransformer implements Transformer<Mat,Mat> {

    @Override
    public Mat transform(Mat input) {
        return findBill(input);
    }
    private Mat findBill(Mat imageMat){
        Mat orig = new Mat();
        Imgproc.cvtColor(imageMat, orig, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(orig, orig, 127, 255, 0);


        Float[] xs = findX(imageMat);
        Float[] ys = findY(imageMat);

        return crop_2(imageMat,xs[0].intValue(),xs[1].intValue(), ys[0].intValue(),ys[1].intValue());
    }
    public int crop_area(Mat imageMat){
        return imageMat.width() * imageMat.height();
    }

    private float ratio(Mat im) {
        //im = cv2.cvtColor(im, cv2.COLOR_BGR2GRAY)
        //_, im = cv2.threshold(im, 127, 255, 0)
        int nzCount = Core.countNonZero(im);
        int area = crop_area(im);
        return nzCount / (float)(area - nzCount);
    }
    private Mat crop_2(Mat im,int x,int xw,int y,int yh) {
        Mat crop_img = im.submat(y,yh, x,xw);
        return crop_img;
    }

    private Float[] findX(Mat orig1) {
        int height = orig1.height();
        int width = orig1.width();
        //int channels = orig1.channels();

        Mat orig = new Mat();
        Imgproc.cvtColor(orig1, orig, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(orig, orig, 127, 255, 0);
        int w_n = 50;
        int h_n = 50;
        int w_st = width / w_n;
        int h_st = height / h_n;

        int yh = height;
        int y = 0;
        int xw = w_st;
        int x = 0;

        List<Float> rs = new ArrayList<Float>();
        int count = 0;
        Mat crop = crop_2(orig, x, xw, y, yh);
        float r = ratio(crop);
        rs.add(r);
        while ((x < width - w_st*2)) {
            x = x + w_st;
            xw = xw + w_st;
            crop = crop_2(orig, x, xw, y, yh);
            r = ratio(crop);
            rs.add(r);
            count = count + 1;
        }
        DescriptiveStatistics stats = new DescriptiveStatistics();

        // Add the data from the array
        for( int i = 0; i < rs.size(); i++) {
            stats.addValue(rs.get(i));
        }

// Compute some statistics
        //double mean = stats.getMean();
        //double std = stats.getStandardDeviation();
        double median = stats.getPercentile(50);

        int[] indexes = IntStream.range(0, rs.size())
                .filter(i -> rs.get(i) > median)
                .toArray();

        //List<Float> indexes = rs.stream().filter( t -> t > median).collect(Collectors.toList());

        Float index = (float)indexes[0];
        Float index1 = index * w_st;
        Float index2 = (float)indexes[indexes.length-1] ;
        index2 = index2 * w_st;
        return new Float[] {index1,index2};
    }

    private Float[] findY(Mat orig1) {
        int height = orig1.height();
        int width = orig1.width();
        //int channels = orig1.channels();

        Mat orig = new Mat();
        Imgproc.cvtColor(orig1, orig, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(orig, orig, 127, 255, 0);
        int w_n = 50;
        int h_n = 50;
        int w_st = width / w_n;
        int h_st = height / h_n;

        int yh = h_st;
        int y = 0;
        int xw = width;
        int x = 0;

        List<Float> rs = new ArrayList<Float>();
        int count = 0;
        Mat crop = crop_2(orig, x, xw, y, yh);
        float r = ratio(crop);
        rs.add(r);
        while ((y < height - h_st*2)) {
            y = y + h_st;
            yh = yh + h_st;
            crop = crop_2(orig, x, xw, y, yh);
            r = ratio(crop);
            rs.add(r);
            count = count + 1;
        }
        DescriptiveStatistics stats = new DescriptiveStatistics();

        // Add the data from the array
        for( int i = 0; i < rs.size(); i++) {
            stats.addValue(rs.get(i));
        }

// Compute some statistics
        //double mean = stats.getMean();
        //double std = stats.getStandardDeviation();
        double median = stats.getPercentile(50);

        int[] indexes = IntStream.range(0, rs.size())
                .filter(i -> rs.get(i) > median)
                .toArray();


        //List<Float> indexes = rs.stream().filter( t -> t > median).collect(Collectors.toList());

        Float index = (float)indexes[0];
        Float index1 = index * h_st;
        Float index2 = (float)indexes[indexes.length-1] ;
        index2 = index2 * h_st;
        return new Float[] {index1,index2};
    }


    private Mat findBillOld(Mat imageMatIn){
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
}
