package org.motechproject.ananya.framework.domain;

import com.gargoylesoftware.htmlunit.Page;
import org.motechproject.ananya.framework.MyWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class JobAidWebService {

    @Qualifier("ananyaProperties")
    @Autowired
    protected Properties ananyaProperties;

    private MyWebClient webClient = new MyWebClient();

    public JobAidResponse requestForCallerData(JobAidRequest request) throws IOException {
        String webPage = "/ananya/generated/js/dynamic/jobaid/caller_data.js?callerId=" + request.getCallerId() +
                "&operator=" + request.getOperator() + "&circle=" + request.getCircle() + "&callId=" + request.getCallId();
        Page page = webClient.getPage(getAppServerUrl() + webPage);
        return JobAidResponse.make(page.getWebResponse().getContentAsString());
    }

    public JobAidResponse requestForDisconnect(JobAidDisconnectRequest request) throws IOException {
        String webPage ="/ananya/jobaid/transferdata/disconnect";
        MyWebClient.PostParam callId = MyWebClient.PostParam.param("callId", request.getCallId());
        MyWebClient.PostParam callerId = MyWebClient.PostParam.param("callerId", request.getCallerId());
        MyWebClient.PostParam dataToPost = MyWebClient.PostParam.param("dataToPost", request.getJsonPostData());
        MyWebClient.PostParam calledNumber = MyWebClient.PostParam.param("calledNumber", request.getCalledNumber());
        MyWebClient.PostParam callDuration = MyWebClient.PostParam.param("callDuration", request.getCallDuration());
        MyWebClient.PostParam promptList = MyWebClient.PostParam.param("promptList", request.getPromptList());

        Page page = webClient.post(getAppServerUrl() + webPage, callId, callerId, dataToPost, calledNumber, callDuration, promptList);
        return JobAidResponse.makeForNonJson(page.getWebResponse().getContentAsString());
    }

    protected String getAppServerUrl() {
        return "http://localhost:" + ananyaProperties.getProperty("app.server.port");
    }

}
