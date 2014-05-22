package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.webservice.SendSMSClient;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendSMSHandlerTest {

    @Mock
    private SendSMSClient smsClient;
    private SendSMSHandler smsHandler;

    @Before
    public void setUp(){
        initMocks(this);
        smsHandler = new SendSMSHandler(smsClient);
    }

    @Test
    public void shouldSendSingleSMS(){
        Map<String, Object> eventParams = new HashMap<String, Object>();
        Map<String, String> myParams = new HashMap<String, String>();
        String mobileNumber = "9876543210";
        myParams.put(SendSMSHandler.PARAMETER_MOBILE_NUMBER, mobileNumber);
        String smsMessage = "Hello";
        myParams.put(SendSMSHandler.PARAMETER_SMS_MESSAGE, smsMessage);
        String smsRefNum = "141241";
        myParams.put(SendSMSHandler.PARAMETER_SMS_REFERENCE_NUMBER, smsRefNum);
        eventParams.put("0", myParams);

        MotechEvent motechEvent = new MotechEvent(SendSMSHandler.SUBJECT_SEND_SINGLE_SMS, eventParams);
        smsHandler.sendSingleSMS(motechEvent);

        verify(smsClient).sendSingleSMS(argThat(is(mobileNumber)), argThat(is(smsMessage)), argThat(is(smsRefNum)));
    }
}
