package org.motechproject.ananya.request;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AudioTrackerRequestListTest {

    @Test
    public void shouldAddContentToTheList () {
        String callerId = "555";
        String callId = "555:123";
        String dataToken = "1";
        String jsonString =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"timeStamp\" : \"123456789\"                          " +
                        "}";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callId,  callerId);

        audioTrackerRequestList.add(jsonString, dataToken);

        assertEquals(1, audioTrackerRequestList.getAll().size());
    }

    @Test
    public void shouldReturnNotEmptyIfTheListHasItems () {
        String callerId = "555";
        String callId = "555:123";
        String dataToken = "1";
        String jsonString =
                "{" +
                        "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                        "    \"duration\" : \"123\",                             " +
                        "    \"timeStamp\" : \"123456789\"                          " +
                        "}";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callId,  callerId);
        audioTrackerRequestList.add(jsonString, dataToken);

        boolean audioTrackerRequestListEmpty = audioTrackerRequestList.isEmpty();

        assertEquals(false, audioTrackerRequestListEmpty);
    }

    @Test
    public void shouldReturnEmptyIfDataIsEmpty (){
        String callerId = "555";
        String callId = "555:123";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callId,  callerId);

        boolean audioTrackerRequestListEmpty = audioTrackerRequestList.isEmpty();

        assertEquals(true, audioTrackerRequestListEmpty);
    }
}
