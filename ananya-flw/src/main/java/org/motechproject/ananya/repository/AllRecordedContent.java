package org.motechproject.ananya.repository;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
public class AllRecordedContent {

    private static Logger log = LoggerFactory.getLogger(AllRecordedContent.class);

    public void add(String msisdn, List<FileItem> fileItems) {
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) continue;
            File savedFile = new File(msisdn + "_" + fileItem.getFieldName() + ".wav");
            try {
                savedFile.createNewFile();
                fileItem.write(savedFile);
                log.info("recorded file: " + savedFile);
            } catch (Exception e) {
                log.error("error in creating file " + savedFile, e);
            }
        }
    }
}