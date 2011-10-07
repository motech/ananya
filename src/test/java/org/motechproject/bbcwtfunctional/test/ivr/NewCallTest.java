package org.motechproject.bbcwtfunctional.test.ivr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwtfunctional.framework.MotechWebClient;
import org.motechproject.bbcwtfunctional.ivr.Caller;
import org.motechproject.bbcwtfunctional.testdata.ivrreponse.IVRResponse;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewCallTest {

    private Random random;
    private Caller caller;

    @Before
    public void setup() {
        random = new Random();
        caller = new Caller("123", random.nextInt() + "", new MotechWebClient());
    }

    @After
    public void teardown() {
        caller.hangup();
    }

    @Test
    public void newCallFlow() throws IOException {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));

        response = caller.enter("1");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0006_chapter_1_lesson_2"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0007_chapter_1_lesson_2_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0008_chapter_1_lesson_3"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0009_chapter_1_lesson_3_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0010_chapter_1_lesson_4"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));

        response = caller.enter("1");

        assertTrue(response.promptPlayed("0010_chapter_1_lesson_4"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0012_a_chapter_1_quiz_start_header"));

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

        assertTrue(response.promptPlayed("0030_chapter_2_lesson_1"));
        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0031_chapter_2_lesson_1_option_prompt"));

        response = caller.enter("2");
        response = caller.continueWithoutInteraction();

        assertTrue(response.audioPlayed("0032_congarts_and_tease"));
    }

    @Test
    public void repeatWelcomePrompt() throws Exception {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("1");

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));
    }

    @Test
    public void userDoesNotKeyInAnyInputAtWelcomePrompt() throws Exception {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("");

        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("");

        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));
    }

    @Test
    public void userKeysInInvalidInputsAtWelcomePrompt() throws Exception {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("%");
        assertTrue(response.audioPlayed("0000_error_in_pressing_number"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));


        response = caller.enter("");
        assertFalse(response.audioPlayed("0000_error_in_pressing_number"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        //TODO: Why cannot I give #? # is not being sent properly to the server...
        response = caller.enter("9");

        assertTrue(response.audioPlayed("0000_error_in_pressing_number"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("3");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));
    }

    @Test
    public void userAsksForHelpWhileALessonIsBeingPlayed() throws Exception {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));
        assertTrue("Timeout should be 1 millisecond, if the help is in lesson.", response.collectDtmf().hasTimeOut(1));
        response = caller.enter("%");

        assertTrue("Hitting any key while a lesson is being played should take user to help.",
                response.audioPlayed("0003_main_menu_help"));
        assertTrue("And after playing help, the lesson being played should restart.",
                response.promptPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue("If user does not press any key while a lesson is being played, " +
                "which means he did not request help, lesson prompt should be played.",
                response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));
    }

    @Test
    public void userAsksForHelpWhileALessonEndMenuIsBeingPlayed() throws Exception {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));

        response = caller.enter("%");

        assertTrue(response.audioPlayed("0003_main_menu_help"));
        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));
    }

    @Test
    public void userAsksForHelpWhileLastLessonEndMenuIsBeingPlayed() throws Exception {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0006_chapter_1_lesson_2"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0007_chapter_1_lesson_2_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0008_chapter_1_lesson_3"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0009_chapter_1_lesson_3_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0010_chapter_1_lesson_4"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));

        response = caller.enter("%");

        assertTrue(response.audioPlayed("0003_main_menu_help"));
        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));
    }

    @Test
    public void userAsksForHelpWhenQuizHeaderIsBeingPlayed() throws Exception {
        IVRResponse response = caller.call();

        assertTrue(response.audioPlayed("0001_welcome_new_user"));
        assertTrue(response.promptPlayed("0002_start_course_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0004_chapter_1_lesson_1"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0005_chapter_1_lesson_1_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0006_chapter_1_lesson_2"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0007_chapter_1_lesson_2_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0008_chapter_1_lesson_3"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0009_chapter_1_lesson_3_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0010_chapter_1_lesson_4"));

        response = caller.continueWithoutInteraction();

        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));

        response = caller.enter("%");

        assertTrue(response.audioPlayed("0003_main_menu_help"));
        assertTrue(response.promptPlayed("0011_chapter_1_lesson_4_option_prompt"));

        response = caller.enter("2");

        assertTrue(response.promptPlayed("0012_a_chapter_1_quiz_start_header"));

        response = caller.enter("*");

        assertTrue("Help should be played when * is pressed at quiz header.", response.audioPlayed("003_main_menu_help"));
        assertTrue("Should restart the quiz header.", response.promptPlayed("0012_a_chapter_1_quiz_start_header"));

        response = caller.enter("*");

        assertTrue("If asked for help again, the help should be played again.", response.audioPlayed("003_main_menu_help"));
        assertTrue("followed by restarting the quiz header.", response.promptPlayed("0012_a_chapter_1_quiz_start_header"));

        response = caller.continueWithoutInteraction();

        assertTrue("Then if user proceeds, should ask the question", response.promptPlayed("0012_b_chapter_1_q_1"));
    }
}