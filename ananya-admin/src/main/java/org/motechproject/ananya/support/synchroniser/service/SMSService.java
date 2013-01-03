package org.motechproject.ananya.support.synchroniser.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.webservice.SendSMSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SMSService {

    private static final Logger log = Logger.getLogger(SMSService.class);
    private static final String COURSE_COMPLETION_SMS_MESSAGE_KEY = "course.completion.sms.message";

    private SendSMSClient sendSMSClient;
    private Properties ananyaProperties;

    @Autowired
    public SMSService(Properties ananyaProperties, SendSMSClient sendSMSClient) {
        this.ananyaProperties = ananyaProperties;
        this.sendSMSClient = sendSMSClient;
    }

    public void buildAndSendSMS(String callerId, String locationId, Integer courseAttemptNumber) {
        String districtCode = extractDistrictCode(locationId);
        String blockCode = extractBlockCode(locationId);
        String referenceNumber = districtCode + blockCode + callerId + String.format("%02d", courseAttemptNumber);
        String smsMessageToSend = this.ananyaProperties.getProperty(COURSE_COMPLETION_SMS_MESSAGE_KEY) + referenceNumber;

        sendSMSClient.sendSingleSMS(callerId, smsMessageToSend, referenceNumber);
        log.info("sent sms for " + callerId + "|" + referenceNumber);
    }

    private String extractBlockCode(String locationId) {
        String startCode = "B";
        return extractCode(locationId, startCode);
    }

    private String extractDistrictCode(String locationId) {
        String startCode = "D";
        return extractCode(locationId, startCode);
    }

    private String extractCode(String locationId, String startCode) {
        String result = "";
        int indexStart = locationId.indexOf(startCode);
        if (indexStart != -1)
            result = locationId.substring(indexStart + 1, indexStart + 4);
        return result;
    }
}
