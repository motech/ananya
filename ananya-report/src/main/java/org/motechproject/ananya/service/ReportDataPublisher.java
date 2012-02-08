package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.ReportData;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ReportDataPublisher {

    public static final String SEND_REPORT_DATA_KEY = "org.motechproject.ananya.report.data";

    private EventContext eventContext;

    @Autowired
    public ReportDataPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publish(ReportData reportData) {
        eventContext.send(SEND_REPORT_DATA_KEY,reportData);
    }
}
