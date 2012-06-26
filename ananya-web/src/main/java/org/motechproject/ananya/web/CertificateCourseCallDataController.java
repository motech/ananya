package org.motechproject.ananya.web;

import org.motechproject.ananya.domain.CallerIdParam;
import org.motechproject.ananya.request.CertificateCourseServiceRequest;
import org.motechproject.ananya.service.CertificateCourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CertificateCourseCallDataController {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseCallDataController.class);

    private CertificateCourseService certificateCourseService;

    @Autowired
    public CertificateCourseCallDataController(CertificateCourseService certificateCourseService) {
        this.certificateCourseService = certificateCourseService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata/disconnect")
    @ResponseBody
    public String handleDisconnect(@RequestParam String callId,
                                   @RequestParam String callerId,
                                   @RequestParam String calledNumber,
                                   @RequestParam String dataToPost) {

        callerId = new CallerIdParam(callerId).getValue();

        CertificateCourseServiceRequest serviceRequest = new CertificateCourseServiceRequest(callId, callerId, calledNumber, dataToPost);
        certificateCourseService.handleDisconnect(serviceRequest);

        log.info(callId + "- course disconnect completed");
        log.info(callId + "- course call ended");
        return getReturnVxml();
    }

    private String getReturnVxml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<vxml version=\"2.1\" xsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml21/vxml.xsd\">");
        builder.append("<form id=\"endCall\">");
        builder.append("<block><disconnect/></block>");
        builder.append("</form></vxml>");
        return builder.toString();
    }

}