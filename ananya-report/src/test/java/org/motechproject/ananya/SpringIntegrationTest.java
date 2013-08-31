package org.motechproject.ananya;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public abstract class SpringIntegrationTest {

    @Autowired
    @Qualifier("testDataAccessTemplate")
    protected TestDataAccessTemplate template;
}
