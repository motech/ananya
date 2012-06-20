package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.measure.SMSSentMeasureService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSSentMeasureServiceTest {

    private SMSSentMeasureService service;

    @Mock
    FrontLineWorkerService frontLineWorkerService;

    @Mock
    AllFrontLineWorkerDimensions frontLineWorkerDimensions;

    @Mock
    AllTimeDimensions timeDimensions;

    @Mock
    AllRegistrationMeasures allRegistrationMeasures;

    @Mock
    ReportDB db;

    @Before
    public void setUp() {
        initMocks(this);
        service = new SMSSentMeasureService(db, frontLineWorkerDimensions, timeDimensions, frontLineWorkerService, allRegistrationMeasures);
    }

    @Test
    public void shouldCreateSMSSentMeasureWhenSMSIsSent() {
        String callerId = "9876543210";
        Integer courseAttemptNum = 1;
        String smsRefNum = "41413";
        String flwId = "77abcd";
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(Long.valueOf(callerId), "", "", "", "", "");
        frontLineWorkerDimension.setId(1);
        LocationDimension locationDimension = new LocationDimension("", "", "", "");
        TimeDimension timeDimension = new TimeDimension(DateTime.now());

        when(frontLineWorkerService.getCurrentCourseAttempt(callerId)).thenReturn(courseAttemptNum);
        SMSReference smsReference = new SMSReference(callerId,flwId);
        smsReference.add(smsRefNum, courseAttemptNum);
        when(frontLineWorkerService.getSMSReferenceNumber(callerId)).thenReturn(smsReference);
        when(frontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(timeDimensions.getFor(any(DateTime.class))).thenReturn(timeDimension);
        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "");
        when(allRegistrationMeasures.fetchFor(1)).thenReturn(registrationMeasure);

        service.createSMSSentMeasure(callerId);

        ArgumentCaptor<SMSSentMeasure> captor = ArgumentCaptor.forClass(SMSSentMeasure.class);
        verify(db).add(captor.capture());

        SMSSentMeasure smsSentMeasure = captor.getValue();
        assertEquals(smsSentMeasure.getSmsReferenceNumber(), smsRefNum);
        assertEquals(smsSentMeasure.getFrontLineWorkerDimension().getMsisdn(), Long.valueOf(callerId));
        assertTrue(smsSentMeasure.getSmsSent());
        assertEquals(smsSentMeasure.getCourseAttempt(),courseAttemptNum);
    }

    @Test
    public void shouldCreateSMSSentMeasureWithUpdatedCourseAttemptWhenSMSIsNotSent() {
        String callerId = "9876543210";
        Integer courseAttemptNum = 1;
        String flwId = "77abcd";
        int flwd_id = 1;

        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(Long.valueOf(callerId), "", "", "", "", "");
        frontLineWorkerDimension.setId(flwd_id);
        LocationDimension locationDimension = new LocationDimension("", "", "", "");
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        SMSReference smsReference = new SMSReference(callerId, flwId);


        when(frontLineWorkerService.getCurrentCourseAttempt(callerId)).thenReturn(courseAttemptNum);
        when(frontLineWorkerService.getSMSReferenceNumber(callerId)).thenReturn(smsReference);
        when(frontLineWorkerDimensions.fetchFor(Long.valueOf(callerId))).thenReturn(frontLineWorkerDimension);
        when(timeDimensions.getFor(any(DateTime.class))).thenReturn(timeDimension);

        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, "");
        when(allRegistrationMeasures.fetchFor(flwd_id)).thenReturn(registrationMeasure);

        service.createSMSSentMeasure(callerId);

        ArgumentCaptor<SMSSentMeasure> captor = ArgumentCaptor.forClass(SMSSentMeasure.class);
        verify(db).add(captor.capture());

        SMSSentMeasure smsSentMeasure = captor.getValue();
        assertEquals(null ,smsSentMeasure.getSmsReferenceNumber());
        assertEquals(Long.valueOf(callerId) , smsSentMeasure.getFrontLineWorkerDimension().getMsisdn());
        assertFalse(smsSentMeasure.getSmsSent());
        assertEquals(smsSentMeasure.getCourseAttempt(),courseAttemptNum);
    }
}
