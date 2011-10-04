package org.motechproject.bbcwtfunctional.framework;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;

public class MotechWebClient {

    private WebClient webClient = new WebClient();

    public MotechWebClient() {
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
    }

    public Page getPage(String url) {
        try {
            return webClient.getPage(url);
        } catch (IOException e) {
            return null;
        }
    }

    public void shutDown() {
        webClient.getCookieManager().clearCookies();
        webClient.closeAllWindows();
    }
}
