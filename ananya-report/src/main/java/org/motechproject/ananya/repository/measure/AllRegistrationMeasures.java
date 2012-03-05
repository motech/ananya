package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllRegistrationMeasures {

    @Autowired
    private DataAccessTemplate template;

    public RegistrationMeasure fetchFor(Integer flwId, Integer timeId, Integer locationId) {
        return (RegistrationMeasure) template.getUniqueResult(
            RegistrationMeasure.FIND_BY_FLW_LOCATION_TIME,
                new String[]{"flw_id", "time_id", "location_id"},
                new Object[]{flwId, timeId, locationId});
    }

    public void removeAll() {
        template.bulkUpdate("delete from RegistrationMeasure");
    }
}