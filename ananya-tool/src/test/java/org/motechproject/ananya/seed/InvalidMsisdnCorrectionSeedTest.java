package org.motechproject.ananya.seed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.repository.*;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InvalidMsisdnCorrectionSeedTest {

    @Mock
    private AllCallLogs allCallLogs;
    @Mock
    private AllRegistrationLogs allRegistrationLogs;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AllFrontLineWorkerKeys allFrontLineWorkerKeys;
    @Mock
    private AllCertificateCourseLogs allCertificateCourseLogs;
    @Mock
    private AllAudioTrackerLogs allAudioTrackerLogs;
    @Mock
    private AllSMSLogs allSMSLogs;

    @Test
    public void shouldRemoveAllLogsAndFLWRecordsWithInvalidMsisdn() {
        InvalidMsisdnCorrectionSeed invalidMsisdnCorrectionSeed = new InvalidMsisdnCorrectionSeed(allCallLogs, allRegistrationLogs, allFrontLineWorkers, allFrontLineWorkerKeys, allSMSLogs, allAudioTrackerLogs, allCertificateCourseLogs);

        invalidMsisdnCorrectionSeed.removeInvalidMsisdnRecordsFromCouch();

        verify(allFrontLineWorkerKeys).deleteFLWsWithInvalidMsisdn();
        verify(allFrontLineWorkers).deleteFLWsWithInvalidMsisdn();
        verify(allCallLogs).deleteCallLogsForInvalidMsisdns();
        verify(allCertificateCourseLogs).deleteCCLogsForInvalidMsisdns();
        verify(allRegistrationLogs).deleteRegistrationLogsForInvalidMsisdns();
        verify(allSMSLogs).deleteSMSLogsForInvalidMsisdns();
        verify(allAudioTrackerLogs).deleteAudioTrackerLogsForInvalidMsisdns();
    }
}
