package org.motechproject.ananya.importer.csv;


import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.importer.csv.exception.FileReadException;
import org.motechproject.ananya.importer.csv.exception.InvalidArgumentException;
import org.motechproject.ananya.importer.csv.exception.WrongNumberArgsException;
import org.motechproject.importer.CSVDataImporter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class CsvImporter {

    private static final String APPLICATION_CONTEXT_XML = "applicationContext-DataImporter.xml";

    public static void main(String args[]) throws Exception {
        try {
            validateArguments(args);

            String entityType = args[0];
            String filePath = args[1];
            ImportType importType = validateAndSetImportType(entityType);
            validateImportFile(filePath);

            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
            CSVDataImporter csvDataImporter = (CSVDataImporter) context.getBean("csvDataImporter");
            importType.performAction(filePath, csvDataImporter);
        } catch (Exception exception) {
            throw exception;
        }
    }

    private static void validateArguments(String[] args) throws WrongNumberArgsException {
        if (args.length != 2)
            throw new WrongNumberArgsException("Wrong number of arguments. Arguments expected in order : <entity_type> <file_name>");
    }

    private static ImportType validateAndSetImportType(String entity) throws Exception {
        if (ImportType.isInValid(entity))
            throw new InvalidArgumentException("Invalid entity. Valid entities are : FrontLineWorker, Location");
        return ImportType.findFor(entity);
    }

    private static void validateImportFile(String importFile) {
        if (!new File(importFile).canRead()) {
            new FileReadException("Cannot read import file " + importFile);
        }
    }
}

enum ImportType {
    FrontLineWorker() {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            csvDataImporter.importData("frontLineWorkerImporter", importFile);
        }
    }, Location {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
        }
    };

    public static boolean isInValid(String entity) {
        return findFor(entity) == null;
    }

    public static ImportType findFor(String entity) {
        for (ImportType designation : ImportType.values()) {
            if (designation.name().equals(StringUtils.trimToEmpty(entity).toUpperCase())) {
                return designation;
            }
        }
        return null;
    }

    abstract void performAction(String importFile, CSVDataImporter csvDataImporter);
};

