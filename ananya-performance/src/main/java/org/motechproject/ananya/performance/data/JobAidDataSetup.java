package org.motechproject.ananya.performance.data;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.service.JobAidService;
import org.motechproject.ananya.service.OperatorService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class JobAidDataSetup {

    private final int usersPerOperator = 25000;

    private OperatorService operatorService;
    private JobAidService jobAidService;
    private RegistrationMeasureService registrationMeasureService;
    private RegistrationLogService registrationLogService;

    private AllNodes allNodes;


    @Autowired
    public JobAidDataSetup(OperatorService operatorService, JobAidService jobAidService, AllNodes allNodes, RegistrationMeasureService registrationMeasureService, RegistrationLogService registrationLogService) {
        this.operatorService = operatorService;
        this.jobAidService = jobAidService;
        this.allNodes = allNodes;
        this.registrationMeasureService = registrationMeasureService;
        this.registrationLogService = registrationLogService;
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

//    @PerformanceData(testName = "jobaid", description = "create undefined subscribers")
//    public void loadUndefinedSubscribers() {
//        loadUsers("undefined", 7);
//    }

    @PerformanceData(testName = "jobaid", description = "prepare data for posting")
    public void prepareDataForPosting() throws IOException {
        Node jobAidCourse = allNodes.findByName("JobAidCourse");
        String jobAidTokens = getClass().getResource("/jmeter/js/job_aid_tokens.js").getPath();
        String templateFileName = getClass().getResource("/jmeter/js/job_aid_template.js").getPath();
        BufferedReader templateReader = new BufferedReader(new FileReader(templateFileName));
        BufferedWriter jobAidTokensWriter = new BufferedWriter(new FileWriter(jobAidTokens));
        
        ArrayList<String> contentIds = new ArrayList<String>();
        recursivelyWriteAudioTrackerArrayForJobAid(jobAidCourse, contentIds);

        jobAidTokensWriter.write(String.format("var contentIds = [%s];", StringUtils.join(contentIds, ',')));

        String line = templateReader.readLine();
        while (line != null) {
            jobAidTokensWriter.newLine();
            jobAidTokensWriter.write(line);
            line = templateReader.readLine();
        }
        jobAidTokensWriter.close();
        templateReader.close();
    }

    private void recursivelyWriteAudioTrackerArrayForJobAid(Node node, List<String> contentArray) {
        for(String contenId : node.contentIds()){
            contentArray.add("\"" + contenId + "\"");
        }

        if(node.children().size() == 0)
            return;

        for(Node nextNode : node.children()) {
            recursivelyWriteAudioTrackerArrayForJobAid(nextNode, contentArray);
        }
    }


    private void loadUsers(String operatorName, int prefix) {
        for (int j = 0; j < usersPerOperator; j++) {
            String callerId = "9999" + prefix + "" + j;
            Operator operator = getOperatorFor(operatorName);
            jobAidService.createCallerData(callerId, operator.getName(), "circle");
            registrationLogService.deleteFor(callerId);
            registrationMeasureService.createRegistrationMeasure(callerId);
            jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(callerId, j % (operator.getAllowedUsagePerMonth() + 1));
            System.out.println("loaded callerid=" + callerId + "|thread=" + Thread.currentThread().getId()+"|count="+j);
        }
    }

    private Operator getOperatorFor(String operatorName) {
        for (Operator operator : operatorService.getAllOperators())
            if (operator.getName().equalsIgnoreCase(operatorName))
                return operator;
        return null;
    }

    public static void main(String... args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-performance.xml");
        JobAidDataSetup jobAidDataSetup = (JobAidDataSetup) context.getBean("jobAidDataSetup");
//        jobAidDataSetup.loadAirtelSubscribers();
//        jobAidDataSetup.loadRelianceSubscribers();
//        jobAidDataSetup.loadBsnlSubscribers();
//        jobAidDataSetup.loadIdeaSubscribers();
//        jobAidDataSetup.loadTataSubscribers();
//        jobAidDataSetup.loadVodafoneSubscribers();
        jobAidDataSetup.prepareDataForPosting();

        System.out.println("done");
    }
}
