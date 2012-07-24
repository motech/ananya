package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.service.SMSLogService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class SendSMSActionTest {

    @Mock
    private SMSLogService smsLogService;

    private SendSMSAction sendSMSAction;

    @Before
    public void setUp() {
        initMocks(this);
        sendSMSAction = new SendSMSAction(smsLogService);
    }

    @Test
    public void shouldSendSMSIfCoursePassed() {
        String callId = "123456";
        String callerId = "919986574410";

        ReportCard reportCard = mock(ReportCard.class);
        when(reportCard.totalScore()).thenReturn(FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE + 1);

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "airtel", "circle");
        ReflectionTestUtils.setField(frontLineWorker, "reportCard", reportCard);

        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList(callId, callerId);
        String json = "{\"result\":true,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"playCourseResult\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}";
        stateRequestList.add(json, "1");

        sendSMSAction.process(frontLineWorker, stateRequestList);

        ArgumentCaptor<SMSLog> captor = ArgumentCaptor.forClass(SMSLog.class);
        verify(smsLogService).add(captor.capture());
        SMSLog captured = captor.getValue();

        assertThat(captured.getCallerId(), is(callerId));
        assertThat(captured.getCallId(), is(callId));
        assertThat(captured.getCourseAttempts(), is(0));
    }
}
