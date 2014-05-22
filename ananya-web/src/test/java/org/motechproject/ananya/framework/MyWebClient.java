package org.motechproject.ananya.framework;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MyWebClient {

    private WebClient webClient = new WebClient();

    public Page getPage(String url) throws IOException {
    	return webClient.getPage(url);
    }

    public Page post(String url, PostParam... params) throws IOException {
        WebRequest request = new WebRequest(new URL(url), HttpMethod.POST);
        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (PostParam param : params) {
            parameters.add(param.toNameValuePair());
        }
        request.setRequestParameters(parameters);
        return webClient.getPage(request);
    }

    public static class PostParam {
        public String name;
        public String value;

        public PostParam(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static PostParam param(String name, String value) {
            return new PostParam(name, value);
        }

        public NameValuePair toNameValuePair() {
            return new NameValuePair(name, value);
        }
    }
}
