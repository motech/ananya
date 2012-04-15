package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllCourseItemMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllCourseItemMeasures() {
    }

    public CourseItemMeasure fetchFor(Integer flwId, String event) {
        return (CourseItemMeasure) template.getUniqueResult(
                CourseItemMeasure.FIND_BY_FLW_AND_EVENT,
                new String[]{"flw_id","event"},
                new Object[]{flwId,event});
    }
    
    
    public CourseItemMeasure fetchFor(Integer flwId, CourseItemDimension courseItemDimension, String event) {
        return (CourseItemMeasure) template.getUniqueResult(
                CourseItemMeasure.FIND_BY_FLW_AND_COURSE_ITEM_MEASURE_AND_EVENT,
                new String[]{"flw_id","course_item_id","event"},
                new Object[]{flwId,courseItemDimension.getId(),event});
    }
    

    public void save(CourseItemMeasure courseItemMeasure) {
        template.save(courseItemMeasure);
    }
}
