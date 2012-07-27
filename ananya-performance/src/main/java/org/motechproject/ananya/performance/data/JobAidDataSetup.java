package org.motechproject.ananya.performance.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.Operator;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.performance.framework.PerformanceData;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.motechproject.ananya.service.OperatorService;
import org.motechproject.ananya.service.RegistrationLogService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class JobAidDataSetup {

    private final int usersPerOperator = 25000;
    private final String msisdnPrefix = "99";
    private final String noOfOperators = "6";

    private OperatorService operatorService;
    private RegistrationMeasureService registrationMeasureService;
    private AllNodes allNodes;
    private FrontLineWorkerService frontLineWorkerService;
    private RegistrationLogService registrationLogService;

    @Autowired
    public JobAidDataSetup(OperatorService operatorService, AllNodes allNodes,
                           RegistrationMeasureService registrationMeasureService,
                           FrontLineWorkerService frontLineWorkerService, RegistrationLogService registrationLogService) {
        this.operatorService = operatorService;
        this.allNodes = allNodes;
        this.registrationMeasureService = registrationMeasureService;
        this.frontLineWorkerService = frontLineWorkerService;
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

    @PerformanceData(testName = "jobaid-content", description = "Jobaid call-flow javascript generation")
    public void prepareDataForPosting() throws Exception {
        createJobAidTokensFile();
        updateJMeterFile();
    }

    private void loadUsers(String operatorName, int prefix) {
        for (int j = 0; j < usersPerOperator; j++) {
            String callerId = msisdnPrefix + prefix + "" + j;
            String callId = callerId + "-" + DateTime.now().getMillisOfDay();
            String circle = "bihar";

            frontLineWorkerService.createOrUpdateForCall(callerId, operatorName, circle);
            registrationLogService.add(new RegistrationLog(callId, callerId, operatorName, circle));
            registrationMeasureService.createFor(callId);

            System.out.println("loaded [callerid=" + callerId + "|thread=" + Thread.currentThread().getId() + "|count=" + j + "|operator=" + operatorName + "]");
        }
    }

    private void createJobAidTokensFile() throws IOException {
        Node jobAidCourse = allNodes.findByName("JobAidCourse");
        BufferedReader templateReader = new BufferedReader(new FileReader("jmeter/js/job_aid_template.js"));
        BufferedWriter jobAidTokensWriter = new BufferedWriter(new FileWriter("jmeter/js/job_aid_tokens.js"));

        List<String> contentIds = new ArrayList<String>();
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

    private void updateJMeterFile() throws Exception {
        String jmx = FileUtils.readFileToString(new File("jmeter/jobaid_course_flows.jmx"));
        String tokens = FileUtils.readFileToString(new File("jmeter/js/job_aid_tokens.js"));

        String finalJmx = jmx.replace("${no_of_operators}", noOfOperators)
                .replace("${no_of_subscribers_per_operator}", String.valueOf(usersPerOperator))
                .replace("${msisdn_prefix}", msisdnPrefix)
                .replace("${job_aid_tokens}", StringEscapeUtils.escapeXml(tokens));

        FileUtils.writeStringToFile(new File("jmeter/jobaid_course_flows.jmx"), finalJmx);
    }

    private void recursivelyWriteAudioTrackerArrayForJobAid(Node node, List<String> contentArray) {
        for (String contentId : node.contentIds())
            contentArray.add("\"" + contentId + "\"");
        if (node.children().size() == 0) return;
        for (Node nextNode : node.children())
            recursivelyWriteAudioTrackerArrayForJobAid(nextNode, contentArray);
    }

    private Operator getOperatorFor(String operatorName) {
        for (Operator operator : operatorService.getAllOperators())
            if (operator.getName().equalsIgnoreCase(operatorName))
                return operator;
        return null;
    }

}
