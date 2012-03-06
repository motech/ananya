package org.motechproject.ananya.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendSMSClient {

    private static final String SENDER_ID = "correctSenderIDHere"; //TODO
    private OnMobileSendSMSService smsService;

    @Autowired
    public SendSMSClient(OnMobileSendSMSService smsService) {
        this.smsService = smsService;
    }

    public String sendSingleSMS(String mobileNumber, String smsMessage) {
        String result = smsService.singlePush(mobileNumber, SENDER_ID, smsMessage);

        if("failure".equals(result)) {
            throw new RuntimeException("SMS failed to deliver");
        }

        return result;
    }
}
