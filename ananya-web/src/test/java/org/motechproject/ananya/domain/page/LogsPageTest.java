package org.motechproject.ananya.domain.page;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
public class LogsPageTest {
    @Autowired
    private LogsPage logsPage;

    @Test
    public void shouldReturnModelContainingListOfAllFilesInLogDirectory() {
        ModelAndView modelAndView = logsPage.display();

        List<FileInfo> filesInfo = (List<FileInfo>) modelAndView.getModel().get("logFilesInfo");

        assertEquals(2, filesInfo.size());
        FileInfo fileInfo = filesInfo.get(0);
        assertEquals("ananya.log", fileInfo.getName());
        assertNotNull(fileInfo.getSize());
        assertNotNull(fileInfo.getLastUpdated());
    }


}