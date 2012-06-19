package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.domain.dimension.*;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.measure.CourseAudioTrackerMeasureService;

import java.sql.Timestamp;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CourseAudioTrackerMeasureServiceTest {

    @Mock
    private ReportDB reportDB;
    @Mock
    private AllTimeDimensions allTimeDimensions;
    @Mock
    private AllCourseItemDimensions allCourseItemDimensions;
    @Mock
    private AudioTrackerLogService audioTrackerLogService;
    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;

    private String callId = "callId";
    private String callerId = "123456789";
    private Integer flw_id = 1;
    private DateTime now = DateTime.now();

    private FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
    private LocationDimension locationDimension = new LocationDimension();
    private TimeDimension timeDimension;
    private RegistrationMeasure registrationMeasure;
    private CourseAudioTrackerMeasureService courseAudioTrackerMeasureService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        frontLineWorkerDimension = new FrontLineWorkerDimension();
        frontLineWorkerDimension.setId(flw_id);

        locationDimension = new LocationDimension("locationId", "district", "block", "panchayat");
        timeDimension = new TimeDimension();

        registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, callId);

        courseAudioTrackerMeasureService = new CourseAudioTrackerMeasureService(
                reportDB, allTimeDimensions, allCourseItemDimensions, audioTrackerLogService, allFrontLineWorkerDimensions, allRegistrationMeasures);
    }

    @Test
    public void shouldDeleteAudioTrackerLogAndReturnWhenItemsAreEmpty() {
        String contentName = "Chapter 1";
        CourseItemType contentType = CourseItemType.CHAPTER;
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.CERTIFICATE_COURSE);

        when(audioTrackerLogService.getLogFor(callId)).thenReturn(audioTrackerLog);

        courseAudioTrackerMeasureService.createFor(callId);

        verify(allTimeDimensions, never()).getFor(now);
        verify(allCourseItemDimensions, never()).getFor(contentName, contentType);
        verify(audioTrackerLogService).remove(audioTrackerLog);
    }

    @Test
    public void shouldSaveAudioTrackerLogToCourseItemMeasure() {
        int duration = 38;
        int totalCourseDuration = 1000;
        String contentId = "contentId";

        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.CERTIFICATE_COURSE);
        DateTime dateTime = DateTime.parse("2012-04-29T09:38:49Z");

        AudioTrackerLogItem audioTrackerLogItem = new AudioTrackerLogItem(contentId, dateTime, duration);
        audioTrackerLog.addItem(audioTrackerLogItem);

        TimeDimension timeDimension = new TimeDimension(dateTime);
        CourseItemDimension courseItemDimension = new CourseItemDimension("blah", contentId, CourseItemType.AUDIO, null, "filename", totalCourseDuration);

        when(audioTrackerLogService.getLogFor(callId)).thenReturn(audioTrackerLog);
        when(allTimeDimensions.getFor(any(DateTime.class))).thenReturn(timeDimension);
        when(allCourseItemDimensions.getFor(audioTrackerLogItem.getContentId())).thenReturn(courseItemDimension);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flw_id)).thenReturn(registrationMeasure);
        courseAudioTrackerMeasureService.createFor(callId);

        ArgumentCaptor<CourseItemMeasure> captor = ArgumentCaptor.forClass(CourseItemMeasure.class);
        verify(audioTrackerLogService).remove(audioTrackerLog);
        verify(reportDB).add(captor.capture());
        CourseItemMeasure courseItemMeasure = captor.getValue();

        assertEquals(frontLineWorkerDimension, courseItemMeasure.getFrontLineWorkerDimension());
        assertEquals(locationDimension, courseItemMeasure.getLocationDimension());
        assertEquals(timeDimension, courseItemMeasure.getTimeDimension());
        assertEquals(new Timestamp(dateTime.getMillis()), courseItemMeasure.getTimestamp());
        assertEquals(4, (int) courseItemMeasure.getPercentage());
    }

    @Test
    public void shouldDoNothingIfAudioTrackerLogIsNull() {
        courseAudioTrackerMeasureService.createFor(callId);
        verify(reportDB, never()).add(any(CourseItemMeasure.class));
    }

}
