package org.motechproject.ananya.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.IOException;

@WebService
public class MockOnMobileSendSMSService {

    private static Logger log = LoggerFactory.getLogger(MockOnMobileSendSMSService.class);

    @WebMethod
    public String singlePush(String mobileNumber, String senderId, String message) throws IOException {
        String result = "success";
        log.info("sent SMS for [" + mobileNumber + "|senderId=" + senderId + "|message=" + message + "]");
        return result;
    }
}
