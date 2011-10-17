package org.motechproject.bbcwt.listeners;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Service
public class SendSMSHandler {
    private static final Logger LOG = Logger.getLogger(SendSMSHandler.class);

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    private HttpClient httpClient = new HttpClient();

    public void sendSMS(MotechEvent motechEvent) {
        final Map<String,Object> parameters = motechEvent.getParameters();

        String number = (String) parameters.get("number");
        String validNumber = last10DigitsOfNumber(number);
        String sms = (String) parameters.get("text");

        LOG.info(String.format("Sending message: %s to validNumber: %s.", sms, validNumber));

        GetMethod getMethod = new GetMethod(properties.get(IVRMessage.KOOKOO_OUTBOUND_SMS_URL).toString());
        getMethod.setQueryString(new NameValuePair[]{
                new NameValuePair("api_key", properties.get(IVRMessage.KOOKOO_API_KEY).toString()),
                new NameValuePair("message", sms),
                new NameValuePair("phone_no", validNumber)
        });
        try {
            int responseCode = httpClient.executeMethod(getMethod);
            String response = getMethod.getResponseBodyAsString();
            LOG.info(String.format("The message to:\n%s has been sent with\nresponsecode: %d\nresponse: %s", validNumber, responseCode, response));
        } catch(IOException ioe) {
            LOG.error("Sending message failed", ioe);
        }
    }

    protected  String last10DigitsOfNumber(String number) {
        int length = number.length();
        if(length < 10) {
            return number;
        }
        return number.substring(length-10, length);
    }
}