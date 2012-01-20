package org.motechproject.ananyafunctional;

import com.gargoylesoftware.htmlunit.Page;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananyafunctional.framework.MyWebClient;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DynamicJSHandlerTest extends SpringIntegrationTest{

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldGetCallerDataWithBookmarkDetailsWhenThereIsABookmark() throws IOException {
        FrontLineWorker flw = new FrontLineWorker("999").status(FrontLineWorkerStatus.REGISTERED);
        flw.addBookMark(new BookMark("lesson", "0", "2"));
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        Page page = new MyWebClient().getPage("http://localhost:9979/ananya/dynamic/js/caller_data.js?callerId=999");

        String expectedPageResponse = callerDataFor(true, "lesson", 0, 2);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    @Test
    public void shouldGetCallerDataWithoutBookmarkDetailsWhenThereIsNoBookmark() throws IOException {
        FrontLineWorker flw = new FrontLineWorker("999").status(FrontLineWorkerStatus.REGISTERED);
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        Page page = new MyWebClient().getPage("http://localhost:9979/ananya/dynamic/js/caller_data.js?callerId=999");

        String expectedPageResponse = callerDataWithoutBookmarkFor(true);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    @Test
    public void shouldGetCallerDataWithoutAnyDetailsWhenTheFLWDoesNotExist() throws IOException {
        Page page = new MyWebClient().getPage("http://localhost:9979/ananya/dynamic/js/caller_data.js?callerId=1234");

        String expectedPageResponse = callerDataWithoutBookmarkFor(false);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    private String callerDataFor(final boolean isRegistered, final String typeOfBookmark, final int chapter, final int lesson) {
        return "var callerData = {\n" +
                "    \"isRegistered\" : \"" + isRegistered + "\",\n" +
                "    \"bookmark\" : {\"type\" : \"" + typeOfBookmark + "\" , \"chapterIndex\" : \"" + chapter + "\" , \"lessonIndex\" : \"" + lesson + "\"}\n" +
                "};";
    }
    private String callerDataWithoutBookmarkFor(final boolean isRegistered) {
        return "var callerData = {\n" +
                "    \"isRegistered\" : \"" + isRegistered + "\",\n" +
                "    \"bookmark\" : {}\n" +
                "};";
    }

    private String trim(String someString) {
        return someString.replaceAll("\n\\s*", "\n").replaceAll("\r\n", "\n").trim();
    }
}
