package org.motechproject.ananya.functional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.repository.AllNodes;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class CertificationCourseSeedTest extends SpringIntegrationTest {
    @Autowired
    private AllNodes allNodes;

    @Test
    public void shouldLoadSeedForCertificationCourse() {
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
}
