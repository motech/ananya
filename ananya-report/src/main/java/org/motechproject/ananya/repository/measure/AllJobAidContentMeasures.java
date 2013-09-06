package org.motechproject.ananya.repository.measure;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class AllJobAidContentMeasures extends AllTransferableMeasures {

    public AllJobAidContentMeasures() {
    }

    @Autowired
    public AllJobAidContentMeasures(DataAccessTemplate template) {
        this.template = template;
    }

    public void add(JobAidContentMeasure jobAidContentMeasure) {
        template.save(jobAidContentMeasure);
    }

    public List<Long> getFilteredFrontLineWorkerMsisdns(Date startDate, Date endDate) {
        DetachedCriteria criteria = DetachedCriteria.forClass(JobAidContentMeasure.class);
        criteria.createAlias("timeDimension", "td");
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.setProjection(Projections.distinct(Projections.property("flw.msisdn")));
        criteria.add(Restrictions.between("td.date", startDate, endDate));

        return template.findByCriteria(criteria);
    }

    public List<JobAidContentMeasure> findByCallId(String callId) {
        return (List<JobAidContentMeasure>) template.findByNamedQueryAndNamedParam(JobAidContentMeasure.FIND_BY_CALL_ID, new String[]{"call_id"}, new Object[]{callId});
    }

    public List<JobAidContentMeasure> findByCallerId(Long callerId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(JobAidContentMeasure.class);
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.add(Restrictions.eq("flw.msisdn", callerId));

        return template.findByCriteria(criteria);
    }

    public void updateAll(List<JobAidContentMeasure> jobAidContentMeasureList) {
        template.saveOrUpdateAll(jobAidContentMeasureList);
    }

    public List<JobAidContentMeasure> findByLocationId(String locationId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(JobAidContentMeasure.class);
        criteria.createAlias("locationDimension", "loc");
        criteria.add(Restrictions.eq("loc.locationId", locationId));

        return template.findByCriteria(criteria);
    }
}
