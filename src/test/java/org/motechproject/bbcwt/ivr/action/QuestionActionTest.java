package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Question;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.MilestonesRepository;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class QuestionActionTest extends BaseActionTest {

    @Mock
    private MilestonesRepository milestonesRepository;

    @Mock
    private ChaptersRespository chaptersRespository;

    private QuestionAction questionAction;
    private Chapter chapter;
    private Question question;
    private String callerId;

    @Before
    public void setUp(){
        callerId = "9876543210";
        question = new Question(1, "http://question/1", 1, null, null);
        chapter = new Chapter(1);
        chapter.addQuestion(question);

        questionAction = new QuestionAction(chaptersRespository, milestonesRepository, messages);

        when(chaptersRespository.findByNumber(anyInt())).thenReturn(chapter);
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
    }

    @Test
    public void shouldMarkStartOfNewQuestionInMilestone(){

        questionAction.get(chapter.getNumber(), question.getNumber(), request, response);

        verify(milestonesRepository).markNewQuestionStart(callerId, chapter.getNumber(), question.getNumber());
        verify(ivrDtmfBuilder).withPlayAudio(CONTENT_LOCATION + question.getQuestionLocation());
        verify(ivrResponseBuilder).withCollectDtmf(collectDtmf);
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/collectAnswer");
    }
}