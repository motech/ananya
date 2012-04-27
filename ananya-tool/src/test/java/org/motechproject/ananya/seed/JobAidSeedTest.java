package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

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

        Node jobAidCourse = allNodes.findByName("JobAidCourse");
        Node level1 = jobAidCourse.children().get(0);
        Node level1Chap1 = level1.children().get(0);
        Node level1Chap1Lesson1 = level1Chap1.children().get(0);

        assertEquals(1,jobAidCourse.contents().size());
        assertEquals(4,jobAidCourse.children().size());

        assertEquals(1, level1.contents().size());
        assertEquals(4, level1.children().size());

        assertEquals(2, level1Chap1.contents().size());
        assertEquals(4, level1Chap1.children().size());

        assertEquals(1, level1Chap1Lesson1.contents().size());
        assertEquals(0, level1Chap1Lesson1.children().size());
    }

}
