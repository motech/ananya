package org.motechproject.ananya;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class SpringIntegrationTest {

    @Qualifier("ananyaDbConnector")
    @Autowired
	protected CouchDbConnector ananyaDbConnector;

    @Qualifier("ananyaProperties")
    @Autowired
    protected Properties ananyaProperties;

	protected ArrayList<BulkDeleteDocument> toDelete;

	@Before
	public void before() {
		toDelete = new ArrayList<>();
	}

	@After
	public void after() {
		ananyaDbConnector.executeBulk(toDelete);
	}
	
	protected void markForDeletion(Object document) {
		toDelete.add(BulkDeleteDocument.of(document));
	}
    
    protected String getAppServerPort() {
        return ananyaProperties.getProperty("app.server.port");
    }
    
    protected String getAppServerHostUrl() {
        return "http://localhost:" + getAppServerPort();
    }
}
