package org.motechproject.ananya.seed;

import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.domain.dimension.OperatorDimension;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.repository.dimension.AllOperatorDimensions;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

@Component
public class OperatorSeed {

    @Autowired
    AllOperators allOperators;

    @Autowired
    private AllOperatorDimensions allOperatorDimensions;

    protected final static HashMap<String, Integer> operator_usage = new HashMap<>();
    protected final static HashMap<String, Integer> start_of_pulse_map = new HashMap<>();

    protected final static HashMap<String, Integer> end_of_pulse_map = new HashMap<>();
    public static final int DEFAULT_START_OF_PULSE_IN_MILLI_SEC = 0;
    public static final int DEFAULT_END_OF_PULSE_IN_MILLI_SEC = 60000;

    {
        operator_usage.put("airtel", convertMinutesToMilliSeconds(39));
        operator_usage.put("tata", convertMinutesToMilliSeconds(48));
        operator_usage.put("vodafone", convertMinutesToMilliSeconds(39));
        operator_usage.put("idea", convertMinutesToMilliSeconds(38));
        operator_usage.put("reliance", convertMinutesToMilliSeconds(34));
        operator_usage.put("bsnl", convertMinutesToMilliSeconds(28));
        operator_usage.put("undefined", convertMinutesToMilliSeconds(28));
        operator_usage.put("longcode", convertMinutesToMilliSeconds(50));

        start_of_pulse_map.put("tata", 500);
        start_of_pulse_map.put("idea", 500);
        start_of_pulse_map.put("reliance", 500);
        start_of_pulse_map.put("airtel", DEFAULT_START_OF_PULSE_IN_MILLI_SEC);
        start_of_pulse_map.put("bsnl", 500);
        start_of_pulse_map.put("vodafone", DEFAULT_START_OF_PULSE_IN_MILLI_SEC);
        start_of_pulse_map.put("undefined", DEFAULT_START_OF_PULSE_IN_MILLI_SEC);
        start_of_pulse_map.put("longcode", DEFAULT_START_OF_PULSE_IN_MILLI_SEC);

        end_of_pulse_map.put("tata", 60500);
        end_of_pulse_map.put("idea", 60500);
        end_of_pulse_map.put("reliance", 60500);
        end_of_pulse_map.put("airtel", DEFAULT_END_OF_PULSE_IN_MILLI_SEC);
        end_of_pulse_map.put("bsnl", 60500);
        end_of_pulse_map.put("vodafone", DEFAULT_END_OF_PULSE_IN_MILLI_SEC);
        end_of_pulse_map.put("undefined", DEFAULT_END_OF_PULSE_IN_MILLI_SEC);
        end_of_pulse_map.put("longcode", DEFAULT_END_OF_PULSE_IN_MILLI_SEC);
    }

    public static Integer convertMinutesToMilliSeconds(int minutes) {
        return minutes * 60 * 1000;
    }

    @Seed(priority = 0, version = "1.0", comment = "load the usage limit for the operators")
    public void load() throws IOException {
        Iterator<String> operatorNameIterator = operator_usage.keySet().iterator();

        while (operatorNameIterator.hasNext()) {
            String nextName = operatorNameIterator.next();
            Operator operator = new Operator(nextName, operator_usage.get(nextName), DEFAULT_START_OF_PULSE_IN_MILLI_SEC, DEFAULT_END_OF_PULSE_IN_MILLI_SEC);
            allOperators.add(operator);
        }
    }

    @Seed(priority = 0, version = "1.2", comment = "load long code")
    public void loadLongCode() throws IOException {
        Operator operator = new Operator("longcode", operator_usage.get("longcode"), DEFAULT_START_OF_PULSE_IN_MILLI_SEC, DEFAULT_END_OF_PULSE_IN_MILLI_SEC);
        allOperators.add(operator);

    }

    @Seed(priority = 0, version = "1.10", comment = "adding pulse to second mapping for operators")
    public void addPulseToSec() throws IOException {
        for (String operatorName : end_of_pulse_map.keySet()) {
            Operator operator = allOperators.findByName(operatorName);
            operator.setStartOfPulseInMilliSec(start_of_pulse_map.get(operatorName));
            operator.setEndOfPulseInMilliSec(end_of_pulse_map.get(operatorName));
            allOperators.update(operator);

            allOperatorDimensions.add(new OperatorDimension(operator.getName(), operator.getAllowedUsagePerMonth(),
                    operator.getStartOfPulseInMilliSec(), operator.getEndOfPulseInMilliSec() ));
        }
    }
}