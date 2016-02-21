package com.sandh.billanalyzer.transformers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by hamed on 21/02/2016.
 */
public class TransformerMachine {

    Map<String,Transformer> registeredTransformers = new ConcurrentHashMap<String,Transformer>();

    public TransformerMachine(){
        loadTransformers();
    }
    public Object process(String transformerName,Object input){
        Transformer transformer = registeredTransformers.get(transformerName);
        if(transformer!=null){
            return transformer.transform(input);
        }else{
            throw new RuntimeException("Transformer["+transformerName+"] not registered.");
        }
    }

    private void loadTransformers(){
        List transformersList = findAvailableTransformers();

        transformersList.forEach(obj -> {
            Transformer t =(Transformer)obj;
            registeredTransformers.put(t.getClass().getName(),t);
        });

    }




    private List findAvailableTransformers(){
        List transformerClasses=new ArrayList();
        try {
            Class[] clazzes=getClasses("com.sandh.billanalyzer.transformers");
            transformerClasses =
                    Arrays.stream(clazzes).filter(c->
                            Transformer.class.isAssignableFrom(c) && !c.isInterface() )
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
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
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
    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
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
}
