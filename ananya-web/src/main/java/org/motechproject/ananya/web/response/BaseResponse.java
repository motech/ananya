package org.motechproject.ananya.web.response;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class BaseResponse {

    private static final String ERROR = "ERROR";
    private static final String SUCCESS = "SUCCESS";

    @JsonProperty
    @XmlElement
    protected String status;
    @JsonProperty
    @XmlElement
    protected String description;

    public BaseResponse(String status) {
        this.status = status;
    }

    private BaseResponse(String status, String description) {
        this.status = status;
        this.description = description;
    }

    BaseResponse() {
    }

    public static BaseResponse success() {
        return new BaseResponse(SUCCESS);
    }

    public static BaseResponse failure(String description) {
        return new BaseResponse(ERROR, description);
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseResponse)) return false;

        BaseResponse that = (BaseResponse) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}