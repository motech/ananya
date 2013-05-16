package org.motechproject.ananya.repository.measure;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllRegistrationMeasures {

    private DataAccessTemplate template;

    public AllRegistrationMeasures() {
    }

    @Autowired
    public AllRegistrationMeasures(DataAccessTemplate template) {
        this.template = template;
    }

    public RegistrationMeasure fetchFor(Integer flwId, Integer timeId, Integer locationId) {
        return (RegistrationMeasure) template.getUniqueResult(
                RegistrationMeasure.FIND_BY_FLW_LOCATION_TIME,
                new String[]{"flw_id", "time_id", "location_id"},
                new Object[]{flwId, timeId, locationId});
    }

    public RegistrationMeasure fetchFor(Integer flwId) {
        return (RegistrationMeasure) template.getUniqueResult(
                RegistrationMeasure.FIND_BY_FLW,
                new String[]{"flw_id"},
                new Object[]{flwId});
    }

    public void createOrUpdate(RegistrationMeasure registrationMeasure) {
        template.saveOrUpdate(registrationMeasure);
    }

    public void removeAll() {
        template.bulkUpdate("delete from RegistrationMeasure");
    }

    public void remove(RegistrationMeasure registrationMeasure) {
        template.delete(registrationMeasure);
    }

    public List<RegistrationMeasure> findByLocationId(String locationId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(RegistrationMeasure.class);
        criteria.createAlias("locationDimension", "loc");
        criteria.add(Restrictions.eq("loc.locationId", locationId));

        return template.findByCriteria(criteria);
    }

    public void updateAll(List<RegistrationMeasure> registrationMeasureList) {
        template.saveOrUpdateAll(registrationMeasureList);
    }
}
