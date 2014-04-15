package org.motechproject.ananya.seed;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.TestDataAccessTemplate;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.domain.dimension.OperatorDimension;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.repository.dimension.AllOperatorDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class OperatorSeedIT {

    @Autowired
    private OperatorSeed seed;

    @Autowired
    private AllOperators allOperators;

    @Autowired
    private AllOperatorDimensions allOperatorDimensions;

    @Autowired
    private TestDataAccessTemplate template;

    @After
    @Before
    public void tearDown() {
        allOperators.removeAll();
        template.deleteAll(template.loadAll(OperatorDimension.class));
    }

    @Test
    @Rollback(true)
    public void shouldLoadAllOperatorsFromSeed() throws IOException {
        seed.load();

        String airtelOperator = "airtel";
        Operator airtel = allOperators.findByName(airtelOperator, null);
        Assert.assertEquals(airtel.getAllowedUsagePerMonth(), OperatorSeed.operator_usage.get(airtelOperator));
    }

    @Test
    public void shouldConvertMinutesToMilliSeconds() {
        assertEquals(OperatorSeed.convertMinutesToMilliSeconds(1), Integer.valueOf(60000));
        assertEquals(OperatorSeed.convertMinutesToMilliSeconds(0), Integer.valueOf(0));
    }

    @Test
    public void shouldAddStartAndEndOFPulseInMilliSec_and_populateAllOperatorDimensions() throws IOException {
        seed.load();
        seed.loadLongCode();

        seed.addPulseToSec(); // our test

        List<Operator> operators = allOperators.getAll();
        for (Operator operator : operators) {
            assertEquals(OperatorSeed.start_of_pulse_map.get(operator.getName()), operator.getStartOfPulseInMilliSec());
            assertEquals(OperatorSeed.end_of_pulse_map.get(operator.getName()), operator.getEndOfPulseInMilliSec());
        }

        List<OperatorDimension> operatorDimensions = template.loadAll(OperatorDimension.class);
        for (OperatorDimension operatorDimension : operatorDimensions) {
            assertEquals(OperatorSeed.operator_usage.get(operatorDimension.getName()),
                    operatorDimension.getAllowedUsagePerMonth());
            assertEquals(OperatorSeed.start_of_pulse_map.get(operatorDimension.getName()),
                    operatorDimension.getStartOfPulseInMilliSec());
            assertEquals(OperatorSeed.end_of_pulse_map.get(operatorDimension.getName()),
                    operatorDimension.getEndOfPulseInMilliSec());
        }
    }
}