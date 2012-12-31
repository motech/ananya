package org.motechproject.ananya.utils.log4j.rolling.helper;

import org.apache.log4j.rolling.helper.ActionBase;

import java.io.File;
import java.io.IOException;

public class FileDeleteAction extends ActionBase {
    private File[] filesToBeDeleted;

    public FileDeleteAction(File[] filesToBeDeleted) {
        this.filesToBeDeleted = filesToBeDeleted;
    }

    @Override
    public boolean execute() throws IOException {
        boolean success = true;

        for (File fileToBeDeleted : filesToBeDeleted) {
            if (fileToBeDeleted.exists())
                success &= fileToBeDeleted.delete();
        }

        return success;
    }

    public File[] getFilesToBeDeleted() {
        return filesToBeDeleted;
    }
}
