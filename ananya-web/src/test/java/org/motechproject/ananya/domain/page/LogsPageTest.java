package org.motechproject.ananya.domain.page;

import org.junit.Test;
import org.motechproject.ananya.domain.FileInfo;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.Assert.*;

public class LogsPageTest {
    private LogsPage logsPage;

    @Test
    public void shouldReturnModelContainingListOfAllFilesInLogDirectoryForDev() {
        logsPage = new LogsPage("/logs","dev");
        ModelAndView modelAndView = logsPage.display();

        List<FileInfo> filesInfo = (List<FileInfo>) modelAndView.getModel().get("logFilesInfo");

        assertEquals(2, filesInfo.size());
        FileInfo fileInfo = filesInfo.get(0);
        assertTrue(fileInfo.getName().contains("ananya.file"));
        assertNotNull(fileInfo.getSize());
        assertNotNull(fileInfo.getLastUpdated());
    }

    @Test
    public void shouldReturnModelContainingListOfAllFilesInLogDirectoryForPeerBox() {
        logsPage = new LogsPage("/logs","dev");
        ModelAndView modelAndView = logsPage.displayAsPeerLogs();

        List<FileInfo> filesInfo = (List<FileInfo>) modelAndView.getModel().get("logFilesInfo");

        assertEquals(2, filesInfo.size());
        FileInfo fileInfo = filesInfo.get(0);
        assertTrue(fileInfo.getName().contains("ananya.file"));
        assertNotNull(fileInfo.getSize());
        assertNotNull(fileInfo.getLastUpdated());
    }

    @Test
    public void shouldReturnModelContainingListOfAllFilesInLogDirectoryForProd() {
        logsPage = new LogsPage("/logs","prod");
        ModelAndView modelAndView = logsPage.display();

        List<FileInfo> filesInfo = (List<FileInfo>) modelAndView.getModel().get("logFilesInfo");

        assertEquals(0, filesInfo.size());
    }
}
