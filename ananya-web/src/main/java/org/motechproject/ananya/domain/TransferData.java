package org.motechproject.ananya.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class TransferData {
    private String token;
    private DataTransferType type;
    private String data;

    public TransferData(String token, String data) {
        this.token = token;
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public DataTransferType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}