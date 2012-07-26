package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AllCourseItemMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllCourseItemMeasures() {
    }

    public void save(CourseItemMeasure courseItemMeasure) {
        template.save(courseItemMeasure);
    }

    public List<CourseItemMeasure> fetchFor(String callId) {
        return template.findByNamedQueryAndNamedParam(CourseItemMeasure.FIND_BY_CALL_ID, new String[]{"callId"}, new Object[]{callId});
    }
}
