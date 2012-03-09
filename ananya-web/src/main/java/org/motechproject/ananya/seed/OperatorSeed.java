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

    public final static HashMap<String,Integer> operator_usage = new HashMap<String, Integer>();

    {
        operator_usage.put("airtel",39);
        operator_usage.put("tata",48);
        operator_usage.put("vodafone",39);
        operator_usage.put("idea",38);
        operator_usage.put("reliance",34);
        operator_usage.put("bsnl",28);
    }

    @Seed(priority = 0)
    public void load() throws IOException {
        Iterator<String> operatorNameIterator = operator_usage.keySet().iterator();

        while(operatorNameIterator.hasNext()) {
            String nextName = operatorNameIterator.next();
            Operator operator = new Operator(nextName, operator_usage.get(nextName));
            allOperators.add(operator);
        }
    }
}
