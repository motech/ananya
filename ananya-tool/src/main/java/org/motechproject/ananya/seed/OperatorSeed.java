package org.motechproject.ananya.seed;

import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.repository.AllOperators;
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

    public final static HashMap<String, Integer> operator_usage = new HashMap<String, Integer>();

    public final static HashMap<String, Integer> pulse_to_second = new HashMap<String, Integer>();

    public static final int DEFAULT_PULSE_TO_MILLI_SEC = 60000;

    {
        operator_usage.put("airtel", convertMinutesToMilliSeconds(39));
        operator_usage.put("tata", convertMinutesToMilliSeconds(48));
        operator_usage.put("vodafone", convertMinutesToMilliSeconds(39));
        operator_usage.put("idea", convertMinutesToMilliSeconds(38));
        operator_usage.put("reliance", convertMinutesToMilliSeconds(34));
        operator_usage.put("bsnl", convertMinutesToMilliSeconds(28));
        operator_usage.put("undefined", convertMinutesToMilliSeconds(28));

        pulse_to_second.put("airtel", DEFAULT_PULSE_TO_MILLI_SEC);
        pulse_to_second.put("tata", DEFAULT_PULSE_TO_MILLI_SEC);
        pulse_to_second.put("vodafone", DEFAULT_PULSE_TO_MILLI_SEC);
        pulse_to_second.put("idea", DEFAULT_PULSE_TO_MILLI_SEC);
        pulse_to_second.put("reliance", DEFAULT_PULSE_TO_MILLI_SEC);
        pulse_to_second.put("bsnl", DEFAULT_PULSE_TO_MILLI_SEC);
        pulse_to_second.put("undefined", DEFAULT_PULSE_TO_MILLI_SEC);
        pulse_to_second.put("longcode", DEFAULT_PULSE_TO_MILLI_SEC);
    }

    public static Integer convertMinutesToMilliSeconds(int minutes) {
        return minutes * 60 * 1000;
    }

    @Seed(priority = 0, version = "1.0", comment = "load the usage limit for the operators")
    public void load() throws IOException {
        Iterator<String> operatorNameIterator = operator_usage.keySet().iterator();

        while (operatorNameIterator.hasNext()) {
            String nextName = operatorNameIterator.next();
            Operator operator = new Operator(nextName, operator_usage.get(nextName), 0);
            allOperators.add(operator);
        }
    }

    @Seed(priority = 0, version = "1.2", comment = "load long code")
    public void loadLongCode() throws IOException {
        Operator operator = new Operator("longcode", convertMinutesToMilliSeconds(50), 0);
        allOperators.add(operator);

    }

    @Seed(priority = 0, version = "1.10", comment = "adding pulse to second mapping for operators")
    public void addPulseToSec() throws IOException {
        for (String operatorName : pulse_to_second.keySet()) {
            Operator operator = allOperators.findByName(operatorName);
            operator.setPulseToMilliSec(pulse_to_second.get(operatorName));
            allOperators.update(operator);
        }
    }

}