package com.sandh.billanalyzer.utility;

import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by hamed on 02/12/2015.
 */
public class Utility {
    public static final String TEST_RECEIPTS_OUTPUT ="TEST_RECEIPTS_OUTPUT";

    public enum fileType{
        UNKOWN("unknown"),IMAGE("png"),TEXT("txt");

        private String value;
        fileType(String pvalue){
            value=pvalue;
        }

        public String asString(){
            return value;
        }
    }
    private static Logger LOG = LoggerFactory.getLogger(Utility.class.getName());


    public static void storeOuputInTempFile(
            TraceableOperator traceableOperator,
            String prefix){

        String fileName = constructFileNameForOperationOutput(traceableOperator,prefix);
        InputStream imageStreamIn=traceableOperator.getOutput().getInputStream();

        File file =storeStreamInTempFile(
                imageStreamIn,
                fileName,
                traceableOperator.getOutput().mimeType().asString(),
                traceableOperator.isDebugMode());

    }

    public static File storeStreamInTempFile(InputStream is,
                                             String fileName,
                                             String extension,
                                             boolean keepFile) {

        File tempFile = null;

        try {
            tempFile = Utility.getTempFile(fileName,"."+extension);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileOutputStream fos = new FileOutputStream(tempFile)){
            byte[] imageBytes = IOUtils.toByteArray(is);

            fos.write(imageBytes);
            fos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(keepFile) {
            LOG.info("File loc:{}", tempFile.getAbsolutePath());
        }else{
            tempFile.deleteOnExit();
        }
        return tempFile;
    }

    public static String constructFileNameForOperationOutput(TraceableOperator traceableOperator,
                                                             String prefix) {

        String originalName = getFileName(traceableOperator.getOriginName());
        StringJoiner fileNameJoiner = new StringJoiner("_");
        fileNameJoiner.add(prefix);
        fileNameJoiner.add("input");
        fileNameJoiner.add(originalName);
        fileNameJoiner.add("output");
        fileNameJoiner.add(traceableOperator.getFilterName());
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


    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = (URL)resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    public static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static List findClassImplmentingInterface(String interfaceName){
        List transformerClasses=new ArrayList();
        try {
            Class[] clazzes= Utility.getClasses(interfaceName);
            transformerClasses =
                    Arrays.stream(clazzes).filter(c->
                            com.sandh.billanalyzer.transformers.Transformer.class.isAssignableFrom(c) && !c.isInterface() )
                            .map(c -> {
                                Object obj = null;

                                try {
                                    obj= c.newInstance();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                return obj;

                            }).collect(Collectors.toList());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transformerClasses;
    }

}
