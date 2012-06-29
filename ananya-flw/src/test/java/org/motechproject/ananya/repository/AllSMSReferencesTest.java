package org.motechproject.ananya.repository;

import org.junit.Test;
import org.motechproject.ananya.domain.SMSReference;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AllSMSReferencesTest extends SpringBaseIT {

    @Autowired
    private AllSMSReferences allSMSReferences;

    @Test
    public void shouldFindByMsisdn(){
        String msisdn = "123";
        SMSReference smsReference = new SMSReference(msisdn,"flw_id");
        allSMSReferences.add(smsReference);
        markForDeletion(smsReference);

        SMSReference smsReferenceFromDB = allSMSReferences.findByMsisdn(msisdn);
        assertThat(smsReferenceFromDB.getMsisdn(),is(msisdn));
    }


}
