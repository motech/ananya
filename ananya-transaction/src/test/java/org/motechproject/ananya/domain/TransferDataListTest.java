package org.motechproject.ananya.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TransferDataListTest {

    private TransferDataList transferDataList;

    @Before
    public void setUp() {
        String json = "[{\"token\":0,\"type\":\"callDuration\",\"data\":{\"callEvent\":\"CALL_START\",\"time\":1331211295810}},\n" +
                "{\"token\":1,\"type\":\"callDuration\",\"data\":{\"callEvent\":\"CERTIFICATECOURSE_START\",\"time\":1331211297476}},\n" +
                "{\"token\":2,\"type\":\"audioTracker\",\"data\":{\"contentId\":\"content_id\",\"time\":1331211297476,\"duration\":123}},\n" +
                "{\"token\":3,\"type\":\"ccState\",\"data\":{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lessonEndMenu\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}}]";

        transferDataList = new TransferDataList(json);
    }

    @Test
    public void shouldBuildAListFromJson() {
        List<TransferData> list = transferDataList.all();
        assertEquals(4, list.size());

        TransferData callStartData = list.get(0);
        assertThat(callStartData.tokenIntValue(), is(0));
        assertFalse(callStartData.isCCState());

        TransferData courseStartData = list.get(1);
        assertThat(courseStartData.tokenIntValue(), is(1));
        assertFalse(courseStartData.isCCState());

        TransferData audioTracker = list.get(2);
        assertThat(audioTracker.tokenIntValue(), is(2));
        assertTrue(audioTracker.isAudioTrackerState());

        TransferData ccStateData = list.get(3);
        assertThat(ccStateData.tokenIntValue(), is(3));
        assertTrue(ccStateData.isCCState());
    }

    @Test
    public void shouldGiveMaxTokenValueFromTheList() {
        assertThat(transferDataList.maxToken(), is(3));
    }

    @Test
    public void shouldRemoveTokensOlderThanGiven() {
        transferDataList.removeTokensOlderThan(1);
        List<TransferData> list = transferDataList.all();

        assertEquals(2, list.size());
        TransferData ccStateData = list.get(1);
        assertThat(ccStateData.tokenIntValue(), is(3));
        assertTrue(ccStateData.isCCState());

    }
}
