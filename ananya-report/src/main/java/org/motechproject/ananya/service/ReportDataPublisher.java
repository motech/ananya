package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.LogData;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ReportDataPublisher {

    public static final String SEND_REGISTRATION_DATA_KEY = "org.motechproject.ananya.report.registration";
    public static final String SEND_REGISTRATION_COMPLETION_DATA_KEY = "org.motechproject.ananya.report.registration.completion";
    public static final String SEND_CALL_DURATION_DATA_KEY = "org.motechproject.ananya.report.call.duration";

    private EventContext eventContext;

    @Autowired
    public ReportDataPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishRegistration(LogData logData) {
        eventContext.send(SEND_REGISTRATION_DATA_KEY, logData);
    }
    
    public void publishRegistrationUpdate(LogData logData) {
        eventContext.send(SEND_REGISTRATION_COMPLETION_DATA_KEY, logData);
    }

    public void publishCallDuration(LogData logData) {
        eventContext.send(SEND_CALL_DURATION_DATA_KEY, logData);
    }
}
