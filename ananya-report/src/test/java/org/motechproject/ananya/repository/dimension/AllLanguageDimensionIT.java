package org.motechproject.ananya.repository.dimension;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.springframework.beans.factory.annotation.Autowired;

public class AllLanguageDimensionIT extends SpringIntegrationTest{
    @Autowired
    AllLanguageDimension allLanguageDimension;

    @Before
    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(LanguageDimension.class));
    }

    @Test
    public void shouldSaveOperatorDimension() {
        String languageName = "bhojpuri";
        allLanguageDimension.add(new LanguageDimension(languageName,"bho", "abc"));
        List<LanguageDimension> languageDimensions = template.loadAll(LanguageDimension.class);
        assertEquals(languageName, languageDimensions.get(0).getLanguageName());
    }


}
