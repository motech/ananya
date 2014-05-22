package org.motechproject.ananya.repository.dimension;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.OperatorDimension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllOperatorDimensionsIT extends SpringIntegrationTest{
    @Autowired
    AllOperatorDimensions allOperatorDimensions;

    @Before
    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(OperatorDimension.class));
    }

    @Test
    public void shouldSaveOperatorDimension() {
        String operatorName = "airtel";
        allOperatorDimensions.add(new OperatorDimension(operatorName,0,0,0,""));
        List<OperatorDimension> operatorDimensions = template.loadAll(OperatorDimension.class);
        assertEquals(operatorName, operatorDimensions.get(0).getName());
    }

}
