package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllCallDurationMeasuresTest {
    @Mock
    DataAccessTemplate template;

    @Test
    public void shouldUpdateCallDurationMeasure() {
        AllCallDurationMeasures allCallDurationMeasures = new AllCallDurationMeasures(template);
        ArrayList<CallDurationMeasure> callDurationMeasureList = new ArrayList<CallDurationMeasure>();

        allCallDurationMeasures.updateAll(callDurationMeasureList);

        verify(template).saveOrUpdateAll(callDurationMeasureList);
    }
}
