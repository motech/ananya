package org.motechproject.ananya.service.dimension;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public FrontLineWorkerDimension createOrUpdate(Long msisdn, String operator, String circle, String name, String designation, String registrationStatus) {
        return allFrontLineWorkerDimensions.createOrUpdate(msisdn, operator, circle, name, designation, registrationStatus);
    }

    public boolean exists(Long msisdn) {
        return allFrontLineWorkerDimensions.fetchFor(msisdn) != null;
    }

    public List<FrontLineWorkerDimension> getFilteredFLW(List<Long> allFilteredMsisdns, Long msisdn, String name, String status, String designation, String operator, String circle) {
        return allFrontLineWorkerDimensions.getFilteredFLWFor(allFilteredMsisdns, msisdn, name, status, designation, operator, circle);
    }
}
