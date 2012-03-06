package org.motechproject.ananya.service;

import org.motechproject.ananya.handler.SendSMSHandler;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class SendSMSService {
    private static final String COURSE_COMPLETION_SMS_MESSAGE_KEY = "course.completion.sms.message";
    private Properties ananyaServiceProperties;
    private EventContext eventContext;

    @Autowired
    public SendSMSService(Properties ananyaServiceProperties, @Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
        this.ananyaServiceProperties = ananyaServiceProperties;
    }

    public void buildAndSendSMS(String callerId, String locationId, Integer courseAttemptNumber) {
        String districtCode = extractDistrictCode(locationId);
        String blockCode = extractBlockCode(locationId);
        String referenceNumber = districtCode + blockCode + callerId + String.format("%02d", courseAttemptNumber);

        String smsMessageToSend = this.ananyaServiceProperties.getProperty(COURSE_COMPLETION_SMS_MESSAGE_KEY) + referenceNumber;

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SendSMSHandler.PARAMETER_SMS_MESSAGE, smsMessageToSend);
        parameters.put(SendSMSHandler.PARAMETER_MOBILE_NUMBER, callerId);

        eventContext.send(SendSMSHandler.SUBJECT_SEND_SINGLE_SMS, parameters);
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
        if(indexStart != -1) {
            result = locationId.substring(indexStart + 1, indexStart + 4);
        }

        return result;
    }
}
