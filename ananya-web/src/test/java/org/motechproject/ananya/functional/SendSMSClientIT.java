package org.motechproject.ananya.functional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.webservice.SendSMSClient;
import org.springframework.beans.factory.annotation.Autowired;

public class SendSMSClientIT extends SpringIntegrationTest {
    @Autowired
    private SendSMSClient sendSMSClient;

    @Test
    public void shouldWork() {
        sendSMSClient.sendSingleSMS("9999998888", "Hello!", "001001999999888801");
    }

}
