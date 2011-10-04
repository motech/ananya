package org.motechproject.bbcwtfunctional.test.ivr;

import org.junit.Test;
import org.motechproject.bbcwtfunctional.framework.MotechWebClient;
import org.motechproject.bbcwtfunctional.ivr.Caller;
import org.motechproject.bbcwtfunctional.testdata.ivrreponse.IVRResponse;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class NewCallTest {
    @Test
    public void newCallFlow() throws IOException {
        Caller caller = new Caller("123", "9980930495", new MotechWebClient());
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0003_main_menu_help"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("1");

        assertTrue(response.audioPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));

        response = caller.enter("1");

        assertTrue(response.audioPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0006_chapter_1_lesson_2"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0007_chapter_1_lesson_2_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0008_chapter_1_lesson_3"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0009_chapter_1_lesson_3_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0010_chapter_1_lesson_4"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));

        response = caller.enter("1");

        assertTrue(response.audioPlayed("0010_chapter_1_lesson_4"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0012_a_chapter_1_quiz_start_header"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0012_b_chapter_1_q_1"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0013_chapter_1_quiz_q_1_correct_answer"));

        assertTrue(response.promptPlayed("0015_chapter_1_quiz_q_2"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0017_chapter_1_quiz_q_2_wrong_answer"));

        assertTrue(response.promptPlayed("0018_chapter_1_quiz_q_3"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0019_chapter_1_quiz_q_3_correct_answer"));
        assertTrue(response.promptPlayed("0021_chapter_1_quiz_q_4"));

        response = caller.enter("1");

        assertTrue(response.audioPlayed("0022_chapter_1-quiz_q_4_correct_answer"));
        assertTrue(response.audioPlayed("3_out_of_4"));
        assertTrue(response.promptPlayed("0029_chapter_1_end_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.audioPlayed("0030_chapter_2_lesson_1"));
        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0031_chapter_2_lesson_1_option_prompt"));

        response = caller.enter("2");
        response = caller.continueWithoutInteraction();

        assertTrue(response.audioPlayed("0032_congarts_and_tease"));
    }
}