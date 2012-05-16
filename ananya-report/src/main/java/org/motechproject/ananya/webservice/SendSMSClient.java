package org.motechproject.ananya.webservice;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.SMSSentMeasureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendSMSClient {

    private static Logger log = LoggerFactory.getLogger(SendSMSClient.class);

    private OnMobileSendSMSService smsService;
    private FrontLineWorkerService frontLineWorkerService;
    private SMSSentMeasureService smsSentMeasureService;
    private String senderId;

    @Autowired
    public SendSMSClient(OnMobileSendSMSService smsService,
                         FrontLineWorkerService frontLineWorkerService,
                         SMSSentMeasureService smsSentMeasureService,
                         @Value("#{ananyaProperties['sms.sender.id']}") String senderId) {
        this.smsService = smsService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.senderId = senderId;
        this.smsSentMeasureService = smsSentMeasureService;
    }

    public void sendSingleSMS(String mobileNumber, String smsMessage, String smsReferenceNumber) {
        String result = smsService.singlePush(mobileNumber, senderId, smsMessage);
        if ("failure".equals(result))
            throw new RuntimeException("SMS failed to deliver");
        try {
            addSMSReferenceNumber(mobileNumber, smsReferenceNumber);
        } catch (Exception e) {
            log.error("Exception:", e);
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
        smsSentMeasureService.createSMSSentMeasure(callerId);
    }
}
