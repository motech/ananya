package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class CertificationCourseSeedTest {

    @Autowired
    private CertificationCourseSeed certificationCourseSeed;

    @Autowired
    private AllNodes allNodes;

    @Autowired
    private TestDataAccessTemplate template;

    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;

    @Before
    @After
    public void setUp() throws Exception {
        allNodes.removeAll();
        template.deleteAll(template.loadAll(CourseItemDimension.class));
    }

    @Test
    public void shouldLoadCertificateCourseSeedData() throws Exception {
        certificationCourseSeed.loadSeed();

        Node course = allNodes.findByName("CertificationCourse");
        assertEquals(9, course.children().size());
        assertEquals("course", course.data().get("type"));

        Node chapter = course.children().get(0);
        assertEquals(8, chapter.children().size());
        assertEquals("chapter", chapter.data().get("type"));

        Node lesson = chapter.children().get(0);
        assertEquals(0, lesson.children().size());
        assertEquals("lesson", lesson.data().get("type"));
    }

    @Test
    public void shouldLoadAudioContentDetails() {
        certificationCourseSeed.loadSeed();
        certificationCourseSeed.loadAudioContentDetails();

        Node chapterNode = allNodes.findByName("Chapter 1");

        assertEquals(7, chapterNode.contents().size());
        for(StringContent content : chapterNode.contents()) {
            assertTrue(allCourseItemDimensions.getFor(content.getId()).getName().contains(content.getValue()));
        }
    }
}
