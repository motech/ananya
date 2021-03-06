package org.motechproject.ananya.service.integrationtests;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.CallMessageType;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.handler.CertificateCourseDataHandler;
import org.motechproject.event.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class CertificateCourseDataHandlerIT extends SpringIntegrationTest {

    @Autowired
    private CertificateCourseDataHandler handler;

    @Autowired
    AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    AllCourseItemDimensions allCourseItemDimensions;

    @Autowired
    AllTimeDimensions allTimeDimensions;

    @Autowired
    AllCertificateCourseLogs allCertificateCourseLogs;

    @Autowired
    AllLocationDimensions allLocationDimensions;

    @Autowired
    AllLanguageDimension allLanguageDimension;
    
    @Autowired
    AllRegistrationMeasures allRegistrationMeasures;

    @Autowired
    AllCallLogs allCallLogs;

    @Autowired
    AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    RegistrationLogService registrationLogService;

    @Autowired
    private AllOperators allOperators;

    @After
    @Before
    public void tearDown() {
        allCertificateCourseLogs.removeAll();
        allFrontLineWorkers.removeAll();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(LocationDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
        template.deleteAll(template.loadAll(CourseItemMeasure.class));
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(CallDurationMeasure.class));
        template.deleteAll(template.loadAll(RegistrationMeasure.class));
        template.deleteAll(template.loadAll(LanguageDimension.class));
    }

    @Test
    public void shouldBindToTheCorrectHandlerForCertificateCourseDataEvent() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        //TODO Context is missing from platform starting 12 SNAPSHOT. Fix this.
//        EventListenerRegistry registry = Context.getInstance().getEventListenerRegistry();
//        Set<EventListener> listeners = registry.getListeners(ReportPublishEventKeys.CERTIFICATE_COURSE_CALL_MESSAGE);
//
//        MotechListenerAbstractProxy motechListenerAbstractProxy = (MotechListenerAbstractProxy) listeners.toArray()[0];
//        Field declaredField = MotechListenerAbstractProxy.class.getDeclaredField("method");
//        declaredField.setAccessible(true);
//        Method handler = (Method) declaredField.get(motechListenerAbstractProxy);
//
//        assertEquals(1, listeners.size());
//        assertEquals(CertificateCourseDataHandler.class,handler.getDeclaringClass());
//        assertEquals("handleCertificateCourseData",handler.getName());
    }


    @Test
    public void shouldMapCertificateCourseLogsToCourseItemMeasure() {
        String callId = "919986574410-12345678";
        String calledNumber = "57889";
        String callerId = "919986574410";
        String contentName = "Chapter 1";
        String contentId = "contentId";
        String operatorName = "airtel";
        String language = "hindi";

        DateTime now = DateTime.now();
        DateTime callStartTime = now;
        DateTime callEndTime = now.plusSeconds(20);
        DateTime certificateCourseStartTime = now.plusSeconds(5);
        DateTime certificateCourseEndTime = now.plusSeconds(15);

        Location location = new Location("", "", "", "", 1, 0, 0, 0, null, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, null, "", Designation.AWW, location, language, null, UUID.randomUUID());
        frontLineWorker.setRegisteredDate(now);
        frontLineWorker.setOperator(operatorName);
        allFrontLineWorkers.add(frontLineWorker);
        registrationLogService.add(new RegistrationLog(callId, callerId, "", ""));
        allOperators.add(new Operator(operatorName, 39 * 60 * 1000, 0, 60000));

        LocationDimension locationDimension = new LocationDimension("S01D000B000V000", "", "", "", "", "VALID");
        allLocationDimensions.saveOrUpdate(locationDimension);
        LanguageDimension languageDimension = new LanguageDimension(language, "hin", "badhai ho...");
        allLanguageDimension.addOrUpdate(languageDimension);
        TimeDimension callStartTimeDimension = allTimeDimensions.addOrUpdate(callStartTime);

        CallLog callLog = new CallLog(callId, callerId, calledNumber);
        callLog.addItem(new CallLogItem(CallFlowType.CALL, callStartTime, callEndTime));
        callLog.addItem(new CallLogItem(CallFlowType.CERTIFICATECOURSE, certificateCourseStartTime, certificateCourseEndTime));
        allCallLogs.add(callLog);

        CertificationCourseLog courseLog = new CertificationCourseLog(callerId, calledNumber, "", callId, "", language);
        courseLog.addCourseLogItem(new CertificationCourseLogItem(contentId, CourseItemType.CHAPTER, contentName, "",
                CourseItemState.START, certificateCourseStartTime, language));
        courseLog.addCourseLogItem(new CertificationCourseLogItem(contentId, CourseItemType.CHAPTER, contentName, "",
                CourseItemState.END, certificateCourseEndTime, language));
        allCertificateCourseLogs.add(courseLog);
        allCourseItemDimensions.add(new CourseItemDimension(contentName, contentId, CourseItemType.CHAPTER, null));

        TimeDimension certificateCourseStartTimeDimension = allTimeDimensions.addOrUpdate(certificateCourseStartTime);
        TimeDimension certificateCourseEndTimeDimension = allTimeDimensions.addOrUpdate(certificateCourseEndTime);


        CallMessage logData = new CallMessage(CallMessageType.CERTIFICATE_COURSE_DATA, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleCertificateCourseData(event);

        List<RegistrationMeasure> registrationMeasures = template.loadAll(RegistrationMeasure.class);
        RegistrationMeasure registrationMeasure = registrationMeasures.get(0);
        assertEquals(Long.valueOf(callerId), registrationMeasure.getFrontLineWorkerDimension().getMsisdn());

        List<CallDurationMeasure> callDurationMeasures = template.loadAll(CallDurationMeasure.class);

        assertEquals(2, callDurationMeasures.size());
        assertThat(callDurationMeasures, hasItems(callDurationMeasureMatcher(callId, callerId, locationDimension.getId(), callStartTimeDimension.getId(), 20, "CALL", new Timestamp(callStartTime.getMillis()), new Timestamp(callEndTime.getMillis()), 1)));
        assertThat(callDurationMeasures, hasItems(callDurationMeasureMatcher(callId, callerId, locationDimension.getId(), callStartTimeDimension.getId(), 10, "CERTIFICATECOURSE", new Timestamp(certificateCourseStartTime.getMillis()), new Timestamp(certificateCourseEndTime.getMillis()), 1)));

        List<CourseItemMeasure> courseItemMeasures = template.loadAll(CourseItemMeasure.class);

        assertEquals(2, courseItemMeasures.size());
        assertThat(courseItemMeasures, hasItems(courseItemMeasureMatcher(CourseItemState.START, callerId,
                certificateCourseStartTimeDimension.getId(), contentName)));
        assertThat(courseItemMeasures, hasItems(courseItemMeasureMatcher(CourseItemState.END, callerId,
                certificateCourseEndTimeDimension.getId(), contentName)));

        List<CourseItemDimension> courseItemDimensions = template.loadAll(CourseItemDimension.class);
        assertEquals(1, courseItemDimensions.size());
        CourseItemDimension courseItemDimension = courseItemDimensions.get(0);
        assertEquals(contentId, courseItemDimension.getContentId());
        assertEquals(contentName, courseItemDimension.getName());
        assertEquals(CourseItemType.CHAPTER, courseItemDimension.getType());

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = template.loadAll(FrontLineWorkerDimension.class);
        FrontLineWorkerDimension frontLineWorkerDimension = frontLineWorkerDimensions.get(0);
        assertEquals(Long.valueOf(callerId), frontLineWorkerDimension.getMsisdn());
        assertNull(allCertificateCourseLogs.findByCallId(callId));
    }

    private Matcher<CourseItemMeasure> courseItemMeasureMatcher(final CourseItemState event, final String msisdn, final Integer timeDimensionId, final String courseItemName) {
        return new BaseMatcher<CourseItemMeasure>() {
            @Override
            public boolean matches(Object o) {
                CourseItemMeasure o1 = (CourseItemMeasure) o;
                return o1.getEvent() == event &&
                        o1.getFrontLineWorkerDimension().getMsisdn().equals(Long.valueOf(msisdn)) &&
                        o1.getTimeDimension().getId().equals(timeDimensionId) &&
                        o1.getCourseItemDimension().getName().equals(courseItemName);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private Matcher<CallDurationMeasure> callDurationMeasureMatcher(final String callId,
                                                                    final String callerId, final Integer locationId, final Integer timeId,
                                                                    final int duration, final String type, final Timestamp startTime,
                                                                    final Timestamp endTime, final Integer durationInPulse) {

        return new BaseMatcher<CallDurationMeasure>() {
            @Override
            public boolean matches(Object o) {
                CallDurationMeasure o1 = (CallDurationMeasure) o;
                return o1.getLocationDimension().getId().equals(locationId) &&
                        o1.getTimeDimension().getId().equals(timeId) &&
                        o1.getCallId().equals(callId) &&
                        o1.getDuration() == duration &&
                        o1.getFrontLineWorkerDimension().getMsisdn().equals(Long.valueOf(callerId)) &&
                        o1.getType().equals(type) &&
                        o1.getStartTime().equals(startTime) &&
                        o1.getEndTime().equals(endTime) &&
                        o1.getDurationInPulse().equals(durationInPulse);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
