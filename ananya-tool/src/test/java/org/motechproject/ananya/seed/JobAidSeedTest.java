package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class JobAidSeedTest {

    @Autowired
    private JobAidSeed jobAidSeed;

    @Autowired
    private AllNodes allNodes;

    @Autowired
    private TestDataAccessTemplate template;

    @Before
    @After
    public void setUp() {
        allNodes.removeAll();
        template.deleteAll(template.loadAll(JobAidContentDimension.class));
    }

    @Test
    public void shouldLoadJobAidSeedData() throws Exception {
        jobAidSeed.load();
    }

}
