package com.sandh.billanalyzer.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by hamed on 01/12/2015.
 */
public class SampleReceipt {
    private final URL imageURL;
    private final URL textURL;

    private String result;
    private StringBuffer resultProccessingParameters = new StringBuffer();

    public void setHistory(TraceableOperator[] history) {
        this.history = history;
    }

    public TraceableOperator[] getHistory() {
        return history;
    }

    private TraceableOperator[] history;


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

    public Reader getReferenceText() throws IOException{
        Reader textReader = new InputStreamReader(textURL.openStream());
        return  textReader;
    }

    public String getReferenceTextString() throws IOException {
        Path path = Paths.get(textURL.getPath());

        String textcontent = new String(Files.readAllBytes(path));

        return textcontent;
    }
}
