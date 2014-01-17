package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.JobAidContentDetailsDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllJobAidContentDetailsDimensions {
    
	@Autowired
    private DataAccessTemplate template;

    public AllJobAidContentDetailsDimensions() {
    }

    public JobAidContentDetailsDimension getFor(String contentId, Integer languageId) {
        return (JobAidContentDetailsDimension) template.getUniqueResult(JobAidContentDetailsDimension.FIND_BY_CONTENT_ID_AND_LANGUAGE_ID, new String[]{"contentId", "languageId"}, new Object[]{contentId.trim(), languageId});
    }

    public JobAidContentDetailsDimension add(JobAidContentDetailsDimension jobAidContentDetailsDimension) {
        template.save(jobAidContentDetailsDimension);
        return jobAidContentDetailsDimension;
    }

    public void update(JobAidContentDetailsDimension jobAidContentDetailsDimension) {
        template.update(jobAidContentDetailsDimension);
    }
    
    public JobAidContentDetailsDimension addOrUpdate(JobAidContentDetailsDimension jobAidContentDetailsDimension) {
        template.saveOrUpdate(jobAidContentDetailsDimension);
        return jobAidContentDetailsDimension;
    }
}
