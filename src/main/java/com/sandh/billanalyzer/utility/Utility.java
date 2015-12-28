package com.sandh.billanalyzer.utility;

import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;


/**
 * Created by hamed on 02/12/2015.
 */
public class Utility {
    public static final String TEST_RECEIPTS_OUTPUT ="TEST_RECEIPTS_OUTPUT";

    private static Logger LOG = LoggerFactory.getLogger(Utility.class.getName());


    public static void storeImageMatInTempFile(Mat imageMat,
                                           TraceableOperator traceableOperator,
                                               String prefix){

        File file =storeImageStreamInTempFile(
                matToInputStream(imageMat),
                traceableOperator,prefix,"png");
    }
    public static void storeTextInTempFile(String text, TraceableOperator filter,String prefix) {
        File file =storeImageStreamInTempFile(
                new ByteArrayInputStream(text.getBytes()),
                filter,prefix,"txt");
    }
    public static File storeImageStreamInTempFile(
                                            InputStream imageStreamIn,
                                            TraceableOperator traceableOperator,
                                            String prefix,
                                            String extension) {

        String fileName = constructFileNameForOperationOutput(traceableOperator,prefix, extension);

        File tempFile = null;

        try {
            tempFile = Utility.getTempFile(fileName,"."+extension);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileOutputStream fos = new FileOutputStream(tempFile)){
            byte[] imageBytes = IOUtils.toByteArray(imageStreamIn);

            fos.write(imageBytes);
            fos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(traceableOperator.isDebugMode()) {
            LOG.info("File loc:{}", tempFile.getAbsolutePath());
        }else{
            tempFile.deleteOnExit();
        }
        return tempFile;
    }

    public static String constructFileNameForOperationOutput(TraceableOperator traceableOperator,
                                                             String prefix,
                                                             String extension) {

        String originalName = getFileName(traceableOperator.getOriginName());
        StringJoiner fileNameJoiner = new StringJoiner("_");
        fileNameJoiner.add(prefix);
        fileNameJoiner.add("input");
        fileNameJoiner.add(originalName);
        fileNameJoiner.add("output");
        fileNameJoiner.add(extension);
        fileNameJoiner.add(traceableOperator.getOperation());
        String fileName=fileNameJoiner.toString();

        return fileName;
    }

    public static String getFileName(String filePath) {
        Path path= Paths.get(filePath);

        return path.getFileName().toString();
    }

    public static  File getTempFile(String fileName,
                                    String extension) throws IOException {
        String debugOutputFolder = System.getProperty(TEST_RECEIPTS_OUTPUT);

        File dir = null;
        File tempFile = null;
        if(null != debugOutputFolder){
            Path path = Paths.get(debugOutputFolder);
            path.toFile().mkdirs();
            if (!Files.isDirectory(path)){
                Files.createDirectory(path);
            }
            dir = path.toFile();
            tempFile = File.createTempFile(fileName,extension,dir);
        }else{
            tempFile = File.createTempFile(
                    fileName, extension, null);
        }


        return tempFile;
    }


    public static Mat readInputStreamIntoMat(InputStream inputStream) throws IOException {
        byte[] temporaryImageInMemory = IOUtils.toByteArray(inputStream);
        // Decode into mat. Use any IMREAD_ option that describes your image appropriately
        Mat outputImage = Imgcodecs.imdecode(new MatOfByte(temporaryImageInMemory), Imgcodecs.IMREAD_COLOR);
        return outputImage;
    }

    public static InputStream matToInputStream(Mat imageMatIn) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", imageMatIn, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new ByteArrayInputStream(buffer.toArray());
    }


}
