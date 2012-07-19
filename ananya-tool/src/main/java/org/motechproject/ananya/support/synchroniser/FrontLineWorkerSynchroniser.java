package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class FrontLineWorkerSynchroniser extends BaseSynchronizer implements Synchroniser {

    private RegistrationMeasureService registrationMeasureService;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private RegistrationLogService registrationLogService;

    @Autowired
    public FrontLineWorkerSynchroniser(RegistrationMeasureService registrationMeasureService,
                                       RegistrationLogService registrationLogService,
                                       AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                       @Qualifier("ananyaProperties") Properties properties) {
        this.registrationMeasureService = registrationMeasureService;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.registrationLogService = registrationLogService;
        this.properties = properties;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("FrontLineWorker");
        List<RegistrationLog> registrationLogs = registrationLogService.getAll();

        for (RegistrationLog registrationLog : registrationLogs) {
            Long msisdn = registrationLog.callerIdAsLong();
            String callId = registrationLog.getCallId();
            try {
                if(!shouldProcessLog(registrationLog)) continue;
                registrationMeasureService.createFor(callId);
                synchroniserLog.add(msisdn.toString(), "Success");
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
