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
        String webPage = "/ananya/generated/js/dynamic/caller_data.js?callerId=" + request.getCallerId()
                + "&operator=" + request.getOperator()+"&callId="+request.getCallId()+"&circle="+request.getCircle();
        return makeRequest(webPage);
    }

    protected String getAppServerUrl() {
        return "http://localhost:" + ananyaProperties.getProperty("app.server.port");
    }

    public CertificateCourseResponse requestForDisconnect(CertificateCourseRequest request) throws IOException {
        String webPage ="/ananya/transferdata/disconnect";
        MyWebClient.PostParam callId = MyWebClient.PostParam.param("callId", request.getCallId());
        MyWebClient.PostParam callerId = MyWebClient.PostParam.param("callerId", request.getCallerId());
        MyWebClient.PostParam dataToPost = MyWebClient.PostParam.param("dataToPost", request.getJsonPostData());
        MyWebClient.PostParam operator = MyWebClient.PostParam.param("operator", request.getOperator());
        MyWebClient.PostParam circle = MyWebClient.PostParam.param("circle", request.getCircle());
        MyWebClient.PostParam calledNumber = MyWebClient.PostParam.param("calledNumber", request.getCalledNumber());
        MyWebClient.PostParam language = MyWebClient.PostParam.param("language", request.getLanguage());
        return makePostRequestForDisconnect(webPage,callId, callerId, dataToPost, circle, operator, calledNumber, language);
    }

    private CertificateCourseResponse makeRequest(String webPage) throws IOException {
        Page page = webClient.getPage(getAppServerUrl() + webPage);
        return CertificateCourseResponse.make(page.getWebResponse().getContentAsString());
    }

    private CertificateCourseResponse makePostRequestForDisconnect(String webPage, MyWebClient.PostParam callId, MyWebClient.PostParam callerId, MyWebClient.PostParam dataToPost, MyWebClient.PostParam circle, MyWebClient.PostParam operator, MyWebClient.PostParam calledNumber, MyWebClient.PostParam language) throws IOException {
        Page page = webClient.post(getAppServerUrl() + webPage, callId, callerId, dataToPost, operator, circle, calledNumber, language);
        return CertificateCourseResponse.makeForNonJson(page.getWebResponse().getContentAsString());
    }
}
