package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.mapper.CertificateCourseServiceRequestMapper;
import org.motechproject.ananya.service.publish.FailedRecordsPublishService;
import org.motechproject.ananya.utils.OMFtpSource;
import org.motechproject.importer.CSVDataImporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
    public void shouldProcessFailedCSVRecords_forCC() {
        String msisdn = "1234567890";
        String calledNumber = "calledNumber";
        String dataToPost = "";
        String callId = "9886000002-1346784033040";
        String operator = "airtel";
        String circle = "bihar";
        Integer callDuration = 4;
        String fieldsToPost = "callId:" + callId + ";operator:" + operator + ";circle:" + circle + ";callDuration:" + callDuration;
        final FailedRecordCSVRequest failedrecordCsvRequest1 = new FailedRecordCSVRequest(msisdn, "certificateCourse",
                calledNumber, "callStartTimestamp", dataToPost,
                fieldsToPost,
                "lastUpdatedTimeStamp", "retryTimeStamp", "dataPostResponse");
        final FailedRecordCSVRequest failedrecordCsvRequest2 = new FailedRecordCSVRequest(msisdn, "certificateCourse",
                calledNumber, "callStartTimestamp", dataToPost,
                fieldsToPost,
                "lastUpdatedTimeStamp", "retryTimeStamp", "dataPostResponse");

        final ArrayList<FailedRecordCSVRequest> failedRecordCSVRequests = new ArrayList<FailedRecordCSVRequest>() {{
            add(failedrecordCsvRequest1);
            add(failedrecordCsvRequest2);
        }};

        failedRecordsService.processFailedCSVRequests(failedRecordCSVRequests);

        ArgumentCaptor<CertificateCourseServiceRequest> certificateCourseServiceRequestArgumentCaptor = ArgumentCaptor.forClass(CertificateCourseServiceRequest.class);
        verify(certificateCourseService, times(2)).handleDisconnect(certificateCourseServiceRequestArgumentCaptor.capture());
        List<CertificateCourseServiceRequest> certificateCourseServiceRequest = certificateCourseServiceRequestArgumentCaptor.getAllValues();
        assertEquals(msisdn, certificateCourseServiceRequest.get(0).getCallerId());
        assertEquals(msisdn, certificateCourseServiceRequest.get(1).getCallerId());
    }

    @Test
    public void shouldProcessFailedCSVRecords_forJA() {
        String msisdn = "1234567890";
        String calledNumber = "calledNumber";
        String dataToPost = "";
        String callId = "9886000002-1346784033040";
        String operator = "airtel";
        String circle = "bihar";
        Integer callDuration = 4;
        List<String> promptList = new ArrayList<String>() {
            {
                add("prompt1");
                add("prompt2");
            }
        };
        String fieldsToPost = "callId:" + callId + ";operator:" + operator + ";circle:" + circle + ";callDuration:" + callDuration + ";promptList:" + promptList;
        final FailedRecordCSVRequest failedrecordCsvRequest1 = new FailedRecordCSVRequest(msisdn, "jobAid",
                calledNumber, "callStartTimestamp", dataToPost,
                fieldsToPost,
                "lastUpdatedTimeStamp", "retryTimeStamp", "dataPostResponse");
        final FailedRecordCSVRequest failedrecordCsvRequest2 = new FailedRecordCSVRequest(msisdn, "jobAid",
                calledNumber, "callStartTimestamp", dataToPost,
                fieldsToPost,
                "lastUpdatedTimeStamp", "retryTimeStamp", "dataPostResponse");

        final ArrayList<FailedRecordCSVRequest> failedRecordCSVRequests = new ArrayList<FailedRecordCSVRequest>() {{
            add(failedrecordCsvRequest1);
            add(failedrecordCsvRequest2);
        }};

        failedRecordsService.processFailedCSVRequests(failedRecordCSVRequests);

        ArgumentCaptor<JobAidServiceRequest> jobAidServiceRequestArgumentCaptor = ArgumentCaptor.forClass(JobAidServiceRequest.class);
        verify(jobAidService, times(2)).handleDisconnect(jobAidServiceRequestArgumentCaptor.capture());
        List<JobAidServiceRequest> jobAidServiceRequest = jobAidServiceRequestArgumentCaptor.getAllValues();
        assertEquals(msisdn, jobAidServiceRequest.get(0).getCallerId());
        assertEquals(msisdn, jobAidServiceRequest.get(1).getCallerId());
    }

    @Test
    public void shouldProcessFailedCSVRecords_throwRTEOnError() {
        final String callerId = "1234567890";
        String expectedErrorMessage = "CertificateCourseHandleDisconnectError";

        final FailedRecordCSVRequest failedRecordCSVRequest = new FailedRecordCSVRequest(callerId, "certificateCourse", "", "", "", "key1:val1;key2:val2", "", "", "");
        List<FailedRecordCSVRequest> failedRecordsCSVRequests = new ArrayList() {{
            add(failedRecordCSVRequest);
        }};

        doThrow(new IllegalStateException(expectedErrorMessage)).when(certificateCourseService).handleDisconnect(any(CertificateCourseServiceRequest.class));

        try {
            failedRecordsService.processFailedCSVRequests(failedRecordsCSVRequests);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertTrue(e.getMessage().contains("Error while processing failed certificate course service request"));
            return;
        }

        assertFalse(true);
    }

    @Test
    public void shouldGroupFailedRecordsByMsisdnAndTypeAndPublishHandlingDisconnect() {
        final String msisdn1 = "1234567890";
        final String msisdn2 = "1234567891";
        final String typeCC = "certificateCourse";
        final String typeJA = "jobAid";
        List<FailedRecordCSVRequest> failedRecordsCSVRequests = new ArrayList() {{
            add(new FailedRecordCSVRequest(msisdn1, typeCC, "", "", "", "", "", "", ""));
            add(new FailedRecordCSVRequest(msisdn2, typeCC, "", "", "", "", "", "", ""));
            add(new FailedRecordCSVRequest(msisdn1, typeCC, "", "", "", "", "", "", ""));
            add(new FailedRecordCSVRequest(msisdn2, typeCC, "", "", "", "", "", "", ""));
            add(new FailedRecordCSVRequest(msisdn2, typeJA, "", "", "", "", "", "", ""));
        }};

        failedRecordsService.publishFailedRecordsForProcessing(failedRecordsCSVRequests);

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(failedRecordPublisherService, times(3)).publishFailedRecordsMessage(listArgumentCaptor.capture());

        List<List> allValues = listArgumentCaptor.getAllValues();

        List<FailedRecordCSVRequest> list1 = allValues.get(0);
        assertEquals(2, list1.size());
        assertEquals(msisdn1, list1.get(0).getMsisdn());
        assertEquals(typeCC, list1.get(0).getApplicationName());
        assertEquals(msisdn1, list1.get(1).getMsisdn());
        assertEquals(typeCC, list1.get(1).getApplicationName());

        List<FailedRecordCSVRequest> list2 = allValues.get(1);
        assertEquals(2, list2.size());
        assertEquals(msisdn2, list2.get(0).getMsisdn());
        assertEquals(typeCC, list2.get(0).getApplicationName());
        assertEquals(msisdn2, list2.get(1).getMsisdn());
        assertEquals(typeCC, list2.get(1).getApplicationName());

        List<FailedRecordCSVRequest> list3 = allValues.get(2);
        assertEquals(1, list3.size());
        assertEquals(msisdn2, list3.get(0).getMsisdn());
        assertEquals(typeJA, list3.get(0).getApplicationName());
    }
}