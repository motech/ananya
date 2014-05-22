package org.motechproject.ananya.console;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.motechproject.ananya.exporter.csv.console.CsvExporterApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

public class CsvExporterAppTest {
    @Test
    public void shouldBuildCsvReportWithTheGivenArguments() throws IOException {
        String outputFileName = "outputFileName.csv";
        URL filtersFile = this.getClass().getResource("/filters.txt");
        String[] arguments = {"Sample-FLW", "-f " + filtersFile.getPath(), "-o " + outputFileName};

        CsvExporterApp.main(arguments);

        FileInputStream fileInputStream = new FileInputStream(outputFileName);
        String fileContent = IOUtils.toString(fileInputStream);

        assertEquals("Msisdn,Custom column name\n" +
                "1234,title\n", fileContent);
        FileUtils.deleteQuietly(new File(outputFileName));
    }
}


