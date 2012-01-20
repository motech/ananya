package org.motechproject.ananyafunctional;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.motechproject.ananyafunctional.framework.MyWebClient.PostParam;
import static org.motechproject.ananyafunctional.framework.MyWebClient.PostParam.param;

public class BookMarkCreationTest extends SpringIntegrationTest {
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldAssociateABookmarkWithAFLW() throws IOException {
        FrontLineWorker flw = new FrontLineWorker("999").status(FrontLineWorkerStatus.REGISTERED);
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        PostParam bookmarkType = param("bookmark.type", "lesson");
        PostParam bookmarkChapterIndex = param("bookmark.chapterIndex", "0");
        PostParam bookmarkLessonIndex = param("bookmark.lessonIndex", "1");
        PostParam callerId = param("callerId", "999");
        new MyWebClient().post("http://localhost:9979/ananya/bookmark/add", bookmarkType, bookmarkChapterIndex, bookmarkLessonIndex, callerId);

        markForDeletion(allFrontLineWorkers.findByMsisdn("999"));
        Assert.assertEquals(allFrontLineWorkers.findByMsisdn("999").getBookmark(), new BookMark("lesson", "0", "1"));
    }
}
