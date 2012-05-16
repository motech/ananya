package org.motechproject.ananya.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface OnMobileSendSMSService {

    String SINGLE_PUSH_SUCCESS = "success";
    String SINGLE_PUSH_FAILURE = "failure";
    
    @WebMethod
    String singlePush(String mobileNumber, String senderId, String message);
}
