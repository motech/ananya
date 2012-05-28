package org.motechproject.ananya.console;

import org.motechproject.ananya.util.CsvExporterArgumentsParser;
import org.motechproject.export.model.AllReportDataSources;
import org.motechproject.export.model.ReportDataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CsvExporterApp {

    private static final String APPLICATION_CONTEXT_XML = "applicationContext-csv-exporter.xml";

    public static void main(String arguments[]) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        CsvExporterArgumentsParser csvExporterArgumentsParser = new CsvExporterArgumentsParser();
        try {
            csvExporterArgumentsParser.parse(arguments);
            String filterFilePath = csvExporterArgumentsParser.getFilterFilePath();
            String outputFilePath = csvExporterArgumentsParser.getOutputFilePath();
            String entityName = csvExporterArgumentsParser.getEntityName();

            AllReportDataSources allReportDataSources = (AllReportDataSources) applicationContext.getBean("allReportDataSources");
            ReportDataSource reportDataSource = allReportDataSources.get(entityName);

            new CsvExporter(reportDataSource, filterFilePath, outputFilePath).buildReport();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            csvExporterArgumentsParser.printUsage();
        } finally {
            applicationContext.close();
        }
    }
}
