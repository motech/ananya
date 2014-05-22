package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.ananya.repository.AllSMSReferences;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSReferenceServiceTest {
    @Mock
    private AllSMSReferences allSMSReferences;

    private SMSReferenceService smsReferenceService;

    @Before
    public void setUp() {
        initMocks(this);
        smsReferenceService = new SMSReferenceService(allSMSReferences);
    }

    @Test
    public void shouldAddSMSReferenceNumber() {
        SMSReference smsReference = new SMSReference("1234", "123456");
        smsReferenceService.addSMSReferenceNumber(smsReference);
        verify(allSMSReferences).add(smsReference);
    }

    @Test
    public void shouldUpdateSMSReferenceNumber() {
        SMSReference smsReference = new SMSReference("1234", "123456");
        smsReferenceService.updateSMSReferenceNumber(smsReference);
        verify(allSMSReferences).update(smsReference);
    }


}
