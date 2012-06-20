package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class JobAidSeedTest {

    @Autowired
    private JobAidSeed jobAidSeed;

    @Autowired
    private AllNodes allNodes;

    @Autowired
    private AllJobAidContentDimensions allJobAidContentDimensions;

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
        jobAidSeed.createJobAidCourseStructure();

        Node jobAidCourse = allNodes.findByName("JobAidCourse");
        Node level1 = jobAidCourse.children().get(0);
        Node level1Chap1 = level1.children().get(0);
        Node level1Chap1Lesson1 = level1Chap1.children().get(0);

        assertEquals(1, jobAidCourse.contents().size());
        assertEquals(4, jobAidCourse.children().size());

        assertEquals(1, level1.contents().size());
        assertEquals(4, level1.children().size());

        assertEquals(2, level1Chap1.contents().size());
        assertEquals(4, level1Chap1.children().size());

        assertEquals(1, level1Chap1Lesson1.contents().size());
        assertEquals(0, level1Chap1Lesson1.children().size());
    }

    @Test
    public void shouldPropagateAllChangesToPostgres() throws Exception {
        jobAidSeed.createJobAidCourseStructure();
        jobAidSeed.addJobAidContentDimensions();

        Node jobAidCourse = allNodes.findByName("JobAidCourse");
        Node level1 = jobAidCourse.children().get(0);
        Node level1Chap1 = level1.children().get(0);
        Node level1Chap1Lesson1 = level1Chap1.children().get(0);

        assertNotNull(allJobAidContentDimensions.findByContentId(jobAidCourse.contentIds().get(0)));
        assertNotNull(allJobAidContentDimensions.findByContentId(level1.contentIds().get(0)));
        assertNotNull(allJobAidContentDimensions.findByContentId(level1Chap1.contentIds().get(0)));
        assertNotNull(allJobAidContentDimensions.findByContentId(level1Chap1Lesson1.contentIds().get(0)));
    }

    @Test
    public void shouldUpdateTheDbsForUpdatedAudioFile() {
        jobAidSeed.createJobAidCourseStructure();
        jobAidSeed.addJobAidContentDimensions();

        String nodeName1 = "Level 3 Chapter 2 Lesson2";
        String nodeName2 = "Level 4 Chapter 2 Lesson2";
        assertNodeDuration(nodeName1, "119581");
        assertNodeDuration(nodeName2, "119581");

        jobAidSeed.accommodateForUpdatedAudioFile0022_iud();

        assertNodeDuration(nodeName1, "138742");
        assertNodeDuration(nodeName2, "138742");

        JobAidContentDimension contentDimension1 = allJobAidContentDimensions.findByContentId(getStringContent(nodeName1).getId());
        assertThat(contentDimension1.getDuration(), is(138742));
        JobAidContentDimension contentDimension2 = allJobAidContentDimensions.findByContentId(getStringContent(nodeName2).getId());
        assertThat(contentDimension2.getDuration(), is(138742));

    }

    private void assertNodeDuration(String nodeName, String duration) {
        StringContent stringContent = getStringContent(nodeName);
        assertThat(stringContent.getMetadata().get("duration"), is(duration));
    }

    private StringContent getStringContent(String nodeName) {
        Node node = allNodes.findByName(nodeName);
        List<StringContent> contents = node.contents();
        return contents.get(0);
    }
}
