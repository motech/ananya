package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.response.RegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private static Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private RegistrationMeasureService registrationMeasureService;

    @Autowired
    public RegistrationService(FrontLineWorkerService frontLineWorkerService,
                               RegistrationMeasureService registrationMeasureService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.registrationMeasureService = registrationMeasureService;
    }

    public RegistrationResponse registerFlw(String callerId, String name, String designation, String district, String block, String village, LocationList locationList) {
        RegistrationResponse registrationResponse = new RegistrationResponse();

        if (isInvalidCallerId(callerId))
            return registrationResponse.withInvalidCallerId();

        Location location = locationList.findFor(district, block, village);
        if (location == null)
            return registrationResponse.withInvalidLocationStatus();

        RegistrationStatus registrationStatus = StringUtils.isBlank(name) || Designation.isInValid(designation) ? RegistrationStatus.PARTIALLY_REGISTERED : RegistrationStatus.REGISTERED;
        Designation desgn = Designation.getFor(designation);

        frontLineWorkerService.createOrUpdate(callerId, name, desgn, location, registrationStatus);
        registrationMeasureService.createRegistrationMeasure(new LogData(LogType.REGISTRATION, callerId));

        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }

    private boolean isInvalidCallerId(String callerId) {
        return StringUtils.isBlank(callerId) || !StringUtils.isNumeric(callerId);
    }
}
