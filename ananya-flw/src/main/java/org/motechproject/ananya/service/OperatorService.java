package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.repository.AllOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Integer usageByPulseInMilliSec(String operatorName, Integer durationInMilliSec) {
        Operator operator = allOperators.findByName(operatorName);
        Integer pulseToMilliSecForOperator = operator.getPulseToMilliSec();
        Integer startOfPulseInMilliSec = operator.getStartOfPulseInMilliSec();

        double durationConsideringStartOfPulse = findDurationConsideringStartOfPulse(durationInMilliSec, startOfPulseInMilliSec);
        return calculatePulse(pulseToMilliSecForOperator, durationConsideringStartOfPulse) * pulseToMilliSecForOperator;
    }

    public Integer usageInPulse(String operatorName, Integer durationInMilliSec) {
        Operator operator = allOperators.findByName(operatorName);
        Integer pulseToMilliSecForOperator = operator.getPulseToMilliSec();
        Integer startOfPulseInMilliSec = operator.getStartOfPulseInMilliSec();

        double durationConsideringStartOfPulse = findDurationConsideringStartOfPulse(durationInMilliSec, startOfPulseInMilliSec);
        return calculatePulse(pulseToMilliSecForOperator, durationConsideringStartOfPulse);
    }

    private double findDurationConsideringStartOfPulse(Integer durationInMilliSec, Integer startOfPulseInMilliSec) {
        double durationConsideringStartOfPulse = durationInMilliSec - startOfPulseInMilliSec;
        durationConsideringStartOfPulse = durationConsideringStartOfPulse < 0 ? 0 : durationConsideringStartOfPulse;
        return durationConsideringStartOfPulse;
    }

    private int calculatePulse(Integer pulseToMilliSecForOperator, double durationConsideringStartOfPulse) {
        return (int) Math.ceil(durationConsideringStartOfPulse / pulseToMilliSecForOperator);
    }
}
