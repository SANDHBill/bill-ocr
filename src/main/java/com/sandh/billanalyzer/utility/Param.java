package com.sandh.billanalyzer.utility;

/**
 * Created by hamed on 27/02/2016.
 */
public class Param {
    public static String entry(String key,String value){
        return key+":"+value;
    }
    public static String entry(String key,int value){
        return key+":"+value;
    }
    public static String entry(String key,double value){
        return key+":"+value;
    }

}
