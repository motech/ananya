package org.motechproject.ananya.util;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.junit.Test;
import org.motechproject.ananya.exporter.csv.util.CsvExporterArgumentsParser;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class CsvExporterArgumentsParserTest {

    @Test
    public void shouldParseTheArgumentsString() throws Exception {
        String entityName = "FLW";
        String filterFile = "filters.txt";
        String outputFile = "outputFile.csv";
        String[] arguments = {entityName, "-f " + filterFile, "-o " + outputFile};
        CsvExporterArgumentsParser csvExporterArgumentsParser = new CsvExporterArgumentsParser();

        csvExporterArgumentsParser.parse(arguments);

        assertEquals(entityName, csvExporterArgumentsParser.getEntityName());
        assertEquals(filterFile, csvExporterArgumentsParser.getFilterFilePath());
        assertEquals(outputFile, csvExporterArgumentsParser.getOutputFilePath());
    }

    @Test
    public void shouldParseTheArgumentsAndPopulateAppropriateOptions() throws Exception {
        String entityName = "FLW";
        String filterFile = "filters.txt";
        String[] arguments = {entityName, "-f " + filterFile};
        CsvExporterArgumentsParser csvExporterArgumentsParser = new CsvExporterArgumentsParser();

        csvExporterArgumentsParser.parse(arguments);

        assertEquals(entityName, csvExporterArgumentsParser.getEntityName());
        assertEquals(filterFile, csvExporterArgumentsParser.getFilterFilePath());
        assertNull(csvExporterArgumentsParser.getOutputFilePath());
    }

    @Test(expected = WrongNumberArgsException.class)
    public void shouldRaiseExceptionIfEntityNameIsNotProvided() throws Exception {
        String filterFile = "filters.txt";
        String outputFile = "outputFile.csv";
        String[] arguments = {"-f " + filterFile, "-o " + outputFile};

        CsvExporterArgumentsParser csvExporterArgumentsParser = new CsvExporterArgumentsParser();

        csvExporterArgumentsParser.parse(arguments);
        assertNull(csvExporterArgumentsParser.getFilterFilePath());
        assertNull(csvExporterArgumentsParser.getOutputFilePath());
    }
}
