package org.motechproject.ananya.functional;

import com.gargoylesoftware.htmlunit.Page;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DynamicJSControllerIT extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Test
    public void shouldGetCallerDataWithBookmarkDetailsWhenThereIsABookmark() throws IOException {
       FrontLineWorker flw = new FrontLineWorker("999", Designation.ASHA, "1234","").status(RegistrationStatus.REGISTERED);
        flw.addBookMark(new BookMark("lesson", 0, 2));
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        Page page = new MyWebClient().getPage("http://localhost:9979/ananya/generated/js/dynamic/caller_data.js?callerId=999");

        String expectedPageResponse = callerDataFor(true, "lesson", 0, 2);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }



    @Test
    public void shouldGetCallerDataWithScoresIfThereAreScores() throws IOException {
        FrontLineWorker flw = new FrontLineWorker("999", Designation.ANM, "1234","").status(RegistrationStatus.REGISTERED);

        ReportCard reportCard = flw.reportCard();
        flw.addBookMark(new BookMark("lesson",3,0));

        final ReportCard.Score ch1q1score = new ReportCard.Score("0", "4", true);
        final ReportCard.Score ch1q2score = new ReportCard.Score("0", "5", false);
        final ReportCard.Score ch1q3score = new ReportCard.Score("0", "6", true);

        final ReportCard.Score ch2q1score = new ReportCard.Score("1", "4", false);
        final ReportCard.Score ch2q2score = new ReportCard.Score("1", "5", false);
        final ReportCard.Score ch2q3score = new ReportCard.Score("1", "6", true);

        final ReportCard.Score ch3q1score = new ReportCard.Score("2", "4", false);
        final ReportCard.Score ch3q2score = new ReportCard.Score("2", "5", false);
        final ReportCard.Score ch3q3score = new ReportCard.Score("2", "6", false);

        reportCard.addScore(ch1q1score);
        reportCard.addScore(ch1q2score);
        reportCard.addScore(ch1q3score);

        reportCard.addScore(ch2q1score);
        reportCard.addScore(ch2q2score);
        reportCard.addScore(ch2q3score);

        reportCard.addScore(ch3q1score);
        reportCard.addScore(ch3q2score);
        reportCard.addScore(ch3q3score);

        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        Page page = new MyWebClient().getPage("http://localhost:9979/ananya/generated/js/dynamic/caller_data.js?callerId=999");

        String callerDataAssignmentStmt = page.getWebResponse().getContentAsString();
        String callerDataJson = jsonWithWhichCallerDataVarIsBeingAssigned(callerDataAssignmentStmt);
        JsonElement root = new JsonParser().parse(callerDataJson);
        JsonObject scoresByChapterJson = root.getAsJsonObject().get("scoresByChapter").getAsJsonObject();
        final int noOfChapters = scoresByChapterJson.entrySet().size();

        assertThat(noOfChapters, equalTo(3));
        assertThat(scoresByChapterJson, hasChapterWithScore("0", 2));
        assertThat(scoresByChapterJson, hasChapterWithScore("1", 1));
        assertThat(scoresByChapterJson, hasChapterWithScore("2", 0));
    }

    private String jsonWithWhichCallerDataVarIsBeingAssigned(String jsonResponse) {
        return jsonResponse.replace("var callerData = ", "").replace(";", "");
    }

    private Matcher<JsonObject> hasChapterWithScore(final String chapterIndexToBePresent, final int scoreForChapterToBePreset) {
        return new BaseMatcher<JsonObject>(){

            @Override
            public void describeTo(Description description) {
            }

            @Override
            public boolean matches(Object o) {
                JsonObject scoresByChapterJson = (JsonObject) o;
                final JsonElement scoreJson = scoresByChapterJson.get(chapterIndexToBePresent);
                return scoreJson == null ? false : (scoreJson.getAsInt() == scoreForChapterToBePreset);
            }
        };
    }

    @Test
    public void shouldGetCallerDataWithoutBookmarkDetailsWhenThereIsNoBookmark() throws IOException {
        FrontLineWorker flw = new FrontLineWorker("999", Designation.ASHA, "1234","").status(RegistrationStatus.REGISTERED);
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        Page page = new MyWebClient().getPage("http://localhost:9979/ananya/generated/js/dynamic/caller_data.js?callerId=999");

        String expectedPageResponse = callerDataWithoutBookmarkFor(true);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    @Test
    public void shouldGetCallerDataWithoutAnyDetailsWhenTheFLWDoesNotExist() throws IOException {
        Page page = new MyWebClient().getPage("http://localhost:9979/ananya/generated/js/dynamic/caller_data.js?callerId=1234");

        String expectedPageResponse = callerDataWithoutBookmarkFor(false);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    private String callerDataFor(final boolean isRegistered, final String typeOfBookmark, final int chapter, final int lesson) {
        return trim("var callerData = {\n" +
                "    \"isRegistered\" : \"" + isRegistered + "\",\n" +
                "    \"bookmark\" : {\"type\" : \"" + typeOfBookmark + "\" , \"chapterIndex\" : " + chapter + " , \"lessonIndex\" : " + lesson + "},\n" +
                "    \"scoresByChapter\" : {\n" +
                "}\n" +
                "};");
    }
    private String callerDataWithoutBookmarkFor(final boolean isRegistered) {
        return trim("var callerData = {\n" +
                "    \"isRegistered\" : \"" + isRegistered + "\",\n" +
                "    \"bookmark\" : {},\n" +
                "    \"scoresByChapter\" : {\n" +
                "}\n" +
                "};");
    }

    private String trim(String someString) {
        return StringUtils.deleteWhitespace(someString);
    }

//    @Test
//    public void shouldGetCallerDataWithBookmarkDetailsWhenThereIsABookmark() throws IOException {
//       FrontLineWorker flw = new FrontLineWorker("14202", Designation.ASHA, "1234").status(RegistrationStatus.REGISTERED);
//        flw.addBookMark(new BookMark("lesson", 8, 0));
//
//        ReportCard reportCard = flw.reportCard();
//
//        final ReportCard.Score ch1q1score = new ReportCard.Score("0", "4", true);
//        final ReportCard.Score ch1q2score = new ReportCard.Score("0", "5", false);
//        final ReportCard.Score ch1q3score = new ReportCard.Score("0", "6", true);
//
//        final ReportCard.Score ch2q1score = new ReportCard.Score("1", "4", true);
//        final ReportCard.Score ch2q2score = new ReportCard.Score("1", "5", true);
//        final ReportCard.Score ch2q3score = new ReportCard.Score("1", "6", true);
//
//        final ReportCard.Score ch3q1score = new ReportCard.Score("2", "4", true);
//        final ReportCard.Score ch3q2score = new ReportCard.Score("2", "5", true);
//        final ReportCard.Score ch3q3score = new ReportCard.Score("2", "6", true);
//
//        final ReportCard.Score ch4q1score = new ReportCard.Score("3", "4", true);
//        final ReportCard.Score ch4q2score = new ReportCard.Score("3", "5", true);
//        final ReportCard.Score ch4q3score = new ReportCard.Score("3", "6", true);
//
//        final ReportCard.Score ch5q1score = new ReportCard.Score("4", "4", true);
//        final ReportCard.Score ch5q2score = new ReportCard.Score("4", "5", true);
//        final ReportCard.Score ch5q3score = new ReportCard.Score("4", "6", true);
//
//        final ReportCard.Score ch6q1score = new ReportCard.Score("5", "4", true);
//        final ReportCard.Score ch6q2score = new ReportCard.Score("5", "5", true);
//        final ReportCard.Score ch6q3score = new ReportCard.Score("5", "6", true);
//
//        final ReportCard.Score ch7q1score = new ReportCard.Score("6", "4", true);
//        final ReportCard.Score ch7q2score = new ReportCard.Score("6", "5", true);
//        final ReportCard.Score ch7q3score = new ReportCard.Score("6", "6", true);
//
//        final ReportCard.Score ch8q1score = new ReportCard.Score("7", "4", false);
//        final ReportCard.Score ch8q2score = new ReportCard.Score("7", "5", false);
//        final ReportCard.Score ch8q3score = new ReportCard.Score("7", "6", false);
//
//        reportCard.addScore(ch1q1score);
//        reportCard.addScore(ch1q2score);
//        reportCard.addScore(ch1q3score);
//
//        reportCard.addScore(ch2q1score);
//        reportCard.addScore(ch2q2score);
//        reportCard.addScore(ch2q3score);
//
//        reportCard.addScore(ch3q1score);
//        reportCard.addScore(ch3q2score);
//        reportCard.addScore(ch3q3score);
//
//        reportCard.addScore(ch4q1score);
//        reportCard.addScore(ch4q2score);
//        reportCard.addScore(ch4q3score);
//
//        reportCard.addScore(ch5q1score);
//        reportCard.addScore(ch5q2score);
//        reportCard.addScore(ch5q3score);
//
//        reportCard.addScore(ch6q1score);
//        reportCard.addScore(ch6q2score);
//        reportCard.addScore(ch6q3score);
//
//        reportCard.addScore(ch7q1score);
//        reportCard.addScore(ch7q2score);
//        reportCard.addScore(ch7q3score);
//
//        reportCard.addScore(ch8q1score);
//        reportCard.addScore(ch8q2score);
//        reportCard.addScore(ch8q3score);
//
//        allFrontLineWorkers.add(flw);
//        //markForDeletion(flw);
//
//        Page page = new MyWebClient().getPage("http://localhost:8081/ananya/generated/js/dynamic/caller_data.js?callerId=14202");
//
////        String expectedPageResponse = callerDataFor(true, "lesson", 0, 2);
////        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
//    }
}
