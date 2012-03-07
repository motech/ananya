package org.motechproject.ananya.service.integrationtests;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.handler.CallDurationHandler;
import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.annotations.MotechListenerAbstractProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public class CallDurationHandlerIT extends SpringIntegrationTest{


    @Autowired
    private CallDurationHandler handler;

    @Autowired
    AllCallLogs allCallLogs;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @After
    public void tearDown(){
        allCallLogs.removeAll();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(CallDurationMeasure.class));
    }

    @Test
    public void shouldBindToTheCorrectHandlerForCallDurationEvent() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        EventListenerRegistry registry = Context.getInstance().getEventListenerRegistry();
        Set<EventListener> listeners = registry.getListeners(ReportPublishEventKeys.SEND_CALL_DURATION_DATA_KEY);

        MotechListenerAbstractProxy motechListenerAbstractProxy = (MotechListenerAbstractProxy) listeners.toArray()[0];
        Field declaredField = MotechListenerAbstractProxy.class.getDeclaredField("method");
        declaredField.setAccessible(true);
        Method handler = (Method) declaredField.get(motechListenerAbstractProxy);

        assertEquals(1, listeners.size());
        assertEquals(CallDurationHandler.class,handler.getDeclaringClass());
        assertEquals("handleCallDuration",handler.getName());
    }

    @Test
    public void shouldMapCallLogToCallDurationMeasure() {
        String msisdn = "555";
        String callId = "callId";

        DateTime now = DateTime.now();
        allCallLogs.addOrUpdate(new CallLog(callId, msisdn, CallFlowType.CALL, now, now.plusMinutes(5)));
        allCallLogs.addOrUpdate(new CallLog(callId, msisdn, CallFlowType.JOBAID, now.plusMinutes(2), now.plusMinutes(5)));
        allCallLogs.addOrUpdate(new CallLog(callId, msisdn, CallFlowType.CERTIFICATECOURSE, now.plusMinutes(2), null));

        allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf(msisdn), "", "", "");

        LogData logData = new LogData(LogType.CALL_DURATION, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleCallDuration(event);

        List<CallDurationMeasure> callDurationMeasures = template.loadAll(CallDurationMeasure.class);

        assertEquals(2, callDurationMeasures.size());
        assertThat(callDurationMeasures, hasItems(callDurationMeasureMatcher(callId, 300, msisdn, CallFlowType.CALL.name())));
        assertThat(callDurationMeasures, hasItems(callDurationMeasureMatcher(callId, 180, msisdn, CallFlowType.JOBAID.name())));
        assertEquals(0,allCallLogs.findByCallId(callId).size());
    }

    @Test
    public void shouldMapCallLogToCallDurationMeasureWhileCreatingAFrontLineWorker() {
        String msisdn = "555";
        String callId = "callId";

        DateTime now = DateTime.now();
        allCallLogs.addOrUpdate(new CallLog(callId, msisdn, CallFlowType.CALL, now, now.plusMinutes(5)));

        LogData logData = new LogData(LogType.CALL_DURATION, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleCallDuration(event);

        List<FrontLineWorkerDimension> lineWorkerDimensionList = template.loadAll(FrontLineWorkerDimension.class);
        assertEquals(1, lineWorkerDimensionList.size());
        assertEquals(Long.valueOf(msisdn), lineWorkerDimensionList.get(0).getMsisdn());
        assertEquals(0,allCallLogs.findByCallId(callId).size());
    }

    private Matcher<CallDurationMeasure> callDurationMeasureMatcher(final String callId, final int duration, final String msisdn, final String type) {
        return new BaseMatcher<CallDurationMeasure>() {
            @Override
            public boolean matches(Object o) {
                CallDurationMeasure o1 = (CallDurationMeasure) o;
                return o1.getCallId().equals(callId) &&
                        o1.getDuration() == duration &&
                        o1.getType().equals(type) &&
                        o1.getFrontLineWorkerDimension().getMsisdn().equals(Long.valueOf(msisdn));
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }


}
