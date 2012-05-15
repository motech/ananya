package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationMeasureService {
    private static final Logger log = LoggerFactory.getLogger(RegistrationMeasureService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private AllLocationDimensions allLocationDimensions;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;

    public RegistrationMeasureService() {
    }

    @Autowired
    public RegistrationMeasureService(FrontLineWorkerService frontLineWorkerService,
                                      AllLocationDimensions allLocationDimensions,
                                      AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                      AllTimeDimensions allTimeDimensions,
                                      AllRegistrationMeasures allRegistrationMeasures) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.allTimeDimensions = allTimeDimensions;
        this.allLocationDimensions = allLocationDimensions;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }

    @Transactional
    public void createRegistrationMeasure(String callerId) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(callerId);
        LocationDimension locationDimension = allLocationDimensions.getFor(frontLineWorker.getLocationId());

        FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(
                frontLineWorker.msisdn(), frontLineWorker.getOperator(), frontLineWorker.getCircle(),
                frontLineWorker.name(), frontLineWorker.designationName(), frontLineWorker.status().toString());

        TimeDimension timeDimension = allTimeDimensions.getFor(frontLineWorker.getRegisteredDate());

        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension);
        allRegistrationMeasures.add(registrationMeasure);

        log.info("Added registration measure for "
                + callerId + "[Location=" + locationDimension.getId() +
                "|Time=" + timeDimension.getId() + "|flw=" + frontLineWorkerDimension.getId() + "]");
    }
}
