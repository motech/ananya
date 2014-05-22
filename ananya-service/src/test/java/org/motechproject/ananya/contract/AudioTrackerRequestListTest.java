package org.motechproject.ananya.contract;


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
                        "    \"time\" : \"123456789\"                          " +
                        "}";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callId,  callerId);

        String language= "language";
        
        audioTrackerRequestList.add(jsonString, dataToken, language);

        assertEquals(1, audioTrackerRequestList.all().size());
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
                        "    \"time\" : \"123456789\"                          " +
                        "}";
        String language= "language";
        AudioTrackerRequestList audioTrackerRequestList = new AudioTrackerRequestList(callId,  callerId);
        audioTrackerRequestList.add(jsonString, dataToken, language);

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
