package com.sandh.billanalyzer.utility;

import com.sandh.billanalyzer.transformers.TransformerMachine;
import org.opencv.core.*;
import java.util.LinkedList;


/**
 * Created by hamed on 02/12/2015.
 */
public class ImageFilter extends AbstractTraceableOperator {

    private TransformerMachine transformerMachine = new TransformerMachine();


    private volatile boolean used=false;
    private Mat imageMat=null;



    public ImageFilter(Mat inputImage) {
        super();
        this.imageMat = inputImage;
    }

    private ImageFilter(ImageFilter parent){
        super(parent);
        Object input = parent.getOutput().getAsObject();
        if(input instanceof Mat){
            imageMat=(Mat)input;
        }

    }


    public ImageFilter apply(String transformerName,String...params){
        proccessPreFileterActions(transformerName);

        Object out =
                transformerMachine.process(transformerName,this.imageMat,params).getProccessedObject();

        return processPostFilterActions(transformerName,out);
    }


    private ImageFilter processPostFilterActions(String operation,Object result) {
        final ImageFilter thisFilter = this;
        this.output = new ProcessMaterial() {
            @Override
            public String getAsString() {
                String outPutAsString=null;

                if(result instanceof String) {
                    outPutAsString = (String)result;
                }

                return outPutAsString;
            }

            @Override
            public Object getAsObject() {
                return result;
            }

            @Override
            public ImageFilter getImageFilter() {
                return thisFilter;
            }
        };
        return new ImageFilter(this);
    }

    private  synchronized void proccessPreFileterActions(String pFilterName) {
        if(used) {
            throw new RuntimeException("Filter has been used. No new operation is permitted.");
        }
        filterName=pFilterName;

        used=true;
    }


}
