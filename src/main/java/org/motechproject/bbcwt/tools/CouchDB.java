package org.motechproject.bbcwt.tools;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;

public class CouchDB {
    @Autowired
    private CouchDbInstance couchDbInstance;
    @Autowired
    private CouchDbConnector dbConnector;

    public void recreate() {
        String dbName = dbConnector.getDatabaseName();
        couchDbInstance.deleteDatabase(dbName);
        couchDbInstance.createDatabase(dbName);
    }
}

