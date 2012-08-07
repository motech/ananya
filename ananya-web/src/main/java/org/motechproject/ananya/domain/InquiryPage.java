package org.motechproject.ananya.domain;

import org.motechproject.ananya.mapper.AcademyCallsMapper;
import org.motechproject.ananya.mapper.CallDetailsMapper;
import org.motechproject.ananya.mapper.KunjiCallsMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InquiryPage {

    public Map<String, Object> display(String msisdn) {
        Map<String, Object> result = new HashMap<String, Object>();

        List<AcademyKunjiCallContent> academyCallsContent = AcademyCallsMapper.mapFrom(null); //TODO
        List<AcademyKunjiCallContent> kunjiCallsContent = KunjiCallsMapper.mapFrom(null); //TODO
        List<CallDetails.Content> callDetailsContent = CallDetailsMapper.mapFrom(null); //TODO

        String callerDataJs = "var callerData = {}"; //TODO

        result.put("academyCalls", AcademyCalls.forContent(academyCallsContent));
        result.put("kunjiCalls", KunjiCalls.forContent(kunjiCallsContent));
        result.put("callDetails", CallDetails.forContent(callDetailsContent));
        result.put("callerDataJs", callerDataJs);

        return result;
    }

    public ModelAndView display() {
        return new ModelAndView("admin/inquiry")
                .addObject("menuMap", new Sidebar().getMenu());
    }
}
