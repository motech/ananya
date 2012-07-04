package org.motechproject.ananya.transformers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.request.BaseRequest;
import org.motechproject.ananya.request.CertificateCourseServiceRequest;
import org.motechproject.ananya.request.JobAidServiceRequest;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CalledNumberTransformerTest {

    private CalledNumberTransformer transformer;

    @Mock
    private AllNodes allnodes;
    private String jobAidShortCode;
    private String courseShortCode;
    private String jobAidLongCode;
    private String courseLongCode;
    private Integer codeSize;

    @Before
    public void setUp() {
        initMocks(this);
        jobAidShortCode = "57711";
        courseShortCode = "5771102";
        jobAidLongCode = "08033578056";
        courseLongCode = "08033578058";
        codeSize = 7;
    }

    @Test
    public void shouldReturnCorrectCalledNumber() {
        List<String> shortCodes = Arrays.asList("10", "11", "12");
        when(allnodes.findValuesForKey("shortcode", "JobAidCourse")).thenReturn(shortCodes);

        transformer = new CalledNumberTransformer(allnodes, jobAidShortCode, courseShortCode, jobAidLongCode, courseLongCode, codeSize);

        verifyForJobAid("57711b", jobAidShortCode);
        verifyForJobAid("5771110", "5771110");
        verifyForJobAid("5771118", jobAidShortCode);
        verifyForJobAid("5771111b5", "5771111");
        verifyForJobAid("57711b5567", jobAidShortCode);
        verifyForJobAid("08033578056", jobAidLongCode);

        verifyForCourse("5771102b5", courseShortCode);
        verifyForCourse("5771102", courseShortCode);
        verifyForCourse("08033578058", courseLongCode);
    }

    private void verifyForJobAid(String calledNumberInput, String expectedCalledNumber) {
        BaseRequest request = new JobAidServiceRequest("callId", "9986574420", calledNumberInput);
        transformer.transform(request);
        assertEquals(expectedCalledNumber, request.getCalledNumber());
    }

    private void verifyForCourse(String calledNumberInput, String expectedCalledNumber) {
        BaseRequest request = new CertificateCourseServiceRequest("callId", "9986574420", calledNumberInput);
        transformer.transform(request);
        assertEquals(expectedCalledNumber, request.getCalledNumber());
    }
}
