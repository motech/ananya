package org.motechproject.ananya.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@WebService
public class MockOnMobileSendSMSService {

    @WebMethod
    public String singlePush(String mobileNumber, String senderId, String message) throws IOException {
        String debugMsg = "Params: " + mobileNumber + "|" + senderId + "|" + message;

//        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("mock-server-log.txt")));
//        bw.append(debugMsg);
//        bw.close();

        if(mobileNumber.length() != 10){
            return "failure";
        }

        return "success";
    }
}
