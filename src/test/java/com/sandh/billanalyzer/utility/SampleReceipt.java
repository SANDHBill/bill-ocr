package com.sandh.billanalyzer.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by hamed on 01/12/2015.
 */
public class SampleReceipt {
    private final URL imageURL;
    private final URL textURL;
    private String result;
    private InputStream imageInputStream;
    private StringBuffer resultProccessingParameters = new StringBuffer();

    public void setHistory(FilterHistory history) {
        this.history = history;
    }

    public FilterHistory getHistory() {
        return history;
    }

    private FilterHistory history;


    public SampleReceipt(URL imageUrl, URL textUrl) {
        this.imageURL = imageUrl;
        this.textURL = textUrl;
    }

    public String getImageName() {
        return imageURL.getFile();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public InputStream getImageInputStream() throws IOException {
        return imageURL.openStream();
    }

    public String getResultProccessingParameters() {
        return resultProccessingParameters.toString();
    }

    public void addProccessingParameters(String proccessingParameters) {
        resultProccessingParameters.append(proccessingParameters);
        resultProccessingParameters.append(System.lineSeparator());
    }
}
