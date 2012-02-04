package org.motechproject.ananya.functional;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.motechproject.ananya.functional.MyWebClient.PostParam.param;


public class BookMarkCreationTest extends SpringIntegrationTest {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldAssociateABookmarkWithAFLW() throws IOException {
        FrontLineWorker flw = new FrontLineWorker("999", Designation.ASHA, "123").status(RegistrationStatus.REGISTERED);
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        MyWebClient.PostParam bookmarkType = param("bookmark.type", "lesson");
        MyWebClient.PostParam bookmarkChapterIndex = param("bookmark.chapterIndex", "0");
        MyWebClient.PostParam bookmarkLessonIndex = param("bookmark.lessonIndex", "1");
        MyWebClient.PostParam callerId = param("callerId", "999");
        new MyWebClient().post("http://localhost:9979/ananya/bookmark/add", bookmarkType, bookmarkChapterIndex, bookmarkLessonIndex, callerId);

        markForDeletion(allFrontLineWorkers.findByMsisdn("999"));
        Assert.assertEquals(allFrontLineWorkers.findByMsisdn("999").bookMark(), new BookMark("lesson", "0", "1"));
    }
}
