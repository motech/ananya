package org.motechproject.ananya.service;

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
        return allOperators.findByName(operator).getAllowedUsagePerMonth();
    }

    //only used in perf test
    public List<Operator> getAllOperators() {
        return allOperators.getAll();
    }
}
