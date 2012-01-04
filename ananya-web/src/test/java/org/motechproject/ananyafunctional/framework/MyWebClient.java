package org.motechproject.ananyafunctional.framework;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;

public class MyWebClient {

    private WebClient webClient = new WebClient();

    public Page getPage(String url) throws IOException {
        return webClient.getPage(url);
    }

    public CallFlow getCallFlow(String url) throws IOException {
        Page page = webClient.getPage(url);
        return new CallFlow(page.getWebResponse().getContentAsString());
    }
}
