package org.motechproject.ananya.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CSVRecordTest {

    @Test
    public void shouldConstructACSVRecord() {
        assertEquals("r1", new CSVRecord().append("r1").toString());
        assertEquals("", new CSVRecord().toString());
        assertEquals("r1,r2", new CSVRecord().append("r1").append("r2").toString());
        assertEquals("r1,r2,,,r3", new CSVRecord().append("r1").append("r2").append(null).append(null).append("r3").toString());
        assertEquals("r1,", new CSVRecord().append("r1").append("").toString());
        assertEquals("r1,", new CSVRecord().append("r1").append(null).toString());
        assertEquals("r1,  ", new CSVRecord().append("r1").append("  ").toString());
    }

    @Test
    public void shouldQuoteAColumnIfTheValueHasComma() {
        assertEquals("r1,\"r2\"\",\"", new CSVRecord().append("r1").append("r2\",").toString());
    }

    @Test
    public void shouldQuoteAllColumns() {
        assertEquals("\"r1\",\"r2\"\",\",\"\",\"\"", new CSVRecord(true).append("r1").append("r2\",").append(null).append("").toString());
    }
}
