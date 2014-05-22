package org.motechproject.ananya.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.CertificationCourseLogItem;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.service.CertificateCourseLogService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CourseLogActionTest {

    private CourseLogAction courseLogAction;
    @Mock
    private CertificateCourseLogService courseLogService;

    @Before
    public void setUp(){

        initMocks(this);
        courseLogAction = new CourseLogAction(courseLogService);
    }
    @Test
    public void shouldPersistCourseLog(){
        FrontLineWorker frontLineWorker =  new FrontLineWorker();
        String callId = "123456";
        String callerId = "123";
        CertificateCourseStateRequestList stateRequestList = new CertificateCourseStateRequestList(callId, callerId);
        String json1 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 1\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":0}";
        String json2 = "{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6a66d5\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"startQuiz\",\"courseItemState\":\"end\"," +
                "\"contentName\":\"Chapter 1 Lesson 2\",\"time\":\"123456789\",\"chapterIndex\":0,\"lessonOrQuestionIndex\":1}";
        String language= "language";
        
        stateRequestList.add(json1, "1", language);
        stateRequestList.add(json2, "2", language);

        courseLogAction.process(frontLineWorker,stateRequestList);

        ArgumentCaptor<CertificationCourseLog> captor = ArgumentCaptor.forClass(CertificationCourseLog.class);
        verify(courseLogService).createNew(captor.capture());
        CertificationCourseLog captured = captor.getValue();

        assertThat(captured.getCallId(),is(callId));
        assertThat(captured.getCallerId(),is(callerId));
        assertThat(captured.items().size(),is(2));
        List<CertificationCourseLogItem> items = captured.items();
        assertEquals(items.get(0).getContentName(),"Chapter 1 Lesson 1");
        assertEquals(items.get(0).getContentId(),"0cccd9b516233e4bb1c6c04fed6a66d5");
        assertEquals(items.get(1).getContentName(),"Chapter 1 Lesson 2");
        assertEquals(items.get(1).getContentId(),"0cccd9b516233e4bb1c6c04fed6a66d5");

    }
}
