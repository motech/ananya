package org.motechproject.ananya.utils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CSVRecord {
    private boolean forceQuote;
    private StringBuilder stringBuilder;

    public CSVRecord(boolean forceQuote) {
        this.forceQuote = forceQuote;
    }

    public CSVRecord() {
        this(false);
    }

    private void appendToStringBuilder(String string) {
        if(stringBuilder == null) {
            stringBuilder = new StringBuilder();
        } else {
            stringBuilder.append(",");
        }

        stringBuilder.append(string);
    }

    public CSVRecord append(String column) {
        if(column == null) {
            column = "";
        }

        if(forceQuote || column.indexOf(",") > -1) {
            column = quote(column);
        }

        appendToStringBuilder(column);

        return this;
    }


    private String quote(String column) {
        return "\"" + column.replaceAll("\"", "\"\"") + "\"";
    }

    @Override
    public String toString() {
        return stringBuilder == null? "" : stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CSVRecord that = (CSVRecord) o;

        return new EqualsBuilder()
                .append(this.forceQuote, that.forceQuote)
                .append(this.stringBuilder, that.stringBuilder)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.forceQuote)
                .append(this.stringBuilder)
                .toHashCode();
    }
}

