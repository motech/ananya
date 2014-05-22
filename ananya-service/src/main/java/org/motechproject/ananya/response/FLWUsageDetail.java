package org.motechproject.ananya.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FLWUsageDetail {

    @XmlElement
    private Integer year;
    @XmlElement
    private Integer month;
    @XmlElement
    private Long mobileKunji;
    @XmlElement
    private Long mobileAcademy;

    public FLWUsageDetail() {
    }

    public FLWUsageDetail(Integer year, Integer month, Long mobileKunji, Long mobileAcademy) {
        this.year = year;
        this.month = month;
        this.mobileKunji = mobileKunji;
        this.mobileAcademy = mobileAcademy;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Long getMobileKunji() {
        return mobileKunji;
    }

    public Long getMobileAcademy() {
        return mobileAcademy;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
