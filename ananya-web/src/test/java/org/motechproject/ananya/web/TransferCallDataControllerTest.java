package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.domain.CertificationCourseStateRequest;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.service.CallLogCounterService;
import org.motechproject.ananya.service.CallLoggerService;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.ReportPublisherService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TransferCallDataControllerTest {
    
    private TransferCallDataController transferCallDataController;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private CertificateCourseService certificateCourseService;
    @Mock
    private CallLoggerService callLoggerService;
    @Mock
    private CallLogCounterService callLogCounterService;

    @Mock
    private ReportPublisherService reportPublisherService;
    @Before
    public void Setup() {
        initMocks(this);
        transferCallDataController = new TransferCallDataController(callLoggerService, certificateCourseService, callLogCounterService, reportPublisherService);
    }

    @Test
    public void shouldRetrieveIVRData() {
        final String callerId = "123";
        final String callId = "456";

        when(request.getParameter("callerId")).thenReturn(callerId);
        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("dataToPost")).thenReturn(postedData());
        
        transferCallDataController.receiveIVRData(request);

        verify(callLogCounterService).purgeRedundantPackets(argThat(is(callId)), argThat(new TransferDataCollectionMatcher(
                new ArrayList<TransferData>() {
                    {
                        add(new TransferData("0", TransferData.TYPE_CC_STATE));
                    }
                }
        )));

        verify(certificateCourseService).saveState(argThat(
                new CertificationCourseStateRequestListMatcher(
                        new ArrayList<CertificationCourseStateRequest>(){
                            {
                                add(new CertificationCourseStateRequest(){
                                    {
                                        setCallId("456");
                                        setToken("0");
                                    }
                                });
                            }
                        })));
        
        verify(callLoggerService).save(argThat(new CallDurationMatcher(new CallDuration(callId, callerId, CallEvent.CALL_START, 1231413))));
        
    }
    
    @Test
    public void shouldCallAppropriateServicesToHandleDisconnectEvent() {
        final String callerId = "123";
        final String callId = "456";

        when(request.getParameter("callerId")).thenReturn(callerId);
        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("dataToPost")).thenReturn("[]");
        
        transferCallDataController.receiveIVRDataAtDisconnect(request);
        verify(reportPublisherService).publishCallDisconnectEvent(callId);
    }

    @Test
    public void tryJsonParse() {
        String jsonString = postedData();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TransferData.class, new TransferData());
        Gson gson = gsonBuilder.create();

        Type collectionType = new TypeToken<Collection<TransferData>>(){}.getType();
        Collection<TransferData> dataCollection = gson.fromJson(jsonString, collectionType);
        
        for(TransferData item : dataCollection) {
            System.out.println(item.getData());
        }
    }
    
    private String postedData () {
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
                "   \"event\" : \"CALL_START\"," +
                "   \"time\"  : 1231413" +
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
                "   }" +
                "]";
    }

    private static class CertificationCourseStateRequestListMatcher extends BaseMatcher<List<CertificationCourseStateRequest>> {
        private List<CertificationCourseStateRequest> certificationCourseStateRequests;

        public CertificationCourseStateRequestListMatcher(List<CertificationCourseStateRequest> certificationCourseStateRequests){
            this.certificationCourseStateRequests = certificationCourseStateRequests;
        }

        @Override
        public boolean matches(Object o) {
            List<CertificationCourseStateRequest> courseRequests = (List<CertificationCourseStateRequest>) o;

            if(this.certificationCourseStateRequests.size() != courseRequests.size()){
                return false;
            }

            for(int i=0; i < courseRequests.size(); ++i){
                CertificationCourseStateRequest thisRequest = this.certificationCourseStateRequests.get(i);
                CertificationCourseStateRequest request = courseRequests.get(i);
                
                if(!(thisRequest.getCallId().equals(request.getCallId()) || thisRequest.getToken().equals(request.getToken()))){
                    return false;
                }
            }

            return true;
        }

        @Override
        public void describeTo(Description description) {

        }
    }

    private static class TransferDataCollectionMatcher extends BaseMatcher<Collection<TransferData>> {
        
        private Collection<TransferData> transferDataCollection;
        
        public TransferDataCollectionMatcher(Collection<TransferData> transferDataCollection){
            this.transferDataCollection = transferDataCollection;
        }

        @Override
        public boolean matches(Object o) {
            Collection<TransferData> matchCollection = (Collection<TransferData>) o;
            
            if(this.transferDataCollection.size() != transferDataCollection.size()){
                return false;
            }
            
            TransferData matchTransferData;
            Iterator<TransferData> matchIterator = matchCollection.iterator();
            for(TransferData transferData : this.transferDataCollection) {
                matchTransferData = matchIterator.next();

                if(!(transferData.getToken().equals(matchTransferData.getToken()) ||
                        transferData.getType().equals(matchTransferData.getType()))){
                    return false;
                }
            }

            return true;
        }

        @Override
        public void describeTo(Description description) {

        }
    }

    private class CallDurationMatcher extends BaseMatcher<CallDuration> {
        
        private CallDuration callDuration;
        
        public CallDurationMatcher(CallDuration callDuration){
            this.callDuration = callDuration;
        }
        
        @Override
        public boolean matches(Object o) {
            CallDuration matchCallDuration = (CallDuration) o;
            
            if(!(callDuration.getCallId().equals(matchCallDuration.getCallId())) ||
                callDuration.getCallEvent().equals(matchCallDuration.getCallEvent())){
                return false;
            }

            return true;
        }

        @Override
        public void describeTo(Description description) {}
    }

    @Test
    public void shouldSaveACallLogForCallStartEvent() throws Exception {
        String callId = "123";
        String callerId = "456";

        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("callerId")).thenReturn(callerId);

        when(request.getParameter("dataToPost")).thenReturn("[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":{\"time\":1330320462000,\"callEvent\":\"CALL_START\"}}]");

        String s = transferCallDataController.receiveIVRData(request);

        verify(callLoggerService).save(argThat(callDurationMatcher("123", "456",CallEvent.CALL_START, 1330320462000L )));
    }

    @Test
    public void shouldSaveACallLogForRegistrationStartAndEndEvent() throws Exception {
        String callId = "123";
        String callerId = "456";

        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("callerId")).thenReturn(callerId);

        when(request.getParameter("dataToPost")).thenReturn("[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":{\"time\":1330320462000,\"callEvent\":\"REGISTRATION_START\"}},{\"token\":\"1\",\"type\":\"callDuration\",\"data\":{\"time\":1330320480000,\"callEvent\":\"REGISTRATION_END\"}}]");

        String s = transferCallDataController.receiveIVRData(request);

        verify(callLoggerService).save(argThat(callDurationMatcher("123", "456",CallEvent.REGISTRATION_START, 1330320462000L )));
        verify(callLoggerService).save(argThat(callDurationMatcher("123", "456",CallEvent.REGISTRATION_END, 1330320480000L )));
    }

    private Matcher<CallDuration> callDurationMatcher(final String callId, final String callerId, final CallEvent callEvent, final long time) {
        return new BaseMatcher<CallDuration>() {
            @Override
            public boolean matches(Object o) {
                CallDuration o1 = (CallDuration) o;
                return o1.getCallId() == callId &&
                        o1.getCallerId() == callerId &&
                        o1.getCallEvent() == callEvent &&
                        o1.getTime() == time;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
