package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.CourseItemDetailsDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllCourseItemDetailsDimensions {

    @Autowired
    private DataAccessTemplate template;

    public AllCourseItemDetailsDimensions() {
    }

    public CourseItemDetailsDimension getFor(String contentId, Integer languageId) {
        return (CourseItemDetailsDimension) template.getUniqueResult(CourseItemDetailsDimension.FIND_BY_CONTENT_ID_AND_LANGUAGE_ID, new String[]{"contentId", "languageId"}, new Object[]{contentId.trim(), languageId});
    }

    public CourseItemDetailsDimension add(CourseItemDetailsDimension courseItemDetailsDimension) {
        template.save(courseItemDetailsDimension);
        return courseItemDetailsDimension;
    }

    public void update(CourseItemDetailsDimension courseItemDetailsDimension) {
        template.update(courseItemDetailsDimension);
    }
    
    public CourseItemDetailsDimension addOrUpdate(CourseItemDetailsDimension courseItemDetailsDimension) {
        template.saveOrUpdate(courseItemDetailsDimension);
        return courseItemDetailsDimension;
    }
}
