package org.motechproject.ananya.contract;


import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AudioTrackerRequestTest {

    @Test
    public void shouldConvertJsonToAudioTrackerRequest() {
        String callerId = "555";
        String callId = "555:123";
        String dataToken = "1";

        String jsonString =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"time\" : \"123456789\"                          " +
                        "}";

        AudioTrackerRequest audioTrackerRequest = AudioTrackerRequest.createFrom(callId, callerId, jsonString, dataToken);

        assertEquals("e79139b5540bf3fc8d96635bc2926f90", audioTrackerRequest.getContentId());
        assertEquals(123, (int) audioTrackerRequest.getDuration());

        assertEquals(new DateTime(123456789l), audioTrackerRequest.getTimeAsDateTime());
        assertEquals(callerId, audioTrackerRequest.getCallerId());
        assertEquals(callId, audioTrackerRequest.getCallId());
        assertEquals(dataToken, audioTrackerRequest.getToken());
    }
}
