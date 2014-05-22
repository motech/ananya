package org.motechproject.ananya.web.request;


import org.motechproject.ananya.domain.Channel;
import org.motechproject.ananya.domain.PhoneNumber;
import org.motechproject.ananya.domain.WebRequestValidator;
import org.motechproject.ananya.request.FLWNighttimeCallsRequest;
import org.motechproject.ananya.response.ValidationResponse;
import org.motechproject.ananya.utils.DateUtils;

public class FLWNighttimeCallsWebRequest {
    private final String msisdn;
    private final String channel;
    private final String startDate;
    private final String endDate;

    public FLWNighttimeCallsWebRequest(String msisdn, String channel, String startDate, String endDate) {
        this.msisdn = msisdn;
        this.channel = channel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ValidationResponse validate() {
        ValidationResponse validationResponse = new ValidationResponse();
        WebRequestValidator validator = new WebRequestValidator();
        validator.validateMsisdn(msisdn, validationResponse);
        validator.validateChannel(channel, validationResponse);
        validator.validateDateRange(startDate, endDate, validationResponse);
        return validationResponse;
    }

    public FLWNighttimeCallsRequest getRequest() {
        String formattedMsisdn = new PhoneNumber(msisdn).getFormattedMsisdn();
        return new FLWNighttimeCallsRequest(formattedMsisdn, Channel.from(channel), DateUtils.parseLocalDate(startDate), DateUtils.parseLocalDate(endDate));
    }
}
