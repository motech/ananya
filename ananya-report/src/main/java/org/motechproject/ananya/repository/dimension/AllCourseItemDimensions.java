package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllCourseItemDimensions {

    @Autowired
    private DataAccessTemplate template;

    public CourseItemDimension getOrMakeFor(String name, String contentId, CourseItemType type) {
        CourseItemDimension dimension = (CourseItemDimension) template.getUniqueResult(CourseItemDimension.FIND_BY_NAME, new String[]{"name"}, new Object[]{name});
        if (dimension == null) {
            dimension = new CourseItemDimension(name, contentId, type);
            template.save(dimension);
        }
        return dimension;
    }
}
