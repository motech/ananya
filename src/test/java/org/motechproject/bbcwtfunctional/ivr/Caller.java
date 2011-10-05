package org.motechproject.bbcwtfunctional.ivr;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.apache.commons.lang.StringUtils;
import org.motechproject.bbcwtfunctional.framework.FunctionalTestObject;
import org.motechproject.bbcwtfunctional.framework.KooKooResponseParser;
import org.motechproject.bbcwtfunctional.framework.MotechWebClient;
import org.motechproject.bbcwtfunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.bbcwtfunctional.testdata.ivrrequest.CallInfo;
import org.motechproject.bbcwtfunctional.testdata.ivrrequest.NoCallInfo;

import java.io.IOException;
import java.net.URLEncoder;

public class Caller extends FunctionalTestObject {
    private String sid;
    private String phoneNumber;
    private MotechWebClient webClient;
    private CallInfo callInfo = new NoCallInfo();

    public Caller(String sid, String phoneNumber, MotechWebClient webClient) {
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
        StringBuffer url = new StringBuffer();
        url.append(
                String.format(
                        "http://localhost:%s/bbcwt/ivr/reply?", System.getProperty("jetty.port", "8080")));

        if(StringUtils.isNotEmpty(sid)) {
            url.append(
                    String.format("sid=%s&", sid)
            );
        }

        if(StringUtils.isNotEmpty(cid)) {
            url.append(
                    String.format("cid=%s&", cid)
            );
        }

        if(StringUtils.isNotEmpty(event)) {
            url.append(
                    String.format("event=%s&", event)
            );
        }

        if(StringUtils.isNotEmpty(data)) {
            url.append(
                    String.format("data=%s&", URLEncoder.encode(data))
            );
        }

        return String.format("%s&tamaData=%s", url, callInfo.asQueryParameter());
    }

    public IVRResponse enter(String number) {
        String completeUrl = urlFor("GotDTMF", number);
        return invoke(completeUrl);
    }

    public IVRResponse continueWithoutInteraction() {
        String completeUrl = urlFor(sid, null, null, null);
        return invoke(completeUrl);
    }

    public void hangup() {
        String completeUrl = urlFor("Hangup", null);
        webClient.getPage(completeUrl);
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
