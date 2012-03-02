package org.motechproject.ananya.repository.measure;

import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallMeasures {

    @Autowired
    private DataAccessTemplate template;

    public AllCallMeasures() {
    }
}
