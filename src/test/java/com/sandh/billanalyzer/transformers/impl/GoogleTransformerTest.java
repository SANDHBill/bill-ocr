package com.sandh.billanalyzer.transformers.impl;

import com.mashape.unirest.http.JsonNode;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;

/**
 * Created by hamed on 25/02/2016.
 */
public class GoogleTransformerTest {

    @Test
    public void testCreateJason() throws Exception {
        ByteArrayInputStream byteIn = new ByteArrayInputStream("base64-encoded file data".getBytes());
        GoogleTrf googleTrf = new GoogleTrf();
        JsonNode imageJson = googleTrf.createJason(byteIn);

        Assert.notNull(imageJson);
    }
}