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
public class AllJobAidContentMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllJobAidContentMeasures() {
    }

    public void add(JobAidContentMeasure jobAidContentMeasure) {
        template.save(jobAidContentMeasure);
    }

    public JobAidContentMeasure findByCallId(String callId) {
        return (JobAidContentMeasure) template.getUniqueResult(JobAidContentMeasure.FIND_BY_CALL_ID,
                new String[]{"call_id"}, new Object[]{callId});
    }

    public List<Long> getFilteredFrontLineWorkerMsisdns(Date startDate, Date endDate) {
        DetachedCriteria criteria = DetachedCriteria.forClass(JobAidContentMeasure.class);
        criteria.createAlias("timeDimension", "td");
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.setProjection(Projections.projectionList().add(Projections.property("flw.msisdn")));
        criteria.add(Restrictions.between("td.date", startDate, endDate));

        return template.findByCriteria(criteria);
    }
}
