package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.handler.SendSMSHandler;
import org.motechproject.context.EventContext;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class SendSMSServiceTest {

    private SendSMSService sendSMSService;

    @Mock
    EventContext context;

    @Mock
    Properties ananyaServiceProperties;

    @Before
    public void setUp(){
        initMocks(this);
        sendSMSService = new SendSMSService(ananyaServiceProperties, context);
    }

    @Test
    public void shouldBuildAndSendSMS() {

        // the paras
        String callerId = "9876543210";
        String locationId = "S001D001B012V111";
        int courseAttempts = 1;

        // the results
        String refNum = "001012987654321001";
        String message = "Hello";

        when(ananyaServiceProperties.getProperty(argThat(is("course.completion.sms.message")))).thenReturn(message);

        sendSMSService.buildAndSendSMS(callerId, locationId, courseAttempts);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SendSMSHandler.PARAMETER_MOBILE_NUMBER, callerId);
        parameters.put(SendSMSHandler.PARAMETER_SMS_MESSAGE, message + refNum);
        parameters.put(SendSMSHandler.PARAMETER_SMS_REFERENCE_NUMBER, refNum);

        verify(context).send(argThat(is(SendSMSHandler.SUBJECT_SEND_SINGLE_SMS)), argThat(is(parameters)));
    }
}
