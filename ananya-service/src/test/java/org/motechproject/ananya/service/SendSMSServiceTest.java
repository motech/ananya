package org.motechproject.ananya.service;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;
import org.motechproject.ananya.service.handler.SendSMSHandler;
import org.motechproject.scheduler.context.EventContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendSMSServiceTest {

    private SendSMSService sendSMSService;

    @Mock
    EventContext context;
    
    @Mock
    Properties smsProperties;

    @Mock
    AllLanguageDimension allLanguageDimension;
    
    @Before
    public void setUp(){
        initMocks(this);
        sendSMSService = new SendSMSService(smsProperties, context, allLanguageDimension);
    }

    @Test
    public void shouldBuildAndSendSMS() {

        // the paras
        String callerId = "9876543210";
        String locationId = "S001D001B012V111";
        int courseAttempts = 1;

        // Added state code
        String refNum = "00001012987654321001";
        String message = "Hello";
        String language = "hindi";

        LanguageDimension languageDimension = new LanguageDimension(language, "hin", message);
        when(allLanguageDimension.getFor(language)).thenReturn(languageDimension);
        
        sendSMSService.buildAndSendSMS(callerId, locationId, courseAttempts, language);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SendSMSHandler.PARAMETER_MOBILE_NUMBER, callerId);
        parameters.put(SendSMSHandler.PARAMETER_SMS_MESSAGE, message + refNum);
        parameters.put(SendSMSHandler.PARAMETER_SMS_REFERENCE_NUMBER, refNum);

        verify(context).send(argThat(Matchers.is(SendSMSHandler.SUBJECT_SEND_SINGLE_SMS)), argThat(is(parameters)));
    }
}
