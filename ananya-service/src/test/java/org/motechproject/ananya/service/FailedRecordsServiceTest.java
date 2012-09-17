package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.service.publish.FailedRecordsPublishService;
import org.motechproject.ananya.utils.OMFtpSource;
import org.motechproject.importer.CSVDataImporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FailedRecordsServiceTest {

    @Mock
    private FailedRecordsPublishService failedRecordPublisherService;
    @Mock
    private CertificateCourseService certificateCourseService;
    @Mock
    private Properties ananyaProperties;
    @Mock
    private JobAidService jobAidService;
    @Mock
    private CSVDataImporter csvDataImporter;
    @Mock
    private OMFtpSource OMFtpSource;

    private FailedRecordsService failedRecordsService;

    @Before
    public void setUp() {
        when(ananyaProperties.getProperty("om.ftpservice.hostname")).thenReturn("hostname");
        when(ananyaProperties.getProperty("om.ftpservice.port")).thenReturn("10");
        when(ananyaProperties.getProperty("om.ftpservice.username")).thenReturn("username");
        when(ananyaProperties.getProperty("om.ftpservice.password")).thenReturn("password");

        failedRecordsService = new FailedRecordsService(failedRecordPublisherService, jobAidService, certificateCourseService, csvDataImporter, OMFtpSource);
    }

    @Test
    public void shouldProcessFailedCSVRecords() throws IOException {
        String msisdn = "1234567890";
        String calledNumber = "calledNumber";
        String dataToPost = "";
        String callId = "9886000002-1346784033040";
        String operator = "airtel";
        String circle = "bihar";
        Integer callDuration = 4;
        List<String> promptList = new ArrayList<String>(){{
            add("prompt1");
            add("prompt2");
        }
        };
        String fieldsToPost = "callId:" + callId + ";operator:" + operator + ";circle:" + circle + ";callDuration:" + callDuration + ";promptList:" + promptList;
        final FailedRecordCSVRequest failedrecordCsvRequest1 = new FailedRecordCSVRequest(msisdn, "certificateCourse",
                calledNumber, "callStartTimestamp", dataToPost,
                fieldsToPost,
                "lastUpdatedTimeStamp", "retryTimeStamp", "dataPostResponse");
        final FailedRecordCSVRequest failedrecordCsvRequest2 = new FailedRecordCSVRequest(msisdn, "jobAid",
                calledNumber, "callStartTimestamp", dataToPost,
                fieldsToPost,
                "lastUpdatedTimeStamp", "retryTimeStamp", "dataPostResponse");

        final ArrayList<FailedRecordCSVRequest> failedRecordCSVRequests = new ArrayList<FailedRecordCSVRequest>(){{
            add(failedrecordCsvRequest1);
            add(failedrecordCsvRequest2);
        }};

        failedRecordsService.processFailedCSVRequests(failedRecordCSVRequests);

        ArgumentCaptor<JobAidServiceRequest> jobAidServiceRequestArgumentCaptor = ArgumentCaptor.forClass(JobAidServiceRequest.class);
        verify(jobAidService).handleDisconnect(jobAidServiceRequestArgumentCaptor.capture());
        JobAidServiceRequest jobAidServiceRequest = jobAidServiceRequestArgumentCaptor.getValue();
        assertEquals(msisdn, jobAidServiceRequest.getCallerId());

        ArgumentCaptor<CertificateCourseServiceRequest> certificateCourseServiceRequestArgumentCaptor = ArgumentCaptor.forClass(CertificateCourseServiceRequest.class);
        verify(certificateCourseService).handleDisconnect(certificateCourseServiceRequestArgumentCaptor.capture());
        CertificateCourseServiceRequest certificateCourseServiceRequest = certificateCourseServiceRequestArgumentCaptor.getValue();
        assertEquals(msisdn, certificateCourseServiceRequest.getCallerId());
    }
}