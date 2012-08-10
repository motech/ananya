package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllRegistrationMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllRegistrationMeasures() {
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
}
