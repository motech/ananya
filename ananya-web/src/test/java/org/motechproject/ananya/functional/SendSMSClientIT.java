package org.motechproject.ananya.functional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.webservice.SendSMSClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: imdadah
 * Date: 05/03/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendSMSClientIT extends SpringIntegrationTest {
    @Autowired
    private SendSMSClient sendSMSClient;

    @Test
    public void shouldWork() {
        String result = sendSMSClient.sendSingleSMS("9999998888", "Hello!");
        System.out.print(result);
    }

}
