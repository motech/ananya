package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllSMSSentMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllSMSSentMeasures() {
    }

    public void save(SMSSentMeasure smsSentMeasure) {
        template.save(smsSentMeasure);
    }
}
