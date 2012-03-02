package org.motechproject.ananya.service.handler;

import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.ananya.service.ReportDataPublisher;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationDataHandler {

    private RegistrationMeasureService registrationMeasureServiceService;

    @Autowired
    public RegistrationDataHandler(RegistrationMeasureService registrationMeasureServiceService) {
        this.registrationMeasureServiceService = registrationMeasureServiceService;
    }

    @MotechListener(subjects = {ReportDataPublisher.SEND_REGISTRATION_DATA_KEY})
    public void handleRegistration(MotechEvent event) {
        for (Object log : event.getParameters().values())
            this.registrationMeasureServiceService.createRegistrationMeasure((LogData) log);
    }

    @MotechListener(subjects = {ReportDataPublisher.SEND_REGISTRATION_COMPLETION_DATA_KEY})
    public void handleRegistrationCompletion(MotechEvent event) {
        for (Object log : event.getParameters().values())
            this.registrationMeasureServiceService.updateRegistrationStatusAndName((LogData) log);
    }
}
