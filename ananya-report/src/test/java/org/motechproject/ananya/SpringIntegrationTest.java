package org.motechproject.ananya;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-report.xml")
public abstract class SpringIntegrationTest {

    @Autowired
    @Qualifier("testDataAccessTemplate")
    protected TestDataAccessTemplate template;

//    protected FrontLineWorkerDimension getTestFrontLineWorkerDimension() {
//        return new FrontLineWorkerDimension(1234567890, "airtel", "bihar", "abcd", "ANM", "PARTIALLY_REGISTERED");
//    }

}
