package org.motechproject.ananya.repository.measure;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
                new String[]{"flw_id", "event"},
                new Object[]{flwId, event});
    }


    public CourseItemMeasure fetchFor(Integer flwId, CourseItemDimension courseItemDimension, String event) {
        return (CourseItemMeasure) template.getUniqueResult(
                CourseItemMeasure.FIND_BY_FLW_AND_COURSE_ITEM_MEASURE_AND_EVENT,
                new String[]{"flw_id", "course_item_id", "event"},
                new Object[]{flwId, courseItemDimension.getId(), event});
    }

    public List<Long> getFilteredFrontLineWorkerMsisdns(Date startDate, Date endDate) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CourseItemMeasure.class);
        criteria.createAlias("timeDimension", "td");
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.setProjection(Projections.projectionList().add(Projections.property("flw.msisdn")));
        criteria.add(Restrictions.between("td.date", startDate, endDate));

        return template.findByCriteria(criteria);
    }

    public void save(CourseItemMeasure courseItemMeasure) {
        template.save(courseItemMeasure);
    }
}
