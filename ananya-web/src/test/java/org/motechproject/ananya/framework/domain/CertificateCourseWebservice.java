package org.motechproject.ananya.framework.domain;

import com.gargoylesoftware.htmlunit.Page;
import org.motechproject.ananya.framework.MyWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class CertificateCourseWebService {
    @Qualifier("ananyaProperties")
    @Autowired
    protected Properties ananyaProperties;

    private MyWebClient webClient = new MyWebClient();

    public CertificateCourseResponse requestForCallerData(CertificateCourseRequest request) throws IOException {
        String webPage = "/ananya/generated/js/dynamic/caller_data.js?callerId=" + request.getCallerId() + "&operator=" + request.getOperator();
        return makeRequest(webPage);
    }

    public CertificateCourseResponse requestForCallerData(CertificateCourseRequest request) throws IOException {
        String webPage = "/ananya/generated/js/dynamic/caller_data.js?callerId=" + request.getCallerId() + "&operator=" + request.getOperator();
        return makeRequest(webPage);
    }

    private CertificateCourseResponse makeRequest(String webPage) throws IOException {
        Page page = webClient.getPage(getAppServerUrl() + webPage);
        return CertificateCourseResponse.make(page.getWebResponse().getContentAsString());
    }

    protected String getAppServerUrl() {
        return "http://localhost:" + ananyaProperties.getProperty("app.server.port");
    }
}
