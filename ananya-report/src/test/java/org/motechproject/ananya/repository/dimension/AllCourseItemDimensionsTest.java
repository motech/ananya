package org.motechproject.ananya.repository.dimension;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AllCourseItemDimensionsTest extends SpringIntegrationTest{

    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(CourseItemDimension.class));
    }

    @Test
    public void shouldSaveCourseItemDimension() {
        String name = "chapter 1";
        String contentId = "contentId";
        CourseItemType type = CourseItemType.CHAPTER;

        CourseItemDimension chapter = allCourseItemDimensions.getOrMakeFor(name, contentId, type);

        assertNotNull(chapter.getId());
    }

    @Test
    public void shouldGetCourseItemDimensionIfExists() {
        String name = "chapter 1";
        String contentId = "contentId";
        CourseItemType type = CourseItemType.CHAPTER;

        CourseItemDimension chapter = allCourseItemDimensions.getOrMakeFor(name, contentId, type);
        CourseItemDimension chapterNew = allCourseItemDimensions.getOrMakeFor(name, contentId, type);

        assertEquals(chapter.getId(), chapterNew.getId());
    }


}
