package org.motechproject.ananya.support.synchroniser;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.support.synchroniser.*;
import org.motechproject.ananya.support.synchroniser.base.AllSynchronisers;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-tool.xml")
@Ignore
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
    @Autowired
    private AudioTrackerSynchroniser audioTrackerSynchroniser;

    @Test
    public void shouldPickUpAllSynchronisersSortedInPriority() {
        List<Synchroniser> synchronisers = allSynchronisers.getAll();
        assertThat(synchronisers.size(), is(5));
        assertTrue(synchronisers.get(0).equals(frontLineWorkerSynchroniser));
        assertTrue(synchronisers.get(1).equals(smsSynchroniser));
        assertTrue(synchronisers.contains(callDurationSychroniser));
        assertTrue(synchronisers.contains(certificateCourseItemSynchroniser));
        assertTrue(synchronisers.contains(audioTrackerSynchroniser));
    }

}
