package org.motechproject.ananya.service.integrationtests;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllAudioTrackerLogs;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.handler.JobAidDataHandler;
import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.EventListenerRegistry;
import org.motechproject.server.event.annotations.MotechListenerAbstractProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class JobAidDataHandlerIT extends SpringIntegrationTest {

    @Autowired
    private JobAidDataHandler handler;

    @Autowired
    private AllJobAidContentDimensions allJobAidContentDimensions;

    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;

    @Autowired
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    @Autowired
    private AllRegistrationMeasures allRegistrationMeasures;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers ;
    @Autowired
    private AllCallLogs allCallLogs;

    @Before
    @After
    public void tearDown() {
        allAudioTrackerLogs.removeAll();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.flush();
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.flush();
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.flush();
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
        template.flush();
        template.deleteAll(template.loadAll(JobAidContentDimension.class));
        template.flush();
        template.deleteAll(template.loadAll(JobAidContentMeasure.class));
        template.flush();

    }

    @Test
    public void shouldBindToTheCorrectHandlerForJobAidDataEvent() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        EventListenerRegistry registry = Context.getInstance().getEventListenerRegistry();
        Set<EventListener> listeners = registry.getListeners(ReportPublishEventKeys.SEND_JOB_AID_CONTENT_DATA_KEY);

        MotechListenerAbstractProxy motechListenerAbstractProxy = (MotechListenerAbstractProxy) listeners.toArray()[0];
        Field declaredField = MotechListenerAbstractProxy.class.getDeclaredField("method");
        declaredField.setAccessible(true);
        Method handler = (Method) declaredField.get(motechListenerAbstractProxy);

        assertEquals(1, listeners.size());
        assertEquals(JobAidDataHandler.class, handler.getDeclaringClass());
        assertEquals("handleJobAidData", handler.getName());
    }

    @Test
    @Ignore
    public void shouldMapJobAidCallDataToReportDB() {
        String callerId = "9876543210";
        String callId = "callId";
        DateTime now = DateTime.now();
        DateTime callStartTime = now;
        DateTime callEndTime = now.plusSeconds(20);
        DateTime jobAidStartTime = now.plusSeconds(5);
        DateTime jobAidEndTime = now.plusSeconds(15);

        Location location = new Location("", "", "", 0, 0, 0);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "",Designation.ANGANWADI, location,RegistrationStatus.UNREGISTERED);
        frontLineWorker.setRegisteredDate(now);
        allFrontLineWorkers.add(frontLineWorker);

        LocationDimension locationDimension = new LocationDimension("S01D000B000V000", "", "", "");
        allLocationDimensions.add(locationDimension);

        CallLog callLog = new CallLog(callId, callerId.toString(), "321");
        callLog.addItem(new CallLogItem(CallFlowType.CALL, callStartTime, callEndTime));
        callLog.addItem(new CallLogItem(CallFlowType.JOBAID, jobAidStartTime, jobAidEndTime));
        allCallLogs.add(callLog);
        
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);
        audioTrackerLog.addItem(new AudioTrackerLogItem("content1", now, 10));
        audioTrackerLog.addItem(new AudioTrackerLogItem("content2", now, 20));
        allAudioTrackerLogs.add(audioTrackerLog);

        JobAidContentDimension jobAidContentDimension = new JobAidContentDimension("content1", null, "CHAPTER", "filename", "AUDIO", 100);
        allJobAidContentDimensions.add(jobAidContentDimension);
        allJobAidContentDimensions.add(new JobAidContentDimension("content2", null, "LESSON", "filename", "AUDIO", 100));

        TimeDimension jobAidStartTimeDimension = allTimeDimensions.addOrUpdate(jobAidStartTime);
        TimeDimension jobAidEndTimeDimension = allTimeDimensions.addOrUpdate(jobAidEndTime);
        TimeDimension timeDimension = allTimeDimensions.addOrUpdate(now);
        
        LogData logData = new LogData(LogType.JOBAID, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleJobAidData(event);

        List<JobAidContentMeasure> jobAidContentMeasureList = template.loadAll(JobAidContentMeasure.class);
        assertEquals(2,  jobAidContentMeasureList.size());

        JobAidContentMeasure jobAidContentMeasure = jobAidContentMeasureList.get(0);
        assertNotNull(jobAidContentMeasure);

        assertNotNull(jobAidContentMeasure.getJobAidContentDimension());
        assertEquals(jobAidContentDimension.getId(), jobAidContentMeasure.getJobAidContentDimension().getId());

        assertNotNull(jobAidContentMeasure.getLocationDimension());
        assertEquals(locationDimension.getId(), jobAidContentMeasure.getLocationDimension().getId());

       

        assertEquals("callId", jobAidContentMeasure.getCallId());
        assertEquals(10, (int)jobAidContentMeasure.getDuration());
        assertEquals(new Timestamp(now.getMillis()), jobAidContentMeasure.getTimestamp());
        assertEquals(10, (int)jobAidContentMeasure.getPercentage());
    }

}
