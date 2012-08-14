package org.motechproject.ananya.exporter.csv.console;

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
        String outputFilePath = this.getClass().getResource("/out.txt").getPath();
        String filterFilePath = this.getClass().getResource("/filters.txt").getPath();

        csvExporter.buildReport(sampleEntity, outputFilePath, filterFilePath);

        ArgumentCaptor<Writer> writerArgumentCaptor = ArgumentCaptor.forClass(Writer.class);
        ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(exportService).exportAsCSV(eq(sampleEntity), writerArgumentCaptor.capture(), objectArgumentCaptor.capture());
        assertEquals(FileWriter.class, writerArgumentCaptor.getValue().getClass());
        Map<String, String> actualParametersMap = (Map<String, String>) objectArgumentCaptor.getValue();
        assertEquals(1, actualParametersMap.size());
        assertEquals("1234", actualParametersMap.get("msisdn"));
    }
}
