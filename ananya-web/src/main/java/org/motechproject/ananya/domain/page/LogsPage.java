package org.motechproject.ananya.domain.page;

import org.motechproject.ananya.domain.FileInfo;
import org.motechproject.ananya.domain.Sidebar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogsPage {
    private String view = "admin/logs";

    private File logDirectory;

    @Autowired
    public LogsPage(@Value("#{ananyaProperties['log.location']}") String logLocation,
                    @Value("#{ananyaProperties['environment']}") String environment) {
        this.logDirectory = environment.equals("prod") ?
                new File(logLocation) : new File(getClass().getResource(logLocation).getPath());
    }

    public ModelAndView display() {
        List<FileInfo> logFilesInfo = new ArrayList<FileInfo>();
        if(logDirectory.exists()){
            File[] logs = logDirectory.listFiles();
            for (File log : logs)
                logFilesInfo.add(new FileInfo(log.getName(), log.length(), log.lastModified()));
        }
        return new ModelAndView(view)
                .addObject("logFilesInfo", logFilesInfo)
                .addObject("menuMap", new Sidebar().getMenu());
    }

    public FileInputStream getLogFile(final String logFilename) throws FileNotFoundException {
        File[] files = logDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(logFilename);
            }
        });
        if (files.length > 0)
            return new FileInputStream(files[0]);

        throw new FileNotFoundException(String.format("%s log file not found", logFilename));
    }
}
