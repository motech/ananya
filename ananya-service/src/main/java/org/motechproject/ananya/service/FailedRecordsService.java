package org.motechproject.ananya.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.mapper.CertificateCourseServiceRequestMapper;
import org.motechproject.ananya.mapper.JobAidServiceRequestMapper;
import org.motechproject.ananya.service.publish.FailedRecordsPublishService;
import org.motechproject.ananya.utils.OMFtpSource;
import org.motechproject.importer.CSVDataImporter;
import org.motechproject.importer.domain.CSVImportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class FailedRecordsService {

    private FailedRecordsPublishService failedRecordsPublishService;
    private JobAidService jobAidService;
    private CertificateCourseService certificateCourseService;
    private CSVDataImporter csvDataImporter;
    private OMFtpSource OMFtpSource;
    private FrontLineWorkerService frontLineWorkerService;

    public static final String FAILED_RECORDS_PUBLISH_MESSAGE = "org.motechproject.ananya.service.failed.records.publish";
    private static Logger log = LoggerFactory.getLogger(FailedRecordsService.class);

    @Autowired
    public FailedRecordsService(FailedRecordsPublishService failedRecordsPublishService,
                                JobAidService jobAidService,
                                CertificateCourseService certificateCourseService,
                                CSVDataImporter csvDataImporter,
                                OMFtpSource OMFtpSource, FrontLineWorkerService frontLineWorkerService) {
        this.failedRecordsPublishService = failedRecordsPublishService;
        this.jobAidService = jobAidService;
        this.certificateCourseService = certificateCourseService;
        this.csvDataImporter = csvDataImporter;
        this.OMFtpSource = OMFtpSource;
        this.frontLineWorkerService = frontLineWorkerService;
    }

    public void processFailedRecords(DateTime recordDate) {
        DateTime lastFailedRecordsProcessedDate = frontLineWorkerService.getLastFailedRecordsProcessedDate();
        List<File> csvFiles = OMFtpSource.downloadAllCsvFilesBetween(lastFailedRecordsProcessedDate == null ? recordDate.minusDays(1) : lastFailedRecordsProcessedDate, recordDate);

        if (csvFiles.isEmpty()) {
            log.info("File not present for " + recordDate + ". Returning.");
            return;
        }

        CSVImportResponse csvImportResponse = csvDataImporter.importData("FailedRecordCSVRequest", getAbsolutePaths(csvFiles));
        recordDate = csvImportResponse.isImportSuccessful() ? recordDate : getDate(lastFailedRecordsProcessedDate, csvImportResponse.getLastProcessedFileName());

        for (File file : csvFiles) {
            file.delete();
        }

        frontLineWorkerService.updateLastFailedRecordsProcessedDate(recordDate);
    }

    private DateTime getDate(DateTime lastFailedRecordsProcessedDate, String lastProcessedFileName) {
        return StringUtils.isEmpty(lastProcessedFileName) ? lastFailedRecordsProcessedDate :
                DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(lastProcessedFileName.replace("datapostmaxretry.", "").replace(".csv", ""));
    }

    private String[] getAbsolutePaths(List<File> csvFiles) {
        String[] csvFilePaths = new String[csvFiles.size()];
        for (int i = 0; i < csvFilePaths.length; ++i) {
            csvFilePaths[i] = csvFiles.get(i).getAbsolutePath();
        }
        return csvFilePaths;
    }

    public void publishFailedRecordsForProcessing(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        List<List<FailedRecordCSVRequest>> byMsisdnAndRecordTypeList = groupByMsisdnAndRecordType(failedRecordCSVRequests);

        log.info("Publishing failed records for processing: " + byMsisdnAndRecordTypeList);
        for (List<FailedRecordCSVRequest> byMsisdnAndRecordType : byMsisdnAndRecordTypeList) {
            failedRecordsPublishService.publishFailedRecordsMessage(byMsisdnAndRecordType);
        }
    }

    private List<List<FailedRecordCSVRequest>> groupByMsisdnAndRecordType(final List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        List<List<FailedRecordCSVRequest>> result = new ArrayList<>();

        collectForRecord(CallFlowType.CERTIFICATECOURSE.name(), failedRecordCSVRequests, result);
        collectForRecord(CallFlowType.JOBAID.name(), failedRecordCSVRequests, result);

        return result;
    }

    private void collectForRecord(final String recordType, List<FailedRecordCSVRequest> failedRecordCSVRequests, List<List<FailedRecordCSVRequest>> result) {
        List<FailedRecordCSVRequest> typeSpecificRecords = (List<FailedRecordCSVRequest>) CollectionUtils.select(failedRecordCSVRequests, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                FailedRecordCSVRequest failedRecordCSVRequest = (FailedRecordCSVRequest) object;
                return recordType.equalsIgnoreCase(failedRecordCSVRequest.getApplicationName());
            }
        });

        Set<String> msisdns = new LinkedHashSet<>((List<String>) CollectionUtils.collect(typeSpecificRecords, new Transformer() {
            @Override
            public Object transform(Object input) {
                FailedRecordCSVRequest failedRecordCSVRequest = (FailedRecordCSVRequest) input;
                return failedRecordCSVRequest.getMsisdn();
            }
        }));

        for (final String msisdn : msisdns) {
            result.add((List<FailedRecordCSVRequest>) CollectionUtils.select(typeSpecificRecords, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    FailedRecordCSVRequest failedRecordCSVRequest = (FailedRecordCSVRequest) object;
                    return msisdn.equals(failedRecordCSVRequest.getMsisdn());
                }
            }));
        }
    }

    public void processFailedCSVRequests(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        log.info("Processing Failed Record Requests : Count(" + failedRecordCSVRequests.size() + ")");

        String applicationName = failedRecordCSVRequests.get(0).getApplicationName();

        if (applicationName.equalsIgnoreCase(CallFlowType.CERTIFICATECOURSE.name())) {
            for (FailedRecordCSVRequest failedRecordCSVRequest : failedRecordCSVRequests) {
                CertificateCourseServiceRequest certificateCourseServiceRequest = CertificateCourseServiceRequestMapper.map(failedRecordCSVRequest);
                try {
                    certificateCourseService.handleDisconnect(certificateCourseServiceRequest);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Error while processing failed certificate course service request{%s}: ", certificateCourseServiceRequest.toString()) + e.getMessage(), e);
                }
            }
        } else if (applicationName.equalsIgnoreCase(CallFlowType.JOBAID.name())) {
            for (FailedRecordCSVRequest failedRecordCSVRequest : failedRecordCSVRequests) {
                JobAidServiceRequest jobAidServiceRequest = JobAidServiceRequestMapper.map(failedRecordCSVRequest);
                try {
                    jobAidService.handleDisconnect(jobAidServiceRequest);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Error while processing failed job aid service request{%s}: ", jobAidServiceRequest.toString()) + e.getMessage(), e);
                }
            }
        }
    }
}