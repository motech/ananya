package org.motechproject.ananya.repository.measure;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AllCallDurationMeasures {
    private DataAccessTemplate template;

    public AllCallDurationMeasures() {
    }

    @Autowired
    public AllCallDurationMeasures(DataAccessTemplate template) {
        this.template = template;
    }

    public void add(CallDurationMeasure callDurationMeasure) {
        template.save(callDurationMeasure);
    }

    public List<CallDurationMeasure> findByCallId(String callId) {
        return (List<CallDurationMeasure>) template.findByNamedQueryAndNamedParam(CallDurationMeasure.FIND_BY_CALL_ID, new String[]{"callId"}, new Object[]{callId});
    }

    public List<CallDurationMeasure> findByCallerId(Long callerId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CallDurationMeasure.class);
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.add(Restrictions.eq("flw.msisdn", callerId));

        return template.findByCriteria(criteria);
    }

    public void updateAll(List<CallDurationMeasure> callDurationMeasureList) {
        template.saveOrUpdateAll(callDurationMeasureList);
    }
}
