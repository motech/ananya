package org.motechproject.ananya.performance.data;

import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.service.JobAidService;
import org.motechproject.ananya.service.OperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobAidDataSetup {

    private static Logger log = LoggerFactory.getLogger(JobAidDataSetup.class);
    private final int usersPerOperator = 10;

    private OperatorService operatorService;
    private JobAidService jobAidService;


    @Autowired
    public JobAidDataSetup(OperatorService operatorService, JobAidService jobAidService) {
        this.operatorService = operatorService;
        this.jobAidService = jobAidService;
    }

    @PerformanceData(testName = "jobaid", description = "create airtel subscribers")
    public void loadAirtelSubscribers() {
        loadUsers("airtel", 0);
    }

    @PerformanceData(testName = "jobaid", description = "create reliance subscribers")
    public void loadRelianceSubscribers() {
        loadUsers("reliance", 1);
    }

    @PerformanceData(testName = "jobaid", description = "create tata subscribers")
    public void loadTataSubscribers() {
        loadUsers("tata", 2);
    }

    @PerformanceData(testName = "jobaid", description = "create idea subscribers")
    public void loadIdeaSubscribers() {
        loadUsers("idea", 3);
    }

    @PerformanceData(testName = "jobaid", description = "create bsnl subscribers")
    public void loadBsnlSubscribers() {
        loadUsers("bsnl", 4);
    }

    @PerformanceData(testName = "jobaid", description = "create vodafone subscribers")
    public void loadVodafoneSubscribers() {
        loadUsers("vodafone", 5);
    }

    @PerformanceData(testName = "jobaid", description = "create undefined subscribers")
    public void loadUndefinedSubscribers() {
        loadUsers("undefined", 6);
    }


    private void loadUsers(String operatorName, int prefix) {
        for (int j = 0; j < usersPerOperator; j++) {
            String callerId = prefix + "" + j;
            Operator airtel = getOperatorFor(operatorName);
            jobAidService.createCallerData(callerId, airtel.getName());
            jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(callerId, j % (airtel.getAllowedUsagePerMonth() + 1));
        }
    }

    private Operator getOperatorFor(String operatorName) {
        for (Operator operator : operatorService.getAllOperators())
            if (operator.getName().equalsIgnoreCase(operatorName))
                return operator;
        return null;
    }
}
