package org.motechproject.ananya.framework;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.lang.reflect.Type;

public class JsonHttpClient {
    private Type responseClass;
    private HttpClient httpClient;
    private PostMethod postMethod;
    private Gson gson;

    public JsonHttpClient(String uri, Type responseClass) {
        httpClient = new HttpClient();
        postMethod = new PostMethod(uri);
        this.responseClass = responseClass;
        gson = new Gson();
    }

    public Object post(final Object req) throws IOException {
        return post(gson.toJson(req));
    }

    public Object post(final String json) throws IOException {
        postMethod.setRequestEntity(new StringRequestEntity(json, "application/json", null));
        httpClient.executeMethod(postMethod);
        String responseBodyAsString = postMethod.getResponseBodyAsString();
        return responseClass == String.class? responseBodyAsString : gson.fromJson(responseBodyAsString, responseClass);
    }

    public void addHeader(String name, String value) {
        postMethod.addRequestHeader(name, value);
    }

    public int getStatus() {
        return postMethod.getStatusCode();
    }
}
