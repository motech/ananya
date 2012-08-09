package org.motechproject.ananya.webservice;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.SMSLogService;
import org.motechproject.ananya.service.SMSReferenceService;
import org.motechproject.ananya.service.measure.SMSSentMeasureService;
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
    private SMSReferenceService smsReferenceService;
    private SMSLogService smsLogService;
    private String senderId;

    @Autowired
    public SendSMSClient(FrontLineWorkerService frontLineWorkerService,
                         OnMobileSendSMSService smsService,
                         SMSSentMeasureService smsSentMeasureService,
                         SMSReferenceService smsReferenceService,
                         SMSLogService smsLogService,
                         @Value("#{ananyaProperties['sms.sender.id']}") String senderId) {
        this.senderId = senderId;
        this.smsService = smsService;
        this.smsLogService = smsLogService;
        this.smsReferenceService = smsReferenceService;
        this.smsSentMeasureService = smsSentMeasureService;
        this.frontLineWorkerService = frontLineWorkerService;
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
        Integer courseAttempts = frontLineWorker.currentCourseAttempt();

        SMSReference smsReference = smsReferenceService.getSMSReferenceNumber(callerId);
        if (smsReference == null) {
            smsReference = new SMSReference(callerId, frontLineWorker.getId());
            smsReferenceService.addSMSReferenceNumber(smsReference);
        }
        smsReference.add(smsReferenceNumber, courseAttempts);
        smsReferenceService.updateSMSReferenceNumber(smsReference);
        smsSentMeasureService.createSMSSentMeasure(callerId);
        smsLogService.deleteFor(callerId, courseAttempts);
    }
}
