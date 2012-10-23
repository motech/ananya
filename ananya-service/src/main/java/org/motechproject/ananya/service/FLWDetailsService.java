package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.exceptions.FLWDoesNotExistException;
import org.motechproject.ananya.mapper.FrontLineWorkerUsageResponseMapper;
import org.motechproject.ananya.response.FrontLineWorkerUsageResponse;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FLWDetailsService {
    private FrontLineWorkerService frontLineWorkerService;
    private CallDurationMeasureService callDurationMeasureService;
    private LocationService locationService;
    private SMSReferenceService smsReferenceService;
    private FrontLineWorkerUsageResponseMapper frontLineWorkerUsageResponseMapper;

    @Autowired
    public FLWDetailsService(FrontLineWorkerService frontLineWorkerService,
                             CallDurationMeasureService callDurationMeasureService,
                             LocationService locationService,
                             SMSReferenceService smsReferenceService,
                             FrontLineWorkerUsageResponseMapper frontLineWorkerUsageResponseMapper) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.locationService = locationService;
        this.smsReferenceService = smsReferenceService;
        this.frontLineWorkerUsageResponseMapper = frontLineWorkerUsageResponseMapper;
    }

    public FrontLineWorkerUsageResponse getUsageData(String flwGuid) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(flwGuid);
        String msisdn = frontLineWorker.getMsisdn();
        Location location = locationService.findByExternalId(frontLineWorker.getLocationId());
        CallDetailsResponse callDetails = callDurationMeasureService.getCallDetails(msisdn);
        SMSReference smsReferenceNumber = smsReferenceService.getSMSReferenceNumber(msisdn);

        FrontLineWorkerUsageResponse frontLineWorkerUsageResponse = frontLineWorkerUsageResponseMapper.mapFrom(frontLineWorker, location, callDetails, smsReferenceNumber);

        return frontLineWorkerUsageResponse;
    }

    private FrontLineWorker getFrontLineWorker(String flwGuid) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByFlwGuid(flwGuid);
        if (frontLineWorker == null) {
            throw FLWDoesNotExistException.withUnknownFlwGuid(flwGuid);
        }
        return frontLineWorker;
    }
}
