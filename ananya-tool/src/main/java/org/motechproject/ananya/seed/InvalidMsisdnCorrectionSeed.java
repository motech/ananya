package org.motechproject.ananya.seed;

import org.motechproject.ananya.repository.*;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvalidMsisdnCorrectionSeed {

    private final AllCallLogs allCallLogs;
    private final AllRegistrationLogs allRegistrationLogs;
    private final AllFrontLineWorkers allFrontLineWorkers;
    private final AllFrontLineWorkerKeys allFrontLineWorkerKeys;
    private final AllSMSLogs allSMSLogs;
    private final AllAudioTrackerLogs allAudioTrackerLogs;
    private final AllCertificateCourseLogs allCertificateCourseLogs;

    @Autowired
    public InvalidMsisdnCorrectionSeed(AllCallLogs allCallLogs,
                                       AllRegistrationLogs allRegistrationLogs,
                                       AllFrontLineWorkers allFrontLineWorkers,
                                       AllFrontLineWorkerKeys allFrontLineWorkerKeys,
                                       AllSMSLogs allSMSLogs,
                                       AllAudioTrackerLogs allAudioTrackerLogs,
                                       AllCertificateCourseLogs allCertificateCourseLogs) {
        this.allCallLogs = allCallLogs;
        this.allRegistrationLogs = allRegistrationLogs;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allFrontLineWorkerKeys = allFrontLineWorkerKeys;
        this.allSMSLogs = allSMSLogs;
        this.allAudioTrackerLogs = allAudioTrackerLogs;
        this.allCertificateCourseLogs = allCertificateCourseLogs;
    }


    @Seed(priority = 0, version = "1.9", comment = "Remove invalid msisdn records from couch")
    public void removeInvalidMsisdnRecordsFromCouch() {
        allFrontLineWorkerKeys.deleteFLWsWithInvalidMsisdn();
        allFrontLineWorkers.deleteFLWsWithInvalidMsisdn();
        allCallLogs.deleteCallLogsForInvalidMsisdns();
        allAudioTrackerLogs.deleteAudioTrackerLogsForInvalidMsisdns();
        allCertificateCourseLogs.deleteCCLogsForInvalidMsisdns();
        allRegistrationLogs.deleteRegistrationLogsForInvalidMsisdns();
        allSMSLogs.deleteSMSLogsForInvalidMsisdns();
    }
}
