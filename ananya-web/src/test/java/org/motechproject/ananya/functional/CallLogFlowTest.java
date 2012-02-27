package org.motechproject.ananya.functional;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallFlow;
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

    @Test
    public void shouldRegisterNewFLW() throws IOException {
        MyWebClient.PostParam callerId = param("callerId", "9986574420");
        MyWebClient.PostParam callId = param("callId", "99865744201234567890");
        MyWebClient.PostParam dataToPost = param("dataToPost",
    "[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":'{\"time\":1330320462642,\"callEvent\":\"CALL_START\"}'},{\"token\":\"1\",\"type\":\"callDuration\",\"data\":'{\"time\":1330320582936,\"callEvent\":\"REGISTRATION_START\"}'},{\"token\":\"2\",\"type\":\"callDuration\",\"data\":'{\"time\":1330320606087,\"callEvent\":\"REGISTRATION_END\"}'},{\"token\":\"3\",\"type\":\"callDuration\",\"data\":'{\"time\":1330320624871,\"callEvent\":\"JOBAID_START\"}'}]");

        myWebClient.post("http://localhost:9979/ananya/transferdata",callId, callerId, dataToPost);

        Collection<CallLog> allCallLogsByCallId = allCallLogs.findByCallId("99865744201234567890");
        Assert.assertEquals(3,allCallLogsByCallId.size());

        assertThat(allCallLogsByCallId, hasItems(callLogMatcher(new DateTime(1330320462642L).toDateTime(DateTimeZone.UTC), CallFlow.CALL, null, "99865744201234567890")));
        assertThat(allCallLogsByCallId, hasItems(callLogMatcher(new DateTime(1330320582936L).toDateTime(DateTimeZone.UTC), CallFlow.REGISTRATION, new DateTime(1330320606087L).toDateTime(DateTimeZone.UTC), "99865744201234567890")));
        assertThat(allCallLogsByCallId, hasItems(callLogMatcher(new DateTime(1330320624871L).toDateTime(DateTimeZone.UTC), CallFlow.JOBAID, null, "99865744201234567890")));
    }

    private Matcher<CallLog> callLogMatcher(final DateTime startTime, final CallFlow callFlow, final DateTime endTime, final String callId) {
        return new BaseMatcher<CallLog>() {
            @Override
            public boolean matches(Object o) {
                CallLog o1 = (CallLog) o;
                return ((o1.getStartTime() == null && startTime == null) ||o1.getStartTime().equals(startTime))
                        && o1.getCallFlow() == callFlow
                        && ((o1.getEndTime() == null && endTime == null) || o1.getEndTime().equals(endTime))
                        && o1.getCallId().equals(callId);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
