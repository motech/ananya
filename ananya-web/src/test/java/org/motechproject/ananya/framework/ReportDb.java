package org.motechproject.ananya.framework;

import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDb {

    @Autowired
    private AllLocationDimensions allLocationDimensions;

    public void verifyIfExists(String callerId) {
        throw new RuntimeException("Not implemented.");
    }
}
