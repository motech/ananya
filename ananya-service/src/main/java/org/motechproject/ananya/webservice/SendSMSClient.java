package org.motechproject.ananya.webservice;

import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.domain.LogType;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.ReportPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendSMSClient {

    public static final String SENDER_ID = "correctSenderIDHere"; //TODO
    private OnMobileSendSMSService smsService;
    private FrontLineWorkerService frontLineWorkerService;
    private ReportPublisherService reportPublisherService;

    @Autowired
    public SendSMSClient(OnMobileSendSMSService smsService, FrontLineWorkerService frontLineWorkerService, ReportPublisherService reportPublisherService) {
        this.smsService = smsService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.reportPublisherService = reportPublisherService;
    }

    public void sendSingleSMS(String mobileNumber, String smsMessage, String smsReferenceNumber) {
        String result = smsService.singlePush(mobileNumber, SENDER_ID, smsMessage);

        if("failure".equals(result)) {
            throw new RuntimeException("SMS failed to deliver");
        }

        try {
            frontLineWorkerService.addSMSReferenceNumber(mobileNumber, smsReferenceNumber);
            LogData logData = new LogData(LogType.SMS_SENT, mobileNumber);
            reportPublisherService.publishSMSSent(logData);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
