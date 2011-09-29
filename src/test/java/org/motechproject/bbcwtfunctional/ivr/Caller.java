package org.motechproject.bbcwtfunctional.ivr;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.motechproject.bbcwtfunctional.framework.FunctionalTestObject;
import org.motechproject.bbcwtfunctional.framework.KooKooResponseParser;
import org.motechproject.bbcwtfunctional.framework.MyWebClient;
import org.motechproject.bbcwtfunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.bbcwtfunctional.testdata.ivrrequest.CallInfo;
import org.motechproject.bbcwtfunctional.testdata.ivrrequest.NoCallInfo;

import java.io.IOException;

public class Caller extends FunctionalTestObject {
    private String sid;
    private String phoneNumber;
    private MyWebClient webClient;
    private CallInfo callInfo = new NoCallInfo();

    public Caller(String sid, String phoneNumber, MyWebClient webClient) {
        this.sid = sid;
        this.phoneNumber = phoneNumber;
        this.webClient = webClient;
    }

    public IVRResponse call() throws IOException {
        Page page = webClient.getPage(urlFor("NewCall", ""));
        WebResponse webResponse = page.getWebResponse();
        return KooKooResponseParser.fromXml(webResponse.getContentAsString().toLowerCase());
    }

    protected String urlFor(String sid, String cid, String event, String data) {
        String url = String.format("http://localhost:%s/bbcwt/ivr/reply?sid=%s&cid=%s&event=%s&data=%s", System.getProperty("jetty.port", "8080"), sid, cid, event, data);
        return String.format("%s&tamaData=%s", url, callInfo.asQueryParameter());
    }

    public IVRResponse enter(String number) {
        String completeUrl = urlFor("GotDTMF", number);
        return invoke(completeUrl);
    }

    private IVRResponse invoke(String completeUrl) {
        logInfo("{Caller} {Invoking} {Url=%s}", completeUrl);
        Page page = webClient.getPage(completeUrl);
        return KooKooResponseParser.fromXml(page.getWebResponse().getContentAsString());
    }

    private String urlFor(String event, String data) {
        return urlFor(sid, phoneNumber, event, data);
    }

    public IVRResponse replyToCall(CallInfo callInfo) {
        this.callInfo = callInfo;
        String completeUrl = urlFor("NewCall", "");
        return invoke(completeUrl);
    }
}
