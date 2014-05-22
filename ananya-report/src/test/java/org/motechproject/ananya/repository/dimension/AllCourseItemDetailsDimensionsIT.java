package org.motechproject.ananya.repository.dimension;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.CourseItemDetailsDimension;
import org.springframework.beans.factory.annotation.Autowired;

public class AllCourseItemDetailsDimensionsIT extends SpringIntegrationTest {

    @Autowired
    private AllCourseItemDetailsDimensions allCourseItemDetailDimensions;

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(CourseItemDetailsDimension.class));
    }

    @Test
    public void shouldGetCourseItemDetailsDimensionIfExists() {
        Integer languageId = 1;
        String contentId = "contentId";
        String fileName = "chapter01.wav";
        Integer duration = 120;
        CourseItemDetailsDimension chapter = allCourseItemDetailDimensions.add(new CourseItemDetailsDimension(1, contentId, fileName, duration));

        CourseItemDetailsDimension chapterNew = allCourseItemDetailDimensions.getFor(contentId, languageId);

        assertEquals(chapter.getId(), chapterNew.getId());
    }

    @Test
    public void shouldReturnNullIfTheContentIsNotPresent() {
        String contentId = "invalidContentId";
        Integer languageId = 0;
        CourseItemDetailsDimension courseItemDetailsDimension = allCourseItemDetailDimensions.getFor(contentId, languageId);

        assertNull(courseItemDetailsDimension);
    }

}
