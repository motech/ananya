package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
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
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private AllLocationDimensions allLocationDimensions;
    private AllTimeDimensions allTimeDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;

    public RegistrationMeasureService() {
    }

    @Autowired
    public RegistrationMeasureService(FrontLineWorkerService frontLineWorkerService,
                                      FrontLineWorkerDimensionService frontLineWorkerDimensionService, AllLocationDimensions allLocationDimensions,
                                      AllTimeDimensions allTimeDimensions,
                                      AllRegistrationMeasures allRegistrationMeasures) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.allTimeDimensions = allTimeDimensions;
        this.allLocationDimensions = allLocationDimensions;
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.allRegistrationMeasures = allRegistrationMeasures;
    }

    @Transactional
    public void createOrUpdateFor(String callerId) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(callerId);
        LocationDimension locationDimension = allLocationDimensions.getFor(frontLineWorker.getLocationId());
        boolean flwDimensionAlreadyExists = frontLineWorkerDimensionService.exists(frontLineWorker.msisdn());
        TimeDimension timeDimension = allTimeDimensions.getFor(frontLineWorker.getRegisteredDate());

        FrontLineWorkerDimension frontLineWorkerDimension = frontLineWorkerDimensionService.createOrUpdate(
                frontLineWorker.msisdn(),
                frontLineWorker.getOperator(),
                frontLineWorker.getCircle(),
                frontLineWorker.name(),
                frontLineWorker.designationName(),
                frontLineWorker.getStatus().toString());

        RegistrationMeasure registrationMeasure = getRegistrationMeasureFor(flwDimensionAlreadyExists, frontLineWorkerDimension, locationDimension, timeDimension);
        allRegistrationMeasures.createOrUpdate(registrationMeasure);

        log.info("RegistrationMeasure created/updated for" + callerId + "[Location=" + registrationMeasure.getLocationDimension().getId() +
                "|Time=" + registrationMeasure.getTimeDimension().getId() + "|flw=" + registrationMeasure.getFrontLineWorkerDimension().getId() + "]");
    }

    private RegistrationMeasure getRegistrationMeasureFor(boolean flwDimensionAlreadyExists, FrontLineWorkerDimension frontLineWorkerDimension, LocationDimension locationDimension, TimeDimension timeDimension) {
        return flwDimensionAlreadyExists ? allRegistrationMeasures.fetchFor(frontLineWorkerDimension.getId()).update(locationDimension)
                : new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension);
    }

}
