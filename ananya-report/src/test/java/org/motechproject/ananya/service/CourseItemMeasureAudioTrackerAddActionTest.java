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
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;

import java.sql.Timestamp;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CourseItemMeasureAudioTrackerAddActionTest {

    @Mock
    private ReportDB reportDB;
    @Mock
    private AllTimeDimensions allTimeDimensions;
    @Mock
    private AllCourseItemDimensions allCourseItemDimensions;
    @Mock
    private AudioTrackerLogService audioTrackerLogService;

    String callId;
    String callerId;
    String calledNumber;
    DateTime now;
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private LocationDimension locationDimension;
    private int flw_id;
    private CourseItemMeasureAudioTrackerAddAction courseItemMeasureAudioTrackerAddAction;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        callId = "callId";
        callerId = "123456789";
        calledNumber = "1234";
        now = DateTime.now();
        courseItemMeasureAudioTrackerAddAction = new CourseItemMeasureAudioTrackerAddAction(
                reportDB,  allTimeDimensions,  allCourseItemDimensions, audioTrackerLogService);

        frontLineWorkerDimension = new FrontLineWorkerDimension();
        flw_id = 1;
        frontLineWorkerDimension.setId(flw_id);
        locationDimension = new LocationDimension();
    }

    @Test
    public void shouldDeleteCourseLogAndAudioTrackerLogAndReturnWhenItemsAreEmpty() {
        String contentName = "Chapter 1";
        CourseItemType contentType = CourseItemType.CHAPTER;
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.CERTIFICATE_COURSE);

        courseItemMeasureAudioTrackerAddAction.process(callId, audioTrackerLog, frontLineWorkerDimension, locationDimension);

        verify(allTimeDimensions, never()).getFor(now);
        verify(allCourseItemDimensions, never()).getFor(contentName, contentType);
        verify(audioTrackerLogService).remove(audioTrackerLog);
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
        TimeDimension timeDimension = new TimeDimension(dateTime);
        CourseItemDimension courseItemDimension = new CourseItemDimension("blah", "contentid", CourseItemType.AUDIO, null, "filename", totalCourseDuration);

        when(allTimeDimensions.getFor(any(DateTime.class))).thenReturn(timeDimension);
        when(allCourseItemDimensions.getFor(audioTrackerLogItem.getContentId())).thenReturn(courseItemDimension);

        courseItemMeasureAudioTrackerAddAction.process(callId, audioTrackerLog, frontLineWorkerDimension, locationDimension);

        ArgumentCaptor<CourseItemMeasure> captor = ArgumentCaptor.forClass(CourseItemMeasure.class);
        verify(reportDB).add(captor.capture());
        CourseItemMeasure courseItemMeasure = captor.getValue();
        verify(audioTrackerLogService).remove(audioTrackerLog);

        assertEquals(frontLineWorkerDimension, courseItemMeasure.getFrontLineWorkerDimension());
        assertEquals(locationDimension, courseItemMeasure.getLocationDimension());
        assertEquals(timeDimension, courseItemMeasure.getTimeDimension());
        assertEquals(new Timestamp(dateTime.getMillis()), courseItemMeasure.getTimestamp());
        assertEquals(4, (int) courseItemMeasure.getPercentage());
    }

    @Test
    public void shouldDoNothingIfAudioTrackerLogIsNull() {
        courseItemMeasureAudioTrackerAddAction.process(callId, null, frontLineWorkerDimension,  locationDimension);

        verify(reportDB, never()).add(any(CourseItemMeasure.class));
    }

}
