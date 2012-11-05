package org.motechproject.ananya.web;


import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.FailedRecordsProcessingState;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.TimedRunner;
import org.motechproject.ananya.repository.AllFailedRecordsProcessingStates;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class FailedRecordsControllerIT extends SpringIntegrationTest {
    private FakeFtpServer fakeFtpServer;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private Properties ananyaProperties;

    @Autowired
    private AllFailedRecordsProcessingStates allFailedRecordsProcessingStates;

    private String certificateCourseCallerId = "919886000002";
    private String jobaidCallerId = "919886000003";

    @Before
    public void setUp() throws IOException {
        clearFLWData();

        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(2116);

        UnixFakeFileSystem fileSystem = new UnixFakeFileSystem();
        FileEntry fileEntry = new FileEntry("/datapostmaxretry.21-09-2012.csv");
        fileEntry.setContents("\"MSISDN\",\"APPLICATION_NAME\",\"CALLED_NUMBER\",\"CALL_START_TS\",\"DATA_TO_POST\",\"FIELDS_TO_POST\",\"LAST_UPDATED_TS\",\"POST_LAST_RETRY_TS\",\"DATA_POST_RESPONSE\"\n" +
                "\"919886000002\",\"certificatecourse\",\"5771102\",\"2012-09-04 18:33:45.0\",\"[{\"\"token\"\":1,\"\"data\"\":{\"\"time\"\":1346784033040,\"\"callEvent\"\":\"\"CALL_START\"\"},\"\"type\"\":\"\"callDuration\"\"},{\"\"token\"\":19,\"\"data\"\":{\"\"time\"\":1346784066005,\"\"callEvent\"\":\"\"DISCONNECT\"\"},\"\"type\"\":\"\"callDuration\"\"}]\",\"callId:9886000002-1346784033040;operator:airtel\",\"2012-09-04 18:49:18.0\",\"2012-09-04 18:49:49.0\",\"Previous data post failed\"\n" +
                "\"919886000003\",\"jobaid\",\"5771102\",\"2012-09-04 18:42:45.0\",\"[{\"\"token\"\":0, \"\"type\"\":callDuration, \"\"data\"\":{\"\"callEvent\"\" : \"\"CALL_START\"\", \"\"time\"\" : \"\"1347871315805\"\"}},{\"\"token\"\":3, \"\"type\"\":callDuration, \"\"data\"\":{\"\"callEvent\"\" : \"\"DISCONNECT\"\", \"\"time\"\" : \"\"1348125687805\"\"}}]\",\"callId:9886000002-1346784033040;operator:airtel;callDuration:30;promptList:['prompt1', 'prompt2']\",\"2012-09-04 18:49:18.0\",\"2012-09-04 18:49:49.0\",\"Previous data post failed\"");
        fileSystem.add(fileEntry);
        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.addUserAccount(new UserAccount("motech", "password", "/"));
        fakeFtpServer.start();

        allFailedRecordsProcessingStates.removeAll();
    }

    @After
    public void tearDown() {
        fakeFtpServer.stop();
        clearFLWData();
    }

    @Test
    @Ignore("need more context to fix this")
    public void shouldTriggerDisconnectForFailedRecords() throws IOException, InterruptedException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        httpClient.execute(new HttpGet("http://localhost:9979/ananya/failedrecords/process?recordDate=21-09-2012"));

        boolean successfulJobAidDisconnect = new TimedRunner(20, 500) {
            @Override
            protected boolean run() {
                FrontLineWorker byMsisdn = allFrontLineWorkers.findByMsisdn(jobaidCallerId);
                return byMsisdn != null;
            }
        }.executeWithTimeout();
        assertTrue(successfulJobAidDisconnect);

        boolean successfulCertificateCourseDisconnect = new TimedRunner(20, 500) {
            @Override
            protected boolean run() {
                FrontLineWorker byMsisdn = allFrontLineWorkers.findByMsisdn(certificateCourseCallerId);
                return byMsisdn != null;
            }
        }.executeWithTimeout();
        assertTrue(successfulCertificateCourseDisconnect);

        boolean successfullyAddedLastProcessedDate = new TimedRunner(20, 500) {
            @Override
            protected boolean run() {
                List<FailedRecordsProcessingState> failedRecordsProcessingStates = allFailedRecordsProcessingStates.getAll();
                return !failedRecordsProcessingStates.isEmpty();
            }
        }.executeWithTimeout();
        assertTrue(successfullyAddedLastProcessedDate);
    }

    private void clearFLWData() {
        reportDb.clearFLWDimensionAndMeasures(certificateCourseCallerId);
        couchDb.clearFLWData(certificateCourseCallerId);
        couchDb.clearAllLogs();

        reportDb.clearFLWDimensionAndMeasures(jobaidCallerId);
        couchDb.clearFLWData(jobaidCallerId);
        couchDb.clearAllLogs();

        allFailedRecordsProcessingStates.removeAll();
    }
}
