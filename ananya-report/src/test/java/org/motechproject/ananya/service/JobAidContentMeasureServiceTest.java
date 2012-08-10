package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllJobAidContentMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidContentMeasureServiceTest {

    @Mock
    private AudioTrackerLogService audioTrackerLogService;
    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Mock
    private AllTimeDimensions allTimeDimensions;
    @Mock
    private AllJobAidContentDimensions allJobAidContentDimensions;
    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;
    @Mock
    private AllJobAidContentMeasures allJobAidContentMeasures;

    private JobAidContentMeasureService jobAidContentMeasureService;

    private int flwId = 1;
    private String callerId = "9876543210";
    private String callId = "callId";

    private RegistrationMeasure registrationMeasure;
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private LocationDimension locationDimension;
    private TimeDimension timeDimension;
    private JobAidContentDimension jobAidContentDimension;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        frontLineWorkerDimension = new FrontLineWorkerDimension();
        frontLineWorkerDimension.setId(flwId);

        locationDimension = new LocationDimension("locationId", "district", "block", "panchayat");
        timeDimension = new TimeDimension();
        jobAidContentDimension = new JobAidContentDimension();
        jobAidContentDimension.setDuration(100);

        registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, callId);
        jobAidContentMeasureService = new JobAidContentMeasureService(audioTrackerLogService, allFrontLineWorkerDimensions,
                allRegistrationMeasures, allJobAidContentDimensions, allTimeDimensions, allJobAidContentMeasures);
    }

    @Test
    public void shouldCreateJobAidContentMeasure() {

        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);
        Integer duration = 30;
        String contentId = "contentId";
        DateTime now = DateTime.now();
        audioTrackerLog.items().add(new AudioTrackerLogItem(contentId, now, duration));

        when(audioTrackerLogService.getLogFor(callId)).thenReturn(audioTrackerLog);
        when(allFrontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);
        when(allJobAidContentDimensions.findByContentId(contentId)).thenReturn(jobAidContentDimension);
        when(allTimeDimensions.getFor(now)).thenReturn(timeDimension);

        jobAidContentMeasureService.createFor(callId);

        ArgumentCaptor<JobAidContentMeasure> captor = ArgumentCaptor.forClass(JobAidContentMeasure.class);
        verify(allJobAidContentMeasures).add(captor.capture());
        verify(audioTrackerLogService).remove(audioTrackerLog);

        JobAidContentMeasure jobAidContentMeasure = captor.getValue();
        assertEquals(timeDimension, jobAidContentMeasure.getTimeDimension());
        assertEquals(frontLineWorkerDimension, jobAidContentMeasure.getFrontLineWorkerDimension());
        assertEquals(jobAidContentDimension, jobAidContentMeasure.getJobAidContentDimension());
        assertEquals(locationDimension, jobAidContentMeasure.getLocationDimension());
        assertEquals(callId, jobAidContentMeasure.getCallId());
        assertEquals(30, (int)jobAidContentMeasure.getPercentage());
        assertEquals(duration, jobAidContentMeasure.getDuration());
    }

    @Test
    public void shouldRemoveAudioTrackerLogWhenNoItemsArePresentAndReturn() {
        AudioTrackerLog audioTrackerLog = new AudioTrackerLog(callId, callerId, ServiceType.JOB_AID);

        when(audioTrackerLogService.getLogFor(callId)).thenReturn(audioTrackerLog);

        jobAidContentMeasureService.createFor(callId);

        verify(allFrontLineWorkerDimensions, never()).fetchFor(Long.parseLong(callerId));
        verify(audioTrackerLogService).remove(audioTrackerLog);
    }

    @Test
    public void shouldNotCreateJobAidContentMeasureWhenAudioTrackerLogIsNotPresent(){
        String callId = "12345";
        when(audioTrackerLogService.getLogFor(callId)).thenReturn(null);
        jobAidContentMeasureService.createFor(callId);
        verify(allJobAidContentMeasures,never()).add(any(JobAidContentMeasure.class));
    }

    @Test
    public void shouldGetFilteredFLWs() {
        Date startDate = DateTime.now().toDate();
        Date endDate = DateTime.now().plusDays(1).toDate();
        ArrayList<Long> listOfMsisdns = new ArrayList<Long>();
        when(allJobAidContentMeasures.getFilteredFrontLineWorkerMsisdns(startDate, endDate)).thenReturn(listOfMsisdns);

        List<Long> actualFilteredFLWs = jobAidContentMeasureService.getAllFrontLineWorkerMsisdnsBetween(startDate, endDate);

        assertEquals(listOfMsisdns, actualFilteredFLWs);
    }
}
