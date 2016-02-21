package com.sandh.billanalyzer.transformers;

/**
 * Created by hamed on 21/02/2016.
 */
public interface Transformer<FROM,TO> {

    TO transform(FROM input);
}
