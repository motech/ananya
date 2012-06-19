package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.helpers.CourseItemMeasureServiceHelper;
import org.motechproject.ananya.service.measure.CourseItemMeasureAddAction;
import org.motechproject.ananya.service.measure.CourseItemMeasureAudioTrackerAddAction;
import org.motechproject.ananya.service.measure.CourseItemMeasureService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CourseItemMeasureServiceTest {

    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Mock
    private CertificateCourseLogService certificateCourseLogService;
    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;
    @Mock
    private AudioTrackerLogService audioTrackerLogService;
    @Mock
    private CourseItemMeasureAddAction courseItemMeasureAddAction;
    @Mock
    private CourseItemMeasureAudioTrackerAddAction courseItemMeasureAudioTrackerAddAction;

    private CourseItemMeasureService courseItemMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        courseItemMeasureService = new CourseItemMeasureService(allFrontLineWorkerDimensions,
                certificateCourseLogService, audioTrackerLogService,
                allRegistrationMeasures, courseItemMeasureAddAction, courseItemMeasureAudioTrackerAddAction);
    }

    @Test
    public void shouldFetchDimensionsAndCallMeasureActions() {
        String callId = "1231234";
        String callerId = "123123";
        CertificationCourseLog courseLog = new CertificationCourseLog(callerId, "123", "asd", callId, "");
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.CERTIFICATE_COURSE);
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
        frontLineWorkerDimension.setId(1);
        LocationDimension locationDimension = new LocationDimension();
        RegistrationMeasure registrationMeasure = new RegistrationMeasure(
                frontLineWorkerDimension, locationDimension, new TimeDimension(), callId);

        when(certificateCourseLogService.getLogFor(callId)).thenReturn(courseLog);
        when(audioTrackerLogService.getLogFor(callId)).thenReturn(audioTrackerLog);
        when(allFrontLineWorkerDimensions.fetchFor(123123L)).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(1)).thenReturn(registrationMeasure);

        CourseItemMeasureServiceHelper courseItemMeasureServiceHelper =
                courseItemMeasureService.getCourseItemMeasureServiceHelper(callId);

        assertEquals(courseLog, courseItemMeasureServiceHelper.getCourseLog());
        assertEquals(audioTrackerLog, courseItemMeasureServiceHelper.getAudioTrackerLog());
        assertEquals(frontLineWorkerDimension, courseItemMeasureServiceHelper.getFrontLineWorkerDimension());
        assertEquals(locationDimension, courseItemMeasureServiceHelper.getLocationDimension());
    }
}
