package org.motechproject.ananya.domain.dimension;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "language_dimension")
@NamedQuery(name = LanguageDimension.FIND_BY_LANGUAGE_NAME, query = "select l from LanguageDimension l where l.name=:name")
public class LanguageDimension {

    public static final String FIND_BY_LANGUAGE_NAME = "find.by.language.name";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "sms_message")
    private String smsMessage;

    public LanguageDimension() {
    }

    public LanguageDimension(String name, String languageCode, String smsMessage) {
        this.name = name;
        this.languageCode = languageCode;
        this.smsMessage = smsMessage;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLanguageName() {
		return name;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LanguageDimension that = (LanguageDimension) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (languageCode != null ? !languageCode.equals(that.languageCode) : that.languageCode != null) return false;
        if (smsMessage != null ? !smsMessage.equals(that.smsMessage) : that.smsMessage != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (languageCode != null ? languageCode.hashCode() : 0);
        result = 31 * result + (smsMessage != null ? smsMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LanguageDimension{" +
                "name='" + name +
                "', languageCode='" + languageCode +
                "', smsMessage='" + smsMessage +
                "'}";
    }
}
