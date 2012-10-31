package org.motechproject.ananya.repository.measure;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AllSMSSentMeasures {

    private DataAccessTemplate template;

    public AllSMSSentMeasures() {
    }

    @Autowired
    public AllSMSSentMeasures(DataAccessTemplate template) {
        this.template = template;
    }

    public void save(SMSSentMeasure smsSentMeasure) {
        template.save(smsSentMeasure);
    }

    public SMSSentMeasure fetchFor(Integer flwId) {
        return (SMSSentMeasure) template.getUniqueResult(
                SMSSentMeasure.FIND_SMS_SENT_MEASURE_BY_FLW,
                new String[]{"flw_id"},
                new Object[]{flwId});
    }

    public List<SMSSentMeasure> findByCallerId(Long callerId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SMSSentMeasure.class);
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.add(Restrictions.eq("flw.msisdn", callerId));

        return template.findByCriteria(criteria);
    }

    public void updateAll(List<SMSSentMeasure> smsSentMeasureList) {
        template.saveOrUpdateAll(smsSentMeasureList);
    }

    public List<SMSSentMeasure> findByLocationId(String locationId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SMSSentMeasure.class);
        criteria.createAlias("locationDimension", "loc");
        criteria.add(Restrictions.eq("loc.locationId", locationId));

        return template.findByCriteria(criteria);
    }
}
