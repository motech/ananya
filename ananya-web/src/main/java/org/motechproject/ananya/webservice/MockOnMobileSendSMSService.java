package org.motechproject.ananya.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.IOException;

@WebService
public class MockOnMobileSendSMSService {

    @WebMethod
    public String singlePush(String mobileNumber, String senderId, String message) throws IOException {
        String result;
//        if(mobileNumber.length() != 10){
//            result = "failure";
//        } else
            result = "success";

//        String debugMsg = "Params: " + mobileNumber + "|" + senderId + "|" + message + "|" + result + "\r\n";
//        FileWriter file= new FileWriter("mock-server-log.txt",true);
//        BufferedWriter bw = new BufferedWriter(file);
//        bw.append(debugMsg);
//        bw.close();

        return result;
    }
}
