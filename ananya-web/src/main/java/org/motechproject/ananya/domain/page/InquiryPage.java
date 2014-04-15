package org.motechproject.ananya.domain.page;

import org.motechproject.ananya.domain.DataMapper;
import org.motechproject.ananya.domain.Sidebar;
import org.motechproject.ananya.support.admin.AdminInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Service
public class InquiryPage {

    private AdminInquiryService adminInquiryService;

    @Autowired
    public InquiryPage(AdminInquiryService adminInquiryService) {
        this.adminInquiryService = adminInquiryService;
    }

    public Map<String, Object> display(String msisdn) {
        Map<String, Object> inquiryData = adminInquiryService.getInquiryData(msisdn);
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : inquiryData.keySet())
            result.put(key, DataMapper.prepareDataFor(key, inquiryData));
        return result;
    }

    public ModelAndView display() {
        return new ModelAndView("admin/inquiry")
                .addObject("menuMap", new Sidebar().getMenu());
    }
}
