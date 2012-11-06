package org.motechproject.ananya.request;

import org.joda.time.LocalDate;
import org.motechproject.ananya.domain.Channel;

import java.util.UUID;

public class FLWNighttimeCallsRequest {
    private UUID flwId;
    private Channel channel;
    private LocalDate startDate;
    private LocalDate endDate;

    public FLWNighttimeCallsRequest(UUID flwId, Channel channel, LocalDate startDate, LocalDate endDate) {
        this.flwId = flwId;
        this.channel = channel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getFlwId() {
        return flwId;
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
