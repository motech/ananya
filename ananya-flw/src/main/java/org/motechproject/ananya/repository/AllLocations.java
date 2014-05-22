package org.motechproject.ananya.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
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
    public Location findByExternalId(String externalId) {
        ViewQuery viewQuery = createQuery("by_externalId").key(externalId).includeDocs(true);
        List<Location> locations = db.queryView(viewQuery, Location.class);
        if (locations == null || locations.isEmpty()) return null;
        return locations.get(0);
    }

    @View(name = "by_district_and_block_and_panchayat", map = "function(doc){if(doc.type === 'Location') emit([doc.district.toLowerCase(), doc.block.toLowerCase(), doc.panchayat.toLowerCase()]);}")
    public Location findByDistrictBlockPanchayat(String district, String block, String panchayat) {
        List<Location> locations = queryView("by_district_and_block_and_panchayat", ComplexKey.of(district.toLowerCase(), block.toLowerCase(), panchayat.toLowerCase()));
        if (locations == null || locations.isEmpty()) return null;
        return locations.get(0);
    }
    
    @View(name = "by_state_and_district_and_block_and_panchayat", map = "function(doc){if(doc.type === 'Location') emit([doc.state.toLowerCase(), doc.district.toLowerCase(), doc.block.toLowerCase(), doc.panchayat.toLowerCase()]);}")
    public Location findByStateDistrictBlockPanchayat(String state, String district, String block, String panchayat) {
        List<Location> locations = queryView("by_state_and_district_and_block_and_panchayat", ComplexKey.of(state.toLowerCase(), district.toLowerCase(), block.toLowerCase(), panchayat.toLowerCase()));
        if (locations == null || locations.isEmpty()) return null;
        return locations.get(0);
    }
}
