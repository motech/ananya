package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.request.AudioTrackerRequest;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
import org.motechproject.ananya.service.CallLoggerService;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class CertificateCourseCallDataControllerTest {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseCallDataControllerTest.class);

    private CertificateCourseCallDataController transferCallDataController;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private CertificateCourseService certificateCourseService;
    @Mock
    private CallLoggerService callLoggerService;
    @Mock
    private DataPublishService dataPublishService;

    @Before
    public void Setup() {
        initMocks(this);
        transferCallDataController = new CertificateCourseCallDataController(callLoggerService,
                certificateCourseService, dataPublishService);
    }

    @Test
    public void shouldRetrieveIVRData() {
        final String callerId = "123";
        final String callId = "456";

        when(request.getParameter("callerId")).thenReturn(callerId);
        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("dataToPost")).thenReturn(postedData());

        transferCallDataController.receiveIVRDataAtDisconnect(request);

        CertificationCourseStateRequest stateRequest = new CertificationCourseStateRequest();
        stateRequest.setCallId(callId);
        stateRequest.setToken("0");
        List<CertificationCourseStateRequest> expectedStateRequestList = Arrays.asList(stateRequest);

        verify(certificateCourseService).saveState(argThat(new CertificationCourseStateRequestListMatcher(expectedStateRequestList)));

        List<CallDuration> expectedCallDurations = Arrays.asList(new CallDuration(CallEvent.CALL_START, 1231413));
        verify(callLoggerService).saveAll(argThat(new CallDurationListMatcher(expectedCallDurations)));

        ArgumentCaptor<AudioTrackerRequestList> captor = ArgumentCaptor.forClass(AudioTrackerRequestList.class);
        verify(certificateCourseService).saveAudioTrackerState(captor.capture());
        AudioTrackerRequestList audioTrackerRequestList = captor.getValue();
        AudioTrackerRequest audioTrackerRequest = audioTrackerRequestList.getAll().get(0);
        assertEquals(1, audioTrackerRequestList.getAll().size());
        assertEquals(callId, audioTrackerRequestList.getCallId());
        assertEquals(callerId, audioTrackerRequestList.getCallerId());
        assertEquals("e79139b5540bf3fc8d96635bc2926f90", audioTrackerRequest.getContentId());
        assertEquals(123, (int)audioTrackerRequest.getDuration());
        assertEquals(DateTime.parse("123456789"), audioTrackerRequest.getTimeAsDateTime());
    }

    @Test
    public void shouldCallAppropriateServicesToHandleDisconnectEvent() {
        final String callerId = "123";
        final String callId = "456";

        when(request.getParameter("callerId")).thenReturn(callerId);
        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("dataToPost")).thenReturn("[]");

        transferCallDataController.receiveIVRDataAtDisconnect(request);
        verify(dataPublishService).publishCallDisconnectEvent(callId, ServiceType.CERTIFICATE_COURSE);
    }

    @Test
    public void tryJsonParse() {
        String jsonString = postedData();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TransferData.class, new TransferData());
        Gson gson = gsonBuilder.create();

        Type collectionType = new TypeToken<Collection<TransferData>>() {
        }.getType();
        Collection<TransferData> dataCollection = gson.fromJson(jsonString, collectionType);

        for (TransferData item : dataCollection) {
            log.info(item.getData());
        }
    }

    @Test
    public void shouldSaveACallLogForCallStartEvent() throws Exception {
        String callId = "123";
        String callerId = "456";

        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("callerId")).thenReturn(callerId);

        when(request.getParameter("dataToPost")).thenReturn("[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":{\"time\":1330320462000,\"callEvent\":\"CALL_START\"}}]");

        String s = transferCallDataController.receiveIVRDataAtDisconnect(request);

        List<CallDuration> expectedCallDurations = Arrays.asList(new CallDuration(CallEvent.CALL_START, 1330320462000L));
        verify(callLoggerService).saveAll(argThat(new CallDurationListMatcher(expectedCallDurations)));
    }


    @Test
    public void shouldSaveACallLogForRegistrationStartAndEndEvent() throws Exception {
        String callId = "123";
        String callerId = "456";

        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("callerId")).thenReturn(callerId);
        when(request.getParameter("dataToPost")).thenReturn("[{\"token\":\"0\",\"type\":\"callDuration\"," +
                "\"data\":{\"time\":1330320462000,\"callEvent\":\"REGISTRATION_START\"}}," +
                "{\"token\":\"1\",\"type\":\"callDuration\",\"data\":{\"time\":1330320480000,\"callEvent\":\"REGISTRATION_END\"}}]");

        transferCallDataController.receiveIVRDataAtDisconnect(request);

        List<CallDuration> expectedCallDurations = Arrays.asList(
                new CallDuration(CallEvent.REGISTRATION_START, 1330320462000L),
                new CallDuration(CallEvent.REGISTRATION_END, 1330320480000L)
        );
        verify(callLoggerService).saveAll(argThat(new CallDurationListMatcher(expectedCallDurations)));
    }

    private String postedData() {
        String packet1 = "{" +
                "    \"chapterIndex\" : 1,                                     " +
                "    \"lessonOrQuestionIndex\" : 2,                            " +
                "    \"questionResponse\" : 1,                                 " +
                "    \"result\" : true,                                        " +
                "    \"interactionKey\" : \"startNextChapter\",                " +

                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"contentType\" : \"lesson\",                             " +
                "    \"courseItemState\" : \"start\",                          " +
                "    \"contentData\" : 6,                                      " +
                "    \"certificateCourseId\" : \"e79139b5540bf3fc8d96635bc2926f90\"  " +
                "}";
        String packet2 = "{" +
                "   \"callEvent\" : \"CALL_START\"," +
                "   \"time\"  : 1231413" +
                "}";

        String packet3 = "{" +
                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"duration\" : \"123\",                             " +
                "    \"timeStamp\" : \"123456789\"                          " +
                "}";

        return "[" +
                "   {" +
                "       \"token\" : 0," +
                "       \"type\"  : \"ccState\", " +
                "       \"data\"  : " + packet1 +
                "   }," +
                "" +
                "   {" +
                "       \"token\" : 1," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet2 +
                "   }," +
                "" +
                "   {" +
                "       \"token\" : 1," +
                "       \"type\"  : \"audioTracker\", " +
                "       \"data\"  : " + packet3 +
                "   }" +
                "]";
    }

    private static class CertificationCourseStateRequestListMatcher extends BaseMatcher<CertificationCourseStateRequestList> {
        private List<CertificationCourseStateRequest> certificationCourseStateRequests;

        public CertificationCourseStateRequestListMatcher(List<CertificationCourseStateRequest> certificationCourseStateRequests) {
            this.certificationCourseStateRequests = certificationCourseStateRequests;
        }

        @Override
        public boolean matches(Object o) {
            List<CertificationCourseStateRequest> matchRequests = ((CertificationCourseStateRequestList) o).all();

            if (this.certificationCourseStateRequests.size() != matchRequests.size())
                return false;

            for (int i = 0; i < matchRequests.size(); ++i) {
                CertificationCourseStateRequest thisRequest = this.certificationCourseStateRequests.get(i);
                CertificationCourseStateRequest request = matchRequests.get(i);

                if (!(thisRequest.getCallId().equals(request.getCallId())
                        || thisRequest.getToken().equals(request.getToken())))
                    return false;

            }
            return true;
        }

        @Override
        public void describeTo(Description description) {

        }
    }

    private class CallDurationListMatcher extends BaseMatcher<CallDurationList> {

        private List<CallDuration> callDurations;

        public CallDurationListMatcher(List<CallDuration> callDurations) {
            this.callDurations = callDurations;
        }

        @Override
        public boolean matches(Object o) {
            List<CallDuration> matchDurations = ((CallDurationList) o).all();
            return CollectionUtils.isEqualCollection(matchDurations, callDurations);
        }

        @Override
        public void describeTo(Description description) {
        }
    }


}
