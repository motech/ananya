package org.motechproject.ananya.repository.dimension;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AllCourseItemDimensionsTest extends SpringIntegrationTest {

    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;

    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(CourseItemDimension.class));
    }

    @Test
    public void shouldGetCourseItemDimensionIfExists() {
        String name = "chapter 1";
        String contentId = "contentId";
        CourseItemType type = CourseItemType.CHAPTER;
        CourseItemDimension chapter = allCourseItemDimensions.add(new CourseItemDimension(name, contentId, type, null));

        CourseItemDimension chapterNew = allCourseItemDimensions.getFor(name, type);

        assertEquals(chapter.getId(), chapterNew.getId());
    }

    @Test
    public void shouldGetCourseItemDimensionOnContentId() {
        String name = "chapter 1";
        String contentId = "contentId";
        CourseItemType type = CourseItemType.CHAPTER;
        CourseItemDimension chapter = allCourseItemDimensions.add(new CourseItemDimension(name, contentId, type, null));

        CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(contentId);

        assertEquals(chapter.getContentId(), courseItemDimension.getContentId());
    }

    @Test
    public void shouldReturnNullIfTheContentIsNotPresent() {
        String contentId = "invalidContentId";

        CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(contentId);

        assertNull(courseItemDimension);
    }
}
