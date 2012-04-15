package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllSMSSentMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllSMSSentMeasures() {
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

}
