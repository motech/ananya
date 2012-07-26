package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllCallDurationMeasures {
    @Autowired
    private DataAccessTemplate template;

    public AllCallDurationMeasures() {
    }

    public void add(CallDurationMeasure callDurationMeasure) {
        template.save(callDurationMeasure);
    }

    public CallDurationMeasure findByCallId(String callId) {
        return (CallDurationMeasure) template.getUniqueResult(CallDurationMeasure.FIND_BY_CALL_ID,
                new String[]{"callId"}, new Object[]{callId});
    }
}
