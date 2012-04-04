package org.motechproject.ananya.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.IOException;

@WebService
public class MockOnMobileSendSMSService {

    @WebMethod
    public String singlePush(String mobileNumber, String senderId, String message) throws IOException {
        String result = "success";
        
//        if(mobileNumber.length() != 10){
//            result = "failure";
//        }

        return result;
    }
}
