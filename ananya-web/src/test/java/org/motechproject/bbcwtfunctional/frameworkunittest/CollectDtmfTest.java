package org.motechproject.bbcwtfunctional.frameworkunittest;

import org.junit.Test;
import org.motechproject.bbcwtfunctional.testdata.ivrreponse.CollectDtmf;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CollectDtmfTest {
    @Test
    public void playsAudio() {
        CollectDtmf collectDtmf = new CollectDtmf().playAudios("http://foo/abc.wav", "http://foo/efg.wav");
        assertTrue(collectDtmf.hasAudio("abc"));
        assertTrue(collectDtmf.hasAudio("abc", "efg"));
        assertFalse(collectDtmf.hasAudio("sddf"));
    }
}
