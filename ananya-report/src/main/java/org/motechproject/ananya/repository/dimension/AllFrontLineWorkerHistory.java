package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllFrontLineWorkerHistory {

    @Autowired
    private DataAccessTemplate template;

    public AllFrontLineWorkerHistory() {
    }

    public void createOrUpdate(FrontLineWorkerHistory frontLineWorkerHistory) {
        template.saveOrUpdate(frontLineWorkerHistory);
    }

    public FrontLineWorkerHistory getCurrent(int flwId) {
        return (FrontLineWorkerHistory) template.getUniqueResult(FrontLineWorkerHistory.GET_CURRENT, new String[]{"flwId"}, new Object[]{flwId});
    }
}
