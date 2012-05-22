package org.motechproject.ananya.functional;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.framework.MyWebClient;
import org.motechproject.ananya.repository.AllCallLogs;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.framework.MyWebClient.PostParam.param;

public class CallLogFlowTest extends SpringIntegrationTest {

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

        DateTimeZone ist = DateTimeZone.getDefault();
        DateTime callStartTime = new DateTime(1330320462642L).toDateTime(ist);
        DateTime regStartTime = new DateTime(1330320582936L).toDateTime(ist);
        DateTime regEndTime = new DateTime(1330320606087L).toDateTime(ist);
        DateTime jobAidStartTime = new DateTime(1330320624871L).toDateTime(ist);
        DateTime disconnectTime = new DateTime(1330320634871L).toDateTime(ist);

        String callId = "99865744201234567890";
        String callerId = "919986574420";
        MyWebClient.PostParam callerIdParam = param("callerId", callerId);
        MyWebClient.PostParam callIdParam = param("callId", callId);
        MyWebClient.PostParam dataToPost = param("dataToPost",
                "[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":{'time':1330320462642,'callEvent':'CALL_START'}}," +
                        "{\"token\":\"1\",\"type\":\"callDuration\",\"data\":{'time':1330320582936,'callEvent':'REGISTRATION_START'}}," +
                        "{\"token\":\"2\",\"type\":\"callDuration\",\"data\":{'time':1330320606087,'callEvent':'REGISTRATION_END'}}," +
                        "{\"token\":\"3\",\"type\":\"callDuration\",\"data\":{'time':1330320624871,'callEvent':'JOBAID_START'}}," +
                        "{\"token\":\"4\",\"type\":\"callDuration\",\"data\":{'time':1330320634871,'callEvent':'DISCONNECT'}}]");

        myWebClient.post(getAppServerHostUrl() + "/ananya/transferdata/disconnect", callIdParam, callerIdParam, dataToPost);

        CallLog callLog = allCallLogs.findByCallId(callId);
        assertEquals(callId, callLog.getCallId());
        assertEquals(callerId, callLog.getCallerId());

        List<CallLogItem> callLogItems = callLog.getCallLogItems();

        assertEquals(3, callLogItems.size());

        CallLogItem callLogForCall = callLogItems.get(0);
        assertEquals(callStartTime.toDateTime(DateTimeZone.UTC), callLogForCall.getStartTime().toDateTime(DateTimeZone.UTC));
        assertEquals(disconnectTime.toDateTime(DateTimeZone.UTC), callLogForCall.getEndTime().toDateTime(DateTimeZone.UTC));
        assertEquals(CallFlowType.CALL, callLogForCall.getCallFlowType());

        CallLogItem callLogForRegistration = callLogItems.get(1);
        assertEquals(regStartTime.toDateTime(DateTimeZone.UTC), callLogForRegistration.getStartTime().toDateTime(DateTimeZone.UTC));
        assertEquals(regEndTime.toDateTime(DateTimeZone.UTC), callLogForRegistration.getEndTime().toDateTime(DateTimeZone.UTC));
        assertEquals(CallFlowType.REGISTRATION, callLogForRegistration.getCallFlowType());

        CallLogItem callLogForJobAid = callLogItems.get(2);
        assertEquals(jobAidStartTime.toDateTime(DateTimeZone.UTC), callLogForJobAid.getStartTime().toDateTime(DateTimeZone.UTC));
        assertEquals(disconnectTime.toDateTime(DateTimeZone.UTC), callLogForJobAid.getEndTime());
        assertEquals(CallFlowType.JOBAID, callLogForJobAid.getCallFlowType());
    }
}
