package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.Location;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllLocations extends MotechBaseRepository<Location> {

    @Autowired
    protected AllLocations(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(Location.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public Location findByExternalId(String panchayat) {
        ViewQuery viewQuery = createQuery("by_externalId").key(panchayat).includeDocs(true);
        List<Location> locations = db.queryView(viewQuery, Location.class);
        if (locations == null || locations.isEmpty()) return null;
        return locations.get(0);
    }
    
    public Location addOrUpdate(Location location) {
        Location existingLocation = findByExternalId(location.getExternalId());
        if (existingLocation == null) {
            add(location);
            return location;
        }
        existingLocation.cloneFrom(location);
        update(existingLocation);
        return existingLocation;
    }
}
