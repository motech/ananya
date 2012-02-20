package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.exceptions.WorkerDoesNotExistException;
import org.motechproject.ananya.request.LogRegistrationRequest;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.LogService;
import org.motechproject.ananya.service.ReportDataPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationService {
    private FrontLineWorkerService frontLineWorkerService;
    private LogService logService;
    private ReportDataPublisher reportPublisher;

    @Autowired
    public RegistrationService(FrontLineWorkerService frontLineWorkerService, LogService logService, ReportDataPublisher reportPublisher) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.logService = logService;
        this.reportPublisher = reportPublisher;
    }

    public void register(RegistrationRequest registrationRequest) {
        String callerId = registrationRequest.callerId();
        String designation = registrationRequest.designation();
        String panchayat = registrationRequest.panchayat();

        LogRegistrationRequest logRegistrationRequest = new LogRegistrationRequest(callerId, registrationRequest.calledNumber(), designation, panchayat);
        frontLineWorkerService.createNew(callerId, Designation.valueOf(designation), panchayat);
        String registeredId = logService.registered(logRegistrationRequest);

        LogData logData = new LogData(LogType.REGISTRATION, registeredId);
        reportPublisher.publishRegistration(logData);
    }

    public void saveTranscribedName(String msisdn, String name)throws WorkerDoesNotExistException{
        FrontLineWorker savedFrontLineWorker = frontLineWorkerService.saveName(msisdn, name);

        LogData logData = new LogData(LogType.REGISTRATION_SAVE_NAME, savedFrontLineWorker.getId());
        reportPublisher.publishRegistrationUpdate(logData);
    }
}
