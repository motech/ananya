package org.motechproject.ananyafunctional;

import org.junit.Test;
import org.voiceunit.server.Caller;
import org.voiceunit.server.IVR;

import java.net.URI;
import java.net.URISyntaxException;

public class JobAidTest {
    private static final String WELCOME = "jobAid/nav_0001_welcome.wav";
    private static final String LEVEL_SELECT = "jobAid/nav_0002_level_select.wav";

    private static final String LEVEL_1_MENU = "jobAid/level_1_menu.wav";
    private static final String LEVEL_1_INTRO = "jobAid/level_1_intro.wav";
    private static final String LEVEL_1_CHAPTER_1_INTRO = "jobAid/level_1_chapter_1_intro.wav";
    private static final String LEVEL_1_CHAPTER_1_MENU = "jobAid/level_1_chapter_1_menu.wav";
    private static final String LEVEL_1_CHAPTER_1_LESSON_1 = "jobAid/level_1_chapter_1_lesson_1.wav";
    private static final String LEVEL_1_CHAPTER_1_LESSON_2 = "jobAid/level_1_chapter_1_lesson_2.wav";
    private static final String LEVEL_1_CHAPTER_2_INTRO = "jobAid/level_1_chapter_2_intro.wav";
    private static final String LEVEL_1_CHAPTER_2_MENU = "jobAid/level_1_chapter_2_menu.wav";
    private static final String LEVEL_1_CHAPTER_2_LESSON_2 = "jobAid/level_1_chapter_2_lesson_2.wav";
    private static final String LEVEL_2_CHAPTER_1_INTRO = "jobAid/nav_0005_chapter_01_title.wav";
    private static final String LEVEL_2_CHAPTER_1_MENU = "jobAid/nav_0006_chapter_01_lesson_select.wav";

    private static final String ERROR_NO_INPUT = "error.no.input.retry.wav";
    private static final String ERROR_NO_INPUT_DISCONNECT = "error.no.input.disconnect.wav";
    private static final String INVALID_INPUT = "0000_error_in_pressing_number.wav";
    private static final String INVALID_INPUT_DISCONNECT = "error.wrong.input.disconnect.wav";
    private static final String HELP_PROMPT = "jobAid/nav_0009_help_prompt.wav";

    @Test
    public void shouldAllowNoInputTwiceInARowBeforeHangingUp() throws Exception {
        Caller caller = new Caller();
        caller.listensTo(WELCOME);
        caller.listensTo(LEVEL_SELECT);

        caller.listensTo(ERROR_NO_INPUT);
        caller.listensTo(LEVEL_SELECT);

        caller.listensTo(ERROR_NO_INPUT);
        caller.listensTo(LEVEL_SELECT);

        caller.listensTo(ERROR_NO_INPUT_DISCONNECT);

        startIVR(caller);
    }

    @Test
    public void shouldAllowInvalidInputTwiceInARowBeforeHangingUp() throws Exception {
        Caller caller = new Caller();
        caller.listensTo(WELCOME);
        caller.respondToAudio(LEVEL_SELECT, '7');

        caller.listensTo(INVALID_INPUT);
        caller.respondToAudio(LEVEL_SELECT, '5');

        caller.listensTo(INVALID_INPUT);
        caller.respondToAudio(LEVEL_SELECT, '6');

        caller.listensTo(INVALID_INPUT_DISCONNECT);

        startIVR(caller);
    }

    @Test
    public void shouldNavigateToChapterMenuAfterLesson_GoingToNextChapterIfLastLesson() throws Exception {
        Caller caller = new Caller();
        goToLessonOneInChapterOneOfLevelOne(caller);
        caller.respondToAudio(LEVEL_1_CHAPTER_1_MENU, '2');
        caller.listensTo(HELP_PROMPT);

        caller.listensTo(LEVEL_1_CHAPTER_1_LESSON_2);
        caller.listensTo(LEVEL_1_CHAPTER_2_INTRO);
        caller.respondToAudio(LEVEL_1_CHAPTER_2_MENU, '2');
        caller.listensTo(HELP_PROMPT);
        caller.listensTo(LEVEL_1_CHAPTER_2_LESSON_2);

        caller.listensTo(LEVEL_2_CHAPTER_1_INTRO);
        caller.listensTo(LEVEL_2_CHAPTER_1_MENU);

        caller.hangup();

        startIVR(caller);
    }

    @Test
    public void shouldGoToRootMenuWhenPressingZeroInChapterAndLevelMenus() throws Exception {
        Caller caller = new Caller();
        caller.listensTo(WELCOME);
        caller.respondToAudio(LEVEL_SELECT, '1');

        caller.listensTo(LEVEL_1_INTRO);
        caller.respondToAudio(LEVEL_1_MENU, '0');
        caller.listensTo(HELP_PROMPT);

        caller.respondToAudio(LEVEL_SELECT, '1');
        caller.listensTo(LEVEL_1_INTRO);
        caller.respondToAudio(LEVEL_1_MENU, '2');
        caller.listensTo(HELP_PROMPT);

        caller.listensTo(LEVEL_1_CHAPTER_2_INTRO);
        caller.respondToAudio(LEVEL_1_CHAPTER_2_MENU, '0');
        caller.listensTo(HELP_PROMPT);

        caller.listensTo(LEVEL_SELECT);
        caller.hangup();

        startIVR(caller);
    }

    private void startIVR(Caller caller) throws URISyntaxException {
        new IVR(new URI("http://localhost:9979/ananya/vxml/jobaid.xml")).at("5771101").getsCallFrom(caller);
    }

    private void goToLessonOneInChapterOneOfLevelOne(Caller caller) {
        caller.listensTo(WELCOME);
        caller.respondToAudio(LEVEL_SELECT, '1');

        caller.listensTo(LEVEL_1_INTRO);
        caller.respondToAudio(LEVEL_1_MENU, '1');
        caller.listensTo(HELP_PROMPT);

        caller.listensTo(LEVEL_1_CHAPTER_1_INTRO);
        caller.respondToAudio(LEVEL_1_CHAPTER_1_MENU, '1');
        caller.listensTo(HELP_PROMPT);

        caller.listensTo(LEVEL_1_CHAPTER_1_LESSON_1);
    }
}
