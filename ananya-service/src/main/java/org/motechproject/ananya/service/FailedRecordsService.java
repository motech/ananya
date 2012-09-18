package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.mapper.CertificateCourseServiceRequestMapper;
import org.motechproject.ananya.mapper.JobAidServiceRequestMapper;
import org.motechproject.ananya.service.publish.FailedRecordsPublishService;
import org.motechproject.ananya.utils.OMFtpSource;
import org.motechproject.importer.CSVDataImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FailedRecordsService {

    private FailedRecordsPublishService failedRecordsPublishService;
    private JobAidService jobAidService;
    private CertificateCourseService certificateCourseService;
    private CSVDataImporter csvDataImporter;
    private OMFtpSource OMFtpSource;

    public static final String FAILED_RECORDS_PUBLISH_MESSAGE = "org.motechproject.ananya.service.failed.records.publish";

    private static Logger log = LoggerFactory.getLogger(FailedRecordsService.class);

    @Autowired
    public FailedRecordsService(FailedRecordsPublishService failedRecordsPublishService,
                                JobAidService jobAidService,
                                CertificateCourseService certificateCourseService,
                                CSVDataImporter csvDataImporter,
                                OMFtpSource OMFtpSource) {
        this.failedRecordsPublishService = failedRecordsPublishService;
        this.jobAidService = jobAidService;
        this.certificateCourseService = certificateCourseService;
        this.csvDataImporter = csvDataImporter;
        this.OMFtpSource = OMFtpSource;
    }

    public void processFailedRecords(DateTime recordDate) throws IOException {
        File csvFile = OMFtpSource.downloadCsvFile(getRemoteFileName(recordDate));

        if (csvFile == null) {
            log.info("File not present for " + recordDate + ". Returning.");
            return;
        }

        csvDataImporter.importData("FailedRecordCSVRequest", csvFile.getAbsolutePath());

        csvFile.delete();
    }

    public void publishFailedRecordsForProcessing(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        failedRecordsPublishService.publishFailedRecordsMessage(failedRecordCSVRequests);
    }

    public void processFailedCSVRequests(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        log.info("Processing Failed Record Requests : Count(" + failedRecordCSVRequests.size() + ")");

        List<JobAidServiceRequest> jobAidServiceRequests = new ArrayList<>();
        List<CertificateCourseServiceRequest> certificateCourseServiceRequests = new ArrayList<>();

        for (FailedRecordCSVRequest failedRecordCSVRequest : failedRecordCSVRequests) {
            if (failedRecordCSVRequest.getApplicationName().equalsIgnoreCase(CallFlowType.CERTIFICATECOURSE.toString())) {
                certificateCourseServiceRequests.add(CertificateCourseServiceRequestMapper.map(failedRecordCSVRequest));
            } else if (failedRecordCSVRequest.getApplicationName().equalsIgnoreCase(CallFlowType.JOBAID.toString())) {
                jobAidServiceRequests.add(JobAidServiceRequestMapper.map(failedRecordCSVRequest));
            }
        }

        for (JobAidServiceRequest jobAidServiceRequest : jobAidServiceRequests) {
            try {
                jobAidService.handleDisconnect(jobAidServiceRequest);
            } catch (Exception e) {
                log.error("Error while processing failed job aid service request : " +
                        jobAidServiceRequest.toString(), e);
            }
        }

        for (CertificateCourseServiceRequest certificateCourseServiceRequest : certificateCourseServiceRequests) {
            try {
                certificateCourseService.handleDisconnect(certificateCourseServiceRequest);
            } catch (Exception e) {
                log.error("Error while processing failed certificate course service request : " +
                        certificateCourseServiceRequest.toString(), e);
            }
        }
    }

    private String getRemoteFileName(DateTime recordDate) {
        return String.format("datapostmaxretry.%s.csv", recordDate.toString("dd-MM-yyyy"));
    }
}