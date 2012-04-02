package org.motechproject.ananya.framework.domain;

import com.gargoylesoftware.htmlunit.Page;
import org.motechproject.ananya.framework.MyWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class CertificateCourseWebservice {
    @Qualifier("ananyaProperties")
    @Autowired
    protected Properties ananyaProperties;

    private MyWebClient webClient = new MyWebClient();

    public CertificateCourseResponse requestForCallerData(CertificateCourseRequest request) throws IOException {
        String webPage = "/ananya/generated/js/dynamic/caller_data.js?callerId=" + request.getCallerId() + "&operator=" + request.getOperator();
        return makeRequest(webPage);
    }

    public CertificateCourseResponse requestForTransferData(CertificateCourseRequest request) throws IOException {
        String webPage ="/ananya/transferdata.js";
        MyWebClient.PostParam callerId = MyWebClient.PostParam.param("callerId", request.getCallerId());
        MyWebClient.PostParam dataToPost = MyWebClient.PostParam.param("dataToPost", request.getJsonPostData());
        MyWebClient.PostParam callId = MyWebClient.PostParam.param("callId", request.getCallId());
        return makePostRequest(webPage, callerId, callId , dataToPost);
    }

    private CertificateCourseResponse makeRequest(String webPage) throws IOException {
        Page page = webClient.getPage(getAppServerUrl() + webPage);
        return CertificateCourseResponse.make(page.getWebResponse().getContentAsString());
    }

    private CertificateCourseResponse makePostRequest(String webPage, MyWebClient.PostParam callerId, MyWebClient.PostParam callId, MyWebClient.PostParam dataToPost) throws IOException {
        Page page = webClient.post(getAppServerUrl() + webPage, callerId, callId , dataToPost);
        return CertificateCourseResponse.makeForNonJson(page.getWebResponse().getContentAsString());
    }

    protected String getAppServerUrl() {
        return "http://localhost:" + ananyaProperties.getProperty("app.server.port");
    }

    public CertificateCourseResponse requestForDisconnect(CertificateCourseRequest request) throws IOException {
        String webPage ="/ananya/transferdata/disconnect.js";
        MyWebClient.PostParam callId = MyWebClient.PostParam.param("callId", request.getCallId());
        MyWebClient.PostParam callerId = MyWebClient.PostParam.param("callerId", request.getCallerId());
        MyWebClient.PostParam dataToPost = MyWebClient.PostParam.param("dataToPost", request.getJsonPostData());
        return makePostRequestForDisconnect(webPage,callId, callerId, dataToPost);
    }

    private CertificateCourseResponse makePostRequestForDisconnect(String webPage, MyWebClient.PostParam callId, MyWebClient.PostParam callerId, MyWebClient.PostParam dataToPost) throws IOException {
        Page page = webClient.post(getAppServerUrl() + webPage, callId, callerId, dataToPost);
        return CertificateCourseResponse.makeForNonJson(page.getWebResponse().getContentAsString());
    }
}
