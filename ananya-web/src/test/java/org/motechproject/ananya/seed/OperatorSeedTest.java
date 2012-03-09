package org.motechproject.ananya.seed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class OperatorSeedTest {

    @Autowired
    private OperatorSeed seed;

    @Autowired
    private AllOperators allOperators;

    @Autowired
    private DataAccessTemplate template;

    //TODO:Find a cleaner(transactional) way to run this test[Imdad/Sush]
    @Before
    public void setUp() {
        allOperators.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @After
    public void tearDown() {
        allOperators.removeAll();
        template.deleteAll(template.loadAll(LocationDimension.class));
    }

    @Test
    @Rollback(true)
    public void shouldLoadAllOperatorsFromSeed() throws IOException {
        seed.load();

        String airtelOperator = "airtel";
        Operator airtel = allOperators.findByName(airtelOperator);
        assertEquals(airtel.getAllowedUsagePerMonth(), OperatorSeed.operator_usage.get(airtelOperator));
    }
}
