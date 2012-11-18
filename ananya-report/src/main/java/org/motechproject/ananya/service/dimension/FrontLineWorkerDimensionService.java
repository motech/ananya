package org.motechproject.ananya.service.dimension;

import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.requests.FLWStatusChangeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FrontLineWorkerDimensionService {

    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    public FrontLineWorkerDimensionService() {
    }

    @Autowired
    public FrontLineWorkerDimensionService(AllFrontLineWorkerDimensions allFrontLineWorkerDimensions) {
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
    }

    @Transactional
    public List<FrontLineWorkerDimension> getAllUnregistered() {
        return allFrontLineWorkerDimensions.getAllUnregistered();
    }

    @Transactional
    public FrontLineWorkerDimension createOrUpdate(Long msisdn, String operator, String circle, String name, String designation, String registrationStatus, UUID flwId, VerificationStatus verificationStatus) {
        return allFrontLineWorkerDimensions.createOrUpdate(msisdn, operator, circle, name, designation, registrationStatus, flwId, verificationStatus);
    }

    public boolean exists(Long msisdn) {
        return allFrontLineWorkerDimensions.fetchFor(msisdn) != null;
    }

    public List<FrontLineWorkerDimension> getFilteredFLW(List<Long> allFilteredMsisdns, Long msisdn, String name, String status, String designation, String operator, String circle) {
        return allFrontLineWorkerDimensions.getFilteredFLWFor(allFilteredMsisdns, msisdn, name, status, designation, operator, circle);
    }

    @Transactional
    public void updateStatus(List<FLWStatusChangeRequest> flwStatusChangeRequests) {
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<>();
        for (FLWStatusChangeRequest request : flwStatusChangeRequests) {
            FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(request.getMsisdn());
            frontLineWorkerDimension.setStatus(request.getRegistrationStatus());
            frontLineWorkerDimensions.add(frontLineWorkerDimension);
        }
        allFrontLineWorkerDimensions.createOrUpdateAll(frontLineWorkerDimensions);
    }
}
