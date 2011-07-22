package org.motechproject.bbcwt.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IVRResponseBuilderTest {

    private IVRResponseBuilder builder;

    @Before
    public void setUp() {
        builder = new IVRResponseBuilder();
    }
    
    @Test
    public void shouldAddSidOnlyIfItsNotEmpty(){
        Response response = builder.withSid("sid").create();
        assertTrue(response.getXML().contains("sid"));
        
        response = builder.withSid("").create();
        assertFalse(response.getXML().contains("sid"));
    }
    
    @Test
    public void shouldAddPlayTextOnlyIfItsNotEmpty(){
        Response response = builder.withPlayText("nova").create();
        assertTrue(response.getXML().contains("nova"));
        
        response = builder.withPlayText("").create();
        assertFalse(response.getXML().contains("sid"));
    }
    
    @Test
    public void shouldAddPlayAudioOnlyIfItsNotEmpty(){
        Response response = builder.withPlayAudio("nova").create();
        assertTrue(response.getXML().contains("nova"));
        
        response = builder.withPlayAudio("").create();
        assertFalse(response.getXML().contains("sid"));
    }
    
    @Test
    public void shouldAddCollectDTMFOnlyIfItsNotNull(){
        Response response = builder.withCollectDtmf(new CollectDtmf()).create();
        assertTrue(response.getXML().contains("<collectdtmf/>"));
        
        response = new IVRResponseBuilder().withCollectDtmf(null).create();
        assertFalse(response.getXML().contains("<collectdtmf/>"));
    }
    
    @Test
    public void shouldHangupOnlyOnlyWhenAskedFor(){
        Response response = builder.withHangUp().create();
        assertTrue(response.getXML().contains("<hangup/>"));

        response = new IVRResponseBuilder().create();
        assertFalse(response.getXML().contains("<hangup/>"));
    }
}
