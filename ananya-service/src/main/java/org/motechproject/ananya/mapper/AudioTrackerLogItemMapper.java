package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.request.AudioTrackerRequest;

public class AudioTrackerLogItemMapper {
    public static AudioTrackerLogItem mapFrom(AudioTrackerRequest audioTrackerRequest) {
        return new AudioTrackerLogItem(audioTrackerRequest.getContentId(),
                audioTrackerRequest.getTimeAsDateTime(),
                audioTrackerRequest.getDuration());
    }
}
