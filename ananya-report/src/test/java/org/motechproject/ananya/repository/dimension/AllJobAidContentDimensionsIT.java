package org.motechproject.ananya.repository.dimension;


import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertNotNull;

public class AllJobAidContentDimensionsIT extends SpringIntegrationTest{

    @Autowired
    private AllJobAidContentDimensions allJobAidContentDimensions;

    @Test
    public void shouldFetchJobAidContentDimensionByContentId(){
        String contentId = "contentId";
        JobAidContentDimension jobAidContentDimension =
                new JobAidContentDimension(contentId, null, "name", "audio");

        template.save(jobAidContentDimension);

        JobAidContentDimension findByContentDimension = allJobAidContentDimensions.findByContentId(contentId);
        assertNotNull(findByContentDimension);

        template.delete(findByContentDimension);
    }

}
