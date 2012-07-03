package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllCourseActionsTest {
    @Mock
    private CourseLogAction courseLogAction;
    @Mock
    private SendSMSAction sendSMSAction;
    @Mock
    private BookmarkAction bookMarkAction;
    @Mock
    private ScoreAction scoreAction;

    private AllCourseActions allCourseActions;

    @Before
    public void setUp() {
        initMocks(this);
        allCourseActions = new AllCourseActions(bookMarkAction, scoreAction, sendSMSAction, courseLogAction);
    }

    @Test
    public void shouldCallAllItsActionsToProcess() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList("123-467", "123");

        allCourseActions.execute(frontLineWorker, stateRequestList);

        verify(bookMarkAction).process(frontLineWorker, stateRequestList);
        verify(scoreAction).process(frontLineWorker, stateRequestList);
        verify(sendSMSAction).process(frontLineWorker, stateRequestList);
        verify(courseLogAction).process(frontLineWorker, stateRequestList);
    }
}
