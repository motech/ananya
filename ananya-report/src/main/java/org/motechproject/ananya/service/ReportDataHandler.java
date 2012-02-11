package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.LogData;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportDataHandler {

    private ReportDataMeasure reportDataMeasureService;

    @Autowired
    public ReportDataHandler(ReportDataMeasure reportDataMeasureService) {
        this.reportDataMeasureService = reportDataMeasureService;
    }

    @MotechListener(subjects={ReportDataPublisher.SEND_REGISTRATION_DATA_KEY})
	public void handleRegistration(MotechEvent event) {
        LogData logData = (LogData) event.getParameters().get("1");
        this.reportDataMeasureService.createRegistrationMeasure(logData);
    }
}