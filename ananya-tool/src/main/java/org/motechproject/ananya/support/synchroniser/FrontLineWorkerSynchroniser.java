package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FrontLineWorkerSynchroniser implements Synchroniser {

    private RegistrationMeasureService registrationMeasureService;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private RegistrationLogService registrationLogService;

    @Autowired
    public FrontLineWorkerSynchroniser(FrontLineWorkerService frontLineWorkerService,
                                       RegistrationMeasureService registrationMeasureService,
                                       AllFrontLineWorkerDimensions allFrontLineWorkerDimensions, RegistrationLogService registrationLogService) {
        this.registrationMeasureService = registrationMeasureService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.registrationLogService = registrationLogService;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("FrontLineWorker");
        List<RegistrationLog> registrationLogs = registrationLogService.getAll();

        for (RegistrationLog registrationLog : registrationLogs) {
            Long msisdn = registrationLog.callerIdAsLong();
            String callId = registrationLog.getCallId();
            try {
                if (allFrontLineWorkerDimensions.fetchFor(msisdn) == null) {
                    registrationMeasureService.createRegistrationMeasureForCall(callId);
                    synchroniserLog.add(msisdn.toString(), "Success");
                }
            } catch (Exception e) {
                synchroniserLog.add(msisdn.toString(), "Error: " + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.high;
    }

}
