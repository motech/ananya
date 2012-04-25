package org.motechproject.ananya.service.integrationtests;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public class CallDurationHandlerIT extends SpringIntegrationTest {


    @Autowired
    private CallDurationHandler handler;

    @Autowired
    AllCallLogs allCallLogs;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    
    @Autowired
    AllRegistrationMeasures allRegistrationMeasures;

    @Autowired
    AllLocationDimensions allLocationDimensions;
    
    @Autowired
    AllTimeDimensions allTimeDimensions;

    @After
    public void tearDown() {
        allCallLogs.removeAll();
        template.deleteAll(template.loadAll(CallDurationMeasure.class));
        template.flush();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.flush();
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.flush();
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
        template.flush();
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
        assertEquals(CallDurationHandler.class, handler.getDeclaringClass());
        assertEquals("handleCallDuration", handler.getName());
    }

    @Test
    public void shouldMapCallLogToCallDurationMeasure() {
        String callId = "callId";
        Long callerId = 123456789L;
        DateTime now = DateTime.now();
        DateTime callStartTime = now;
        DateTime callEndTime = now.plusSeconds(20);
        DateTime certificateCourseStartTime = now.plusSeconds(5);
        DateTime certificateCourseEndTime = now.plusSeconds(15);

        CallLog callLog = new CallLog(callId, callerId.toString());

        callLog.addItem(new CallLogItem(CallFlowType.CALL, callStartTime, callEndTime));
        callLog.addItem(new CallLogItem(CallFlowType.CERTIFICATECOURSE, certificateCourseStartTime, certificateCourseEndTime));
        allCallLogs.add(callLog);

        FrontLineWorkerDimension flwDimension = allFrontLineWorkerDimensions.getOrMakeFor(Long.valueOf(callerId), "", "", "");
        LocationDimension locationDimension = new LocationDimension("", "", "", "");
        allLocationDimensions.add(locationDimension);
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(DateTime.now());
        allRegistrationMeasures.add(new RegistrationMeasure(flwDimension, locationDimension, timeDimension));

        LogData logData = new LogData(LogType.CALL_DURATION, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleCallDuration(event);

        List<CallDurationMeasure> callDurationMeasures = template.loadAll(CallDurationMeasure.class);

        assertEquals(2, callDurationMeasures.size());

        CallDurationMeasure callDurationMeasureForCall = null;
        CallDurationMeasure callDurationMeasureForCourse = null;

        for( CallDurationMeasure callDurationMeasure : callDurationMeasures){
            String type = callDurationMeasure.getType();
            if(type.equals(CallFlowType.CALL.toString()))
                 callDurationMeasureForCall = callDurationMeasure;
            else
                 callDurationMeasureForCourse = callDurationMeasure;
        }

        assertEquals(20, callDurationMeasureForCall.getDuration());
        assertEquals(callId, callDurationMeasureForCall.getCallId());
        assertEquals(CallFlowType.CALL.toString(), callDurationMeasureForCall.getType());
        assertEquals(callerId, callDurationMeasureForCall.getFrontLineWorkerDimension().getMsisdn());
        assertEquals(new Timestamp(callStartTime.getMillis()), callDurationMeasureForCall.getStartTime());
        assertEquals(new Timestamp(callEndTime.getMillis()), callDurationMeasureForCall.getEndTime());

        assertEquals(10, callDurationMeasureForCourse.getDuration());
        assertEquals(callId, callDurationMeasureForCourse.getCallId());
        assertEquals(CallFlowType.CERTIFICATECOURSE.toString(), callDurationMeasureForCourse.getType());
        assertEquals(callerId, callDurationMeasureForCall.getFrontLineWorkerDimension().getMsisdn());
        assertEquals(new Timestamp(certificateCourseStartTime.getMillis()), callDurationMeasureForCourse.getStartTime());
        assertEquals(new Timestamp(certificateCourseEndTime.getMillis()), callDurationMeasureForCourse.getEndTime());
    }
}
