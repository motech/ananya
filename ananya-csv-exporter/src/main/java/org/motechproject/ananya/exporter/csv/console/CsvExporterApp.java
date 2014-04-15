package org.motechproject.ananya.exporter.csv.console;

import org.motechproject.ananya.exporter.csv.util.CsvExporterArgumentsParser;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CsvExporterApp {
    private static final String APPLICATION_CONTEXT_XML = "applicationContext-csv-exporter.xml";

    public static void main(String arguments[]) {
        ClassPathXmlApplicationContext applicationContext = null;
        CsvExporterArgumentsParser csvExporterArgumentsParser = new CsvExporterArgumentsParser();
        try {
            csvExporterArgumentsParser.parse(arguments);

            String filterFilePath = csvExporterArgumentsParser.getFilterFilePath();
            String outputFilePath = csvExporterArgumentsParser.getOutputFilePath();
            String entityName = csvExporterArgumentsParser.getEntityName();

            applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
            CsvExporter csvExporter = (CsvExporter)applicationContext.getBean("csvExporter");

            csvExporter.buildReport(entityName, outputFilePath, filterFilePath);

            System.out.println(String.format("Report file generated successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            csvExporterArgumentsParser.printUsage();
        } finally {
            if (applicationContext != null) {
                applicationContext.close();
            }
        }
    }
}
