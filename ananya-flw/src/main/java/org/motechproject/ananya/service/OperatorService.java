package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.repository.AllOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatorService {

    private AllOperators allOperators;

    @Autowired
    public OperatorService(AllOperators allOperators) {
        this.allOperators = allOperators;
    }

    public Integer findMaximumUsageFor(String operator) {
        return StringUtils.isNotBlank(operator) ?
                allOperators.findByName(operator).getAllowedUsagePerMonth() : 0;
    }

    public List<Operator> getAllOperators() {
        return allOperators.getAll();
    }

    public Integer usageByPulseInMilliSec(String operatorName, Integer durationInMilliSec) {
        Operator operator = allOperators.findByName(operatorName);
        Integer pulseToMilliSec = operator.getPulseToMilliSec();
        Integer usageInPulse = convertToPulse(durationInMilliSec, pulseToMilliSec);

        return usageInPulse * pulseToMilliSec;
    }

    public Integer usageInPulse(String operatorName, Integer durationInMilliSec) {
        Operator operator = allOperators.findByName(operatorName);
        Integer pulseToMilliSec = operator.getPulseToMilliSec();
        Integer usageInPulse = convertToPulse(durationInMilliSec, pulseToMilliSec);

        return usageInPulse;
    }

    private int convertToPulse(Integer durationInMilliSec, double pulseToMilliSec) {
        return (int) Math.ceil(durationInMilliSec / pulseToMilliSec);
    }
}
