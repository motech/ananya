package org.motechproject.ananya.web.request;


import org.motechproject.ananya.domain.Channel;
import org.motechproject.ananya.domain.WebRequestValidator;
import org.motechproject.ananya.request.FLWNighttimeCallsRequest;
import org.motechproject.ananya.response.ValidationResponse;
import org.motechproject.ananya.utils.DateUtils;

import java.util.UUID;

public class FLWNighttimeCallsWebRequest {
    private final String flwId;
    private final String channel;
    private final String startDate;
    private final String endDate;

    public FLWNighttimeCallsWebRequest(String flwId, String channel, String startDate, String endDate) {
        this.flwId = flwId;
        this.channel = channel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ValidationResponse validate() {
        ValidationResponse validationResponse = new ValidationResponse();
        WebRequestValidator validator = new WebRequestValidator();
        validator.validateFlwId(flwId, validationResponse);
        validator.validateChannel(channel, validationResponse);
        validator.validateDateRange(startDate, endDate, validationResponse);
        return validationResponse;
    }

    public FLWNighttimeCallsRequest getRequest() {
        return new FLWNighttimeCallsRequest(UUID.fromString(flwId), Channel.from(channel), DateUtils.parseLocalDate(startDate), DateUtils.parseLocalDate(endDate));
    }
}
