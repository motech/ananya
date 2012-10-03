package org.motechproject.ananya.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class OMFtpSource {

    private final String hostname;
    private final Integer port;
    private final String username;
    private final String password;
    private FTPClient ftpClient;

    @Autowired
    public OMFtpSource(@Value("#{ananyaProperties['om.ftpservice.hostname']}") String hostname,
                       @Value("#{ananyaProperties['om.ftpservice.port']}") String portString,
                       @Value("#{ananyaProperties['om.ftpservice.username']}") String username,
                       @Value("#{ananyaProperties['om.ftpservice.password']}") String password) {
        this.hostname = hostname;
        this.port = StringUtils.isBlank(portString) ? null : Integer.valueOf(portString);
        this.username = username;
        this.password = password;

        this.ftpClient = new FTPClient();
    }

    public List<File> downloadAllCsvFilesBetween(DateTime lastProcessedDate, DateTime today) throws IOException {
        lastProcessedDate = lastProcessedDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        today = today.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);

        List<File> filesToProcess = new ArrayList<>();

        try {
            if (port != null) ftpClient.connect(hostname, port);
            else ftpClient.connect(hostname);

            ftpClient.login(username, password);

            DateTime nextDate = lastProcessedDate.plusDays(1);
            while (nextDate.isBefore(today) || nextDate.isEqual(today)) {
                File file = downloadCsvFileFor(nextDate);
                if (file != null) filesToProcess.add(file);

                nextDate = nextDate.plusDays(1);
            }
        } finally {
            ftpClient.disconnect();
        }

        return filesToProcess;
    }

    private File downloadCsvFileFor(DateTime date) throws IOException {
        OutputStream outputStream = null;
        File file = null;
        try {
            String remoteFileName = getRemoteFileName(date);

            FTPFile[] remoteFiles = ftpClient.listFiles(remoteFileName);
            if (remoteFiles == null || remoteFiles.length == 0) return null;

            file = File.createTempFile(remoteFileName, ".csv");

            outputStream = new FileOutputStream(file);
            ftpClient.retrieveFile(remoteFileName, outputStream);
        } finally {
            if (outputStream != null) outputStream.close();
        }

        return file;
    }

    private String getRemoteFileName(DateTime recordDate) {
        return String.format("datapostmaxretry.%s.csv", recordDate.toString("dd-MM-yyyy"));
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
