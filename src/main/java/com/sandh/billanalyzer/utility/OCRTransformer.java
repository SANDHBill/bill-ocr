package com.sandh.billanalyzer.utility;

import com.sandh.billanalyzer.transformers.TransformerMachine;
import org.opencv.core.Mat;

import java.io.IOException;

import static org.bytedeco.javacpp.lept.pixRead;

/**
 * Created by hamed on 02/12/2015.
 */
public class OCRTransformer extends AbstractTraceableOperator implements Transformer {
    private TransformerMachine transformerMachine = new TransformerMachine();


    private ProcessMaterial processMaterial;

    public OCRTransformer(FilterHistory history){
        this.setHistory(history);
    }

    @Override
    public String transform(ImageFilter imageFilter) throws IOException {
        final String item;

        this.setOriginName(imageFilter.getOriginName());
        this.setDebugMode(imageFilter.isDebugMode());

        Mat imageMat = imageFilter.getImageMat();
        //item = applyOCRToImage(imageMat)[0];
        TraceableProccessedObject obj = transformerMachine.process(
                com.sandh.billanalyzer.transformers.impl.OCRTransformer.class.getName()
                ,imageMat
        );

        item=(String)obj.getProccessedObject();

        processMaterial = new ProcessMaterial() {
            @Override
            public String getAsString() {
                return item;
            }

            @Override
            public ImageFilter getAsImageFilter() {
                return null;
            }
        };
        this.lastOperation="OCRTransforming";
        getHistory().add(this);
        return item;
    }



    @Override
    public ProcessMaterial getProcessMaterial() {
        return processMaterial;
    }
}
