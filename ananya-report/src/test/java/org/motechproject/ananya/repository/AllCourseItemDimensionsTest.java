package org.motechproject.ananya.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
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
        CourseItemType type = CourseItemType.CHAPTER;

        CourseItemDimension chapter = allCourseItemDimensions.getOrMakeFor(name, type);

        assertNotNull(chapter.getId());
    }

    @Test
    public void shouldGetCourseItemDimensionIfExists() {
        String name = "chapter 1";
        CourseItemType type = CourseItemType.CHAPTER;

        CourseItemDimension chapter = allCourseItemDimensions.getOrMakeFor(name, type);
        CourseItemDimension chapterNew = allCourseItemDimensions.getOrMakeFor(name, type);

        assertEquals(chapter.getId(), chapterNew.getId());
    }


}
