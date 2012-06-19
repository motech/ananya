package org.motechproject.ananya.service.measure;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationMeasureService {
    private static final Logger log = LoggerFactory.getLogger(RegistrationMeasureService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private AllLocationDimensions allLocationDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;
    private RegistrationLogService registrationLogService;

    public RegistrationMeasureService() {
    }

    @Autowired
    public RegistrationMeasureService(FrontLineWorkerService frontLineWorkerService,
                                      FrontLineWorkerDimensionService frontLineWorkerDimensionService, AllLocationDimensions allLocationDimensions,
                                      AllTimeDimensions allTimeDimensions,
                                      AllRegistrationMeasures allRegistrationMeasures, RegistrationLogService registrationLogService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.allTimeDimensions = allTimeDimensions;
        this.allLocationDimensions = allLocationDimensions;
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.registrationLogService = registrationLogService;
    }

    @Transactional
    public void createFor(String callId) {
        RegistrationLog registrationLog = registrationLogService.getRegistrationLogFor(callId);
        if (registrationLog == null) {
            log.info(callId + "- registrationLog not present");
            return;
        }
        createMeasure(registrationLog.getCallerId(), callId);

        registrationLogService.delete(registrationLog);
        log.info(callId + "- registrationLog removed");
    }

    @Transactional
    public void createRegistrationMeasure(String callerId, String callId) {
        createMeasure(callerId, callId);
    }

    private void createMeasure(String callerId, String callId) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(callerId);
        LocationDimension locationDimension = allLocationDimensions.getFor(frontLineWorker.getLocationId());
        boolean dimensionAlreadyExists = frontLineWorkerDimensionService.exists(frontLineWorker.msisdn());

        FrontLineWorkerDimension frontLineWorkerDimension = frontLineWorkerDimensionService.createOrUpdate(
                frontLineWorker.msisdn(),
                frontLineWorker.getOperator(),
                frontLineWorker.getCircle(),
                frontLineWorker.name(),
                frontLineWorker.designationName(),
                frontLineWorker.status().toString());
        log.info(callId + "- flwDimension created or updated for " + frontLineWorker);

        if (dimensionAlreadyExists) {
            log.info(callId + "- registrationMeasure already exists for " + frontLineWorker);
            return;
        }
        TimeDimension timeDimension = allTimeDimensions.getFor(frontLineWorker.getRegisteredDate());
        RegistrationMeasure registrationMeasure = new RegistrationMeasure(
                frontLineWorkerDimension,
                locationDimension,
                timeDimension,
                callId);
        allRegistrationMeasures.add(registrationMeasure);
        log.info(callId + "- registrationMeasure created for " + frontLineWorker);
    }

}
