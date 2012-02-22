package org.motechproject.ananya.functional;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.repository.AllCallDetailLogs;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class CallDetailLoggerFlowTest extends SpringIntegrationTest {
    private MyWebClient myWebClient;

    @Autowired
    private AllCallDetailLogs allCallDetailLogs;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @After
    public void tearDown(){
        allCallDetailLogs.removeAll();
    }

    @Test
    public void shouldSaveCallDetailAtTheEndOfTheCall() throws IOException {
        String callId = "callId";
        String callerId = "callerId";
        DateTime dateTime = new DateTime(2012, 2, 22, 14, 53, 13);
        String dataToPost = "[{\"token\":\"0\",\"data\":{\"event\":\"CALL_START\",\"time\":1329902593000}}]";

        MyWebClient.PostParam callIdParam = MyWebClient.PostParam.param("callId", callId);
        MyWebClient.PostParam callerIdParam = MyWebClient.PostParam.param("callerId", callerId);
        MyWebClient.PostParam dataToPostParam = MyWebClient.PostParam.param("dataToPost", dataToPost);

        myWebClient.post("http://localhost:9979/ananya/calldurationdata/add", callerIdParam, callIdParam, dataToPostParam);

        CallDetailLog byCallId = allCallDetailLogs.findByCallId(callId);
        assertEquals(org.motechproject.ananya.domain.CallEvent.CALL_START,byCallId.getCallEvent());
    }
}
