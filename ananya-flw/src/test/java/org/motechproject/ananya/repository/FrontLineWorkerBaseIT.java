package org.motechproject.ananya.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-flw.xml")
public abstract class FrontLineWorkerBaseIT {

    @Qualifier("ananyaDbConnector")
    @Autowired
	protected CouchDbConnector ananyaDbConnector;

	protected ArrayList<BulkDeleteDocument> toDelete;

	@Before
	public void before() {
		toDelete = new ArrayList<BulkDeleteDocument>();
	}

	@After
	public void after() {
		ananyaDbConnector.executeBulk(toDelete);
	}

	protected void markForDeletion(Object document) {
		toDelete.add(BulkDeleteDocument.of(document));
	}
}
