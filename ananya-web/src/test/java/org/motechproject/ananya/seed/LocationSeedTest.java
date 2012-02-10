
package org.motechproject.ananya.seed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.repository.AllLocationDimensions;
import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class LocationSeedTest {
    
    @Autowired
    private LocationSeed locationSeed;
    
    @Autowired
    private AllLocations allLocations;
    
    @Autowired
    private AllLocationDimensions allLocationDimensions;
    
    /*
     * Functional test to ensure data is extracted from CSV into Postgres and
     * CouchDB databases.
     */
    @Test
    public void shouldLoadDataFromCSVToTransactionalAndReportingDBs() 
            throws FileNotFoundException, IOException {
        
        String fileName = "E:\\code\\ananya\\ananya-web\\src\\test\\resources\\Panchayatvillages_WithCodes.csv";
        
        locationSeed.loadFromCsv(fileName);
        
        assertEquals(allLocations.getAll().size(), 56);
        assertEquals(allLocationDimensions.getCount(), 56);
    }
}
