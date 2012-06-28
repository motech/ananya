package org.motechproject.ananya.transformers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.request.BaseRequest;
import org.motechproject.ananya.request.BaseServiceRequest;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CalledNumberTransformerTest {

    private CalledNumberTransformer transformer;

    @Mock
    private AllNodes allnodes;
    private String shortCode;

    @Before
    public void setUp() {
        initMocks(this);
        shortCode = "57711";
        transformer = new CalledNumberTransformer(allnodes, shortCode);
    }

    @Test
    public void shouldReturnCorrectCalledNumber() {

        List<String> shortCodes = new ArrayList<String>();
        shortCodes.add("5771110");

        when(allnodes.findValuesForKey("shortcode", "JobAidCourse")).thenReturn(shortCodes);
        verifyFor("57711b","57711");
        verifyFor("5771110","5771110");
        verifyFor("5771118","57711" );
        verifyFor("5771110b5","5771110");
        verifyFor("57711b5567","57711");
    }

    private void verifyFor(String calledNumberInput, String expectedCalledNumber) {
        BaseRequest request1 = new BaseServiceRequest("callId", "9986574420", calledNumberInput);
        transformer.transform(request1);
        assertEquals(expectedCalledNumber, request1.getCalledNumber());
    }
}
