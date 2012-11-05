package org.motechproject.ananya.seed;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.repository.AllOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class OperatorSeedTest {

    @Autowired
    private OperatorSeed seed;

    @Autowired
    private AllOperators allOperators;

    @Before
    public void setUp() {
        allOperators.removeAll();
    }

    @After
    public void tearDown() {
        allOperators.removeAll();
    }

    @Test
    @Rollback(true)
    public void shouldLoadAllOperatorsFromSeed() throws IOException {
        seed.load();

        String airtelOperator = "airtel";
        Operator airtel = allOperators.findByName(airtelOperator);
        Assert.assertEquals(airtel.getAllowedUsagePerMonth(), OperatorSeed.operator_usage.get(airtelOperator));
    }

    @Test
    public void shouldConvertMinutesToMilliSeconds() {
        assertEquals(OperatorSeed.convertMinutesToMilliSeconds(1), Integer.valueOf(60000));
        assertEquals(OperatorSeed.convertMinutesToMilliSeconds(0), Integer.valueOf(0));
    }

    @Test
    public void shouldAddPulseToSecMapping() throws IOException {
        seed.load();
        seed.loadLongCode();

        seed.addPulseToSec(); // our test

        List < Operator > operators = allOperators.getAll();
        for (Operator operator : operators) {
            assertEquals(60000, (int) operator.getPulseToMilliSec());
        }
    }
}
