package org.motechproject.ananya.exporter.csv.console;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.export.service.ExportService;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CsvExporterTest {
    @Mock
    private ExportService exportService;

    @Test
    public void shouldInvokeExporterServiceToExportCSVData() throws Exception {
        CsvExporter csvExporter = new CsvExporter(exportService);
        String sampleEntity = "SampleEntity";
        String filterFilePath = this.getClass().getResource("/filters.txt").getPath();
        String fileOut = FileUtils.getTempDirectoryPath() + "/out.txt";

        csvExporter.buildReport(sampleEntity, fileOut, filterFilePath);

        ArgumentCaptor<Writer> writerArgumentCaptor = ArgumentCaptor.forClass(Writer.class);
        ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(exportService).exportAsCSV(eq(sampleEntity), writerArgumentCaptor.capture(), objectArgumentCaptor.capture());
        assertEquals(FileWriter.class, writerArgumentCaptor.getValue().getClass());
        Map<String, String> actualParametersMap = (Map<String, String>) objectArgumentCaptor.getValue();
        assertEquals(1, actualParametersMap.size());
        assertEquals("1234", actualParametersMap.get("msisdn"));
    }
}
