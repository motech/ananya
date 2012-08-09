package org.motechproject.ananya.importer.csv;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-csv-importer.xml")
public abstract class SpringIntegrationTest {

    @Autowired
    @Qualifier("testDataAccessTemplate")
    protected TestDataAccessTemplate template;

}