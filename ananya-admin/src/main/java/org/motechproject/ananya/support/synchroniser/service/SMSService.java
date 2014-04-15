package org.motechproject.ananya.support.synchroniser.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;
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
    private AllLanguageDimension allLanguageDimension;

    @Autowired
    public SMSService(AllLanguageDimension allLanguageDimension, Properties ananyaProperties, SendSMSClient sendSMSClient) {
        this.allLanguageDimension = allLanguageDimension;
    	this.ananyaProperties = ananyaProperties;
        this.sendSMSClient = sendSMSClient;
    }

    public void buildAndSendSMS(String callerId, String language, String locationId, Integer courseAttemptNumber) {
    	String stateCode =  extractStateCode(locationId);
        String districtCode = extractDistrictCode(locationId);
        String blockCode = extractBlockCode(locationId);
        String referenceNumber = stateCode + districtCode + blockCode + callerId + String.format("%02d", courseAttemptNumber);
        String smsMessageToSend = allLanguageDimension.getFor(language).getSmsMessage()!=null?allLanguageDimension.getFor(language).getSmsMessage()+referenceNumber:this.ananyaProperties.getProperty(COURSE_COMPLETION_SMS_MESSAGE_KEY+"."+language.toLowerCase(), this.ananyaProperties.getProperty(COURSE_COMPLETION_SMS_MESSAGE_KEY)) + referenceNumber;
        log.info("message from db="+allLanguageDimension.getFor(language).getSmsMessage()+" language="+language);
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

    private String extractStateCode(String locationId) {
        String startCode = "S";
        String result = "";
        int indexStart = locationId.indexOf(startCode);
        if (indexStart != -1)
            result = locationId.substring(indexStart + 1, indexStart + 3);
        return result;
    }
    
    private String extractCode(String locationId, String startCode) {
        String result = "";
        int indexStart = locationId.indexOf(startCode);
        if (indexStart != -1)
            result = locationId.substring(indexStart + 1, indexStart + 4);
        return result;
    }
}
