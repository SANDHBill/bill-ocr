package com.sandh.billanalyzer.transformers;

import com.sandh.billanalyzer.utility.AuditItem;
import com.sandh.billanalyzer.utility.ProcessMaterial;
import com.sandh.billanalyzer.utility.TraceableProccessedObject;
import com.sandh.billanalyzer.utility.Utility;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by hamed on 21/02/2016.
 */
public class TransformerMachine {

    public static final String COM_SANDH_BILLANALYZER_TRANSFORMERS = "com.sandh.billanalyzer.transformers.impl";
    Map<String,Transformer> registeredTransformers = new ConcurrentHashMap<String,Transformer>();

    public TransformerMachine(){
        loadTransformers();
    }

    public TraceableProccessedObject process(String transformerName,Object input,String...params){
        Transformer transformer = registeredTransformers.get(transformerName);
        if(transformer!=null){
            TraceableProccessedObject traceableProccessedObject=makeTraceableIfNotAlready(input);
            return internalProcess(transformer,traceableProccessedObject,params);
        }else{
            throw new RuntimeException("Transformer["+transformerName+"] not registered.");
        }
    }

    private TraceableProccessedObject makeTraceableIfNotAlready(Object input) {
        TraceableProccessedObject traceableObj =null;
        if( input instanceof TraceableProccessedObject){
            traceableObj=(TraceableProccessedObject)input;
        }else{
            traceableObj=new TraceableProccessedObject(input);
        }
        return traceableObj;
    }

    private TraceableProccessedObject internalProcess(Transformer transformer,
                                                      TraceableProccessedObject input,
                                                      String... params){

        Object processedObject = transformer.transform(input.getProccessedObject(),params);

        TraceableProccessedObject traceableResult =
                createAuditeableObject(
                        transformer.getClass().getName(),
                        processedObject,
                        input,
                        params);

        return traceableResult;

    }

    private void loadTransformers(){
        List transformersList = Utility.findClassImplmentingInterface(COM_SANDH_BILLANALYZER_TRANSFORMERS);

        transformersList.forEach(obj -> {
            Transformer t =(Transformer)obj;
            registeredTransformers.put(t.getClass().getName(),t);
        });

    }


    private TraceableProccessedObject createAuditeableObject(
            String operation,
            Object proccessedObject,
            TraceableProccessedObject parent,
            String...params) {
        AuditItem audit = new AuditItem(operation,params);
        TraceableProccessedObject tpo=new TraceableProccessedObject(parent,proccessedObject,audit);
        return tpo;

    }
}
