package org.motechproject.ananya.util;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.apache.commons.cli.*;

public class CsvExporterArgumentsParser {
    private String filterFilePath;
    private String outputFilePath;
    private String entityName;
    private Options options;

    public String getFilterFilePath() {
        return filterFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public String getEntityName() {
        return entityName;
    }

    public void parse(String[] arguments) throws Exception {
        CommandLineParser parser = new PosixParser();

        options = new Options();
        options.addOption("f", true, "filters file path");
        options.addOption("o", true, "output file path");
        CommandLine line = parser.parse(options, arguments);

        if (line.getArgs().length != 1)
            throw new WrongNumberArgsException("Expected 1 Argument, Got Nothing");
        else
            entityName = line.getArgs()[0].toUpperCase();

        if (line.hasOption("f"))
            filterFilePath = line.getOptionValue("f").trim();

        if (line.hasOption("o"))
            outputFilePath = line.getOptionValue("o").trim();
    }

    public void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(" <Entity to be reported>", options);
    }
}
