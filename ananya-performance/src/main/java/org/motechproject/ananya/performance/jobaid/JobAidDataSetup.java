package org.motechproject.ananya.performance.jobaid;

import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.performance.DataSetup;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobAidDataSetup implements DataSetup {

    private FrontLineWorkerService frontLineWorkerService;
    private OperatorService operatorService;

    @Autowired
    public JobAidDataSetup(FrontLineWorkerService frontLineWorkerService, OperatorService operatorService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
    }

    @Override
    public void loadTestData() {
        List<Operator> allOperators = operatorService.getAllOperators();
        int threads = 2000;
        for (int i = 0; i < allOperators.size(); i++)
            for (int j = 0; j < threads; j++) {
                String msisdn = i + "" + j;
                frontLineWorkerService.createNew(msisdn, allOperators.get(i).getName());
                frontLineWorkerService.updateCurrentUsageForUser(msisdn, j%(allOperators.get(i).getAllowedUsagePerMonth()+1));
            }

    }
}
