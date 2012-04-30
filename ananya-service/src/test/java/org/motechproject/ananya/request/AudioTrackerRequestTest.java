package org.motechproject.ananya.request;


import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AudioTrackerRequestTest {

    @Test
    public void shouldConvertJsonToAudioTrackerRequest() {
        String callerId = "555";
        String callId = "555:123";
        String dataToken = "1";
        String timeStamp = "2012-04-29T09:38:49Z";
        String jsonString =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"timeStamp\" : \"" + timeStamp +"\"                          " +
                        "}";

        AudioTrackerRequest audioTrackerRequest = AudioTrackerRequest.createFrom(callId, callerId, jsonString, dataToken);

        assertEquals("e79139b5540bf3fc8d96635bc2926f90", audioTrackerRequest.getContentId());
        assertEquals(123, (int) audioTrackerRequest.getDuration());
        DateTime dateTime = DateTime.parse(timeStamp);
        assertEquals(dateTime, audioTrackerRequest.getTimeAsDateTime());
        assertEquals(callerId, audioTrackerRequest.getCallerId());
        assertEquals(callId, audioTrackerRequest.getCallId());
        assertEquals(dataToken, audioTrackerRequest.getToken());
    }
}
