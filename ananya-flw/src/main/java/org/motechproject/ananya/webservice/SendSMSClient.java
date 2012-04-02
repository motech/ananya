package org.motechproject.ananya.webservice;

import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendSMSClient {

    public static final String SENDER_ID = "bbc-ananya";

    private OnMobileSendSMSService smsService;
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public SendSMSClient(OnMobileSendSMSService smsService, FrontLineWorkerService frontLineWorkerService) {
        this.smsService = smsService;
        this.frontLineWorkerService = frontLineWorkerService;
    }

    public void sendSingleSMS(String mobileNumber, String smsMessage, String smsReferenceNumber) {
        String result = smsService.singlePush(mobileNumber, SENDER_ID, smsMessage);

        if("failure".equals(result))
            throw new RuntimeException("SMS failed to deliver");
        try {
            frontLineWorkerService.addSMSReferenceNumber(mobileNumber, smsReferenceNumber);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
