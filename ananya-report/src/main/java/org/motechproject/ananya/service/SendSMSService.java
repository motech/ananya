package org.motechproject.ananya.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;
import org.motechproject.ananya.service.handler.SendSMSHandler;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class SendSMSService {
    private static final Logger log = Logger.getLogger(SendSMSService.class);
//    private static final String COURSE_COMPLETION_SMS_MESSAGE_KEY = "course.completion.sms.message";

//    private Properties ananyaProperties;
    private EventContext eventContext;
    private AllLanguageDimension allLanguageDimension;

    @Autowired
    public SendSMSService(Properties ananyaProperties, @Qualifier("eventContext") EventContext eventContext, AllLanguageDimension allLanguageDimension) {
        this.eventContext = eventContext;
//        this.ananyaProperties = ananyaProperties;
        this.allLanguageDimension=allLanguageDimension;
    }

    public void buildAndSendSMS(String callerId, String locationId, Integer courseAttemptNumber, String language) {
    	String stateCode = extractStateCode(locationId);
    	String districtCode = extractDistrictCode(locationId);
        String blockCode = extractBlockCode(locationId);
        String referenceNumber = stateCode + districtCode + blockCode + callerId + String.format("%02d", courseAttemptNumber);

//      String smsMessageToSend = this.ananyaProperties.getProperty(COURSE_COMPLETION_SMS_MESSAGE_KEY) + referenceNumber;
        String smsMessageToSend = allLanguageDimension.getFor(language).getSmsMessage() + referenceNumber;
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SendSMSHandler.PARAMETER_SMS_MESSAGE, smsMessageToSend);
        parameters.put(SendSMSHandler.PARAMETER_MOBILE_NUMBER, callerId);
        parameters.put(SendSMSHandler.PARAMETER_SMS_REFERENCE_NUMBER, referenceNumber);

        log.info("published sms message: " + callerId + "|" + referenceNumber);
        eventContext.send(SendSMSHandler.SUBJECT_SEND_SINGLE_SMS, parameters);
    }

    private String extractBlockCode(String locationId) {
        String startCode = "B";
        return extractCode(locationId, startCode);
    }

    private String extractStateCode(String locationId) {
        String startCode = "D";
        String result = "";
        int indexStart = locationId.indexOf(startCode);
        if (indexStart != -1)
            result = locationId.substring(indexStart + 1, indexStart + 3);
        return result;
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
