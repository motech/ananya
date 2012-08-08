package org.motechproject.ananya.domain.page;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.grid.AcademyCallGrid;
import org.motechproject.ananya.domain.grid.CallDetailGrid;
import org.motechproject.ananya.domain.grid.KunjiCallGrid;
import org.motechproject.ananya.support.admin.AdminInquiryService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class InquiryPageTest {
    private InquiryPage inquiryPage;

    @Mock
    private AdminInquiryService adminInquiryService;

    @Before
    public void setUp(){
        initMocks(this);

        inquiryPage = new InquiryPage(adminInquiryService);
    }

    @Test
    public void shouldQueryForInquiryDataAndReturnMappedResult() {
        String msisdn = "919988776655";
        Map<String, Object> inquiryData = new HashMap<String, Object>();
        inquiryData.put(AdminInquiryService.ACADEMY_CALLS, Collections.EMPTY_LIST);
        inquiryData.put(AdminInquiryService.KUNJI_CALLS, Collections.EMPTY_LIST);
        inquiryData.put(AdminInquiryService.CALL_DETAILS, Collections.EMPTY_LIST);
        inquiryData.put(AdminInquiryService.CALLER_DATA_JS, "var sample = {}");

        when(adminInquiryService.getInquiryData(msisdn)).thenReturn(inquiryData);

        Map<String,Object> result = inquiryPage.display(msisdn);

        verify(adminInquiryService).getInquiryData(msisdn);
        assertNotNull(((AcademyCallGrid) result.get(AdminInquiryService.ACADEMY_CALLS)).getContent());
        assertNotNull(((KunjiCallGrid) result.get(AdminInquiryService.KUNJI_CALLS)).getContent());
        assertNotNull(((CallDetailGrid) result.get(AdminInquiryService.CALL_DETAILS)).getContent());
        assertNotNull(result.get(AdminInquiryService.CALLER_DATA_JS));
    }
}
