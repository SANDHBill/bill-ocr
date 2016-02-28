package com.sandh.billanalyzer.transformers;

import com.sandh.billanalyzer.utility.TraceableProccessedObject;

/**
 * Created by hamed on 21/02/2016.
 */
public interface Transformer<FROM, TO> {

    TO transform(FROM input, String...params);
}
