package org.motechproject.ananya.performance;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-performance.xml")
public abstract class SpringIntegrationTest {


    protected static TestDataAccessTemplate template;

    @Autowired
    @Qualifier("testDataAccessTemplate")
    public void setTemplate(TestDataAccessTemplate testDataAccessTemplate) {
        template = testDataAccessTemplate;
    }
}
