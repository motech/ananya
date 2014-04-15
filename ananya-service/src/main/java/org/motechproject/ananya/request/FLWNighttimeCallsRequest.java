package org.motechproject.ananya.request;

import org.joda.time.LocalDate;
import org.motechproject.ananya.domain.Channel;

public class FLWNighttimeCallsRequest {
    private String msisdn;
    private Channel channel;
    private LocalDate startDate;
    private LocalDate endDate;

    public FLWNighttimeCallsRequest(String msisdn, Channel channel, LocalDate startDate, LocalDate endDate) {
        this.msisdn = msisdn;
        this.channel = channel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public Channel getChannel() {
        return channel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
