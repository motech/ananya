package org.motechproject.bbcwt.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IVRDtmfBuilderTest {

    private IVRDtmfBuilder builder;

    @Before
    public void setUp() {
        builder = new IVRDtmfBuilder();
    }

    @Test
    public void shouldAddPlayTextOnlyIfItIsNotEmpty() {
        CollectDtmf collectDtmf = builder.addPlayText("nova").create();
        assertTrue(collectDtmf.getXML().contains("nova"));
    }

    @Test
    public void shouldAddPlayAudioOnlyIfItIsNotEmpty() {
        CollectDtmf collectDtmf = builder.addPlayAudio("nova").create();
        assertTrue(collectDtmf.getXML().contains("nova"));
    }

    @Test
    public void shouldReturnNullIfThereAreNoAudiosOrTexts() {
        CollectDtmf collectDtmf = builder.create();
        assertNull(collectDtmf);
    }

    @Test
    public void shouldReturnObjectIfWithNoPromptsIsSet() {
        CollectDtmf collectDtmf = builder.withNoPrompts().create();
        assertNotNull(collectDtmf);
    }
}
