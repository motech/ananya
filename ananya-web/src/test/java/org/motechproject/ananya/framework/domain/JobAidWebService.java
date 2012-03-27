package org.motechproject.ananya.framework.domain;

import com.gargoylesoftware.htmlunit.Page;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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

    public JobAidResponse whenRequestedForCallerData(JobAidRequest request) throws IOException {
        String webPage = "/ananya/generated/js/dynamic/jobaid/caller_data.js?callerId=" + request.getCallerId() + "&operator=" + request.getOperator();
        return makeRequest(webPage);
    }

    //Facade to create FLW in functional tests
    public JobAidResponse createFLW(JobAidRequest request) throws IOException{
        return whenRequestedForCallerData(request);
    }

    public JobAidResponse updatePromptsHeard(JobAidRequest request) throws IOException {
        String webPage = "/ananya/jobaid/updateprompt?callId=" + getCallID() + "&callerId=" + request.getCallerId() + promptsHeard(request);
        return makeRequest(webPage);
    }

    public JobAidResponse updateCurrentUsage(JobAidRequest request) throws IOException {
        String webPage = "/ananya/jobaid/updateusage?callId=" + getCallID() + "&callerId=" + request.getCallerId() + "&callDuration=" + request.getCallDuration();
        return makeRequest(webPage);
    }


    protected String getAppServerUrl() {
        return "http://localhost:" + ananyaProperties.getProperty("app.server.port");
    }

    private JobAidResponse makeRequest(String webPage) throws IOException {
        Page page = webClient.getPage(getAppServerUrl() + webPage);
        return JobAidResponse.make(page.getWebResponse().getContentAsString());
    }
    
    private String promptsHeard(JobAidRequest request){
        String promptsHeard = StringUtils.join(request.getPromptsHeard(), "','");
        if(!promptsHeard.isEmpty()){
            return "&promptList=['" + promptsHeard + "']";
        }
        return "";
    }
    
    private String getCallID(){
        return new DateTime().toString();
    }

}
