package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.service.SMSLogService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendSMSActionTest {

    @Mock
    private SMSLogService smsLogService;

    @Mock
    private FrontLineWorker mockedFrontLineWorker;

    @Mock
    private CertificateCourseStateRequestList mockedStateRequestList;

    private String callId = "9988776655-12899";
    private String callerId = "9988776655";
    private String locationId = "S00";
    private int courseAttempts = 1;

    private SendSMSAction sendSMSAction;

    @Before
    public void setUp() {
        initMocks(this);
        sendSMSAction = new SendSMSAction(smsLogService);

        when(mockedFrontLineWorker.getMsisdn()).thenReturn(callerId);
        when(mockedFrontLineWorker.getLocationId()).thenReturn(locationId);
        when(mockedFrontLineWorker.currentCourseAttempts()).thenReturn(courseAttempts);
        when(mockedStateRequestList.getCallId()).thenReturn(callId);
    }

    @Test
    public void shouldSendSMSIfCourseInProgressAndCoursePassedAndFLWListenedToPlayCourseResult() {
        //Given
        when(mockedFrontLineWorker.courseInProgress()).thenReturn(true);
        when(mockedFrontLineWorker.hasPassedTheCourse()).thenReturn(true);
        when(mockedStateRequestList.hasCourseCompletionInteraction()).thenReturn(true);

        //When
        sendSMSAction.process(mockedFrontLineWorker, mockedStateRequestList);

        //Then
        assertIncrementAttemptAndSMSLogCreated();
    }

    @Test
    public void shouldNotSendSMSIfCourseNotInProgressAndCoursePassedAndFLWListenedToPlayCourseResult() {
        when(mockedFrontLineWorker.courseInProgress()).thenReturn(false);
        when(mockedFrontLineWorker.hasPassedTheCourse()).thenReturn(true);
        when(mockedStateRequestList.hasCourseCompletionInteraction()).thenReturn(true);

        sendSMSAction.process(mockedFrontLineWorker, mockedStateRequestList);

        assertNoIncrementAttemptAndNoSMSLogCreated();
    }

    @Test
    public void shouldNotSendSMSIfCourseInProgressAndCourseNotPassedAndFLWListenedToPlayCourseResult() {
        when(mockedFrontLineWorker.courseInProgress()).thenReturn(true);
        when(mockedFrontLineWorker.hasPassedTheCourse()).thenReturn(false);
        when(mockedStateRequestList.hasCourseCompletionInteraction()).thenReturn(true);

        sendSMSAction.process(mockedFrontLineWorker, mockedStateRequestList);

        assertNoIncrementAttemptAndNoSMSLogCreated();
    }

    @Test
    public void shouldNotSendSMSIfCourseInProgressAndCoursePassedAndFLWNotListenedToPlayCourseResult() {
        when(mockedFrontLineWorker.courseInProgress()).thenReturn(true);
        when(mockedFrontLineWorker.hasPassedTheCourse()).thenReturn(true);
        when(mockedStateRequestList.hasCourseCompletionInteraction()).thenReturn(false);

        sendSMSAction.process(mockedFrontLineWorker, mockedStateRequestList);

        assertNoIncrementAttemptAndNoSMSLogCreated();
    }

    private void assertNoIncrementAttemptAndNoSMSLogCreated() {
        verify(mockedFrontLineWorker, never()).incrementCertificateCourseAttempts();
        verify(smsLogService, never()).add(Matchers.<SMSLog>any());
    }

    private void assertIncrementAttemptAndSMSLogCreated() {
        verify(mockedFrontLineWorker).incrementCertificateCourseAttempts();

        ArgumentCaptor<SMSLog> smsLogArgumentCaptor = ArgumentCaptor.forClass(SMSLog.class);
        verify(smsLogService).add(smsLogArgumentCaptor.capture());
        SMSLog smsLogArgumentCaptorValue = smsLogArgumentCaptor.getValue();

        assertThat(smsLogArgumentCaptorValue.getCallId(), is(callId));
        assertThat(smsLogArgumentCaptorValue.getCallerId(), is(callerId));
        assertThat(smsLogArgumentCaptorValue.getLocationId(), is(locationId));
        assertThat(smsLogArgumentCaptorValue.getCourseAttempts(), is(courseAttempts));
    }
}
