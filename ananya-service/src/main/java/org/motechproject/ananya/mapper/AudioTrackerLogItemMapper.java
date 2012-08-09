package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.contract.AudioTrackerRequest;

public class AudioTrackerLogItemMapper {
    public static AudioTrackerLogItem mapFrom(AudioTrackerRequest audioTrackerRequest) {
        return new AudioTrackerLogItem(
                audioTrackerRequest.getContentId(),
                audioTrackerRequest.getTimeAsDateTime(),
                audioTrackerRequest.getDuration());
    }
}
