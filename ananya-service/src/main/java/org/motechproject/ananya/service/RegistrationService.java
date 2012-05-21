package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationService {
    private static Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private RegistrationMeasureService registrationMeasureService;
    private LocationService locationService;

    @Autowired
    public RegistrationService(FrontLineWorkerService frontLineWorkerService,
                               FrontLineWorkerDimensionService frontLineWorkerDimensionService,
                               RegistrationMeasureService registrationMeasureService,
                               LocationService locationService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.registrationMeasureService = registrationMeasureService;
        this.locationService = locationService;
    }

    public List<RegistrationResponse> registerAllFLWs(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        LocationList locationList = new LocationList(locationService.getAll());
        List<RegistrationResponse> registrationResponses = new ArrayList<RegistrationResponse>();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            RegistrationResponse registrationResponse = registerFlw(frontLineWorkerRequest.getMsisdn(),
                    frontLineWorkerRequest.getName(),
                    frontLineWorkerRequest.getDesignation(),
                    frontLineWorkerRequest.getOperator(),
                    frontLineWorkerRequest.getLocation().getDistrict(),
                    frontLineWorkerRequest.getLocation().getBlock(),
                    frontLineWorkerRequest.getLocation().getPanchayat(),
                    locationList);
            registrationResponses.add(registrationResponse);
        }
        return registrationResponses;
    }

    public RegistrationResponse createOrUpdateFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        LocationList locationList = new LocationList(locationService.getAll());
        return registerFlw(frontLineWorkerRequest.getMsisdn(),
                frontLineWorkerRequest.getName(),
                frontLineWorkerRequest.getDesignation(),
                frontLineWorkerRequest.getOperator(),
                frontLineWorkerRequest.getLocation().getDistrict(),
                frontLineWorkerRequest.getLocation().getBlock(),
                frontLineWorkerRequest.getLocation().getPanchayat(),
                locationList);
    }

    public List<FrontLineWorkerResponse> getFilteredFLW(String msisdn, String name, String status, String designation, String operator, String circle) {
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionService.getFilteredFLW(msisdn, name, status, designation, operator, circle);
        List<FrontLineWorkerResponse> filteredFlws = new ArrayList<FrontLineWorkerResponse>();
        for(FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions)
            filteredFlws.add(FrontLineWorkerMapper.mapFrom(frontLineWorkerDimension));
        return filteredFlws;
    }

    private RegistrationResponse registerFlw(String callerId, String name, String designation, String operator, String district, String block, String panchayat, LocationList locationList) {
        RegistrationResponse registrationResponse = new RegistrationResponse(name, callerId, designation, operator, district, block, panchayat);

        if (isInvalidCallerId(callerId))
            return registrationResponse.withInvalidCallerId();

        Location location = locationList.findFor(district, block, panchayat);
        if (location == null)
            return registrationResponse.withInvalidLocationStatus();

        RegistrationStatus registrationStatus = StringUtils.isBlank(name) || Designation.isInValid(designation) ? RegistrationStatus.PARTIALLY_REGISTERED : RegistrationStatus.REGISTERED;
        Designation desgn = Designation.getFor(designation);

        frontLineWorkerService.createOrUpdate(callerId, name, desgn, location, registrationStatus);
        registrationMeasureService.createRegistrationMeasure(callerId);

        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }

    private boolean isInvalidCallerId(String callerId) {
        return StringUtils.isBlank(callerId) || !StringUtils.isNumeric(callerId);
    }
}
