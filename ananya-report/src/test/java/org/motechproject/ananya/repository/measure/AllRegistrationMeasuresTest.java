package org.motechproject.ananya.repository.measure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllRegistrationMeasuresTest {
    @Mock
    private DataAccessTemplate template;

    @Test
    public void shouldUpdateAllRegistrationMeasures() {
        AllRegistrationMeasures allRegistrationMeasures = new AllRegistrationMeasures(template);
        ArrayList<RegistrationMeasure> registrationMeasureList = new ArrayList<RegistrationMeasure>();

        allRegistrationMeasures.updateAll(registrationMeasureList);

        verify(template).saveOrUpdateAll(registrationMeasureList);
    }
}
