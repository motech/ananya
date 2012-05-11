package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;

import java.sql.Timestamp;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CourseItemMeasureServiceTest {

    @Mock
    private ReportDB reportDB;
    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Mock
    private AllTimeDimensions allTimeDimensions;
    @Mock
    private AllCourseItemDimensions allCourseItemDimensions;
    @Mock
    private CertificateCourseLogService certificateCourseLogService;
    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;
    @Mock
    private AudioTrackerLogService audioTrackerLogService;

    CourseItemMeasureService courseItemMeasureService;
    String callId;
    String callerId;
    String calledNumber;
    DateTime now;
    private TimeDimension timeDimension;
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private LocationDimension locationDimension;
    private RegistrationMeasure registrationMeasure;
    private int flw_id;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        callId = "callId";
        callerId = "123456789";
        calledNumber = "1234";
        now = DateTime.now();
        courseItemMeasureService = new CourseItemMeasureService(reportDB,
                allFrontLineWorkerDimensions, allTimeDimensions, allCourseItemDimensions,
                certificateCourseLogService, audioTrackerLogService, allRegistrationMeasures);

        timeDimension = new TimeDimension();
        frontLineWorkerDimension = new FrontLineWorkerDimension();
        flw_id = 1;
        frontLineWorkerDimension.setId(flw_id);
        locationDimension = new LocationDimension();
        registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension);
    }

    @Test
    public void shouldSaveCourseItemMeasure() {
        String contentName = "Chapter 1";
        String contentId = "contentId";
        CourseItemType contentType = CourseItemType.CHAPTER;
        CourseItemState event = CourseItemState.START;

        CertificationCourseLog certificationCourseLog = new CertificationCourseLog(callerId, calledNumber, "", callId, "");
        certificationCourseLog.addCourseLogItem(new CertificationCourseLogItem(contentId, contentType, contentName, null, event, now));

        CourseItemDimension courseItemDimension = new CourseItemDimension();

        when(certificateCourseLogService.getLogFor(callId)).thenReturn(certificationCourseLog);
        when(allTimeDimensions.getFor(now)).thenReturn(timeDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allCourseItemDimensions.getFor(contentName, contentType)).thenReturn(courseItemDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flw_id)).thenReturn(registrationMeasure);

        courseItemMeasureService.createCourseItemMeasure(callId);

        ArgumentCaptor<CourseItemMeasure> captor = ArgumentCaptor.forClass(CourseItemMeasure.class);
        verify(reportDB).add(captor.capture());

        CourseItemMeasure courseItemMeasure = captor.getValue();
        assertEquals(timeDimension, courseItemMeasure.getTimeDimension());
        assertEquals(frontLineWorkerDimension, courseItemMeasure.getFrontLineWorkerDimension());
        assertEquals(courseItemDimension, courseItemMeasure.getCourseItemDimension());
        assertEquals(event, courseItemMeasure.getEvent());
        assertEquals(null, courseItemMeasure.getScore());
    }

    @Test
    public void shouldSaveCourseItemWithScore() {
        String contentName = "Chapter 1";
        String contentId = "contentId";
        CourseItemType contentType = CourseItemType.QUIZ;
        CourseItemState event = CourseItemState.START;
        CertificationCourseLog certificationCourseLog = new CertificationCourseLog(callerId, calledNumber, "", callId, "");
        certificationCourseLog.addCourseLogItem(new CertificationCourseLogItem(contentId, contentType, contentName, "3", event, now));

        CourseItemDimension courseItemDimension = new CourseItemDimension();

        when(certificateCourseLogService.getLogFor(callId)).thenReturn(certificationCourseLog);
        when(allTimeDimensions.getFor(now)).thenReturn(timeDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allCourseItemDimensions.getFor(contentName, contentType)).thenReturn(courseItemDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flw_id)).thenReturn(registrationMeasure);

        courseItemMeasureService.createCourseItemMeasure(callId);

        ArgumentCaptor<CourseItemMeasure> captor = ArgumentCaptor.forClass(CourseItemMeasure.class);
        verify(reportDB).add(captor.capture());

        CourseItemMeasure courseItemMeasure = captor.getValue();
        assertEquals(Integer.valueOf(3), courseItemMeasure.getScore());
    }

    @Test
    public void shouldSaveMultipleCourseItem() {
        String contentName1 = "Chapter 1", contentId1 = "contentId1";
        CourseItemType contentType1 = CourseItemType.QUIZ;
        String contentName2 = "Chapter 2", contentId2 = "contentI21";
        CourseItemType contentType2 = CourseItemType.COURSE;
        CourseItemState event = CourseItemState.START;

        TimeDimension timeDimension = new TimeDimension();
        CourseItemDimension courseItemDimension1 = new CourseItemDimension();
        CourseItemDimension courseItemDimension2 = new CourseItemDimension();

        CertificationCourseLog certificationCourseLog = new CertificationCourseLog(callerId, calledNumber, "", callId, "");
        certificationCourseLog.addCourseLogItem(new CertificationCourseLogItem(contentId1, contentType1, contentName1, "3", event, now));
        certificationCourseLog.addCourseLogItem(new CertificationCourseLogItem(contentId2, contentType2, contentName2, "", event, now.plusDays(5)));


        when(certificateCourseLogService.getLogFor(callId)).thenReturn(certificationCourseLog);
        when(allTimeDimensions.getFor(now)).thenReturn(timeDimension);
        when(allTimeDimensions.getFor(now.plusDays(5))).thenReturn(timeDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allCourseItemDimensions.getFor(contentName1, contentType1)).thenReturn(courseItemDimension1);
        when(allCourseItemDimensions.getFor(contentName2, contentType2)).thenReturn(courseItemDimension2);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flw_id)).thenReturn(registrationMeasure);

        courseItemMeasureService.createCourseItemMeasure(callId);

        ArgumentCaptor<CourseItemMeasure> captor = ArgumentCaptor.forClass(CourseItemMeasure.class);
        verify(reportDB, times(2)).add(captor.capture());

        List<CourseItemMeasure> allValues = captor.getAllValues();
        CourseItemMeasure courseItemMeasure1 = allValues.get(0);
        assertEquals(timeDimension, courseItemMeasure1.getTimeDimension());
        assertEquals(frontLineWorkerDimension, courseItemMeasure1.getFrontLineWorkerDimension());
        assertEquals(courseItemDimension1, courseItemMeasure1.getCourseItemDimension());
        assertEquals(event, courseItemMeasure1.getEvent());
        assertEquals(Integer.valueOf(3), courseItemMeasure1.getScore());

        CourseItemMeasure courseItemMeasure2 = allValues.get(1);
        assertEquals(timeDimension, courseItemMeasure2.getTimeDimension());
        assertEquals(frontLineWorkerDimension, courseItemMeasure2.getFrontLineWorkerDimension());
        assertEquals(courseItemDimension2, courseItemMeasure2.getCourseItemDimension());
        assertEquals(event, courseItemMeasure2.getEvent());
        assertEquals(null, courseItemMeasure2.getScore());
    }

    @Test
    public void shouldDeleteCertificateCourseLogAfterSavingTheCourseItemMeasure() {
        String contentName = "Chapter 1";
        String contentId = "contentId";
        CourseItemType contentType = CourseItemType.CHAPTER;
        CourseItemState event = CourseItemState.START;
        CertificationCourseLog certificationCourseLog = new CertificationCourseLog(callerId, calledNumber, "", callId, "");
        certificationCourseLog.addCourseLogItem(new CertificationCourseLogItem(contentId, contentType, contentName, null, event, now));
        CourseItemDimension courseItemDimension = new CourseItemDimension();

        when(certificateCourseLogService.getLogFor(callId)).thenReturn(certificationCourseLog);
        when(allTimeDimensions.getFor(now)).thenReturn(timeDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allCourseItemDimensions.getFor(contentName, contentType)).thenReturn(courseItemDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flw_id)).thenReturn(registrationMeasure);

        courseItemMeasureService.createCourseItemMeasure(callId);

        verify(certificateCourseLogService).remove(certificationCourseLog);
    }

    @Test
    public void shouldDoNothingWhenNoCertificateCourseLogIsPresentForACallId() {
        when(certificateCourseLogService.getLogFor(callId)).thenReturn(null);

        courseItemMeasureService.createCourseItemMeasure("callId");

        verify(reportDB, never()).add(any(CourseItemMeasure.class));
    }

    @Test
    public void shouldSaveAudioTrackerToCourseItemMeasure() {
        int duration = 38;
        int totalCourseDuration = 1000;
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.CERTIFICATE_COURSE);
        String timeStamp = "2012-04-29T09:38:49Z";
        DateTime dateTime = DateTime.parse(timeStamp);
        AudioTrackerLogItem audioTrackerLogItem = new AudioTrackerLogItem("contentid", dateTime, duration);
        audioTrackerLog.addItem(audioTrackerLogItem);
        when(certificateCourseLogService.getLogFor(callId)).thenReturn(null);
        when(audioTrackerLogService.getLogFor(callId)).thenReturn(audioTrackerLog);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        TimeDimension timeDimension = new TimeDimension(dateTime);
        when(allTimeDimensions.getFor(any(DateTime.class))).thenReturn(timeDimension);
        CourseItemDimension courseItemDimension = new CourseItemDimension("blah", "contentid", CourseItemType.AUDIO, null, "filename", totalCourseDuration);
        when(allCourseItemDimensions.getFor(audioTrackerLogItem.getContentId())).thenReturn(courseItemDimension);

        when(allRegistrationMeasures.fetchFor(flw_id)).thenReturn(registrationMeasure);


        courseItemMeasureService.createCourseItemMeasure(callId);

        ArgumentCaptor<CourseItemMeasure> captor = ArgumentCaptor.forClass(CourseItemMeasure.class);
        verify(reportDB).add(captor.capture());
        CourseItemMeasure courseItemMeasure = captor.getValue();
        assertEquals(frontLineWorkerDimension, courseItemMeasure.getFrontLineWorkerDimension());
        assertEquals(locationDimension, courseItemMeasure.getLocationDimension());
        assertEquals(timeDimension, courseItemMeasure.getTimeDimension());
        assertEquals(new Timestamp(dateTime.getMillis()), courseItemMeasure.getTimestamp());
        assertEquals(4, (int) courseItemMeasure.getPercentage());
        verify(audioTrackerLogService).remove(audioTrackerLog);
    }

    @Test
    public void shouldDoNothingIfAudioTrackerLogIsNull() {
        when(certificateCourseLogService.getLogFor(callId)).thenReturn(null);
        when(audioTrackerLogService.getLogFor(callId)).thenReturn(null);

        courseItemMeasureService.createCourseItemMeasure(callId);

        verify(reportDB, never()).add(any(CourseItemMeasure.class));
    }
}
