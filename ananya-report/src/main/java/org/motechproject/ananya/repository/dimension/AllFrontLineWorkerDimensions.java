package org.motechproject.ananya.repository.dimension;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
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
        FrontLineWorkerDimension frontLineWorkerDimension;
        frontLineWorkerDimension = fetchFor(msisdn);
        frontLineWorkerDimension = frontLineWorkerDimension == null ?
                new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status) :
                frontLineWorkerDimension.update(circle, operator, name, status, designation);

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

    public void remove(FrontLineWorkerDimension frontLineWorkerDimension) {
        template.delete(frontLineWorkerDimension);
    }

    public List<FrontLineWorkerDimension> getFilteredFLWFor(List<Long> allFilteredMsisdns, String name, String registrationStatus, String designation, String operator, String circle) {
        DetachedCriteria criteria = DetachedCriteria.forClass(FrontLineWorkerDimension.class);

        if (!allFilteredMsisdns.isEmpty())
            criteria.add(Restrictions.in("msisdn", allFilteredMsisdns));
        if (registrationStatus != null)
            criteria.add(Restrictions.eq("status", registrationStatus));
        if (name != null)
            criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
        if (designation != null)
            criteria.add(Restrictions.eq("designation", designation));
        if (operator != null)
            criteria.add(Restrictions.eq("operator", operator).ignoreCase());
        if (circle != null)
            criteria.add(Restrictions.eq("circle", circle).ignoreCase());

        return template.findByCriteria(criteria);
    }
}
