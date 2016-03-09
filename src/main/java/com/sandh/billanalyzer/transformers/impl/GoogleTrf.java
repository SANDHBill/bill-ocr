package com.sandh.billanalyzer.transformers.impl;

import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.sandh.billanalyzer.transformers.Transformer;
import com.sandh.billanalyzer.utility.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;

/**
 * Created by hamed on 24/02/2016.
 */
public class GoogleTrf implements Transformer<Mat, String> {
    private static final String API_KEY = "AIzaSyA72PDph8o5KeihU1mf1mTdNG3o5gjNJg4";

    @Override
    public String transform(Mat input, String... params) {
        HttpResponse<JsonNode> jsonResponse=null;
        try {
            InputStream in = Utility.matToInputStream(input);
            JsonNode imageJason = createJason(in);
            Unirest.setTimeouts(1000,30000);
            jsonResponse = Unirest
                    .post("https://vision.googleapis.com/v1/images:annotate")
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .queryString("key", API_KEY)
                    .body(imageJason)
                    .asJson();


        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> parsed = JsonPath.parse(jsonResponse.getBody().toString()).read("$..description");
        String description=parsed.get(0);
        return description;
    }

    public JsonNode createJason(InputStream in) throws IOException {


        byte[] imageByte = IOUtils.toByteArray(in);
        String imageBase64 = Base64.getEncoder().encodeToString(imageByte);

        JSONObject js = new JSONObject();
        js.put("requests", new JSONArray().put(
                new JSONObject()
                        .put("image", new JSONObject().put("content", imageBase64))
                        .put("features",
                                new JSONArray().put(
                                        new JSONObject().put("type", "TEXT_DETECTION")
                                                .put("maxResults", 1)

                                )
                        )
                )
        );

        JsonNode jn = new JsonNode(js.toString());
        return jn;
    }


}
