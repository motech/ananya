package org.motechproject.ananya.webservice;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.SMSPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendSMSClient {

    public static final String SENDER_ID = "bbc-ananya";

    private OnMobileSendSMSService smsService;
    private FrontLineWorkerService frontLineWorkerService;
    private SMSPublisherService smsPublisherService;

    @Autowired
    public SendSMSClient(OnMobileSendSMSService smsService, FrontLineWorkerService frontLineWorkerService, SMSPublisherService smsPublisherService) {
        this.smsService = smsService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.smsPublisherService = smsPublisherService;
    }

    public void sendSingleSMS(String mobileNumber, String smsMessage, String smsReferenceNumber) {
        String result = smsService.singlePush(mobileNumber, SENDER_ID, smsMessage);

        if("failure".equals(result))
            throw new RuntimeException("SMS failed to deliver");
        try {
            addSMSReferenceNumber(mobileNumber, smsReferenceNumber);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addSMSReferenceNumber(String callerId, String smsReferenceNumber) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(callerId);
        SMSReference smsReference = frontLineWorkerService.getSMSReferenceNumber(callerId);

        if (smsReference == null) {
            smsReference = new SMSReference(callerId, frontLineWorker.getId());
            frontLineWorkerService.addSMSReferenceNumber(smsReference);
        }
        smsReference.add(smsReferenceNumber, frontLineWorker.currentCourseAttempt());
        frontLineWorkerService.updateSMSReferenceNumber(smsReference);

        smsPublisherService.publishSMSSent(callerId);
    }
}
