package org.motechproject.ananya.transformers;

import org.junit.Test;
import org.motechproject.ananya.request.BaseRequest;
import org.motechproject.ananya.request.JobAidServiceRequest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CallerIdTransformerTest {

    @Test
    public void shouldReturnCorrectedCallerId() {

        BaseRequest request1 = new JobAidServiceRequest("callId", "9986574420", "calledNumber");
        BaseRequest request2 = new JobAidServiceRequest("callId", "919985574420", "calledNumber");
        CallerIdTransformer transformer = new CallerIdTransformer();
        transformer.transform(request1);
        transformer.transform(request2);
        assertThat(request1.getCallerId(), is("919986574420"));
        assertThat(request2.getCallerId(), is("919985574420"));
    }
}
