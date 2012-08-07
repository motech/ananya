package org.motechproject.ananya.domain.page;

import org.motechproject.ananya.domain.Sidebar;
import org.motechproject.ananya.domain.grid.*;
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

        List<CallContentGridUnit> academyCalls = AcademyCallsMapper.mapFrom(null); //TODO
        List<CallContentGridUnit> kunjiCalls = KunjiCallsMapper.mapFrom(null); //TODO
        List<CallDetailGridUnit> callDetails = CallDetailsMapper.mapFrom(null); //TODO

        String callerDataJs = "var callerData = {}"; //TODO

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
}
