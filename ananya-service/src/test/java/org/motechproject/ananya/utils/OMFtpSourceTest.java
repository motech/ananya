package org.motechproject.ananya.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OMFtpSourceTest {

    private OMFtpSource omFtpSource;
    private String hostname;
    private String port;
    private String username;
    private String password;

    @Mock
    private FTPClient ftpClient;

    @Before
    public void setUp() {
        initMocks(this);
        hostname = "hostname";
        port = "21";
        username = "user";
        password = "passwd";
        omFtpSource = new OMFtpSource(hostname, port, username, password);
        omFtpSource.setFtpClient(ftpClient);
    }

    @Test
    public void shouldDownloadAllCsvFilesBetween() throws IOException {
        DateTime recordDate = DateTime.now();
        DateTime lastProcessedDate = recordDate.minusDays(3);
        String nonExistingFile = "datapostmaxretry." + lastProcessedDate.plusDays(1).toString("dd-MM-yyyy") + ".csv";
        String existingFile1 = "datapostmaxretry." + lastProcessedDate.plusDays(2).toString("dd-MM-yyyy") + ".csv";
        String existingFile2 = "datapostmaxretry." + recordDate.toString("dd-MM-yyyy") + ".csv";
        when(ftpClient.listFiles(nonExistingFile)).thenReturn(new FTPFile[]{});
        when(ftpClient.listFiles(existingFile1)).thenReturn(new FTPFile[]{new FTPFile()});
        when(ftpClient.listFiles(existingFile2)).thenReturn(new FTPFile[]{new FTPFile()});

        List<File> files = omFtpSource.downloadAllCsvFilesBetween(lastProcessedDate, recordDate);

        verify(ftpClient).connect(hostname, Integer.parseInt(port));
        verify(ftpClient).login(username, password);
        verify(ftpClient, never()).retrieveFile(argThat(is(nonExistingFile)), any(OutputStream.class));
        verify(ftpClient).retrieveFile(argThat(is(existingFile1)), any(OutputStream.class));
        verify(ftpClient).retrieveFile(argThat(is(existingFile2)), any(OutputStream.class));
        verify(ftpClient).disconnect();
        assertEquals(2, files.size());
    }
}
