package org.motechproject.ananya.performance.datasetup;

import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.performance.PerformanceData;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.OperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobAidDataSetup{

    private static Logger log = LoggerFactory.getLogger(JobAidDataSetup.class);
    private static final int NUMBER_OF_THREADS = 20;

    private FrontLineWorkerService frontLineWorkerService;
    private OperatorService operatorService;

    @Autowired
    public JobAidDataSetup(FrontLineWorkerService frontLineWorkerService, OperatorService operatorService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
    }

    @PerformanceData(testName = "jobaid", description = "create worker groups of different operators, msisdn incremented by jmeter thread count")
    public void loadData() {
        List<Operator> allOperators = operatorService.getAllOperators();
        for (int i = 0; i < allOperators.size(); i++)
            for (int j = 0; j < NUMBER_OF_THREADS; j++) {
                String msisdn = i + "" + j;
                frontLineWorkerService.createNew(msisdn, allOperators.get(i).getName());
                frontLineWorkerService.updateCurrentUsageForUser(msisdn, j % (allOperators.get(i).getAllowedUsagePerMonth() + 1));
            }
        log.info("Loaded jobaid performance data");
    }
}
