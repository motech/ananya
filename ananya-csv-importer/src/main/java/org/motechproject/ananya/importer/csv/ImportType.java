package org.motechproject.ananya.importer.csv;

import org.apache.commons.lang.StringUtils;
import org.motechproject.importer.CSVDataImporter;

public enum ImportType {
    FrontLineWorker() {
        @Override
        void performAction(String importFile, CSVDataImporter csvDataImporter) {
            csvDataImporter.importData(ImportType.FrontLineWorker.name(), importFile);
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
        for (ImportType importType : ImportType.values()) {
            if (StringUtils.equalsIgnoreCase(importType.name(), (StringUtils.trimToEmpty(entity)))) {
                return importType;
            }
        }
        return null;
    }

    abstract void performAction(String importFile, CSVDataImporter csvDataImporter);
}
