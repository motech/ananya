package org.motechproject.ananya.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class OMFtpSource {

    private final String hostname;
    private final Integer port;
    private final String username;
    private final String password;

    @Autowired
    public OMFtpSource(@Value("#{ananyaProperties['om.ftpservice.hostname']}") String hostname,
                       @Value("#{ananyaProperties['om.ftpservice.port']}") String portString,
                       @Value("#{ananyaProperties['om.ftpservice.username']}") String username,
                       @Value("#{ananyaProperties['om.ftpservice.password']}") String password) {
        this.hostname = hostname;
        this.port = StringUtils.isBlank(portString) ? null : Integer.valueOf(portString);
        this.username = username;
        this.password = password;
    }

    public File downloadCsvFile(String remoteFileName) throws IOException {
        FTPClient ftpClient = new FTPClient();
        OutputStream outputStream = null;
        File file = null;

        try {
            ftpClient = new FTPClient();

            if (port != null) ftpClient.connect(hostname, port);
            else ftpClient.connect(hostname);

            ftpClient.login(username, password);

            FTPFile[] remoteFiles = ftpClient.listFiles(remoteFileName);
            if (remoteFiles == null || remoteFiles.length == 0) return null;

            file = File.createTempFile(remoteFileName, ".csv");

            outputStream = new FileOutputStream(file);
            ftpClient.retrieveFile(remoteFileName, outputStream);

        } finally {
            if (outputStream != null) outputStream.close();
            ftpClient.disconnect();
        }

        return file;
    }
}
