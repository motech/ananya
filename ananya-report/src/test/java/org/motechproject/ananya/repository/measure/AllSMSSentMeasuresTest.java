package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.measure.SMSSentMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllSMSSentMeasuresTest {
    @Mock
    private DataAccessTemplate template;

    @Test
    public void shouldUpdateAllSMSSentMeasures() {
        AllSMSSentMeasures allSMSSentMeasures = new AllSMSSentMeasures(template);
        ArrayList<SMSSentMeasure> expectedSMSSentMeasures = new ArrayList<SMSSentMeasure>();

        allSMSSentMeasures.updateAll(expectedSMSSentMeasures);

        verify(template).saveOrUpdateAll(expectedSMSSentMeasures);
    }
}
