package org.motechproject.ananya.support.synchroniser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
public class AllSynchronisersIT {

    @Autowired
    private AllSynchronisers allSynchronisers;

    @Autowired
    private CallDurationSychroniser callDurationSychroniser;

    @Autowired
    private FrontLineWorkerSynchroniser frontLineWorkerSynchroniser;

    @Autowired
    private SMSSynchroniser smsSynchroniser;

    @Autowired
    private CertificateCourseItemSynchroniser certificateCourseItemSynchroniser;

    @Test
    public void shouldPickUpAllSynchronisers() {
        List<Synchroniser> synchronisers = allSynchronisers.getAll();
        assertThat(synchronisers.size(), is(4));
        assertTrue(synchronisers.contains(frontLineWorkerSynchroniser));
        assertTrue(synchronisers.contains(callDurationSychroniser));
        assertTrue(synchronisers.contains(smsSynchroniser));
        assertTrue(synchronisers.contains(certificateCourseItemSynchroniser));
    }
}
