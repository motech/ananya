package org.motechproject.ananya.repository;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllFrontLineWorkerDimensions {

    @Autowired
    private DataAccessTemplate template;

    public FrontLineWorkerDimension getFrontLineWorkerDimension(
            Long msisdn, String operator, String name, String status) {

        FrontLineWorkerDimension frontLineWorkerDimensionDimension =
                (FrontLineWorkerDimension) template.getUniqueResult(
                FrontLineWorkerDimension.FIND_BY_MSISDN, new String[]{"msisdn"}, new Object[]{msisdn});

        return frontLineWorkerDimensionDimension != null ? frontLineWorkerDimensionDimension :
                new FrontLineWorkerDimension(msisdn, operator, name, status);
    }

    public FrontLineWorkerDimension fetchFrontLineWorkerDimensionFromDB(Long msisdn) {
        return (FrontLineWorkerDimension) template.getUniqueResult(
            FrontLineWorkerDimension.FIND_BY_MSISDN, new String[]{"msisdn"}, new Object[]{msisdn});
    }
}
