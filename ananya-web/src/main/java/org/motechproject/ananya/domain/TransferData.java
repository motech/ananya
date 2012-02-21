package org.motechproject.ananya.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class TransferData<T> {
    private String token;
    private T data;

    public TransferData(String token, T data) {
        this.token = token;
        this.data = data;
    }

    public String token() {
        return token;
    }

    public T data() {
        return data;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}