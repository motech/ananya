package org.motechproject.ananya.functional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.repository.AllCallLogs;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;

import static org.motechproject.ananya.functional.MyWebClient.PostParam.param;

public class CallLogFlowTest extends SpringIntegrationTest{

    private MyWebClient myWebClient;
    @Autowired
    private AllCallLogs allCallLogs;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @Test
    @Ignore
    public void shouldRegisterNewFLW() throws IOException {
        MyWebClient.PostParam callerId = param("callerId", "9986574420");
        MyWebClient.PostParam callId = param("callId", "99865744201234567890");
        MyWebClient.PostParam dataToPost = param("dataToPost",
    "[{\"token\":\"0\",\"type\":\"CALL_DURATION\",\"data\":\"{\"time\":234234,\"callEvent\":\"CALL_START\"}\"},{\"token\":\"1\",\"type\":\"CALL_DURATION\",\"data\":\"{\"time\":234234,\"callEvent\":\"REGISTRATION_START\"}\"},{\"token\":\"2\",\"type\":\"CALL_DURATION\",\"data\":\"{\"time\":234234,\"callEvent\":\"REGISTRATION_END\"}\"},{\"token\":\"3\",\"type\":\"CALL_DURATION\",\"data\":\"{\"time\":234234,\"callEvent\":\"JOBAID_START\"}\"},{\"token\":\"4\",\"type\":\"CALL_DURATION\",\"data\":\"{\"time\":234234,\"callEvent\":\"DISCONNECT\"}\"}]");

        myWebClient.post("http://localhost:9979/ananya/transferdata",callId, callerId, dataToPost);

        Collection<CallLog> callLogs = allCallLogs.findByCallId("99865744201234567890");

        Assert.assertEquals(callLogs.size(), 4);
    }
}
