package org.motechproject.ananya.domain.page;

import org.motechproject.ananya.domain.Sidebar;
import org.motechproject.ananya.domain.grid.AcademyCallGrid;
import org.motechproject.ananya.domain.grid.CallDetailGrid;
import org.motechproject.ananya.domain.grid.KunjiCallGrid;
import org.motechproject.ananya.support.admin.AdminInquiryService;
import org.motechproject.ananya.support.admin.domain.CallContent;
import org.motechproject.ananya.support.admin.domain.CallDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InquiryPage {

    private AdminInquiryService adminInquiryService;

    @Autowired
    public InquiryPage(AdminInquiryService adminInquiryService) {
        this.adminInquiryService = adminInquiryService;
    }

    public Map<String, Object> display(String msisdn) {
        msisdn = nineOneize(msisdn);

        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> inquiryData = adminInquiryService.getInquiryData(msisdn);

        List<CallContent> academyCalls = (List<CallContent>) inquiryData.get(AdminInquiryService.ACADEMY_CALLS_KEY);
        List<CallContent> kunjiCalls = (List<CallContent>) inquiryData.get(AdminInquiryService.KUNJI_CALLS_KEY);
        List<CallDetail> callDetails = (List<CallDetail>) inquiryData.get(AdminInquiryService.CALL_DETAILS_KEY);
        String callerDataJs = (String) inquiryData.get(AdminInquiryService.CALLER_DATA_JS_KEY);

        result.put("academyCalls", new AcademyCallGrid(academyCalls));
        result.put("kunjiCalls", new KunjiCallGrid(kunjiCalls));
        result.put("callDetails", new CallDetailGrid(callDetails));
        result.put("callerDataJs", callerDataJs);

        return result;
    }

    public ModelAndView display() {
        return new ModelAndView("admin/inquiry")
                .addObject("menuMap", new Sidebar().getMenu());
    }

    private String nineOneize(String callerId) {
        if (callerId.length() == 10) {
            return "91" + callerId;
        }

        return callerId;
    }
}
