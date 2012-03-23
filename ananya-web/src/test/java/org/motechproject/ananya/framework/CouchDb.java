package org.motechproject.ananya.framework;

import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CouchDb {

    @Autowired
    private AllLocations allLocations;
}
