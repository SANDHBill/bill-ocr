package com.sandh.billanalyzer.utility;

import org.opencv.core.Mat;
import com.sandh.billanalyzer.utility.Utility.fileType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by hamed on 27/12/2015.
 */
public interface ProcessMaterial {

    String getTransformerName();

    String getAsString();

    Object getAsObject();

    ImageFilter getImageFilter();

    default fileType mimeType() {
        fileType mime=fileType.UNKOWN;

        if(getAsObject()!=null && getAsObject() instanceof Mat){
            mime=fileType.IMAGE;
        }else if(getAsString()!=null){
            mime=fileType.TEXT;
        }
        return mime;

    }

    default InputStream getInputStream(){
        fileType mime= mimeType();
        switch (mime){
            case IMAGE: return Utility.matToInputStream((Mat)getAsObject());
            case TEXT: return new ByteArrayInputStream(getAsString().getBytes());
            default: return null;
        }
    }
}
