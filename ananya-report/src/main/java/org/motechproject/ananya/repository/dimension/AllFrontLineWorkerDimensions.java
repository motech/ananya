package org.motechproject.ananya.repository.dimension;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class AllFrontLineWorkerDimensions {

    @Autowired
    private DataAccessTemplate template;

    public AllFrontLineWorkerDimensions() {
    }

    public FrontLineWorkerDimension createOrUpdate(Long msisdn, String operator, String circle, String name, String designation, String status, UUID flwId, VerificationStatus verificationStatus) {
        FrontLineWorkerDimension frontLineWorkerDimension;
        frontLineWorkerDimension = fetchFor(msisdn);
        frontLineWorkerDimension = frontLineWorkerDimension == null ?
                new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status, flwId, verificationStatus) :
                frontLineWorkerDimension.update(circle, operator, name, status, designation, flwId, verificationStatus);

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

    public List<FrontLineWorkerDimension> getFilteredFLWFor(List<Long> allFilteredMsisdns, Long msisdn, String name, String registrationStatus, String designation, String operator, String circle) {
        return allFilteredMsisdns.size() > 30000 ?
                hackToGetOverPostgresParametersLimit(allFilteredMsisdns, msisdn, name, registrationStatus, designation, operator, circle)
                : getFilteredFLW(msisdn, name, registrationStatus, designation, operator, circle, allFilteredMsisdns);
    }

    private List<FrontLineWorkerDimension> hackToGetOverPostgresParametersLimit(List<Long> allFilteredMsisdns, Long msisdn, String name, String registrationStatus, String designation, String operator, String circle) {
        List<List<Long>> filteredMsisdnLists = splitParametersIntoSublist(allFilteredMsisdns);
        List<FrontLineWorkerDimension> accumulatedFrontLineWorkers = new ArrayList<FrontLineWorkerDimension>();

        for (List<Long> filteredMsisdns : filteredMsisdnLists) {
            List filteredFLW = getFilteredFLW(msisdn, name, registrationStatus, designation, operator, circle, filteredMsisdns);
            accumulatedFrontLineWorkers = (List<FrontLineWorkerDimension>) CollectionUtils.union(accumulatedFrontLineWorkers, filteredFLW);
        }
        return accumulatedFrontLineWorkers;
    }

    private List<List<Long>> splitParametersIntoSublist(List<Long> allFilteredMsisdns) {
        int startIndex = 0, endIndex = 0;
        List<List<Long>> splitList = new ArrayList<List<Long>>();

        while (endIndex != allFilteredMsisdns.size()) {
            endIndex = endIndex + 30000 < allFilteredMsisdns.size() ? endIndex + 30000 : allFilteredMsisdns.size();
            splitList.add(allFilteredMsisdns.subList(startIndex, endIndex));
            startIndex = endIndex;
        }
        return splitList;
    }

    private List getFilteredFLW(Long msisdn, String name, String registrationStatus, String designation, String operator, String circle, List<Long> filteredMsisdns) {
        DetachedCriteria criteria = DetachedCriteria.forClass(FrontLineWorkerDimension.class);

        if (!filteredMsisdns.isEmpty())
            criteria.add(Restrictions.in("msisdn", filteredMsisdns));
        if (msisdn != null)
            criteria.add(Restrictions.eq("msisdn", msisdn));
        if (registrationStatus != null)
            criteria.add(Restrictions.eq("status", registrationStatus));
        if (name != null)
            criteria.add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
        if (designation != null) {
            if (StringUtils.equalsIgnoreCase("null", StringUtils.trimToEmpty(designation)))
                criteria.add(Restrictions.isNull("designation"));
            else
                criteria.add(Restrictions.eq("designation", designation));
        }
        if (operator != null)
            criteria.add(Restrictions.eq("operator", operator).ignoreCase());
        if (circle != null)
            criteria.add(Restrictions.eq("circle", circle).ignoreCase());
        return template.findByCriteria(criteria);
    }

    public void createOrUpdateAll(List<FrontLineWorkerDimension> frontLineWorkerDimensions) {
        template.saveOrUpdateAll(frontLineWorkerDimensions);
    }
}
