package org.motechproject.ananya.repository;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllFrontLineWorkerDimensions {

    @Autowired
    private DataAccessTemplate template;

    public FrontLineWorkerDimension getOrMakeFor(Long msisdn, String operator, String name, String status) {
        FrontLineWorkerDimension dimension = (FrontLineWorkerDimension) template.getUniqueResult(FrontLineWorkerDimension.FIND_BY_MSISDN, new String[]{"msisdn"}, new Object[]{msisdn});
        if (dimension == null) {
            dimension = new FrontLineWorkerDimension(msisdn, operator, name, status);
            template.save(dimension);
        }
        return dimension;
    }

    public FrontLineWorkerDimension fetchFor(Long msisdn) {
        return (FrontLineWorkerDimension) template.getUniqueResult(
                FrontLineWorkerDimension.FIND_BY_MSISDN, new String[]{"msisdn"}, new Object[]{msisdn});
    }

    public void update(FrontLineWorkerDimension frontLineWorkerDimension) {
        template.update(frontLineWorkerDimension);
    }
    
    public void removeAll() {
        template.bulkUpdate("delete from FrontLineWorkerDimension");
    }
}
