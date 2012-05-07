package org.motechproject.ananya.performance.data;

import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.service.JobAidService;
import org.motechproject.ananya.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class JobAidDataSetup {

    private final int usersPerOperator = 2500;

    private OperatorService operatorService;
    private JobAidService jobAidService;


    @Autowired
    public JobAidDataSetup(OperatorService operatorService, JobAidService jobAidService) {
        this.operatorService = operatorService;
        this.jobAidService = jobAidService;
    }

    @PerformanceData(testName = "jobaid", description = "create airtel subscribers")
    public void loadAirtelSubscribers() {
        loadUsers("airtel", 1);
    }

    @PerformanceData(testName = "jobaid", description = "create reliance subscribers")
    public void loadRelianceSubscribers() {
        loadUsers("reliance", 2);
    }

    @PerformanceData(testName = "jobaid", description = "create tata subscribers")
    public void loadTataSubscribers() {
        loadUsers("tata", 3);
    }

    @PerformanceData(testName = "jobaid", description = "create idea subscribers")
    public void loadIdeaSubscribers() {
        loadUsers("idea", 4);
    }

    @PerformanceData(testName = "jobaid", description = "create bsnl subscribers")
    public void loadBsnlSubscribers() {
        loadUsers("bsnl", 5);
    }

    @PerformanceData(testName = "jobaid", description = "create vodafone subscribers")
    public void loadVodafoneSubscribers() {
        loadUsers("vodafone", 6);
    }

    @PerformanceData(testName = "jobaid", description = "create undefined subscribers")
    public void loadUndefinedSubscribers() {
        loadUsers("undefined", 7);
    }


    private void loadUsers(String operatorName, int prefix) {
        for (int j = 0; j < usersPerOperator; j++) {
            String callerId = "9999" + prefix + "" + j;
            Operator airtel = getOperatorFor(operatorName);
            jobAidService.createCallerData(callerId, airtel.getName());
            jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(callerId, j % (airtel.getAllowedUsagePerMonth() + 1));
            System.out.println("loaded callerid=" + callerId + "|thread=" + Thread.currentThread().getId()+"|count="+j);
        }
    }

    private Operator getOperatorFor(String operatorName) {
        for (Operator operator : operatorService.getAllOperators())
            if (operator.getName().equalsIgnoreCase(operatorName))
                return operator;
        return null;
    }

    public static void main(String... args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        JobAidDataSetup jobAidDataSetup = (JobAidDataSetup) context.getBean("jobAidDataSetup");
        jobAidDataSetup.loadAirtelSubscribers();
        jobAidDataSetup.loadRelianceSubscribers();
        jobAidDataSetup.loadBsnlSubscribers();
        jobAidDataSetup.loadIdeaSubscribers();
        jobAidDataSetup.loadTataSubscribers();
        jobAidDataSetup.loadVodafoneSubscribers();

        System.out.println("done");
    }
}
