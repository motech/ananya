package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FLWValidationResponse;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.validators.FrontLineWorkerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RegistrationService {
    private static Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    private CourseItemMeasureService courseItemMeasureService;
    private RegistrationMeasureService registrationMeasureService;
    private LocationService locationService;

    @Autowired
    public RegistrationService(FrontLineWorkerService frontLineWorkerService,
                               CourseItemMeasureService courseItemMeasureService,
                               FrontLineWorkerDimensionService frontLineWorkerDimensionService,
                               RegistrationMeasureService registrationMeasureService,
                               LocationService locationService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.courseItemMeasureService = courseItemMeasureService;
        this.frontLineWorkerDimensionService = frontLineWorkerDimensionService;
        this.registrationMeasureService = registrationMeasureService;
        this.locationService = locationService;
    }

    public List<RegistrationResponse> registerAllFLWs(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        LocationList locationList = new LocationList(locationService.getAll());
        List<RegistrationResponse> registrationResponses = new ArrayList<RegistrationResponse>();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            RegistrationResponse registrationResponse = registerFlw(StringUtils.trimToEmpty(frontLineWorkerRequest.getMsisdn()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getName()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getDesignation()),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getOperator()),
                    frontLineWorkerRequest.getLocation().getDistrict(),
                    frontLineWorkerRequest.getLocation().getBlock(),
                    frontLineWorkerRequest.getLocation().getPanchayat(),
                    StringUtils.trimToEmpty(frontLineWorkerRequest.getCircle()),
                    locationList);
            registrationResponses.add(registrationResponse);
        }
        return registrationResponses;
    }

    public RegistrationResponse createOrUpdateFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        LocationList locationList = new LocationList(locationService.getAll());
        return registerFlw(StringUtils.trimToEmpty(frontLineWorkerRequest.getMsisdn()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getName()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getDesignation()),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getOperator()),
                frontLineWorkerRequest.getLocation().getDistrict(),
                frontLineWorkerRequest.getLocation().getBlock(),
                frontLineWorkerRequest.getLocation().getPanchayat(),
                StringUtils.trimToEmpty(frontLineWorkerRequest.getCircle()),
                locationList);
    }

    public List<FrontLineWorkerResponse> getFilteredFLW(Long msisdn, String name, String status, String designation, String operator, String circle, Date activityStartDate, Date activityEndDate) {
        List<FrontLineWorkerResponse> filteredFlws = new ArrayList<FrontLineWorkerResponse>();
        List<Long> allFilteredMsisdns = new ArrayList<Long>();
        if (activityStartDate != null && activityEndDate != null) {
            allFilteredMsisdns = courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate, activityEndDate);
            if (allFilteredMsisdns.isEmpty())
                return filteredFlws;
        }

        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionService.getFilteredFLW(allFilteredMsisdns, msisdn, name, status, designation, operator, circle);

        for (FrontLineWorkerDimension frontLineWorkerDimension : frontLineWorkerDimensions)
            filteredFlws.add(FrontLineWorkerMapper.mapFrom(frontLineWorkerDimension));

        return filteredFlws;
    }

    private RegistrationResponse registerFlw(String callerId, String name, String designation, String operator, String district, String block, String panchayat, String circle, LocationList locationList) {
        Location location = locationList.findFor(district, block, panchayat);
        RegistrationResponse registrationResponse = new RegistrationResponse(name, callerId, designation, operator, circle, district, block, panchayat);
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();
        RegistrationStatus registrationStatus = getRegistrationStatus(designation, name);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, name, Designation.getFor(designation), operator, circle, location, registrationStatus);

        FLWValidationResponse FLWValidationResponse = frontLineWorkerValidator.validate(frontLineWorker, location);
        if (FLWValidationResponse.isInValid())
            return registrationResponse.withValidationResponse(FLWValidationResponse);

        frontLineWorker = frontLineWorkerService.createOrUpdate(frontLineWorker, location);
        registrationMeasureService.createOrUpdateFor(frontLineWorker.getMsisdn());

        log.info("Registered new FLW:" + callerId);
        return registrationResponse.withNewRegistrationDone();
    }

    private RegistrationStatus getRegistrationStatus(String designation, String name) {
        return Designation.isInValid(designation) || StringUtils.isBlank(name) ? RegistrationStatus.PARTIALLY_REGISTERED : RegistrationStatus.REGISTERED;
    }

}
