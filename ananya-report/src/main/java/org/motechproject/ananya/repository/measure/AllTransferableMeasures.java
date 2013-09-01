package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.TransferableMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract class AllTransferableMeasures {
    protected DataAccessTemplate template;

    public void transfer(Class<? extends TransferableMeasure> measure, Integer fromFlwId, Integer toFlwId) {
        template.bulkUpdate("update " + measure.getName() + " measure set measure.flwId = ? where measure.flwId = ?", toFlwId, fromFlwId);
    }

}
