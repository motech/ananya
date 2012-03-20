package org.motechproject.ananya.functional;

import com.gargoylesoftware.htmlunit.Page;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
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
        FrontLineWorker flw = new FrontLineWorker("999", "name", Designation.ASHA, new Location()).status(RegistrationStatus.REGISTERED);
        flw.addBookMark(new BookMark("lesson", 0, 2));
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        Page page = new MyWebClient().getPage(getAppServerHostUrl() + "/ananya/generated/js/dynamic/caller_data.js?callerId=999");

        String expectedPageResponse = callerDataFor(true, "lesson", 0, 2);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    @Test
    public void should() throws IOException {
        Page page = new MyWebClient().getPage(getAppServerHostUrl() + "/ananya/generated/js/metadata.js");

    }

    @Test
    public void shouldGetCallerDataWithScoresIfThereAreScores() throws IOException {
        FrontLineWorker flw = new FrontLineWorker("999", "name", Designation.ANM, new Location()).status(RegistrationStatus.REGISTERED);

        ReportCard reportCard = flw.reportCard();
        flw.addBookMark(new BookMark("lesson", 3, 0));

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

        Page page = new MyWebClient().getPage(getAppServerHostUrl() + "/ananya/generated/js/dynamic/caller_data.js?callerId=999");

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
        return new BaseMatcher<JsonObject>() {

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
        FrontLineWorker flw = new FrontLineWorker("999", "name",Designation.ASHA, new Location()).status(RegistrationStatus.REGISTERED);
        allFrontLineWorkers.add(flw);
        markForDeletion(flw);

        Page page = new MyWebClient().getPage(getAppServerHostUrl() + "/ananya/generated/js/dynamic/caller_data.js?callerId=999");

        String expectedPageResponse = callerDataWithoutBookmarkFor(true);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    @Test
    public void shouldGetCallerDataWithoutAnyDetailsWhenTheFLWDoesNotExist() throws IOException {
        Page page = new MyWebClient().getPage(getAppServerHostUrl() + "/ananya/generated/js/dynamic/caller_data.js?callerId=1234");

        String expectedPageResponse = callerDataWithoutBookmarkFor(false);
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    @Test
    public void shouldGetCallerDataWithResetCurrentUsageAndPromptPlayedForMaxUsage() throws IOException {
        Page page = new MyWebClient().getPage(getAppServerHostUrl() + "/ananya/generated/js/dynamic/jobaid/caller_data.js?callerId=12345&operator=airtel");

        String expectedPageResponse = callerDataForJobAid();
        Assert.assertEquals(trim(expectedPageResponse), trim(page.getWebResponse().getContentAsString()));
    }

    private String callerDataForJobAid() {
        return trim("var callerData = {\n"+
                "\"isRegistered\" : \"false\",\n" +
                "\"currentJobAidUsage\" : 0,\n" +
                "\"maxAllowedUsageForOperator\" : 2340000,\n" +
                "\"promptsHeard\" : {\n" +
        "}\n" +
        "};");
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
}
