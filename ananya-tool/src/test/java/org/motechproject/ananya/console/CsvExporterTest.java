package org.motechproject.ananya.console;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class CsvExporterTest {
    @Test
    public void shouldBuildCsvReportWithTheGivenArguments() throws IOException {
        String fileName = "fileName.csv";
        String[] arguments = {"SampleFLW", fileName};
        CsvExporter.main(arguments);
        FileInputStream fileInputStream = new FileInputStream(fileName);
        String fileContent = IOUtils.toString(fileInputStream);
        assertEquals("Id,Custom column name\n" +
                "id1,title\n" +
                "id2,title\n", fileContent);
        FileUtils.deleteQuietly(new File(fileName));
    }
}


