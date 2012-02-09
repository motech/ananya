package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.ReportData;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportDataHandler {

    @MotechListener(subjects={ReportDataPublisher.SEND_REGISTRATION_DATA_KEY})
	public void execute(MotechEvent event) throws Exception {
        ReportData reportData = (ReportData) event.getParameters().get("1");
    }
}
