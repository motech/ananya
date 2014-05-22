package org.motechproject.ananya.mapper;

import org.junit.Test;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.contract.AudioTrackerRequest;

import static junit.framework.Assert.assertEquals;

public class AudioTrackerLogItemMapperTest {

    @Test
    public void shouldMapAudioTrackRequestToAudioLogItem() {
        String callerId = "555";
        String callId = "555:123";
        String language= "language";
        String dataToken = "1";
        String jsonString =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"time\" : \"123456789\"" +
                        "}";
        AudioTrackerRequest audioTrackerRequest = AudioTrackerRequest.createFrom(callId, callerId, jsonString, dataToken, language);

        AudioTrackerLogItem audioTrackerLogItem = AudioTrackerLogItemMapper.mapFrom(audioTrackerRequest);

        assertEquals(audioTrackerRequest.getContentId(), audioTrackerLogItem.getContentId());
        assertEquals(audioTrackerRequest.getTimeAsDateTime(), audioTrackerLogItem.getTime());
        assertEquals(audioTrackerRequest.getDuration(), audioTrackerLogItem.getDuration());
    }
}
