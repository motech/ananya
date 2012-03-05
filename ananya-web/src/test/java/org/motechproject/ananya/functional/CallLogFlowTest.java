package org.motechproject.ananya.functional;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.repository.AllCallLogs;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.motechproject.ananya.functional.MyWebClient.PostParam.param;

public class CallLogFlowTest extends SpringIntegrationTest{

    private MyWebClient myWebClient;
    @Autowired
    private AllCallLogs allCallLogs;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @After
    public void tearDown() throws Exception {
        allCallLogs.removeAll();
    }

    @Test
    public void shouldCreateCallLogs() throws IOException {
        String callId = "99865744201234567890";
        MyWebClient.PostParam callerIdParam = param("callerId", "9986574420");
        MyWebClient.PostParam callIdParam = param("callId", callId);
        MyWebClient.PostParam dataToPost = param("dataToPost",
    "[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":{'time':1330320462642,'callEvent':'CALL_START'}},{\"token\":\"1\",\"type\":\"callDuration\",\"data\":{'time':1330320582936,'callEvent':'REGISTRATION_START'}},{\"token\":\"2\",\"type\":\"callDuration\",\"data\":{'time':1330320606087,'callEvent':'REGISTRATION_END'}},{\"token\":\"3\",\"type\":\"callDuration\",\"data\":{'time':1330320624871,'callEvent':'JOBAID_START'}},{\"token\":\"4\",\"type\":\"callDuration\",\"data\":{'time':1330320634871,'callEvent':'DISCONNECT'}}]");

        myWebClient.post(getAppServerHostUrl() + "/ananya/transferdata",callIdParam, callerIdParam, dataToPost);

        Collection<CallLog> allCallLogsByCallId = allCallLogs.findByCallId(callId);
        Assert.assertEquals(3,allCallLogsByCallId.size());

        DateTime disconnectTime = new DateTime(1330320634871L);
        DateTime callStartTime = new DateTime(1330320462642L);
        DateTime regStartTime = new DateTime(1330320582936L);
        DateTime regEndTime = new DateTime(1330320606087L);
        DateTime jobAidStartTime = new DateTime(1330320624871L);

        assertThat(allCallLogsByCallId, hasItems(callLogMatcher(callStartTime, CallFlowType.CALL, disconnectTime, callId)));
        assertThat(allCallLogsByCallId, hasItems(callLogMatcher(regStartTime, CallFlowType.REGISTRATION, regEndTime, callId)));
        assertThat(allCallLogsByCallId, hasItems(callLogMatcher(jobAidStartTime, CallFlowType.JOBAID, disconnectTime, callId)));
    }


    @Test
    public void ssss(){

    }

    private Matcher<CallLog> callLogMatcher(final DateTime startTime, final CallFlowType callFlowType, final DateTime endTime, final String callId) {
        return new BaseMatcher<CallLog>() {
            @Override
            public boolean matches(Object o) {
                CallLog o1 = (CallLog) o;
                return ((o1.getStartTime() == null && startTime == null) ||o1.getStartTime().equals(startTime))
                        && o1.getCallFlowType() == callFlowType
                        && ((o1.getEndTime() == null && endTime == null) || o1.getEndTime().equals(endTime))
                        && o1.getCallId().equals(callId);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
