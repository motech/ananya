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

    {
        operator_usage.put("airtel", convertMinutesToMilliSeconds(39));
        operator_usage.put("tata", convertMinutesToMilliSeconds(48));
        operator_usage.put("vodafone", convertMinutesToMilliSeconds(39));
        operator_usage.put("idea", convertMinutesToMilliSeconds(38));
        operator_usage.put("reliance", convertMinutesToMilliSeconds(34));
        operator_usage.put("bsnl", convertMinutesToMilliSeconds(28));
        operator_usage.put("undefined", convertMinutesToMilliSeconds(28));
    }

    public static Integer convertMinutesToMilliSeconds(int minutes) {
        return minutes * 60 * 1000;
    }

    @Seed(priority = 0)
    public void load() throws IOException {
        Iterator<String> operatorNameIterator = operator_usage.keySet().iterator();

        while (operatorNameIterator.hasNext()) {
            String nextName = operatorNameIterator.next();
            Operator operator = new Operator(nextName, operator_usage.get(nextName));
            allOperators.add(operator);
        }
    }
}
