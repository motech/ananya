package org.motechproject.ananya.service.integrationtests;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.CertificationCourseLogItem;
import org.motechproject.ananya.domain.CourseItemState;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.handler.CertificateCourseDataHandler;
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
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
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

    @After
    public void tearDown(){
        allCertificateCourseLogs.removeAll();
        template.deleteAll(template.loadAll(FrontLineWorkerDimension.class));
        template.deleteAll(template.loadAll(CourseItemMeasure.class));
        template.deleteAll(template.loadAll(CourseItemDimension.class));
        template.deleteAll(template.loadAll(TimeDimension.class));
    }

    @Test
    public void shouldBindToTheCorrectHandlerForCertificateCourseDataEvent() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        EventListenerRegistry registry = Context.getInstance().getEventListenerRegistry();
        Set<EventListener> listeners = registry.getListeners(ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY);

        MotechListenerAbstractProxy motechListenerAbstractProxy = (MotechListenerAbstractProxy) listeners.toArray()[0];
        Field declaredField = MotechListenerAbstractProxy.class.getDeclaredField("method");
        declaredField.setAccessible(true);
        Method handler = (Method) declaredField.get(motechListenerAbstractProxy);

        assertEquals(1, listeners.size());
        assertEquals(CertificateCourseDataHandler.class,handler.getDeclaringClass());
        assertEquals("handleCertificateCourseData",handler.getName());
    }


    @Test
    public void shouldMapCallLogToCallDurationMeasure() {
        String callId = "callId";
        String calledNumber = "123";
        String msisdn = "1923456";
        String contentName = "Chapter 1";
        String contentId = "contentId";

        DateTime now = DateTime.now();
        CertificationCourseLog courseLog = new CertificationCourseLog(msisdn, calledNumber, null, null, "", callId, "");
        courseLog.addCourseLogItem(new CertificationCourseLogItem(contentId,CourseItemType.CHAPTER, contentName,"",CourseItemState.START, now));
        courseLog.addCourseLogItem(new CertificationCourseLogItem(contentId,CourseItemType.CHAPTER, contentName,"",CourseItemState.END, now.plusDays(26)));
        allCertificateCourseLogs.add(courseLog);
        allCertificateCourseLogs.add(new CertificationCourseLog(msisdn, calledNumber, null, null, "", "someOtherCallId", ""));

        TimeDimension timeDimension1 = allTimeDimensions.makeFor(now);
        TimeDimension timeDimension2 = allTimeDimensions.makeFor(now.plusDays(26));

        LogData logData = new LogData(LogType.CERTIFICATE_COURSE_DATA, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleCertificateCourseData(event);

        List<CourseItemMeasure> courseItemMeasures = template.loadAll(CourseItemMeasure.class);

        assertEquals(2, courseItemMeasures.size());
        assertThat(courseItemMeasures, hasItems(callDurationMeasureMatcher(CourseItemState.START, msisdn, timeDimension1.getId(),contentName)));
        assertThat(courseItemMeasures, hasItems(callDurationMeasureMatcher(CourseItemState.END, msisdn, timeDimension2.getId(),contentName)));

        List<CourseItemDimension> courseItemDimensions = template.loadAll(CourseItemDimension.class);
        assertEquals(1, courseItemDimensions.size());
        CourseItemDimension courseItemDimension = courseItemDimensions.get(0);
        assertEquals(contentId, courseItemDimension.getContentId());
        assertEquals(contentName, courseItemDimension.getName());
        assertEquals(CourseItemType.CHAPTER, courseItemDimension.getType());

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = template.loadAll(FrontLineWorkerDimension.class);
        FrontLineWorkerDimension frontLineWorkerDimension = frontLineWorkerDimensions.get(0);
        assertEquals(Long.valueOf(msisdn),frontLineWorkerDimension.getMsisdn());
        assertNull(allCertificateCourseLogs.findByCallId(callId));
    }

    private Matcher<CourseItemMeasure> callDurationMeasureMatcher(final CourseItemState event, final String msisdn, final Integer timeDimensionId, final String courseItemName) {
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
}
