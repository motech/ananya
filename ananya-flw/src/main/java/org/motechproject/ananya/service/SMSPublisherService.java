package org.motechproject.ananya.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SMSPublisherService {
    public static final String SUBJECT_SMS_SENT = "smsSent";
    public static final String PARAMETER_MSISDN = "msisdn";

    private static final Logger logger = Logger.getLogger(SMSPublisherService.class);

    private EventContext eventContext;

    @Autowired
    public SMSPublisherService(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishSMSSent(String msisdn){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAMETER_MSISDN, msisdn);
        
        logger.info("Sending publish SMS event key SUBJECT_SMS_SENT " + ToStringBuilder.reflectionToString(parameters));
        eventContext.send(SUBJECT_SMS_SENT,  parameters);
    }
}
