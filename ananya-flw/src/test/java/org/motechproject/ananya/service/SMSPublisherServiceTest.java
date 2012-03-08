package org.motechproject.ananya.service;


import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.handler.SendSMSHandler;
import org.motechproject.context.EventContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSPublisherServiceTest {
    SMSPublisherService smsPublisherService;
    @Mock
    private EventContext eventContext;

    @Before
    public void setUp() {
        initMocks(this);
        smsPublisherService = new SMSPublisherService(eventContext);
    }

    @Test
    public void shouldPublishSmsSentEvent(){
        String msisdn = "msisdn";
        smsPublisherService.publishSMSSent(msisdn);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SMSPublisherService.PARAMETER_MSISDN, msisdn);

        verify(eventContext).send(argThat(Matchers.is(SMSPublisherService.SUBJECT_SMS_SENT)), argThat(is(parameters)));
    }
}
