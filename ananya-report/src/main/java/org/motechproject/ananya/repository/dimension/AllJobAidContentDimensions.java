package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllJobAidContentDimensions {

    @Autowired
    private DataAccessTemplate template;

    public void add(JobAidContentDimension jobAidContentDimension) {
        template.save(jobAidContentDimension);
    }

    public JobAidContentDimension findByContentId(String contentId) {
        return (JobAidContentDimension)template.getUniqueResult(JobAidContentDimension.FIND_BY_CONTENT_ID,
                new String[] {"content_id"}, new Object[] { contentId });
    }
}
