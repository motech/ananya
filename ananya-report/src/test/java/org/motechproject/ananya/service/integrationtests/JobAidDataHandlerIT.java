package org.motechproject.ananya.service.integrationtests;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
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
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.CallMessageType;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.handler.JobAidDataHandler;
import org.motechproject.event.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
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
    private AllTimeDimensions allTimeDimensions;
    @Autowired
    private AllLocationDimensions allLocationDimensions;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllCallLogs allCallLogs;
    @Autowired
    private RegistrationLogService registrationLogService;
    @Autowired
    private AllRegistrationLogs allRegistrationLogs;

    @Before
    @After
    public void setUpAndTearDown() {
        allRegistrationLogs.removeAll();
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
        template.clear();
    }

    @Test
    public void shouldBindToTheCorrectHandlerForJobAidDataEvent() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        //TODO Context is missing from platform starting 12 SNAPSHOT. Fix this.
//        EventListenerRegistry registry = Context.getInstance().getEventListenerRegistry();
//        Set<EventListener> listeners = registry.getListeners(ReportPublishEventKeys.JOBAID_CALL_MESSAGE);
//
//        MotechListenerAbstractProxy motechListenerAbstractProxy = (MotechListenerAbstractProxy) listeners.toArray()[0];
//        Field declaredField = MotechListenerAbstractProxy.class.getDeclaredField("method");
//        declaredField.setAccessible(true);
//        Method handler = (Method) declaredField.get(motechListenerAbstractProxy);
//
//        assertEquals(1, listeners.size());
//        assertEquals(JobAidDataHandler.class, handler.getDeclaringClass());
//        assertEquals("handleJobAidData", handler.getName());
    }

    @Test
    public void shouldMapJobAidCallDataToReportDB() {
        String callerId = "919876543210";
        String callId = "919876543210-12345678";
        String calledNumber = "577965";

        DateTime now = DateTime.now();
        DateTime callStartTime = now;
        DateTime callEndTime = now.plusSeconds(20);
        DateTime jobAidStartTime = now.plusSeconds(5);
        DateTime jobAidEndTime = now.plusSeconds(15);

        Location location = new Location("", "", "", 0, 0, 0);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "", Designation.AWW, location, RegistrationStatus.UNREGISTERED);
        frontLineWorker.setRegisteredDate(now);
        frontLineWorker.setOperator("airtel");
        allFrontLineWorkers.add(frontLineWorker);
        registrationLogService.add(new RegistrationLog(callId, callerId, "airtel", ""));

        LocationDimension locationDimension = new LocationDimension("S01D000B000V000", "", "", "");
        allLocationDimensions.add(locationDimension);

        CallLog callLog = new CallLog(callId, callerId.toString(), calledNumber);
        callLog.addItem(new CallLogItem(CallFlowType.CALL, callStartTime, callEndTime));
        callLog.addItem(new CallLogItem(CallFlowType.JOBAID, jobAidStartTime, jobAidEndTime));
        allCallLogs.add(callLog);

        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);
        audioTrackerLog.addItem(new AudioTrackerLogItem("content1", now, 10));
        audioTrackerLog.addItem(new AudioTrackerLogItem("content2", now, 20));
        allAudioTrackerLogs.add(audioTrackerLog);

        JobAidContentDimension content1Dimension = new JobAidContentDimension("content1", null, "CHAPTER", "filename", "AUDIO", 100);
        JobAidContentDimension content2Dimension = new JobAidContentDimension("content2", null, "LESSON", "filename", "AUDIO", 100);
        allJobAidContentDimensions.add(content1Dimension);
        allJobAidContentDimensions.add(content2Dimension);

        allTimeDimensions.addOrUpdate(jobAidStartTime);
        allTimeDimensions.addOrUpdate(jobAidEndTime);
        allTimeDimensions.addOrUpdate(now);

        CallMessage logData = new CallMessage(CallMessageType.JOBAID, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleJobAidData(event);

        List<RegistrationMeasure> registrationMeasures = template.loadAll(RegistrationMeasure.class);
        RegistrationMeasure registrationMeasure = registrationMeasures.get(0);
        assertNotNull(registrationMeasure);

        List<JobAidContentMeasure> jobAidContentMeasureList = template.loadAll(JobAidContentMeasure.class);
        assertEquals(2, jobAidContentMeasureList.size());

        List<JobAidContentMeasure> filteredJobAidContentMeasure = filter(having(on(JobAidContentMeasure.class).getDuration(), Matchers.equalTo(10)), jobAidContentMeasureList);
        JobAidContentMeasure jobAidContentMeasure = filteredJobAidContentMeasure.get(0);
        assertNotNull(jobAidContentMeasure);

        JobAidContentDimension jobAidDimension1 = jobAidContentMeasure.getJobAidContentDimension();
        assertNotNull(jobAidDimension1);
        JobAidContentDimension byContentId = allJobAidContentDimensions.findByContentId(jobAidDimension1.getContentId());
        assertEquals(byContentId.getId(), jobAidContentMeasure.getJobAidContentDimension().getId());

        assertNotNull(jobAidContentMeasure.getLocationDimension());
        assertEquals(locationDimension.getId(), jobAidContentMeasure.getLocationDimension().getId());

        assertEquals(callId, jobAidContentMeasure.getCallId());
        assertEquals(10, (int) jobAidContentMeasure.getDuration());
        assertEquals(new Timestamp(now.getMillis()), jobAidContentMeasure.getTimestamp());
        assertEquals(10, (int) jobAidContentMeasure.getPercentage());
    }


}
