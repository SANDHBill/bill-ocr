package com.sandh.billanalyzer.transformers.impl;

import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.JsonNode;
import org.junit.Test;
import org.junit.Assert;


import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by hamed on 25/02/2016.
 */
public class GoogleTransformerTest {
    String googleRespJson="{\n" +
            "  \"responses\": [\n" +
            "    {\n" +
            "      \"textAnnotations\": [\n" +
            "        {\n" +
            "          \"locale\": \"it\",\n" +
            "          \"description\": \"Da Mar 10\\n15 Gloucester Road\\nKensington\\nSW7 4PP\\n020 7584 9078\\n26/09/2015\\nServer Shahedu\\n11:20 PM\\nTable 31/1\\n20177\\nGuests: 7\\nPinot Grigio Cantarelle (5 @22. 50\\n112. 50\\n18.40\\nGamberoni Aglio Olio (2 09. 20)\\n15.80\\nFantasia di Bufala (2 07.90)\\n15.80\\nCosa Nostra Salad (2 07. 90)\\n4.90\\nBruschetta Pomodoro\\n9.00\\nGarlic Bread (2 04. 50)\\n15.90\\nGrill Sea Bass\\n31.80\\na SPAG\\nNERO SEPPIA (2 015. 90)\\n9.40\\nPaccher i Arrabiata\\n10.20\\nRigatoni Broccoli\\n10.60\\nRigatoni Da Mario\\n9.90\\nAmerican Hot Pizza\\n10.50\\nCapricciosa Pizza\\n10.20\\nMario Pizza\\n10.50\\nCapricciosa Pizza\\nXT Soft Egg\\n1.30\\nCoke Diet\\n26 Items\\nTotal\\n299. 65\\nOptional Gratuity 12. 50%\\n37.46\\nTotal\\n337. 11\\nBalance Due\\n337- 1 1\\n\",\n" +
            "          \"boundingPoly\": {\n" +
            "            \"vertices\": [\n" +
            "              {\n" +
            "                \"x\": 267\n" +
            "              },\n" +
            "              {\n" +
            "                \"x\": 2079\n" +
            "              },\n" +
            "              {\n" +
            "                \"x\": 2079,\n" +
            "                \"y\": 3228\n" +
            "              },\n" +
            "              {\n" +
            "                \"x\": 267,\n" +
            "                \"y\": 3228\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void testCreateJson() throws Exception {
        ByteArrayInputStream byteIn = new ByteArrayInputStream("base64-encoded file data".getBytes());
        GoogleTrf googleTrf = new GoogleTrf();
        JsonNode imageJson = googleTrf.createJason(byteIn);

        Assert.assertNotNull(imageJson);
    }

    @Test
    public void testParseResponseJson(){
        List<String> parsed = JsonPath.parse(googleRespJson).read("$..locale");
        String locale=parsed.get(0);
        Assert.assertEquals("it",locale);
    }
}