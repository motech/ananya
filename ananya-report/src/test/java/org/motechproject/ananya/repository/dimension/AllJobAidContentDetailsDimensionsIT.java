package org.motechproject.ananya.repository.dimension;

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.JobAidContentDetailsDimension;
import org.springframework.beans.factory.annotation.Autowired;

public class AllJobAidContentDetailsDimensionsIT extends SpringIntegrationTest{

    @Autowired
    private AllJobAidContentDetailsDimensions allJobAidContentDetailsDimensions;

    @Test
    public void shouldFetchJobAidContentDetailsDimensionByContentId(){
        String contentId = "contentId";
        Integer languageId=1;
        JobAidContentDetailsDimension jobAidContentDetailsDimension =
                new JobAidContentDetailsDimension(languageId, contentId, "filename", 12);

        template.save(jobAidContentDetailsDimension);

        JobAidContentDetailsDimension findByContentAndLanguage = allJobAidContentDetailsDimensions.getFor(contentId, languageId);
        assertNotNull(findByContentAndLanguage);

        template.delete(findByContentAndLanguage);
    }
}
