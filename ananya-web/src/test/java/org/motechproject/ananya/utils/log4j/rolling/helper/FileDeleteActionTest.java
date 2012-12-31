package org.motechproject.ananya.utils.log4j.rolling.helper;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileDeleteActionTest {
    @Test
    public void shouldDeleteFilesWhenExecuted() throws IOException {
        File baseDir = new File(this.getClass().getResource("/log4j_test_log_dir/").getPath());
        File newFile1 = new File(baseDir.getPath() + "new.1.log");
        assertTrue(newFile1.createNewFile());
        File newFile2 = new File(baseDir.getPath() + "new.2.log");
        assertTrue(newFile2.createNewFile());
        File newFile3 = new File(baseDir.getPath() + "new.3.log");
        assertTrue(newFile3.createNewFile());

        new FileDeleteAction(new File[]{newFile1, newFile2, newFile3}).execute();

        assertFalse(newFile1.exists());
        assertFalse(newFile2.exists());
        assertFalse(newFile3.exists());
    }
}
