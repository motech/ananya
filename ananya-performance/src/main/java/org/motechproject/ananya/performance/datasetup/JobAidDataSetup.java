package org.motechproject.ananya.performance.datasetup;

import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.performance.PerformanceData;
import org.motechproject.ananya.service.JobAidService;
import org.motechproject.ananya.service.OperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobAidDataSetup {

    private static Logger log = LoggerFactory.getLogger(JobAidDataSetup.class);
    private final int usersPerOperator = 100;

    private OperatorService operatorService;
    private JobAidService jobAidService;

    @Autowired
    public JobAidDataSetup(OperatorService operatorService, JobAidService jobAidService) {
        this.operatorService = operatorService;
        this.jobAidService = jobAidService;
    }

    @PerformanceData(testName = "jobaid", description = "create worker groups of different operators")
    public void loadData() {
        List<Operator> allOperators = operatorService.getAllOperators();
        for (int i = 0; i < allOperators.size(); i++)
            for (int j = 0; j < usersPerOperator; j++) {
                String msisdn = i + "" + j;
                jobAidService.createCallerData(msisdn, allOperators.get(i).getName());
                jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(msisdn, j % (allOperators.get(i).getAllowedUsagePerMonth() + 1));
            }
        log.info("Loaded jobaid performance data");
    }
}
