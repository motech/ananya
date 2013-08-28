package org.motechproject.ananya.repository.measure;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class AllCourseItemMeasures extends AllMeasures {

    public AllCourseItemMeasures() {
    }

    @Autowired
    public AllCourseItemMeasures(DataAccessTemplate template) {
        this.template = template;
    }

    public List<Long> getFilteredFrontLineWorkerMsisdns(Date startDate, Date endDate) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CourseItemMeasure.class);
        criteria.createAlias("timeDimension", "td");
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.setProjection(Projections.distinct(Projections.property("flw.msisdn")));
        criteria.add(Restrictions.between("td.date", startDate, endDate));

        return template.findByCriteria(criteria);
    }

    public void save(CourseItemMeasure courseItemMeasure) {
        template.save(courseItemMeasure);
    }

    public List<CourseItemMeasure> fetchFor(String callId) {
        return template.findByNamedQueryAndNamedParam(CourseItemMeasure.FIND_BY_CALL_ID, new String[]{"callId"}, new Object[]{callId});
    }

    public List<CourseItemMeasure> findByCallerId(Long callerId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CourseItemMeasure.class);
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.add(Restrictions.eq("flw.msisdn", callerId));

        return template.findByCriteria(criteria);
    }

    public void updateAll(List<CourseItemMeasure> courseItemMeasures) {
        template.saveOrUpdateAll(courseItemMeasures);
    }

    public List<CourseItemMeasure> findByLocationId(String locationId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CourseItemMeasure.class);
        criteria.createAlias("locationDimension", "loc");
        criteria.add(Restrictions.eq("loc.locationId", locationId));

        return template.findByCriteria(criteria);
    }

}
