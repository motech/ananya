package org.motechproject.ananya.webservice;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.service.FrontLineWorkerService;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class SendSMSClientTest {

    private SendSMSClient sendSMSClient;

    @Mock
    private OnMobileSendSMSService onMobileSendSMSService;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;

    @Before
    public void setUp(){
        initMocks(this);
        sendSMSClient = new SendSMSClient(onMobileSendSMSService,frontLineWorkerService);
    }

    @Test
    public void shouldSendSingleSMS(){
        String mobileNumber = "9876543210";
        String smsMessage = "Hello";
        String smsRefNum = "141241";

        when(onMobileSendSMSService.singlePush(argThat(is(mobileNumber)),argThat(is(SendSMSClient.SENDER_ID)),argThat(is(smsRefNum)))).thenReturn("success");

        sendSMSClient.sendSingleSMS(mobileNumber, smsMessage, smsRefNum);

        verify(frontLineWorkerService).addSMSReferenceNumber(argThat(is(mobileNumber)), argThat(is(smsRefNum)));
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenSendSingleSMSFails(){
        String mobileNumber = "9876543210";
        String smsMessage = "Hello";
        String smsRefNum = "141241";

        when(onMobileSendSMSService.singlePush(argThat(is(mobileNumber)),argThat(is(SendSMSClient.SENDER_ID)),argThat(is(smsMessage)))).thenReturn("failure");
        try {
            sendSMSClient.sendSingleSMS(mobileNumber, smsMessage, smsRefNum);
        } catch(RuntimeException e){
            assertTrue(true);
            return;
        }

        assertFalse(true);
    }

}
