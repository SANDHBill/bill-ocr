package com.sandh.billanalyzer.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hamed on 27/02/2016.
 */
public class TransformParameters {
    Map<String,String> params = new HashMap<>();

    public TransformParameters(String[] paramsArray) {
        if(paramsArray!=null) {
            extractParams(paramsArray);
        }
    }

    private void extractParams(String[] paramsArray) {
        params=Arrays.stream(paramsArray)
                .map(param ->param.split(":"))
                .collect(Collectors.toMap(e->e[0],e->e[1]));
    }

    public int getInt(String key) {
        String value = getString( key);
        return Integer.parseInt(value);
    }

    public double getDouble(String key) {
        String value = getString( key);
        return Double.parseDouble(value);
    }

    public String getString(String key){
        String value=params.get(key);
        if(value!=null){
            return value;
        }else{
            throw new RuntimeException("Param "+key+" not registered");
        }
    }
}
