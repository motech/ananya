package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.JobAidContentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllJobAidContentMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllJobAidContentMeasures() { }

    public void add(JobAidContentMeasure jobAidContentMeasure){
        template.save(jobAidContentMeasure);
    }

    public JobAidContentMeasure findByCallId(String callId) {
        return (JobAidContentMeasure)template.getUniqueResult(JobAidContentMeasure.FIND_BY_CALL_ID,
                new String[] {"call_id"}, new Object[] { callId });
    }
}
