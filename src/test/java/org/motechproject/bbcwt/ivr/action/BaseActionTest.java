package org.motechproject.bbcwt.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public abstract class BaseActionTest {
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected HttpSession session;
    @Mock
    protected IVRMessage messages;
    @Mock
    protected IVRDtmfBuilder ivrDtmfBuilder;
    @Mock
    protected CollectDtmf collectDtmf;
    @Mock
    protected IVRResponseBuilder ivrResponseBuilder;
    @Mock
    protected Response ivrResponse;

    protected final String CONTENT_LOCATION = "http://localhost/bbcwt/audio/";


    @Before
    public void baseSetup() {
        initMocks(this);
        when(messages.get("content.location")).thenReturn(CONTENT_LOCATION);
        setupSession();
        setupIVRBuilders();
    }

    public void setupSession() {
        when(request.getSession()).thenReturn(session);
    }

    public void setupIVRBuilders() {
        when(request.getAttribute(IVR.Attributes.DTMF_BUILDER)).thenReturn(ivrDtmfBuilder);
        when(request.getAttribute(IVR.Attributes.RESPONSE_BUILDER)).thenReturn(ivrResponseBuilder);

        when(ivrDtmfBuilder.withPlayText(Matchers.<String>any())).thenReturn(ivrDtmfBuilder);
        when(ivrDtmfBuilder.withPlayAudio(Matchers.<String>any())).thenReturn(ivrDtmfBuilder);
        when(ivrDtmfBuilder.withMaximumLengthOfResponse(Matchers.<Integer>any())).thenReturn(ivrDtmfBuilder);
        when(ivrDtmfBuilder.withTimeOutInMillis(Matchers.<Integer>any())).thenReturn(ivrDtmfBuilder);

        when(ivrDtmfBuilder.create()).thenReturn(collectDtmf);

        when(ivrResponseBuilder.withCollectDtmf(Matchers.<CollectDtmf>any())).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.addPlayAudio(Matchers.<String>any())).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.addPlayText(Matchers.<String>any())).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.withNextUrl(Matchers.<String>any())).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.withSid(Matchers.<String>any())).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.withHangUp()).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.create()).thenReturn(ivrResponse);
    }
}
