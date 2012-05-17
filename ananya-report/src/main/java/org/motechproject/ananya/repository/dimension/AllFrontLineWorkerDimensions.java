package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AllFrontLineWorkerDimensions {

    @Autowired
    private DataAccessTemplate template;

    public AllFrontLineWorkerDimensions() {
    }

    public FrontLineWorkerDimension createOrUpdate(Long msisdn, String operator, String circle, String name, String designation, String status) {
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status);
        template.saveOrUpdate(frontLineWorkerDimension);
        return frontLineWorkerDimension;
    }

    public FrontLineWorkerDimension fetchFor(Long msisdn) {
        return (FrontLineWorkerDimension) template.getUniqueResult(
                FrontLineWorkerDimension.FIND_BY_MSISDN, new String[]{"msisdn"}, new Object[]{msisdn});
    }

    public void update(FrontLineWorkerDimension frontLineWorkerDimension) {
        template.update(frontLineWorkerDimension);
    }

    public List<FrontLineWorkerDimension> getAllUnregistered() {
        return template.findByNamedQueryAndNamedParam(FrontLineWorkerDimension.FIND_ALL_UNREGISTERED, new String[0], new Object[0]);
    }

    public void removeAll() {
        template.bulkUpdate("delete from FrontLineWorkerDimension");
    }
}
