package org.motechproject.ananya.service.handler;

import org.apache.log4j.Logger;
import org.motechproject.ananya.webservice.SendSMSClient;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendSMSHandler {
    private static final Logger LOG = Logger.getLogger(SendSMSHandler.class);

    public static final String SUBJECT_SEND_SINGLE_SMS = "sendSingleSMS";
    public static final String PARAMETER_SMS_MESSAGE = "smsMessage";
    public static final String PARAMETER_MOBILE_NUMBER = "mobileNumber";
    public static final String PARAMETER_SMS_REFERENCE_NUMBER = "smsReferenceNumber";

    private SendSMSClient smsClient;

    @Autowired
    public SendSMSHandler(SendSMSClient smsClient) {
        this.smsClient = smsClient;
    }

    @MotechListener(subjects = {SUBJECT_SEND_SINGLE_SMS})
    public void sendSingleSMS(MotechEvent motechEvent) {
        Map parameters = (Map) motechEvent.getParameters().get("0");
        String smsMessage = (String) parameters.get(PARAMETER_SMS_MESSAGE);
        String mobileNumber = (String) parameters.get(PARAMETER_MOBILE_NUMBER);
        String smsReferenceNumber = (String) parameters.get(PARAMETER_SMS_REFERENCE_NUMBER);

        LOG.info("received sms message:" + mobileNumber);
        smsClient.sendSingleSMS(mobileNumber, smsMessage, smsReferenceNumber);
    }
}
