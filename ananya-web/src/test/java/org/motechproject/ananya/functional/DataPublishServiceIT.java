package org.motechproject.ananya.functional;

import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.motechproject.ananya.service.publish.DbPublishService;
import org.motechproject.ananya.service.publish.PublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;

public class DataPublishServiceIT extends SpringIntegrationTest {

    @Autowired
    private DataPublishService dataPublishService;

    @Test
    public void shouldHaveDBPublisherServiceImplementationWiredUp() {
        PublishService publishService = (PublishService) ReflectionTestUtils.getField(dataPublishService, "publishService");
        assertEquals(DbPublishService.class, publishService.getClass());
    }
}
